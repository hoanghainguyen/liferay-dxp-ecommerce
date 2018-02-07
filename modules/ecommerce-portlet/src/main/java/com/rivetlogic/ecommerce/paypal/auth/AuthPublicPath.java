package com.rivetlogic.ecommerce.paypal.auth;

import com.rivetlogic.ecommerce.constants.ShoppingCartPortletKeys;

import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
		property = {
			"auth.public.path=" + AuthPublicPath.ACTION_URL,
		},
		service = Object.class
	)
public class AuthPublicPath {
	public static final String ACTION_URL="/"+ShoppingCartPortletKeys.PORTLET_STRUTS+"/notify";
	public static final String FULL_ACTION_URL="/c"+ACTION_URL;
}
