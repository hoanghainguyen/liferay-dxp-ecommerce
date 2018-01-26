<%@ include file="/shopping_cart_admin/init.jsp" %>

TEST CONFIG
<%

EcommerceRequestHelper ecommerceRequestHelper = new EcommerceRequestHelper(request);

EcommerceGroupServiceConfiguration ecommerceGroupServiceConfiguration = ecommerceRequestHelper.getEcommerceGroupServiceConfiguration();
String storeEmailName = ParamUtil.getString(request, "preferences--storeEmailName--", ecommerceGroupServiceConfiguration.storeEmailName());

String storeEmailAddress = ParamUtil.getString(request, "preferences--storeEmailAddress--", ecommerceGroupServiceConfiguration.storeEmailAddress());

System.out.println(ecommerceGroupServiceConfiguration.storeEmailName());

System.out.println(ecommerceGroupServiceConfiguration.storeEmailAddress());

System.out.println(ecommerceGroupServiceConfiguration.storeEmailSubject());

System.out.println(ecommerceGroupServiceConfiguration.messageCheckoutError());

System.out.println(ecommerceGroupServiceConfiguration.messageCartEmpty());
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL">
	<portlet:param name="serviceName" value="<%= EcommerceConfigurationKeys.SERVICE_NAME %>" />
	<portlet:param name="settingsScope" value="group" />
</liferay-portlet:actionURL>

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<%
	String tabs1Names = "store";
	%>

	<div class="portlet-configuration-body-content">
		<liferay-ui:tabs
			names="<%= tabs1Names %>"
			refresh="<%= false %>"
			type="tabs nav-tabs-default"
		>
		
		<liferay-ui:error key="storeEmailName" message="please-enter-a-valid-name" />
		<liferay-ui:error key="storeEmailAddress" message="please-enter-a-valid-email-address" />
		
		<liferay-ui:section>
				<div class="container-fluid-1280">
					<aui:fieldset-group markupView="lexicon">
						<aui:fieldset>
							<aui:input cssClass="lfr-input-text-container" label="name" name="preferences--storeEmailName--" type="text" value="<%= storeEmailName %>" />
							<aui:input cssClass="lfr-input-text-container" label="name" name="preferences--storeEmailAddress--" type="text" value="<%= storeEmailAddress %>" />

						</aui:fieldset>
					</aui:fieldset-group>
				</div>
			</liferay-ui:section>
		
		</liferay-ui:tabs>
	</div>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>