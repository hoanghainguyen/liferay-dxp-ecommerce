AUI().ready(
	'liferay-sign-in-modal',
	function(A) {
		var signIn = A.one('.sign-in > a');

		if (signIn && signIn.getData('redirect') !== 'true') {
			signIn.plug(Liferay.SignInModal);
    }
    

    $(function() {
      // Product View image click
      $('.side-image').click(function() {
        var $imageLink = $(this).find('img').data('src');
        $('.main-image').find('img').attr('src', $imageLink);
      });
    });
    
    function initCart(link){
      
      var url = Liferay.PortletURL.createURL(link);
      url.setLifecycle(Liferay.PortletURL.RESOURCE_PHASE);
      url.setPortletId('com_rivetlogic_ecommerce_portlet_ShoppingCart');
      url.setResourceId('getCartInfo');
      
      $.post(link+"?p_p_id=shoppingcart_WAR_ecommerceportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_cacheability=cacheLevelPage&_shoppingcart_WAR_ecommerceportlet_cmd=getCartInfo")
      .done(function( data ) {
        var result = JSON.parse(data); 
          var items= result.cartDetails.quantity;
        if(items==0){
          var badge = $("#cart_badge");
          badge.html("");
          badge.hide();
        }else{
          var badge = $("#cart_badge");
          badge.html(items);
          badge.show();
        }
       });
    }
    
    function createSuccessMessage(element){
      var div = document.createElement("div");
      div.className = "added_message alert alert-success";
      var p =  document.createElement("p"); 
      var message = document.createTextNode("Item successfully added.");
      p.appendChild(message);  
      div.appendChild(p); 
      element.append(div); 
      $(div).fadeOut(4000);
    }
    
    
    function addToCart(id,product,price,link,that){
    
      var url = Liferay.PortletURL.createURL(link);
      url.setLifecycle(Liferay.PortletURL.RESOURCE_PHASE);
      url.setPortletId('com_rivetlogic_ecommerce_portlet_ShoppingCart');
      url.setResourceId('addCartItem');
      url.setParameter('itemId', id);
      url.setParameter('itemName', product);
      
      $.post(url.toString())
        .done(function( data ) {
          var result = JSON.parse(data); 
            var items= result.cartDetails.quantity;
          if(items==0){
            var badge = $("#cart_badge");
            badge.html("");
            badge.hide();
          }else{
            var badge = $("#cart_badge");
            badge.html(items);
            badge.show();
          }
            createSuccessMessage($(that).parent());
         });
    }


	}
);
