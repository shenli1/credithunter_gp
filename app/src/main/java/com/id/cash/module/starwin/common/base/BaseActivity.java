package com.id.cash.module.starwin.common.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import com.id.cash.R;
import com.id.cash.common.ActivityManager;
import com.id.cash.common.LogUtil;
import com.id.cash.common.TrackUtil;
import com.id.cash.presenter.BasePresenter;
import com.id.cash.widget.DebouncingOnClickListener;
import com.id.cash.widget.ProgressDialog;

/**
 * Created by linchen on 2018/5/12.
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IView, ILifeCycle, ILoading, LifecycleProvider<ActivityEvent> {
    protected ProgressDialog progressDialog;
    protected P presenter;
    private View statusBar;
    protected TextView tvTitle;
    protected ImageButton backButton;

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    private Handler handler;

    @Override
    public void showLoading() {
        showLoading(null);
    }

    @SuppressWarnings("unused")
    protected void showLoading(String message) {
        if (!isFinishing() && progressDialog != null && !progressDialog.isShowing()) {
            try {
                if (TextUtils.isEmpty(message)) {
                    message = getString(R.string.default_loading_message);
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            } catch (Exception ex) {
                LogUtil.e(ex);
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void hideLoading() {
        if (!isFinishing() && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showError(String action, String code, String message) {
        if (!isFinishing() && progressDialog != null) {
            try {
                progressDialog.show();
                progressDialog.setErrorMessage(message);
            } catch (Exception ex) {
                LogUtil.e(ex);
                progressDialog.dismiss();
            }
        }
    }

    protected abstract P createPresenter();

    protected abstract @LayoutRes
    int getContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.presenter = createPresenter();
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        TrackUtil.logEvent(this.getClass().getSimpleName().toLowerCase() + "_view");

        bindViews();
        bindEvents();

        this.lifecycleSubject.onNext(ActivityEvent.CREATE);

        progressDialog = ProgressDialog.getInstance(this);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                presenter.unsubscribe();
            }
        });
        // can't handle on key listener here, otherwise it finish the entire app.
        // reproduced when cancelling from the AccountKitActivity
//        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && progressDialog.isShowing()) {
//                    BaseActivity.this.finish();
//                    return true;
//                }
//
//                return false;
//            }
//        });
        progressDialog.setCancelable(false);

//        ActivityManager.getInstance().push(this);

        configStatusBar();
    }

    protected void bindViews() {
        tvTitle = findViewById(R.id.tv_actionbar_title);
    }

    protected void bindEvents() {
        backButton = findViewById(R.id.ib_actionbar_back);
        if (backButton != null) {
            backButton.setOnClickListener(new DebouncingOnClickListener() {
                @Override
                public void doClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i("onStart: " + this.getClass().getSimpleName());

        this.lifecycleSubject.onNext(ActivityEvent.START);
        TrackUtil.setCurrentScreen(this, this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();

        LogUtil.i("onResume: " + this.getClass().getSimpleName());
        this.lifecycleSubject.onNext(ActivityEvent.RESUME);
        ActivityManager.getInstance().setTopActivity(this);
    }

    @Override
    protected void onPause() {
        this.lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
        LogUtil.i("onPause: " + this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        this.lifecycleSubject.onNext(ActivityEvent.STOP);
        Activity top = ActivityManager.getInstance().getTopActivity();
        if (top == this) {
            ActivityManager.getInstance().setTopActivity(null);
        }

        super.onStop();

        LogUtil.i("onStop: " + this.getClass().getSimpleName());
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        this.lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
        LogUtil.i("onDestroy: " + this.getClass().getSimpleName());
        if (!isFinishing() && presenter != null) {
            presenter.destroy();
        }
    }

    protected @DrawableRes
    int getStatusBarDrawable() {
        return R.drawable.shape_statusbar;
    }

    private void setStatusBarColor() {
        int res = getStatusBarDrawable();

        if (res != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (statusBar == null) {
//                Window w = context.getWindow();
//                w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                //status bar height
//                int statusBarHeight = Utilities.getStatusBarHeight(context);
                    statusBar = getWindow().findViewById(getResources().getIdentifier("statusBarBackground", "id", "android"));
                }

                if (statusBar != null) {
                    statusBar.setBackgroundResource(res);
                }
            }
        }
    }

    private void configStatusBar() {
        setStatusBarColor();
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                BaseActivity.this.setStatusBarColor();
            }
        });
    }

    @Override
    public ILoading getLoading() {
        return this;
    }

    @Override
    public ILifeCycle getLifeCycle() {
        return this;
    }

    @Override
    public <T> LifecycleTransformer<T> doBindToLifecycle() {
        // the loading dialog is not aware of RxLifecycle
        // if unbinding at onPause, the loading dialog will leave opened for ever
        return bindUntilEvent(ActivityEvent.DESTROY);
    }

    @Override
    public Observable<ActivityEvent> lifecycle() {
        return this.lifecycleSubject.hide();
    }

    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(this.lifecycleSubject, event);
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(this.lifecycleSubject);
    }

    @Override
    public void onApiResult(String action, Object result) {
        // do nothing, child needs to implement it
    }

    protected void postDelayed(Runnable runnable, int delayMills) {
        getHandler().postDelayed(runnable, delayMills);
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }
}
