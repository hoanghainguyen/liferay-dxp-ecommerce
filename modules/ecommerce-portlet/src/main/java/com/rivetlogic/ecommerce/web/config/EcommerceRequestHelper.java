package com.rivetlogic.ecommerce.web.config;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ParameterMapSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.rivetlogic.ecommerce.configuration.EcommerceGroupServiceConfiguration;
import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

public class EcommerceRequestHelper {
	
	public static EcommerceGroupServiceConfiguration getEcommerceGroupServiceConfiguration(PortletRequest portletRequest) {
		return getEcommerceGroupServiceConfiguration(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static EcommerceGroupServiceConfiguration getEcommerceGroupServiceConfiguration(HttpServletRequest request) {

		EcommerceGroupServiceConfiguration ecommerceGroupServiceConfiguration = null;

		try {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

			_log.info("Site group id " + themeDisplay.getSiteGroupId());

			ecommerceGroupServiceConfiguration = ConfigurationProviderUtil.getConfiguration(
					EcommerceGroupServiceConfiguration.class,
					new ParameterMapSettingsLocator(request.getParameterMap(), new GroupServiceSettingsLocator(
							themeDisplay.getSiteGroupId(), ShoppingCartPortletKeys.SHOPPING_CART_ADMIN)));

			if (ecommerceGroupServiceConfiguration != null && ecommerceGroupServiceConfiguration.storeEmailAddress()
					.isEmpty() /* TODO validat config */ ) {
				ecommerceGroupServiceConfiguration = ConfigurationProviderUtil
						.getGroupConfiguration(EcommerceGroupServiceConfiguration.class, themeDisplay.getSiteGroupId());
			}

			return ecommerceGroupServiceConfiguration;
		} catch (PortalException pe) {
			throw new SystemException(pe);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(EcommerceRequestHelper.class);

}
