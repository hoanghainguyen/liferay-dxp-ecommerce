<%@ include file="/init.jsp" %>

<% 
List<ShoppingCartItem>cartItemsIdsList = (List<ShoppingCartItem>) request.getAttribute(ShoppingCartPortletKeys.ATTR_CURRENT_ORDER_ITEMS);
DecimalFormat decimalFormat = new DecimalFormat(ShoppingCartPortletKeys.DECIMAL_FORMAT);
EcommerceGroupServiceConfiguration ecommerceGroupServiceConfiguration = EcommerceRequestHelper.getEcommerceGroupServiceConfiguration(request);
%>

<portlet:actionURL name="checkout" var="checkoutURL">
	<portlet:param name="<%= WebKeys.REDIRECT%>" value="<%= PortalUtil.getCurrentURL(renderRequest) %>"/>
</portlet:actionURL>

<portlet:actionURL name="checkout" var="paypalCheckoutURL">
	<portlet:param name="<%= ShoppingCartPortletKeys.CHECKOUT_PARAMETER_PAYPAL %>" value="true"/>
	<portlet:param name="<%= WebKeys.REDIRECT%>" value="<%= PortalUtil.getCurrentURL(renderRequest) %>"/>
</portlet:actionURL>

<liferay-ui:success key="<%=ShoppingCartPortletKeys.SUCCESS_MESSAGE_CHECKOUT %>" message="<%= ecommerceGroupServiceConfiguration.messageCheckoutSuccess() %>" />
<liferay-ui:error key="<%= ShoppingCartPortletKeys.ERROR_MESSAGE_CHECKOUT %>" message="<%= ecommerceGroupServiceConfiguration.messageCheckoutError() %>" />

<input type="hidden" id="page-reloaded" value="no" />
<div class="row-fluid">
      	<div class="span12">
      		<div class="block">
                <div class="navbar navbar-inner block-header">
                    <div class="muted pull-left">My Cart</div>
                </div>
                <div class="block-content collapse in">
                    <div class="span12">
                    	<div class="alert alert-warning <%= (null != cartItemsIdsList && !cartItemsIdsList.isEmpty() ? "hidden" : StringPool.BLANK)%>"id="cart-empty-msg">
                    		<%=ecommerceGroupServiceConfiguration.messageCartEmpty() %>
                    	</div>
                    	<% if(null != cartItemsIdsList && !cartItemsIdsList.isEmpty()){ %>
                        <table class="table table-striped table-hover cart-items-details">
                        	<tbody><tr>
                        		<th>Image</th>
                        		<th>Title</th>
                        		<th>Price</th>
                        		<th>Quantity</th>
                        		<th>Total</th>
                        	</tr>
                        	<% Double cartTotalPrice = 0d; %>
							<%
								for(ShoppingCartItem orderItem : cartItemsIdsList){		
							%>
                        	<tr class="item-data-row">
                        		<td class="span1"><a href="<%=orderItem.getItemLink()%>"><img src="<%= orderItem.getItemImage() %>"></a></td>
                        		<td class="span5">
                        			<a href="<%=orderItem.getItemLink()%>"><%= orderItem.getItemTitle() %></a>
                        		</td>
                        		<td class="span2">$<%= orderItem.getPrice()%></td>
                        		<td class="span2">
                        			<div class="row-fluid">
                        				<div class="span7">
                        					<portlet:resourceURL id="<%= ShoppingCartPortletKeys.RC_UPDATE_CART_ITEM %>" var="updateCartItemURL" >
												<portlet:param name="<%=ShoppingCartPortletKeys.WEB_CONTENT_ITEM_ID%>" value="<%= orderItem.getItemId() != 0l ? String.valueOf(orderItem.getItemId()): orderItem.getProductId()%>"/>
												<portlet:param name="<%=ShoppingCartPortletKeys.WEB_CONTENT_ITEM_COUNT%>" value=""/>
											</portlet:resourceURL>										
                        					<aui:input name="Quantity" label="" type="number" value="<%= orderItem.getCount()%>" min="1" data-old-value="<%= orderItem.getCount()%>" placeholder="Enter quantity" cssClass="input-mini quantity-input" data-url="${updateCartItemURL}" />
                        				</div>
                        				<div class="span5">
                        					<portlet:resourceURL id="<%= ShoppingCartPortletKeys.RC_REMOVE_ITEM_FROM_CART %>" var="removeCartItemURL" >									
												<portlet:param name="<%=ShoppingCartPortletKeys.WEB_CONTENT_ITEM_ID%>" value="<%= orderItem.getItemId() != 0l ? String.valueOf(orderItem.getItemId()): orderItem.getProductId()%>"/>
											</portlet:resourceURL>
                        					<button type="submit" class="btn btn-danger btn-mini btn-delete" data-url="${removeCartItemURL}"><i class="icon-remove icon-white"></i></button>
                        				</div>
                        			</div>
                        		</td>
                        		<td class="span1 item-total-price">$<%= decimalFormat.format(orderItem.getTotalPrice())  %></td>
                        		<% cartTotalPrice += orderItem.getTotalPrice(); %>
                        	</tr>
						<%
								}
						%>
						<tr>
							<th colspan="4"></th>
							<th id="cart-total-price">$<%= decimalFormat.format(cartTotalPrice) %></th>
						</tr>
                        </tbody></table>
                        <% } %>
                    </div>
                </div>
            </div>
      	</div>
      </div>
      
<%@ include file="/shopping_cart/shopping_info.jspf" %>

<aui:script use="shopping-cart">
    A.ShoppingCart.setPortletNamespace('${pns}');
    A.all('.btn-delete').on('click', function(event){
    	A.ShoppingCart.removeCartItem(event.currentTarget);
    });
    A.all('.quantity-input').on('change', function(event){
    	A.ShoppingCart.updateCartItem(event.currentTarget);
    });
    A.all('#btn-checkout').on('click', function(event){
    	A.ShoppingCart.doCheckout('${checkoutURL}');
    });
    <c:if test="<%=ecommerceGroupServiceConfiguration.enablePaypal() %>">
    A.all('#btn-paypal-checkout').on('click', function(event){
    	A.ShoppingCart.doCheckout('${paypalCheckoutURL}');
    });
    </c:if>
    
    var pageReloaded = A.one('#page-reloaded');
    
    A.on('unload', function(){
    	pageReloaded.attr('value', 'yes');
    });
    
    A.ready(function(){
        if(pageReloaded.attr('value') == 'yes') {
        	A.all('#btn-checkout, #btn-paypal-checkout').attr('disabled', true);
        	A.all('#page-reload-warning').removeClass('hide');
        }
    });
</aui:script>