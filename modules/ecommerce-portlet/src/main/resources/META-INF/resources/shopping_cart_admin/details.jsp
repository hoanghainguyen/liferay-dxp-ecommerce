<%@ include file="/init.jsp" %>

<%
	long orderId = ParamUtil.getLong(request, ShoppingCartPortletConstants.ORDER_ID);
	ShoppingOrder order = ShoppingOrderLocalServiceUtil.fetchShoppingOrder(orderId);
	List<ShoppingOrderItem> items = ShoppingOrderItemLocalServiceUtil.findByOrderId(orderId);
	pageContext.setAttribute("order", order);
	String statusKey = order.getOrderStatus().toLowerCase();
	pageContext.setAttribute("statusKey", statusKey);
	String status = LanguageUtil.get(pageContext, "order-status-" + statusKey);
	pageContext.setAttribute("status", status);
%>

<liferay-ui:success key="success" message="order-notes-update-success" />

<liferay-ui:header title="order-details" backURL="<%= redirect %>"/>

<aui:model-context model="<%= ShoppingOrder.class %>" bean="<%= order %>" />

<portlet:actionURL name="updateOrderNotes" var="updateNotesURL">
	<portlet:param name="orderId" value="<%= Long.toString(orderId) %>" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<liferay-ui:panel-container>
	<liferay-ui:panel title="general">
		<aui:field-wrapper>
			<aui:input name="orderId" disabled="true" />
			<aui:input name="createDate" label="created" disabled="true" type="text" />
			<aui:input name="modifiedDate" label="last-modified" disabled="true" type="text" />
			<aui:input name="total" disabled="true" />
			<aui:input name="order-status" type="text" disabled="true" value="${ status }" helpMessage="help-status-${ statusKey }" />
			<aui:form action="<%= updateNotesURL %>">
			<aui:input name="notes" type="textarea"/>
			<aui:button-row>
				<aui:button type="submit" />
			</aui:button-row>
			</aui:form>
		</aui:field-wrapper>
	</liferay-ui:panel>
	<liferay-ui:panel title="contact" defaultState="closed">
		<aui:field-wrapper>
			<aui:input name="customerName" label="name" disabled="true" inlineLabel="true" />
			<aui:input name="customerEmail" label="email" disabled="true" inlineLabel="true" />
			<aui:input name="customerPhone" label="phone" disabled="true" inlineLabel="true" />
		</aui:field-wrapper>		
	</liferay-ui:panel>
	<liferay-ui:panel title="shipping" defaultState="closed">
		<aui:field-wrapper>
			<aui:input name="shippingCountry" label="country" disabled="true" />
			<aui:input name="shippingCity" label="city" disabled="true" />
			<aui:input name="shippingStateProvince" label="state" disabled="true" />
			<aui:input name="shippingPostalCode" label="postal-code" disabled="true" />
			<aui:field-wrapper label="address">
				<aui:input name="shippingAddress1" disabled="true" inlineField="true" label="" />
				<aui:input name="shippingAddress2" disabled="true" inlineField="true" label="" />
			</aui:field-wrapper>
		</aui:field-wrapper>
	</liferay-ui:panel>
	<liferay-ui:panel title="items" defaultState="closed">
		<aui:layout>
			<aui:row>
				<aui:col span="4">Product Id</aui:col>
				<aui:col span="4">Price</aui:col>
				<aui:col span="4">Quantity</aui:col>
			</aui:row>
			<c:forEach items="<%= items %>" var="item">
			<aui:row>
				<aui:col span="4">${ item.productId }</aui:col>
				<aui:col span="4">${ item.price }</aui:col>
				<aui:col span="4">${ item.quantity }</aui:col>
			</aui:row>
			</c:forEach>
		</aui:layout>
	</liferay-ui:panel>
</liferay-ui:panel-container>
