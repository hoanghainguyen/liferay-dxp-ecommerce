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
package com.rivetlogic.ecommerce.portlet.command;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;
import com.rivetlogic.ecommerce.portlet.ShoppingCartPortlet;
import com.rivetlogic.ecommerce.service.ShoppingOrderItemLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
		property = {
	        "javax.portlet.name="+ShoppingCartPortletKeys.SHOPPING_CART,
	        "mvc.command.name="+ShoppingCartPortletKeys.RC_REMOVE_ITEM_FROM_CART
	    },
	    service = MVCResourceCommand.class
	)
public class RemoveCartItemResourceCommand extends BaseResourceCommand {

	private static final Log _log = LogFactoryUtil.getLog(RemoveCartItemResourceCommand.class);

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		String itemId = null;
		boolean error = false;

		_log.info("Entro al serverResource");

		try {

			itemId = ParamUtil.getString(resourceRequest, ShoppingCartPortletKeys.WEB_CONTENT_ITEM_ID);
			if (null == itemId || itemId.isEmpty()) {
				printJsonResponse(ERROR_MISSING_ITEM_ID_MESSAGE, HttpServletResponse.SC_BAD_REQUEST, resourceResponse);
				error = true;
			}
			doRemoveCartItem(resourceRequest, resourceResponse);
			addCartDetailsOnResponse(resourceRequest, resourceResponse, null);

		} catch (Exception e) {

			_log.error(String.format(ERROR_ADDING_ITEM_TO_CART_LOG, itemId, e));
			error = printJsonResponse(ERROR_ADDING_ITEM_TO_CART_MESSAGE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					resourceResponse);
		}
		return error;

	}

	private void doRemoveCartItem(ResourceRequest request, ResourceResponse response)
			throws PortalException, SystemException {
		String itemId = ParamUtil.getString(request, ShoppingCartPortletKeys.WEB_CONTENT_ITEM_ID);
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		if (!themeDisplay.isSignedIn()) {
			List<String> currentCartItems = ShoppingCartPortlet.getOrderItemsIdsFromSession(request);
			if (null != currentCartItems) {
				List<String> newCartItems = new ArrayList<>();
				for (String cartItemId : currentCartItems) {
					if (!cartItemId.equals(itemId))
						newCartItems.add(cartItemId);
				}
				setSessionOrderItemsIds(request, ListUtil.toString(newCartItems, StringPool.BLANK, StringPool.COMMA));
			}
		} else {
			Long cartItemId = Long.valueOf(itemId);
			ShoppingOrderItemLocalServiceUtil.deleteShoppingOrderItem(cartItemId);
		}
	}

}