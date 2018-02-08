AUI().ready(
	'liferay-sign-in-modal', 'liferay-alert',
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


    $('#product-list').on('click', '.add-to-cart-js', function (e){

      var id = $(e.currentTarget).data('articleId');
      var product = $(e.currentTarget).data('productTitle');
      var link = $(e.currentTarget).data('addLink');      

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
          
          // success message
          new Liferay.Alert(
            {
              delay: {
                hide: 5000,
                show: 0
              },
              message: 'Product added to your cart',
              type: 'success',
              closeable: true,
              icon: 'exclamation-circle'
            }
          ).render('#product-list-message');
      });
    });

    // Product View image click
    $('.side-image').click(function() {
      var $imageLink = $(this).find('img').data('src');
      $('.main-image').find('img').attr('src', $imageLink);
    });

	}
);

(function ($, window) {

  $(document).ready(function () {
    
  });

  /*
  require('template-theme/js/modal/modal.es', function(Modal) { 
    new Modal.default({
      header: 'Test',
      body: 'Hello, modal.',
      buttons: ['close']
    }, '#wrapper');
  });
  */

}) ($, window);
