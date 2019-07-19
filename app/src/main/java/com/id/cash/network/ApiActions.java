package com.id.cash.network;

/**
 * Created by linchen on 2018/5/21.
 */

// each API action maps to a specific data type
public class ApiActions {
    public static final String CASH_LOAN_DETAIL = "CASH_LOAN_DETAIL";
    public static final String CASH_LOAN_PROCEDURE = "CASH_LOAN_PROCEDURE";
    public static final String HOT_WORDS = "HOT_WORDS";
    public static final String LOAD_SEARCH_HISTORY = "LOAD_SEARCH_HISTORY";
    public static final String SEARCH = "SEARCH";
    public static final String SEARCH_CLEAR_HISTORY = "SEARCH_CLEAR_HISTORY";
    public static final String BONUSPOINT_LIST = "BONUSPOINT_LIST";
    public static final String INVITE_LIST = "INVITE_LIST";
    public static final String SHARE_INFO = "SHARE_INFO";
    public static final String SHARE = "SHARE";
    public static final String REGISTER_DEVICE = "REGISTER_DEVICE";
    public static final String GET_USER = "GET_USER";
    public static final String LOGIN_TOKEN = "LOGIN_TOKEN";
    public static final String LOGOUT = "LOGOUT";
    public static final String INSTALL_REFERRER = "INSTALL_REFERRER";

    // main tab
    public static final String COLLECT_PRIVACY_DATA = "COLLECT_PRIVACY_DATA"; // upload user's sms list

    // feeds share
    public static final String FEEDS_LIST = "FEEDS_LIST";
    public static final String FEEDS_GET = "FEEDS_GET";
    public static final String FEEDS_TOGGLE_LIKE = "FEEDS_TOGGLE_LIKE";
    public static final String FEEDS_CLIENT_FEEDS = "FEEDS_CLIENT_FEEDS";
    public static final String FEEDS_BANNER = "FEEDS_BANNER";
    public static final String FEEDS_GET_SHARE = "FEEDS_GET_SHARE";
    public static final String FEEDS_GET_SHARE_THEN_PERFORM_SHARE = "FEEDS_GET_SHARE_THEN_PERFORM_SHARE";

    // cash loan main list fragment
    public static final String CASHLOAN_LIST_MAIN = "LOGOUT";
    public static final String BANNERS = "BANNERS";
    public static final String FILTERS = "FILTERS";
    public static final String MARQUEE = "MARQUEE";

    // bonus point main
    public static final String BONUSPOINT_SUMMARY = "BONUSPOINT_SUMMARY";
    public static final String BONUSPOINT_SHARE_FB = "BONUSPOINT_SHARE_FB";
    public static final String BONUSPOINT_TASK_RESULT = "BONUSPOINT_TASK_RESULT";

    // withdraw
    public static final String BANKCARD_INFO_SUMMARY = "BANKCARD_INFO_SUMMARY";
    public static final String WITHDRAW_REFRESH_BANK_CARD_INFO = "WITHDRAW_REFRESH_BANK_CARD_INFO";
    public static final String SUBMIT_WITHDRAW = "SUBMIT_WITHDRAW";
}
