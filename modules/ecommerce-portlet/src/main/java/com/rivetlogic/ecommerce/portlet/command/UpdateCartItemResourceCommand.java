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
import com.rivetlogic.ecommerce.model.ShoppingOrderItem;
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
	        "mvc.command.name="+ShoppingCartPortletKeys.RC_UPDATE_CART_ITEM
	    },
	    service = MVCResourceCommand.class
	)
public class UpdateCartItemResourceCommand extends BaseResourceCommand {

	private static final Log _log = LogFactoryUtil.getLog(UpdateCartItemResourceCommand.class);

	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		String itemId = null;
		boolean error = false;
		try {
			itemId = ParamUtil.getString(resourceRequest, ShoppingCartPortletKeys.WEB_CONTENT_ITEM_ID);
			_log.info("Entro al serverResource" + itemId);

			Integer count = ParamUtil.getInteger(resourceRequest, ShoppingCartPortletKeys.WEB_CONTENT_ITEM_COUNT);
			if ((null == itemId || itemId.isEmpty()) || (null == count || count <= 0)) {
				printJsonResponse(ERROR_MISSING_ITEM_ID_MESSAGE, HttpServletResponse.SC_BAD_REQUEST, resourceResponse);
				error = true;
			}
			doUpdateCartItem(resourceRequest, resourceResponse);
			addCartDetailsOnResponse(resourceRequest, resourceResponse, itemId);

		} catch (Exception e) {
			_log.error(String.format(ERROR_UPDATING_CART_ITEM_LOG, itemId, e));
			error = printJsonResponse(ERROR_UPDATING_CART_ITEM_MESSAGE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					resourceResponse);

		}
		return error;

	}

	private void doUpdateCartItem(ResourceRequest request, ResourceResponse response) throws SystemException {
		String itemId = ParamUtil.getString(request, ShoppingCartPortletKeys.WEB_CONTENT_ITEM_ID);
		Integer count = ParamUtil.getInteger(request, ShoppingCartPortletKeys.WEB_CONTENT_ITEM_COUNT);

		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		if (!themeDisplay.isSignedIn()) {
			List<String> currentCartItems = ShoppingCartPortlet.getOrderItemsIdsFromSession(request);
			if (null != currentCartItems && currentCartItems.contains(itemId)) {
				List<String> newCartItems = new ArrayList<>();
				for (String cartItemId : currentCartItems) {
					if (!cartItemId.equals(itemId))
						newCartItems.add(cartItemId);
				}
				for (int i = 0; i < count; i++) {
					newCartItems.add(itemId);
				}
				setSessionOrderItemsIds(request, ListUtil.toString(newCartItems, StringPool.BLANK, StringPool.COMMA));
			}
		} else {
			Long cartItemId = Long.valueOf(itemId);
			ShoppingOrderItem shoppingOrderItem = ShoppingOrderItemLocalServiceUtil.fetchShoppingOrderItem(cartItemId);
			shoppingOrderItem.setQuantity(count);
			ShoppingOrderItemLocalServiceUtil.updateShoppingOrderItem(shoppingOrderItem);
		}
	}

}