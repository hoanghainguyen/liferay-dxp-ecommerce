package com.rivetlogic.ecommerce.util;

public enum OrderStatusEnum {
	IN_PROGRESS,
	WAITING_FOR_PAYPAL,
	WAITING_FOR_PAYMENT,
	PAID,
	PROCESSING_DELIVERY,
	SHIPPED,
	CANCELED
}
