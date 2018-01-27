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
package com.rivetlogic.ecommerce.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.rivetlogic.ecommerce.configuration.EcommerceConfigurationKeys;
import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(
	configurationPid = EcommerceConfigurationKeys.SERVICE_CONFIGURATION_ID,
	configurationPolicy = ConfigurationPolicy.OPTIONAL, 
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.hidden",
        "com.liferay.portlet.instanceable=false",
        "com.liferay.portlet.preferences-owned-by-group=true",
        "com.liferay.portlet.preferences-unique-per-layout=false",
        "com.liferay.portlet.render-weight=100",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
        "javax.portlet.security-role-ref=power-user,user",
        "javax.portlet.display-name=Shopping Cart Admin",
        "javax.portlet.init-param.config-template=/shopping_cart_admin/configuration.jsp",
        "javax.portlet.init-param.template-path=/shopping_cart_admin/",
		"javax.portlet.init-param.view-template=/shopping_cart_admin/view.jsp",
		"javax.portlet.name=" + ShoppingCartPortletKeys.SHOPPING_CART_ADMIN,
    },
	service = {ShoppingCartAdminPortlet.class, Portlet.class}

)
public class ShoppingCartAdminPortlet extends MVCPortlet {

}