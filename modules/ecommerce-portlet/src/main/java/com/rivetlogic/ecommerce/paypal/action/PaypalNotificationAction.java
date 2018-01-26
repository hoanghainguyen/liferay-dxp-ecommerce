/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rivetlogic.ecommerce.paypal.action;

import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;
import com.rivetlogic.ecommerce.model.ShoppingOrder;
import com.rivetlogic.ecommerce.model.ShoppingOrderItem;
import com.rivetlogic.ecommerce.notification.EmailNotificationUtil;
import com.rivetlogic.ecommerce.paypal.util.PaypalConstants;
import com.rivetlogic.ecommerce.service.ShoppingOrderItemLocalServiceUtil;
import com.rivetlogic.ecommerce.service.ShoppingOrderLocalServiceUtil;
import com.rivetlogic.ecommerce.util.OrderStatusEnum;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
        "path=/portal/ecommerceportlet"
    },
    service = StrutsAction.class
)
public class PaypalNotificationAction extends BaseStrutsAction  {
    
    private static final String LOG_NOTIFICATION = "Paypal Notification for order %s, status: %s";
    private static final String LOG_UNKNOWN_ORDER = "Unknown order received: %s";
    private static final String LOG_DEBUG_PARAM = "Param %s = %s";
    private static final String LOG_DEBUG_QUERY = "Paypal Query: %s";
    private static final String LOG_STATUS_RESPONSE = "Paypal Status Response: %s";
    private static final String LOG_TRX_VERIFIED = "Transaction Verified, sending emails...";
    private static final String LOG_TRX_ERROR = "Error on Paypal Notification";

	public String execute(
			HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Processing path /c/portal/ecommerceportlet");
		}

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("ecommerceportletstrutsaction/html/portal/ecommerceportlet.jsp");

		requestDispatcher.forward(request, response);

		return doExecute(request, response);
	}

	private String doExecute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        long orderId = ParamUtil.getLong(request, PaypalConstants.PARAM_INVOICE);
        String status = ParamUtil.getString(request, PaypalConstants.PAYMENT_STATUS);
        
        _log.info(String.format(LOG_NOTIFICATION, orderId, status));
                
        ShoppingOrder order = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
        List<ShoppingOrderItem> items = ShoppingOrderItemLocalServiceUtil.findByOrderId(orderId);
        
        if(items.isEmpty()) {
            _log.error(String.format(LOG_UNKNOWN_ORDER, orderId));
            return null;
        }
        
        List<String> itemsIds = new ArrayList<String>();
        for(ShoppingOrderItem item : items) {
            itemsIds.add(Long.toString(item.getItemId()));
        }
        
        String query = PaypalConstants.PARAM_CMD + "=" + PaypalConstants.CMD_VALIDATE;
        
        Enumeration<String> enu = request.getParameterNames();
        
        while (enu.hasMoreElements()) {
            String name = enu.nextElement();
            String value = request.getParameter(name);
            if(_log.isDebugEnabled())
                _log.debug(String.format(LOG_DEBUG_PARAM, name, value));
            query = query + "&" + name + "=" + HttpUtil.encodeURL(value);
        }
        
        if(_log.isDebugEnabled())
            _log.debug(String.format(LOG_DEBUG_QUERY, query));
        
        URL url = new URL(PaypalConstants.PAYPAL_ENDPOINT);
        
        URLConnection urlc = url.openConnection();

        urlc.setDoOutput(true);
        urlc.setRequestProperty(
            "Content-Type","application/x-www-form-urlencoded");

        PrintWriter pw = UnsyncPrintWriterPool.borrow(
            urlc.getOutputStream());

        pw.println(query);

        pw.close();

        UnsyncBufferedReader unsyncBufferedReader =
            new UnsyncBufferedReader(
                new InputStreamReader(urlc.getInputStream()));

        String payPalStatus = unsyncBufferedReader.readLine();

        unsyncBufferedReader.close();
        
        _log.info(String.format(LOG_STATUS_RESPONSE, payPalStatus));
        
        if (payPalStatus.equals(PaypalConstants.TRANSACTION_VERIFIED) && status.equals(PaypalConstants.PAYMENT_COMPLETE)) {
            
            _log.info(LOG_TRX_VERIFIED);
            
            order.setOrderStatus(OrderStatusEnum.PAID.toString());
            ServiceContext serviceContext= ServiceContextFactory.getInstance(request);
            ShoppingOrderLocalServiceUtil.updateOrder(order, serviceContext);
            
            EmailNotificationUtil.sendEmailNotification(orderId);
                
        } else {
            _log.error(LOG_TRX_ERROR);
        }
        
        return "/portal/paypal.jsp";
    }
	
	@Reference(target = "(osgi.web.symbolicname=ecommerceportlet.strutsaction)")
	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PaypalNotificationAction.class);

	private ServletContext _servletContext;

}