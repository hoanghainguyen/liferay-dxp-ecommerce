<%@ include file="/init.jsp" %>

<%
	long groupId = themeDisplay.getScopeGroupId();

	String orderByCol = ParamUtil.getString(request, "orderByCol", "modifiedDate");
	String orderByType = ParamUtil.getString(request, "orderByType", "desc");
	
	OrderByComparator comparator = OrderByComparatorFactoryUtil
	        .create(ShoppingCartPortletKeys.SHOPPING_ORDERS_TABLE_NAME, orderByCol, orderByType.equals("asc"));
	orderByType = orderByType.equals("asc")? "desc" : "asc";
%>

<%
Map<String, Object> data = new HashMap<>();

data.put("qa-id", "navigation");
%>

<aui:nav-bar cssClass="collapse-basic-search" data="<%= data %>" markupView="lexicon">
        <portlet:renderURL var="mainURL" />

        <aui:nav cssClass="navbar-nav">
                <aui:nav-item href="<%= mainURL.toString() %>" label="ecommerce-orders" selected="<%= true %>" />
        </aui:nav>

</aui:nav-bar>

<liferay-ui:success key="success" message="ecommerce-order-status-update-success" />

<liferay-ui:search-container emptyResultsMessage="ecommerce-no-orders-found" orderByType="<%= orderByType %>" total="<%= ShoppingOrderLocalServiceUtil.countByGroupId(groupId) %>">
	
	<liferay-ui:search-container-results 
		results="<%= ShoppingOrderLocalServiceUtil.findByGroupId(groupId, searchContainer.getStart(), searchContainer.getEnd(), comparator) %>" />
	
	<liferay-ui:search-container-row className="com.rivetlogic.ecommerce.model.ShoppingOrder" modelVar="order">
		
		<liferay-ui:search-container-column-text name="order-id" property="orderId" />
		
		<liferay-ui:search-container-column-text name="order-status" orderable="true" orderableProperty="orderStatus" >
			<liferay-ui:icon-help message="help-status-${ order.orderStatus.toLowerCase() }">
				<liferay-ui:message key="order-status-${ order.orderStatus.toLowerCase() }"/>
			</liferay-ui:icon-help>
		</liferay-ui:search-container-column-text>
		
		<liferay-ui:search-container-column-date name="order-last-updated" property="modifiedDate" orderable="true" orderableProperty="modifiedDate" />
		
		<liferay-ui:search-container-column-text name="order-customer-name" property="customerName" />
		
		<liferay-ui:search-container-column-text name="order-customer-email" property="customerEmail" />
		
		<liferay-ui:search-container-column-jsp  name="order-actions" path="/shopping_cart_admin/actions.jsp" />

	</liferay-ui:search-container-row>
	
	<liferay-ui:search-iterator markupView="lexicon" />

</liferay-ui:search-container>