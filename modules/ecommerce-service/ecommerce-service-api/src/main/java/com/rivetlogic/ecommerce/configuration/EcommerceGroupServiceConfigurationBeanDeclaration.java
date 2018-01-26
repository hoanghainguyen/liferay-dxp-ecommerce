package com.rivetlogic.ecommerce.configuration;

import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;

import org.osgi.service.component.annotations.Component;


@Component
public class EcommerceGroupServiceConfigurationBeanDeclaration implements ConfigurationBeanDeclaration {

	@Override
	public Class<?> getConfigurationBeanClass() {
		return EcommerceGroupServiceConfiguration.class;
	}

}
