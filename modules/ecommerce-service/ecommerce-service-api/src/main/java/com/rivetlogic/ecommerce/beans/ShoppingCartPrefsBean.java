package com.rivetlogic.ecommerce.beans;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.rivetlogic.ecommerce.util.PreferencesKeys;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

public class ShoppingCartPrefsBean {

	private String customerNotifSubjectTemplate;
	private String customerNotifBodyTemplate;
	private String storeNotifSubjectTemplate;
	private String storeNotifBodyTemplate;
	private String storeEmail;
	private String storeName;
	private String checkoutSuccessMessage;
	private String checkoutErrorMessage;
	private String cartIsEmptyMessage;

	private boolean paypalEnabled;
	private String paypalEmail;

	public ShoppingCartPrefsBean(PortletRequest request) {
		this(request.getPreferences());
	}

	public ShoppingCartPrefsBean(PortletPreferences preferences) {
		setFields(preferences);
	}

	private void setFields(PortletPreferences portletPreferences) {
		setStoreNotifSubjectTemplate(
				portletPreferences.getValue(PreferencesKeys.STORE_NOTIF_SUBJECT_TEMPLATE, StringPool.BLANK));
		setStoreNotifBodyTemplate(
				portletPreferences.getValue(PreferencesKeys.STORE_NOTIF_BODY_TEMPLATE, StringPool.BLANK));
		setCustomerNotifSubjectTemplate(
				portletPreferences.getValue(PreferencesKeys.CUSTOMER_NOTIF_SUBJECT_TEMPLATE, StringPool.BLANK));
		setCustomerNotifBodyTemplate(
				portletPreferences.getValue(PreferencesKeys.CUSTOMER_NOTIF_BODY_TEMPLATE, StringPool.BLANK));
		setStoreEmail(portletPreferences.getValue(PreferencesKeys.STORE_EMAIL, StringPool.BLANK));
		setStoreName(portletPreferences.getValue(PreferencesKeys.STORE_NAME, StringPool.BLANK));
		setCheckoutErrorMessage(portletPreferences.getValue(PreferencesKeys.CHECKOUT_ERROR_MESSAGE, StringPool.BLANK));
		setCheckoutSuccessMessage(
				portletPreferences.getValue(PreferencesKeys.CHECKOUT_SUCCESS_MESSAGE, StringPool.BLANK));
		setCartIsEmptyMessage(portletPreferences.getValue(PreferencesKeys.CART_EMPTY_MESSAGE, StringPool.BLANK));

		setPaypalEnabled(
				GetterUtil.getBoolean(portletPreferences.getValue(PreferencesKeys.ENABLE_PAYPAL, StringPool.FALSE)));
		setPaypalEmail(portletPreferences.getValue(PreferencesKeys.PAYPAL_EMAIL, StringPool.BLANK));
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

}