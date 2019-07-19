package com.id.cash.service.notification;

/**
 * Created by linchen on 2018/5/24.
 */

public interface PushActionType {
    String NONE = "NONE";    // display title & content in notification & brings up main activity
    String OPEN_LINK = "OPEN_LINK"; // open web, required parameter: link_title, link
    String OPEN_PRODUCT  ="OPEN_PRODUCT";    // open product, required parameter: product_type, product_id
}
