package com.id.cash.activity.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.google.firebase.iid.FirebaseInstanceId;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.id.cash.BuildConfig;
import com.id.cash.R;
import com.id.cash.activity.bonuspoint.BonusPointMainFragment;
import com.id.cash.activity.feeds.FeedsFragment;
import com.id.cash.activity.main.CashLoanMainFragment;
import com.id.cash.activity.other.CommonWebActivities;
import com.id.cash.activity.product.cashloan.SearchMainActivity;
import com.id.cash.activity.user.BonusPointListActivity;
import com.id.cash.adapter.MainViewPagerAdapter;
import com.id.cash.bean.AppVersionBean;
import com.id.cash.bean.BonusPointTaskResultBean;
import com.id.cash.bean.MainTabBean;
import com.id.cash.bean.UserBean;
import com.id.cash.common.ActivityManager;
import com.id.cash.common.GooglePlayUtil;
import com.id.cash.common.LocalNotificationHelper;
import com.id.cash.common.LogUtil;
import com.id.cash.common.Preference;
import com.id.cash.common.PrivacyPolicyUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.module.starwin.accountkit.LoginEvent;
import com.id.cash.module.starwin.accountkit.LoginManager;
import com.id.cash.module.starwin.common.base.BaseActivity;
import com.id.cash.module.starwin.common.bus.BusAction;
import com.id.cash.module.starwin.common.bus.SetUserEvent;
import com.id.cash.network.ApiActions;
import com.id.cash.presenter.AppPresenter;
import com.id.cash.presenter.BonusPointPresenter;
import com.id.cash.presenter.UserPresenter;
import com.id.cash.widget.DebouncingOnClickListener;
import com.id.cash.widget.dialog.UpgradeDialog;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static android.view.Gravity.AXIS_PULL_BEFORE;
import static android.view.Gravity.AXIS_SPECIFIED;
import static com.id.cash.presenter.SharePresenter.SHARE_REQUEST_CODE;

/**
 * Created by linchen on 2018/6/5.
 */

public class MainTabActivity extends BaseActivity<AppPresenter> {
    public static int PRIVACY_POLICY_RESULT_CODE = 1001;
    private static boolean isUpgradeDialogShown = false;

    private static final int TAB_CASHLOAN = 0;
    private static final int TAB_FEEDS = 1;
    private static final int TAB_BONUS = 2;

    // bridge login event to observable instead of using @subscribe to better respect Activity lifecycle
    private final PublishSubject<LoginEvent> loginSubject = PublishSubject.create();
    private final LoginManager loginManager = new LoginManager(this);
    private final UserPresenter userPresenter = new UserPresenter(this);
    private final BonusPointPresenter bonusPointPresenter = new BonusPointPresenter(this);
    TabLayout tabLayout;
    // UI
    private DrawerLayout drawerLayout;
    private TextView tvUserName;
    private LinearLayout layoutLogout;
    private ViewPager viewPager;
    private MainViewPagerAdapter viewPagerAdapter;
    private View actionbarHome;
    private View actionbarBonusPoint;
    private View actionbarFeeds;
    private int currentPage = 0;
    private CashLoanMainFragment cashloanFragment;
    private BonusPointMainFragment bonusPointFragment;
    private FeedsFragment feedsFragment;

    // data
    private ArrayList<String> hotwords;
    private ArrayList<MainTabBean> tabBeanList;

    // only called when started from notification
    public static void actionStart(Context context) {
        if(context==null)return;
        Intent intent = new Intent(context, MainTabActivity.class);
        // clear top makes sure we are on top
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        context.startActivity(intent);
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    @Override
    protected AppPresenter createPresenter() {
        return new AppPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createFragments();

        RxBus.get().register(this);

        // register device
        presenter.registerDevice(ApiActions.REGISTER_DEVICE);

        fetchData();

        // select the first tab
        updateTab(tabBeanList, tabLayout.getTabAt(0), true);
        viewPager.setCurrentItem(0, false);

        if (PrivacyPolicyUtil.shouldShowPrivacyPolicyDialog()) {
            PrivacyPolicyActivity.actionStartForResult(this, PRIVACY_POLICY_RESULT_CODE);
        }

        // collect SMS (if user allowed)
//        postDelayed(() -> collectUserData(), 0);
    }

    private void createFragments() {
        cashloanFragment = new CashLoanMainFragment();
        feedsFragment = new FeedsFragment();
        bonusPointFragment = new BonusPointMainFragment();

        tabBeanList = new ArrayList<MainTabBean>() {{
            add(new MainTabBean(cashloanFragment, R.string.main_tab_home,
                    R.drawable.main_tab_home0, R.drawable.main_tab_home1));

            add(new MainTabBean(feedsFragment, R.string.main_tab_feeds,
                    R.drawable.main_tab_feeds0, R.drawable.main_tab_feeds1));

            add(new MainTabBean(bonusPointFragment, R.string.main_tab_bonus_point,
                    R.drawable.main_tab_bonuspoint0, R.drawable.main_tab_bonuspoint1));
        }};

        viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), tabBeanList);
        viewPager = findViewById(R.id.viewpager_main);
        viewPager.setAdapter(viewPagerAdapter);

        // setup tabs
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(4);
        for (int i = 0; i < tabBeanList.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.item_tab_main);

            updateTab(tabBeanList, tab, false);
        }
    }

    private void registerUserFCM() {
//        String token = FirebaseInstanceId.getInstance().getToken();
//        LogUtil.i("FCM token: " + token);
//
//        if (!TextUtils.isEmpty(token)) {
//            presenter.registerPushToken(token);
//        }
    }

    private void updateTab(ArrayList<MainTabBean> tabBeanList, TabLayout.Tab tab, boolean isSelected) {
        int position = tab.getPosition();
        MainTabBean bean = tabBeanList.get(position);

        if (isSelected) {
            onPageSelected(position);
        }
        View view = tab.getCustomView();
        if (view != null) {
            ImageView icon = view.findViewById(R.id.iv_tab_icon);
            TextView tvTitle = view.findViewById(R.id.tv_tab_title);

            Drawable iconDrawable;
            if (isSelected) {
                iconDrawable = getResources().getDrawable(bean.getIconSelected());
            } else {
                iconDrawable = getResources().getDrawable(bean.getIcon());
            }
            icon.setImageDrawable(iconDrawable);
            tvTitle.setText(bean.getTitle());
            tvTitle.setSelected(isSelected);
        }
    }

    private void updateActionBar(int selectedIndex) {
        if (actionbarHome != null && actionbarBonusPoint != null && actionbarFeeds != null) {
            if (selectedIndex == TAB_CASHLOAN) {
                actionbarHome.setVisibility(View.VISIBLE);
                actionbarFeeds.setVisibility(View.GONE);
                actionbarBonusPoint.setVisibility(View.GONE);
            } else if (selectedIndex == TAB_FEEDS) {
                actionbarHome.setVisibility(View.GONE);
                actionbarFeeds.setVisibility(View.VISIBLE);
                actionbarBonusPoint.setVisibility(View.GONE);
            } else {
                actionbarHome.setVisibility(View.GONE);
                actionbarFeeds.setVisibility(View.GONE);
                actionbarBonusPoint.setVisibility(View.VISIBLE);
            }
        }
    }

    private void fetchUserInfoIfNeeded() {
        // if user is logged in, i.e. user has token & userId
        // but not user info saved, fetch user info
        if (loginManager.isLoggedIn()) {
            UserBean userBean = Preference.getInstance().getUser();
            if (userBean == null || userBean.getId() == null) {
                userPresenter.getUserInfo(ApiActions.GET_USER);
            }
        }
    }

    private void fetchData() {
        if(presenter==null)return;
        this.presenter.loadHotWords(ApiActions.HOT_WORDS);
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        // not using butter knife due to its obfuscation limitations
        drawerLayout = findViewById(R.id.drawer);
        tvUserName = findViewById(R.id.tv_drawer_user_name);
        layoutLogout = findViewById(R.id.ll_user_logout);
        actionbarHome = findViewById(R.id.actionbar_home);
        actionbarBonusPoint = findViewById(R.id.actionbar_bonuspoint);
        actionbarFeeds = findViewById(R.id.actionbar_feeds);
        tabLayout = findViewById(R.id.tab_main);

        TextView tvVersion = findViewById(R.id.tv_version);
        int versionCode = 0;
        try {
            Context context = this;
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (Exception e) {
            LogUtil.e(e.toString());
        }

        if (versionCode == 0) {
            versionCode = BuildConfig.VERSION_CODE;
        }
        tvVersion.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + "\n" + String.valueOf(versionCode));
    }

    private void onPageSelected(int position) {
        currentPage = position;

        updateActionBar(position);

        String event = null;
        switch (position) {
            case TAB_CASHLOAN:
                event = "tab_cashloan_clicked";
                break;
            case TAB_FEEDS:
                event = "tab_feeds_clicked";
                break;
            case TAB_BONUS: {
                // update bonuspoint  fragment on the user events
                if (bonusPointFragment != null) {
                    bonusPointFragment.updateUserUI();
                }
                event = "tab_bonus_clicked";
                break;
            }
        }

        if (event != null) {
            TrackUtil.logEvent(event);
        }
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTab(tabBeanList, tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTab(tabBeanList, tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                updateTab(tabBeanList, tab, true);
            }
        });

        View btnUserHeader = findViewById(R.id.ll_user_header);
        btnUserHeader.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onHeaderClicked();
            }
        });

        View btnLogout = findViewById(R.id.ll_user_logout);
        btnLogout.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onLogoutClicked();
            }
        });

        View btnPointDetail = findViewById(R.id.ll_user_point_detail);
        btnPointDetail.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onPointDetailClicked();
            }
        });

        View btnPrivacyPolicy = findViewById(R.id.ll_user_privacy_policy);
        btnPrivacyPolicy.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onPrivacyPolicyClicked();
            }
        });

        View btnUserService = findViewById(R.id.ll_user_service_agreement);
        btnUserService.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onServiceAgreementClicked();
            }
        });

        View btnAboutUs = findViewById(R.id.ll_user_about_aboutus);
        btnAboutUs.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onAboutUsClicked();
            }
        });

        View btnAvatar = findViewById(R.id.ib_actionbar_home_user);
        btnAvatar.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onActionBarAvatarClicked();
            }
        });

        btnAvatar = findViewById(R.id.ib_actionbar_bonuspoint_user);
        btnAvatar.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onActionBarAvatarClicked();
            }
        });

        View btnSearch = findViewById(R.id.tv_actionbar_search);
        btnSearch.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onSearchClicked();
            }
        });

        View btnPointQA = findViewById(R.id.ib_actionbar_bonuspoint_qa);
        btnPointQA.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                onBonusPointQAClicked();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(BusAction.LOGIN)})
    public void onLogin(LoginEvent loginEvent) {
        // the loginEvent will be relayed to loginSubject
        // the login is not handled here directly, because rxlifecycle makes sure the activity is active
        loginSubject.onNext(loginEvent);
    }

    // called from SWFirebaseInstanceIDService when FCM token is updated
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(BusAction.UPDATE_FCM_TOKEN)})
    public void onUpdateFCMToken(SetUserEvent setUserEvent) {
        registerUserFCM();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(BusAction.APP_UPGRADE)})
    public void onShowAppUpgrade(AppVersionBean appVersionBean) {
        if (!isUpgradeDialogShown) {
            isUpgradeDialogShown = true;
            if (appVersionBean != null && appVersionBean.getUrl() != null) {
                Activity activity = getTopActivity();
                UpgradeDialog upgradeDialog = new UpgradeDialog(activity, appVersionBean);
                upgradeDialog.show();

                upgradeDialog.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MainTabActivity.isUpgradeDialogShown = false;
                    }
                });
                return;
            }
            isUpgradeDialogShown = false;
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(BusAction.SHOW_BONUSPOINT_ANIMATION)})
    public void onShowBonusPointAnimation(BonusPointTaskResultBean bonusPointTaskResultBean) {
        bonusPointPresenter.showBonusPointTaskAnimation(bonusPointTaskResultBean);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindRxEvents();

        LocalNotificationHelper.cancelAll(this);

        fetchUserInfoIfNeeded();

        // update user in onResume to avoid potential lifecycle crashes
        updateDrawerUserInfo(Preference.getInstance().getUser());

        presenter.checkReferrer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxBus.get().unregister(this);

//        ActivityManager.getInstance().finish(this);
    }

    private void bindRxEvents() {
        loginSubject
                .compose(this.<LoginEvent>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<LoginEvent>() {
                    @Override
                    public void accept(LoginEvent loginEvent) throws Exception {
                        loginManager.phoneLogin(MainTabActivity.this, null);
                    }
                });
    }

    void onHeaderClicked() {
        TrackUtil.logEvent("drawer_login_click");
        if (!loginManager.isLoggedIn()) {
            // if not logged in show login
            loginManager.phoneLogin(this, null);
        }
    }

    void onLogoutClicked() {
        TrackUtil.logEvent("drawer_logout_click");
        loginManager.logout();
    }

    private void onPointDetailClicked() {
        TrackUtil.logEvent("drawer_point_detail_click");
        closeDrawer();
        BonusPointListActivity.actionStart(this);
    }

    private void onPrivacyPolicyClicked() {
        TrackUtil.logEvent("drawer_privacy_click");
        closeDrawer();
        CommonWebActivities.showPrivacyPolicy(this);
    }

    private void onServiceAgreementClicked() {
        TrackUtil.logEvent("drawer_eula_click");
        closeDrawer();
        CommonWebActivities.showServiceAgreement(this);
    }

    private void onAboutUsClicked() {
        TrackUtil.logEvent("drawer_aboutus_click");
        closeDrawer();
        AboutUsActivity.actionStart(this);
    }

    void onActionBarAvatarClicked() {
        TrackUtil.logEvent("opendrawer_click");
        openDrawer();

        // check packages
        boolean isLauncherable = GooglePlayUtil.isLaunchable(this, "com.firestorm.sea.cashcash");
        LogUtil.e("isLauncherable: " + isLauncherable);
    }

    void onSearchClicked() {
        TrackUtil.logEvent("search_click");
        SearchMainActivity.actionStart(this, hotwords);
    }

    void onBonusPointQAClicked() {
        TrackUtil.logEvent("point_qa_click");
        CommonWebActivities.showPointQA(this);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(AXIS_SPECIFIED | AXIS_PULL_BEFORE);
    }

    private void closeDrawer() {
        drawerLayout.closeDrawers();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loginManager.handleActivityResult(requestCode, resultCode, data);

        if (requestCode == SHARE_REQUEST_CODE) {
            // sent by BonusPointPresenter.handleActivityResult (when FB share succeeds)
            bonusPointPresenter.handleActivityResult(requestCode, resultCode, data);
        } else if (requestCode == PRIVACY_POLICY_RESULT_CODE) {
            // note:
            // user's privacy policy acceptance is already stored to preferences,
            // collectUserData now, which respects user's choice

//            collectUserData();
        }
    }

//    private void collectUserData() {
//        presenter.requestAndCollectUserData(ApiActions.COLLECT_PRIVACY_DATA, this);
//    }

    private void updateDrawerUserInfo(UserBean userBean) {
        // user mobile
        if (userBean != null) {
            if (userBean.getMobile() != null) {
                tvUserName.setText(userBean.getMobile());
            }
            layoutLogout.setVisibility(View.VISIBLE);
        } else {
            tvUserName.setText(R.string.please_login);
            layoutLogout.setVisibility(View.GONE);
        }
    }

    private Activity getTopActivity() {
        Activity activity = ActivityManager.getInstance().getTopActivity();
        if (activity == null || activity.isFinishing()) {
            activity = this;
        }
        return activity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onApiResult(String action, Object result) {
        if (ApiActions.HOT_WORDS.equals(action)) {
            hotwords = (ArrayList<String>) result;
        } else if (ApiActions.LOGIN_TOKEN.equals(action)) {
            // update drawer
            updateDrawerUserInfo((UserBean) result);
        } else if (ApiActions.LOGOUT.equals(action)) {
            updateDrawerUserInfo(null);
        } else if (ApiActions.BONUSPOINT_TASK_RESULT.equals(action)) {
            bonusPointPresenter.showBonusPointTaskAnimation((BonusPointTaskResultBean) result);
        } else if (ApiActions.INSTALL_REFERRER.equals(action)) {
            Preference.getInstance().setFirstLaunchCalled();
        }
    }
}
