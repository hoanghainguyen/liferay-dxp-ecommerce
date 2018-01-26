package com.rivetlogic.ecommerce.paypal.util;

public class PaypalConstants {
    // DEVELOPMENT ENDPOINT:
    //public static final String PAYPAL_ENDPOINT = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    
    // PRODUCTION ENDPOINT:
    public static final String PAYPAL_ENDPOINT = "https://www.paypal.com/cgi-bin/webscr";
    
    public static final String NOTIFY_ACTION_PATH = "/c/ecommerce/notify";
    
    public static final String PAYMENT_STATUS = "payment_status";
    public static final String PAYMENT_COMPLETE = "Completed";
    
    public static final String TRANSACTION_VERIFIED = "VERIFIED";
    
    public static final String CURRENCY_USD = "USD";
    
    public static final String PARAM_CMD = "cmd";
    public static final String PARAM_BUSINESS = "business";
    public static final String PARAM_ITEM_NAME = "item_name";
    public static final String PARAM_ITEM_NUMBER = "item_number";
    public static final String PARAM_INVOICE = "invoice";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_RETURN = "return";
    public static final String PARAM_NOTIFY_URL = "notify_url";
    public static final String PARAM_FIRST_NAME = "first_name";
    public static final String PARAM_LAST_NAME = "last_name";
    public static final String PARAM_ADDRESS1 = "address1";
    public static final String PARAM_CITY = "city";
    public static final String PARAM_STATE = "state";
    public static final String PARAM_ZIP = "zip";
    public static final String PARAM_NO_NOTE = "no_note";
    public static final String PARAM_CURRENCY = "currency";
    public static final String PARAM_EMAIL = "email";
    
    public static final String CMD_XCLICK = "_xclick";
    public static final String CMD_VALIDATE = "_notify-validate";
}
