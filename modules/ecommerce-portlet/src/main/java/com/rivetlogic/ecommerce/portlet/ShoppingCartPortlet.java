package com.rivetlogic.ecommerce.portlet;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.rivetlogic.ecommerce.beans.ShoppingCartItem;
import com.rivetlogic.ecommerce.beans.ShoppingCartPrefsBean;
import com.rivetlogic.ecommerce.configuration.EcommerceGroupServiceConfiguration;
import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;
import com.rivetlogic.ecommerce.model.ShoppingOrder;
import com.rivetlogic.ecommerce.model.ShoppingOrderItem;
import com.rivetlogic.ecommerce.notification.EmailNotificationUtil;
import com.rivetlogic.ecommerce.notification.NotificationConstants;
import com.rivetlogic.ecommerce.paypal.util.PaypalUtil;
import com.rivetlogic.ecommerce.service.ShoppingOrderItemLocalServiceUtil;
import com.rivetlogic.ecommerce.service.ShoppingOrderLocalServiceUtil;
import com.rivetlogic.ecommerce.util.ShoppingCartItemUtil;
import com.rivetlogic.ecommerce.web.config.EcommerceRequestHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author katalinamarcos
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.header-portlet-javascript=/js/main.js",
		"com.liferay.portlet.struts-path="+ShoppingCartPortletKeys.PORTLET_STRUTS ,
		"javax.portlet.display-name=Shopping Cart",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/shopping_cart/view.jsp",
		"javax.portlet.name=" + ShoppingCartPortletKeys.SHOPPING_CART,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class ShoppingCartPortlet extends MVCPortlet {
	
	private static final Log logger = LogFactoryUtil.getLog(ShoppingCartPortlet.class);

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		if (themeDisplay.isSignedIn()) {
			try {
				ShoppingOrder activeShoppingOrder = ShoppingOrderLocalServiceUtil.getUserActiveOrder(
						themeDisplay.getUserId(), themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(),
						Boolean.FALSE);
				if (null != activeShoppingOrder) {
					List<ShoppingCartItem> cartItems = ShoppingCartItemUtil
							.getCartItems(activeShoppingOrder.getOrderId(), themeDisplay);
					if (null != cartItems && !cartItems.isEmpty()) {
						renderRequest.setAttribute(ShoppingCartPortletKeys.ATTR_CURRENT_ORDER_ITEMS, cartItems);
					}
				}
			} catch (SystemException e) {
				logger.error(e.getMessage());
			}
		} else {
			List<String> orderItems = getOrderItemsIdsFromSession(renderRequest);
			if (null != orderItems) {
				Map<String, ShoppingCartItem> orderItemsMap = new HashMap<String, ShoppingCartItem>();
				for (String orderItemId : orderItems) {
					if (null == orderItemsMap.get(orderItemId)) {
						ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
						shoppingCartItem.setProductId(orderItemId);
						ShoppingCartItemUtil.setCartItemDetails(orderItemId, themeDisplay, shoppingCartItem);
						orderItemsMap.put(orderItemId, shoppingCartItem);
					} else {
						orderItemsMap.get(orderItemId).setCount(orderItemsMap.get(orderItemId).getCount() + 1);
					}
				}
				List<ShoppingCartItem> list = new ArrayList<>();
				list.addAll(orderItemsMap.values());
				renderRequest.setAttribute(ShoppingCartPortletKeys.ATTR_CURRENT_ORDER_ITEMS, list);
			}
		}

		super.doView(renderRequest, renderResponse);
	}
	
	public static List<String> getOrderItemsIdsFromSession(PortletRequest request) {
		List<String> orderItemsIdsList = null;
		PortletSession portletSession = request.getPortletSession();
		if(null != portletSession){
			String cartItemsStrVal = (String)portletSession.getAttribute(ShoppingCartPortletKeys.COOKIE_SHOPPING_CART_ITEMS);
			if (null != cartItemsStrVal && !cartItemsStrVal.isEmpty()) {
				orderItemsIdsList = (Arrays.asList(StringUtil.split(cartItemsStrVal, StringPool.COMMA)));
			}
		}
		return orderItemsIdsList;
	}

	public void checkout(ActionRequest request, ActionResponse response) throws IOException, PortletException {
		SessionMessages.add(request,
				PortalUtil.getPortletId(request) + SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		SessionMessages.add(request,
				PortalUtil.getPortletId(request) + SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);

		String redirect = ParamUtil.getString(request, WebKeys.REDIRECT);
		boolean isPaypal = ParamUtil.getBoolean(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_PAYPAL);

		EcommerceGroupServiceConfiguration conf = EcommerceRequestHelper.getEcommerceGroupServiceConfiguration(request);
		Locale locale = request.getLocale();
		ShoppingCartPrefsBean shoppingCartPrefsBean = new ShoppingCartPrefsBean(conf, locale);
		
		if (!validateCheckoutInfo(request)) {
			SessionErrors.add(request, ShoppingCartPortletKeys.MESSAGE_MISSING_REQUIRED_CHECKOUT_INFO);
		} else if (!(shoppingCartPrefsBean.isCartPrefsValidForCheckout(isPaypal))) {
			SessionErrors.add(request, ShoppingCartPortletKeys.ERROR_MESSAGE_CHECKOUT);
			logger.error(ERROR_CHECKOUT_MISSING_PORTLET_CONFIG);
		} else {
			try {
				String value = doCheckout(request, response, isPaypal);
				if (value != null) {
					redirect = value;
				}
			} catch (Exception e) {
				SessionErrors.add(request, ShoppingCartPortletKeys.ERROR_MESSAGE_CHECKOUT);
				logger.error(e);
			}
		}

		response.sendRedirect(redirect);
	}
	
	private boolean validateCheckoutInfo(ActionRequest request){
		boolean valid = true;
		String name = ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_NAME);
		String email = ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_EMAIL);
		String address1 = ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_ADDRESS_1);
		String city = ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_CITY);
		String stateProvince = ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_STATE_PROVINCE);
		if ((name == null || name.isEmpty())
				|| (email == null || email.isEmpty())
				|| (address1 == null || address1.isEmpty())
				|| (city == null || city.isEmpty())
				|| (stateProvince == null || stateProvince.isEmpty())) {
			valid = false;
		}
		return valid;
	}
	
	private String doCheckout(ActionRequest request, ActionResponse response, boolean isPaypal) throws Exception{
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		ShoppingOrder activeShoppingOrder = null;
		List<String> orderItemsIdsList = null;
		Map<String, Float> prices = null;
		if (themeDisplay.isSignedIn()) {
			activeShoppingOrder = ShoppingOrderLocalServiceUtil.getUserActiveOrder(
					themeDisplay.getUserId(),
					themeDisplay.getScopeGroupId(),
					themeDisplay.getCompanyId(),
					Boolean.TRUE);
			List<ShoppingOrderItem> orderItemsList = ShoppingOrderItemLocalServiceUtil.findByOrderId(activeShoppingOrder.getOrderId());
			if (null == orderItemsList || orderItemsList.isEmpty())
				return null;
		} else {
			orderItemsIdsList = getOrderItemsIdsFromSession(request);
			prices = getSessionOrderItemPrices(request);
			if (null != orderItemsIdsList) {
				activeShoppingOrder = ShoppingOrderLocalServiceUtil.createOrder(CounterLocalServiceUtil.increment(ShoppingOrderItem.class.getName()));
				activeShoppingOrder.setGroupId(themeDisplay.getScopeGroupId());
				activeShoppingOrder.setCompanyId(themeDisplay.getCompanyId());
				activeShoppingOrder.setUserId(-1l);
				//TODO review activeShoppingOrder.setUserName(RoleConstants.GUEST);
			}
		}
		
		List<ShoppingCartItem> shoppingCartItems = 
                (themeDisplay.isSignedIn() ? ShoppingCartItemUtil.getCartItems(activeShoppingOrder.getOrderId(), themeDisplay) : ShoppingCartItemUtil.getCartItemsByProductId(orderItemsIdsList, themeDisplay));
        if (null != shoppingCartItems) {
            double orderTotal = 0l;
            for (ShoppingCartItem shoppingCartItem : shoppingCartItems) {
                orderTotal += Float.valueOf(shoppingCartItem.getPrice())
                        * (float) shoppingCartItem.getCount();
            }
            activeShoppingOrder.setTotal(orderTotal);
        }
		
		activeShoppingOrder.setCustomerName(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_NAME));
		activeShoppingOrder.setCustomerEmail(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_EMAIL));
		activeShoppingOrder.setShippingAddress1(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_ADDRESS_1));
		activeShoppingOrder.setShippingAddress2(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_ADDRESS_2));
		activeShoppingOrder.setShippingCity(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_CITY));
		activeShoppingOrder.setShippingStateProvince(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_STATE_PROVINCE));
		activeShoppingOrder.setShippingPostalCode(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_POSTAL_CODE));
		activeShoppingOrder.setShippingCountry(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_COUNTRY));
		activeShoppingOrder.setCustomerPhone(ParamUtil.getString(request, ShoppingCartPortletKeys.CHECKOUT_PARAMETER_PHONE));
		EcommerceGroupServiceConfiguration conf = EcommerceRequestHelper.getEcommerceGroupServiceConfiguration(request);
		Locale locale = request.getLocale();
		ShoppingCartPrefsBean cartPrefsBean = new ShoppingCartPrefsBean(conf, locale);

		Message customerMessage = EmailNotificationUtil.getNotificationMessage(themeDisplay, activeShoppingOrder, orderItemsIdsList, cartPrefsBean, NotificationConstants.CUSTOMER_NOTIFICATION);
		Message storeMessage = EmailNotificationUtil.getNotificationMessage(themeDisplay, activeShoppingOrder, orderItemsIdsList, cartPrefsBean, NotificationConstants.STORE_NOTIFICATION);
		
		ServiceContext serviceContext = ServiceContextFactory.getInstance(request);
		
		ShoppingOrderLocalServiceUtil.placeOrder(activeShoppingOrder, new Message[]{customerMessage, storeMessage}, orderItemsIdsList, prices, isPaypal, serviceContext);
		removeOrderItemsIdsFromSession(request);
		
		if(isPaypal) {
		    EmailNotificationUtil.storeEmailNotification(activeShoppingOrder.getOrderId(), customerMessage, serviceContext);
		    EmailNotificationUtil.storeEmailNotification(activeShoppingOrder.getOrderId(), storeMessage, serviceContext);
		    return PaypalUtil.getPaypalRedirect(request, response, activeShoppingOrder);
		} else {
		    SessionMessages.add(request, ShoppingCartPortletKeys.SUCCESS_MESSAGE_CHECKOUT);
		    return null;
		}
	}
	
	private Map<String,Float> getSessionOrderItemPrices(PortletRequest request) {
	    PortletSession portletSession = request.getPortletSession();
	    if(portletSession != null) {
	        Map<String, Float> map = new HashMap<String,Float>();
	        String values = (String) portletSession.getAttribute(ShoppingCartPortletKeys.COOKIE_SHOPPING_CART_PRICES);
	        String[] prices = values.split(",");
	        for(String price : prices) {
	            String[] item = price.split("=");
	            map.put(item[0], Float.parseFloat(item[1]));
	        }
	        return map;
	    } else {
	        return null;
	    }
	}
	
	private void removeOrderItemsIdsFromSession(PortletRequest request){
		PortletSession portletSession = request.getPortletSession();
		portletSession.setAttribute(ShoppingCartPortletKeys.COOKIE_SHOPPING_CART_ITEMS, StringPool.BLANK);
	}
	
	private static final String ERROR_CHECKOUT_MISSING_PORTLET_CONFIG = "\n PORTLET CONFIGURATION IS NOT COMPLETE. Some information in the portlet configuration is missing. \n";
	
}