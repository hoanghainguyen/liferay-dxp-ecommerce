package com.rivetlogic.ecommerce.portlet.command;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
		property = {
	        "javax.portlet.name="+ShoppingCartPortletKeys.SHOPPING_CART,
	        "mvc.command.name="+ShoppingCartPortletKeys.RC_GET_CART_INFO
	    },
	    service = MVCResourceCommand.class
	)
public class GetCartInfoResourceCommand extends BaseResourceCommand {

	private static final Log _log = LogFactoryUtil.getLog(GetCartInfoResourceCommand.class);

	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {

		boolean error = false;
		try {
			addCartDetailsOnResponse(resourceRequest, resourceResponse, null);

		} catch (Exception e) {
			_log.error(String.format(ERROR_SERVING_RESOURCE, ShoppingCartPortletKeys.RC_GET_CART_INFO, e));
			error = printJsonResponse(ERROR_SERVING_RESOURCE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					resourceResponse);

		}
		return error;

	}

}