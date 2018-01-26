package com.rivetlogic.ecommerce.panel;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;
import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		property = {
			"panel.app.order:Integer=800",
			"panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT
		},
		service = PanelApp.class
	)
public class ShoppingCartAdminPanel extends BasePanelApp {

	@Override
	public String getPortletId() {
		return ShoppingCartPortletKeys.SHOPPING_CART_ADMIN;
	}
	
	@Override
	@Reference(
		target = "(javax.portlet.name=" + ShoppingCartPortletKeys.SHOPPING_CART_ADMIN+ ")",
		unbind = "-"
	)
	public void setPortlet(Portlet portlet) {
		super.setPortlet(portlet);
	}

}
