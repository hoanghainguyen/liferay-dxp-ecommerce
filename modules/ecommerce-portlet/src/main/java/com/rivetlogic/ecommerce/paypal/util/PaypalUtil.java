package com.rivetlogic.ecommerce.paypal.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.rivetlogic.ecommerce.beans.ShoppingCartPrefsBean;
import com.rivetlogic.ecommerce.configuration.EcommerceGroupServiceConfiguration;
import com.rivetlogic.ecommerce.model.ShoppingOrder;
import com.rivetlogic.ecommerce.web.config.EcommerceRequestHelper;

import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * 
 * @author joseross
 *
 */
public class PaypalUtil {

	private static final Log LOG = LogFactoryUtil.getLog(PaypalUtil.class);

	public static String getPaypalRedirect(ActionRequest request, ActionResponse response,
			ShoppingOrder shoppingOrder) {
		EcommerceGroupServiceConfiguration conf = EcommerceRequestHelper
				.getEcommerceGroupServiceConfiguration(request);
		Locale locale = request.getLocale();
		ShoppingCartPrefsBean prefsBean = new ShoppingCartPrefsBean(conf, locale);

		if (prefsBean.isPaypalEnabled()) {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			String portalUrl = themeDisplay.getPortalURL();
			String returnUrl = portalUrl + request.getParameter(WebKeys.REDIRECT);
			String notifyUrl = portalUrl + PaypalConstants.NOTIFY_ACTION_PATH;
			String paypalEmail = prefsBean.getPaypalEmail();
			String orderId = Long.toString(shoppingOrder.getOrderId());
			double orderTotal = shoppingOrder.getTotal();
			String[] names = shoppingOrder.getCustomerName().split(StringPool.SPACE);
			String firstName = names[0];
			String lastName = names.length > 1 ? names[1] : StringPool.BLANK;

			StringBuilder sb = new StringBuilder();
			sb.append(PaypalConstants.PAYPAL_ENDPOINT).append(StringPool.QUESTION);
			formatParam(sb, PaypalConstants.PARAM_CMD, PaypalConstants.CMD_XCLICK);
			formatParam(sb, PaypalConstants.PARAM_BUSINESS, paypalEmail);
			formatParam(sb, PaypalConstants.PARAM_ITEM_NAME, orderId);
			formatParam(sb, PaypalConstants.PARAM_ITEM_NUMBER, orderId);
			formatParam(sb, PaypalConstants.PARAM_INVOICE, orderId);
			formatParam(sb, PaypalConstants.PARAM_AMOUNT, orderTotal);
			formatParam(sb, PaypalConstants.PARAM_RETURN, returnUrl);
			formatParam(sb, PaypalConstants.PARAM_NOTIFY_URL, notifyUrl);
			formatParam(sb, PaypalConstants.PARAM_FIRST_NAME, firstName);
			formatParam(sb, PaypalConstants.PARAM_LAST_NAME, lastName);
			formatParam(sb, PaypalConstants.PARAM_EMAIL, shoppingOrder.getCustomerEmail());
			formatParam(sb, PaypalConstants.PARAM_ADDRESS1, shoppingOrder.getShippingAddress1());
			formatParam(sb, PaypalConstants.PARAM_CITY, shoppingOrder.getShippingCity());
			formatParam(sb, PaypalConstants.PARAM_STATE, shoppingOrder.getShippingStateProvince());
			formatParam(sb, PaypalConstants.PARAM_ZIP, shoppingOrder.getShippingPostalCode());
			formatParam(sb, PaypalConstants.PARAM_NO_NOTE, "1");
			formatParam(sb, PaypalConstants.PARAM_CURRENCY, PaypalConstants.CURRENCY_USD);

			LOG.info("Paypal URL: " + sb);
			if (LOG.isDebugEnabled())
				LOG.debug("Paypal URL: " + sb);

			return sb.toString();
		} else {
			return null;
		}
	}

	private static void formatParam(StringBuilder sb, String name, Object value) {
		if (!StringPool.QUESTION.equals(sb.subSequence(sb.length() - 1, sb.length() - 1)))
			sb.append(StringPool.AMPERSAND);
		sb.append(String.format("%s=%s", name, HttpUtil.encodeURL(value.toString())));
	}

}
