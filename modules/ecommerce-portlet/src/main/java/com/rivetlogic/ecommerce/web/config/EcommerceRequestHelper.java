package com.rivetlogic.ecommerce.web.config;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ParameterMapSettingsLocator;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.rivetlogic.ecommerce.configuration.EcommerceGroupServiceConfiguration;
import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;

import javax.servlet.http.HttpServletRequest;

public class EcommerceRequestHelper {

	public EcommerceRequestHelper(HttpServletRequest request) {
		_request = request;
	}

	public EcommerceGroupServiceConfiguration getEcommerceGroupServiceConfiguration() {

		try {
			
			if (_ecommerceGroupServiceConfiguration == null) {
				ThemeDisplay themeDisplay = (ThemeDisplay) _request.getAttribute(WebKeys.THEME_DISPLAY);				
				PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();				
				
				if (Validator.isNotNull(portletDisplay.getPortletResource())) {
					_ecommerceGroupServiceConfiguration = ConfigurationProviderUtil
							.getConfiguration(EcommerceGroupServiceConfiguration.class,
									new ParameterMapSettingsLocator(_request.getParameterMap(),
											new GroupServiceSettingsLocator(themeDisplay.getSiteGroupId(),
													ShoppingCartPortletKeys.SHOPPING_CART_ADMIN)));
				} 
				
				if (_ecommerceGroupServiceConfiguration.storeEmailAddress().isEmpty() || Validator.isNull(portletDisplay.getPortletResource()) ) {
					_ecommerceGroupServiceConfiguration = ConfigurationProviderUtil
							.getGroupConfiguration(EcommerceGroupServiceConfiguration.class, themeDisplay.getSiteGroupId());
				}
			}

			return _ecommerceGroupServiceConfiguration;
		} catch (PortalException pe) {
			throw new SystemException(pe);
		}
	}

	private EcommerceGroupServiceConfiguration _ecommerceGroupServiceConfiguration;
	private final HttpServletRequest _request;

}
