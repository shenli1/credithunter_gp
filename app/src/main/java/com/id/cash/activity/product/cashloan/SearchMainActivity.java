package com.id.cash.activity.product.cashloan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.id.cash.R;
import com.id.cash.common.KeyBoardUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.SearchPresenter;
import com.id.cash.widget.DebouncingOnClickListener;
import com.id.cash.widget.NetworkLoadingLayout;
import com.id.cash.widget.TagListView;

/**
 * Created by linchen on 2018/5/26.
 */

public class SearchMainActivity extends BaseActivity<SearchPresenter> implements TextView.OnEditorActionListener, TextWatcher, NetworkLoadingLayout.OnRefreshCallback {
    private static final String HOT_WORDS_TAG = "HOT_WORDS_TAG";
    private TagListView tagHotWordList;
    private TagListView tagSearchHistoryList;
    private EditText etSearch;
    private SearchResultFragment searchResultFragment;
    private View historyView;
    private View resultView;

    private ArrayList<String> hotwords;

    public static void actionStart(Activity activity, ArrayList<String> hotwords) {
        Intent intent = new Intent(activity, SearchMainActivity.class);
        intent.putStringArrayListExtra(HOT_WORDS_TAG, hotwords);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        activity.startActivity(intent);
    }

    private enum Mode {
        HISTORY,    // show search history
        RESULT  // show result
    }

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // logics
        // 1. hot words
        hotwords = getIntent().getStringArrayListExtra(HOT_WORDS_TAG);
        updateHotWords(hotwords);

        // 2. history
        presenter.loadHistory(ApiActions.LOAD_SEARCH_HISTORY);
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        // bind views
        historyView = findViewById(R.id.layout_search_history);
        resultView = findViewById(R.id.layout_search_content);

        etSearch = findViewById(R.id.et_search);
        tagHotWordList = findViewById(R.id.tlv_search_hotword);
        tagSearchHistoryList = findViewById(R.id.tlv_search_history);
        searchResultFragment = new SearchResultFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.layout_search_content, searchResultFragment);
        transaction.commit();
        searchResultFragment.setUserVisibleHint(true);

        setMode(Mode.HISTORY);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        etSearch.setOnEditorActionListener(this);
        etSearch.addTextChangedListener(this);
        tagHotWordList.setTagClickListener((View view, String tag, int i) -> {
            onClickTag(tag);
        });

        // search history list
        tagSearchHistoryList.setTagClickListener((View view, String tag, int i) -> {
            onClickTag(tag);
        });
        searchResultFragment.setRefreshCallback(this);

        View clearHistory = findViewById(R.id.iv_search_clear_history);
        clearHistory.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onClearHistoryClick();
            }
        });
    }

    private void setMode(Mode mode) {
        if (mode == Mode.HISTORY) {
            resultView.setVisibility(View.INVISIBLE);
            historyView.setVisibility(View.VISIBLE);
        } else {
            resultView.setVisibility(View.VISIBLE);
            historyView.setVisibility(View.INVISIBLE);
        }
    }

    void onClickTag(String tag) {
        TrackUtil.logEvent("search_tag_click_" + tag);
        etSearch.setText(tag);
        search(tag);
    }

    public void onClearHistoryClick() {
        TrackUtil.logEvent("search_clear_history_click");
        presenter.clearHistory(ApiActions.SEARCH_CLEAR_HISTORY);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            KeyBoardUtil.hideKeyboard(this, etSearch);
            search();
        }
        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // show history view if text is empty
        String text = etSearch.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            setMode(Mode.HISTORY);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private String lastSearchKeyword;

    private void search(String keyword) {
        TrackUtil.logEvent("search_click_" + keyword);
        if (!TextUtils.isEmpty(keyword) && !TextUtils.isEmpty(keyword.trim())) {
            if (searchResultFragment != null) {
                if (!keyword.equals(lastSearchKeyword)) {
                    setMode(Mode.RESULT);
                    searchResultFragment.search(true, keyword);
                }
            }
            lastSearchKeyword = keyword;
        }
    }

    private void search() {
        String text = etSearch.getText().toString();
        search(text.trim());
    }

    @Override
    public void onRefreshClicked() {
        search();
    }

    @Override
    public void onApiResult(String action, Object result) {
        super.onApiResult(action, result);

        if (ApiActions.LOAD_SEARCH_HISTORY.equals(action) || ApiActions.SEARCH_CLEAR_HISTORY.equals(action)) {
            updateSearchHistory(result);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateSearchHistory(Object result) {
        if (result != null && result instanceof List) {
            List<String> list = (List<String>) result;
            tagSearchHistoryList.setTags(list);
        } else {
            tagSearchHistoryList.setTags(null);
        }
    }

    private void updateHotWords(List<String> result) {
        tagHotWordList.setTags(result);
    }
}
