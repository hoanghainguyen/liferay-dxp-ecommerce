package com.rivetlogic.ecommerce.beans;

import com.rivetlogic.ecommerce.configuration.EcommerceGroupServiceConfiguration;

import java.util.Locale;

public class ShoppingCartPrefsBean {

	private boolean isCustomerEmailEnabled;
	private String customerNotifSubjectTemplate;
	private String customerNotifBodyTemplate;

	private boolean isStoreEmailEnabled;
	private String storeNotifSubjectTemplate;
	private String storeNotifBodyTemplate;

	private String storeEmail;
	private String storeName;

	private String checkoutSuccessMessage;
	private String checkoutErrorMessage;
	private String cartIsEmptyMessage;

	private boolean paypalEnabled;
	private String paypalEmail;

	public ShoppingCartPrefsBean(EcommerceGroupServiceConfiguration ecommerceGroupServiceConfiguration, Locale locale) {
		setFields(ecommerceGroupServiceConfiguration, locale);
	}

	private void setFields(EcommerceGroupServiceConfiguration ecommerceGroupServiceConfiguration, Locale locale) {
		isCustomerEmailEnabled = ecommerceGroupServiceConfiguration.customerEmailEnabled();
		customerNotifSubjectTemplate = ecommerceGroupServiceConfiguration.customerEmailSubject().get(locale);
		customerNotifBodyTemplate = ecommerceGroupServiceConfiguration.customerEmailBody().get(locale);

		isStoreEmailEnabled = ecommerceGroupServiceConfiguration.storeEmailEnabled();
		storeNotifSubjectTemplate = ecommerceGroupServiceConfiguration.storeEmailSubject().get(locale);
		storeNotifBodyTemplate = ecommerceGroupServiceConfiguration.storeEmailBody().get(locale);

		storeEmail = ecommerceGroupServiceConfiguration.storeEmailAddress();
		storeName = ecommerceGroupServiceConfiguration.storeName();

		checkoutSuccessMessage = ecommerceGroupServiceConfiguration.messageCheckoutSuccess();
		checkoutErrorMessage = ecommerceGroupServiceConfiguration.messageCheckoutError();
		cartIsEmptyMessage = ecommerceGroupServiceConfiguration.messageCartEmpty();

		paypalEnabled = ecommerceGroupServiceConfiguration.enablePaypal();
		paypalEmail = ecommerceGroupServiceConfiguration.paypalBusinessEmailAddress();
	}

	public boolean isCartPrefsValidForCheckout(boolean isPaypal) {
		boolean valid = ((null != getStoreEmail() && !getStoreEmail().isEmpty())
				&& (null != getStoreName() && !getStoreName().isEmpty())
				&& (null != getCustomerNotifSubjectTemplate() && !getCustomerNotifSubjectTemplate().isEmpty())
				&& (null != getCustomerNotifBodyTemplate() && !getCustomerNotifBodyTemplate().isEmpty())
				&& (null != getStoreNotifSubjectTemplate() && !getStoreNotifSubjectTemplate().isEmpty())
				&& (null != getStoreNotifBodyTemplate() && !getStoreNotifBodyTemplate().isEmpty()));
		if (isPaypal) {
			valid = valid && ((isPaypalEnabled() && null != getPaypalEmail() && !getPaypalEmail().isEmpty())
					|| !isPaypalEnabled());
		}
		return valid;
	}

	public boolean isCartPrefsValid() {
		return ((null != getStoreEmail() && !getStoreEmail().isEmpty())
				&& (null != getStoreName() && !getStoreName().isEmpty())
				&& (null != getCustomerNotifSubjectTemplate() && !getCustomerNotifSubjectTemplate().isEmpty())
				&& (null != getCustomerNotifBodyTemplate() && !getCustomerNotifBodyTemplate().isEmpty())
				&& (null != getStoreNotifSubjectTemplate() && !getStoreNotifSubjectTemplate().isEmpty())
				&& (null != getStoreNotifBodyTemplate() && !getStoreNotifBodyTemplate().isEmpty())
				&& (null != getCartIsEmptyMessage() && !getCartIsEmptyMessage().isEmpty())
				&& (null != getCheckoutErrorMessage() && !getCheckoutErrorMessage().isEmpty())
				&& (null != getCheckoutSuccessMessage() && !getCheckoutSuccessMessage().isEmpty()));
	}

	public String getStoreEmail() {
		return storeEmail;
	}

	public void setStoreEmail(String storeEmail) {
		this.storeEmail = storeEmail;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getCustomerNotifSubjectTemplate() {
		return customerNotifSubjectTemplate;
	}

	public void setCustomerNotifSubjectTemplate(String customerNotifSubjectTemplate) {
		this.customerNotifSubjectTemplate = customerNotifSubjectTemplate;
	}

	public String getCustomerNotifBodyTemplate() {
		return customerNotifBodyTemplate;
	}

	public void setCustomerNotifBodyTemplate(String customerNotifBodyTemplate) {
		this.customerNotifBodyTemplate = customerNotifBodyTemplate;
	}

	public String getStoreNotifSubjectTemplate() {
		return storeNotifSubjectTemplate;
	}

	public void setStoreNotifSubjectTemplate(String storeNotifSubjectTemplate) {
		this.storeNotifSubjectTemplate = storeNotifSubjectTemplate;
	}

	public String getStoreNotifBodyTemplate() {
		return storeNotifBodyTemplate;
	}

	public void setStoreNotifBodyTemplate(String storeNotifBodyTemplate) {
		this.storeNotifBodyTemplate = storeNotifBodyTemplate;
	}

	public String getCheckoutSuccessMessage() {
		return checkoutSuccessMessage;
	}

	public void setCheckoutSuccessMessage(String checkoutSuccessMessage) {
		this.checkoutSuccessMessage = checkoutSuccessMessage;
	}

	public String getCheckoutErrorMessage() {
		return checkoutErrorMessage;
	}

	public void setCheckoutErrorMessage(String checkoutErrorMessage) {
		this.checkoutErrorMessage = checkoutErrorMessage;
	}

	public String getCartIsEmptyMessage() {
		return cartIsEmptyMessage;
	}

	public void setCartIsEmptyMessage(String cartIsEmptyMessage) {
		this.cartIsEmptyMessage = cartIsEmptyMessage;
	}

	public boolean isPaypalEnabled() {
		return paypalEnabled;
	}

	public void setPaypalEnabled(boolean enablePaypal) {
		this.paypalEnabled = enablePaypal;
	}

	public String getPaypalEmail() {
		return paypalEmail;
	}

	public void setPaypalEmail(String paypalEmail) {
		this.paypalEmail = paypalEmail;
	}

	public boolean isCustomerEmailEnabled() {
		return isCustomerEmailEnabled;
	}

	public void setCustomerEmailEnabled(boolean isCustomerEmailEnabled) {
		this.isCustomerEmailEnabled = isCustomerEmailEnabled;
	}

	public boolean isStoreEmailEnabled() {
		return isStoreEmailEnabled;
	}

	public void setStoreEmailEnabled(boolean isStoreEmailEnabled) {
		this.isStoreEmailEnabled = isStoreEmailEnabled;
	}

}