package com.rivetlogic.ecommerce.util;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.rivetlogic.ecommerce.beans.ShoppingCartItem;
import com.rivetlogic.ecommerce.model.ShoppingOrderItem;
import com.rivetlogic.ecommerce.service.ShoppingOrderItemLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartItemUtil {
		
    public static final String DECIMAL_FORMAT = "0.00";

    public static List<ShoppingCartItem> getCartItems(long orderId,
            ThemeDisplay themeDisplay) throws SystemException {
        List<ShoppingCartItem> cartItems = null;
        List<ShoppingOrderItem> orderItemsList = ShoppingOrderItemLocalServiceUtil
                .findByOrderId(orderId);
        if (null != orderItemsList && !orderItemsList.isEmpty()) {
            cartItems = new ArrayList<>();
            for (ShoppingOrderItem shoppingOrderItem : orderItemsList) {
                ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                shoppingCartItem.setProductId(shoppingOrderItem.getProductId());
                shoppingCartItem.setItemId(shoppingOrderItem.getItemId());
                shoppingCartItem.setCount(shoppingOrderItem.getQuantity());
                setCartItemDetails(shoppingOrderItem.getProductId(),
                        themeDisplay, shoppingCartItem);
                cartItems.add(shoppingCartItem);
            }
        }
        return cartItems;
    }
    
    public static void setCartItemDetails(String productId,
            ThemeDisplay themeDisplay, ShoppingCartItem shoppingCartItem) {
        long groupId = themeDisplay.getScopeGroupId();
        Document document = getItemContent(productId, groupId);
        if (null != document) {
            Node itemTitleNode = document.selectSingleNode(ShoppingCartItem.PRODUCT_TITLE);
            Node itemListPriceNode = document.selectSingleNode(ShoppingCartItem.LIST_PRICE);
            Node itemSalePriceNode = document.selectSingleNode(ShoppingCartItem.SALE_PRICE);
            Node imagesNode = document.selectSingleNode(ShoppingCartItem.PRODUCT_IMAGES);
            shoppingCartItem.setItemTitle(itemTitleNode.getStringValue());
            shoppingCartItem.setSalePrice(itemSalePriceNode.getStringValue());
            shoppingCartItem.setListPrice(itemListPriceNode.getStringValue());
            shoppingCartItem.setItemImage(themeDisplay.getPortalURL() + imagesNode.getStringValue());
        }
        shoppingCartItem.setItemLink(themeDisplay.getPortalURL()
                + WEB_PAGE_PATH + themeDisplay.getScopeGroup().getFriendlyURL()
                + VIEW_PAGE_PATH + shoppingCartItem.getProductId());
    }
    
    public static Document getItemContent(String productId, long groupId) {
    	//TODO review journal article references
        JournalArticle jArticle = null;
        Document document = null;
        try {
            jArticle = JournalArticleLocalServiceUtil.getArticle(groupId,
                    productId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (null != jArticle) {
            try {
                document = SAXReaderUtil.read(jArticle.getContent());
            } catch (DocumentException e) {
                logger.error(e.getMessage());
            }
        }
        return document;
    }
    
    public static List<ShoppingCartItem> getCartItemsByProductId(List<String>productsIdList, ThemeDisplay themeDisplay){
        List<ShoppingCartItem> shoppingCartItemsList = null;
        if(null != productsIdList && null != themeDisplay){
            shoppingCartItemsList = new ArrayList<ShoppingCartItem>();
            for(String itemProductId : productsIdList){
                ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                shoppingCartItem.setProductId(itemProductId);
                ShoppingCartItemUtil.setCartItemDetails(itemProductId, themeDisplay, shoppingCartItem);
                shoppingCartItemsList.add(shoppingCartItem);
            }
        }
        return shoppingCartItemsList;
    }
    
    private static final String WEB_PAGE_PATH = "/web";
    private static final String VIEW_PAGE_PATH = "/view?id=";
    
    private static final Log logger = LogFactoryUtil.getLog(ShoppingCartItemUtil.class);
    
}
