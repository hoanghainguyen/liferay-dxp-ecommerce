package com.rivetlogic.ecommerce.web.config;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.rivetlogic.ecommerce.configuration.EcommerceConfigurationKeys;
import com.rivetlogic.ecommerce.configuration.EcommerceGroupServiceConfiguration;
import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;

import java.util.Map;

import javax.portlet.PortletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(
		configurationPid = EcommerceConfigurationKeys.SERVICE_CONFIGURATION_ID,
		configurationPolicy = ConfigurationPolicy.OPTIONAL,
		immediate = true,
	    property = { 
	        "javax.portlet.name=" + ShoppingCartPortletKeys.SHOPPING_CART_ADMIN
	    }, 
	    service = ConfigurationAction.class)
public class EcommerceConfigurationAction extends DefaultConfigurationAction {
	
	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Blade Message Portlet configuration include");
		}

		httpServletRequest.setAttribute(
				EcommerceGroupServiceConfiguration.class.getName(),
				_ecommerceGroupServiceConfiguration);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}
	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_ecommerceGroupServiceConfiguration = ConfigurableUtil.createConfigurable(
				EcommerceGroupServiceConfiguration.class, properties);
	}
	
	@Override
	@Reference(
		target = "(osgi.web.symbolicname=com.rivetlogic.ecommerce)", unbind = "-"
	)
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(EcommerceConfigurationAction.class);

	private volatile EcommerceGroupServiceConfiguration _ecommerceGroupServiceConfiguration;
}
