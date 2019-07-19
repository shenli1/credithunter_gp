package com.id.cash.module.starwin.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import com.id.cash.R;
import com.id.cash.common.LogUtil;
import com.id.cash.presenter.BasePresenter;
import com.id.cash.widget.ProgressDialog;

/**
 * Created by linchen on 2018/5/28.
 */

public abstract class BaseFragment<P extends BasePresenter> extends BaseLazyFragment implements IView, ILifeCycle, ILoading, LifecycleProvider<FragmentEvent> {
    protected ProgressDialog progressDialog;
    protected P presenter;
    protected FragmentActivity myActivity;
    protected View view;
    private OnFragmentCreateViewListener onFragmentCreateViewListener;
    private Handler handler;

    protected boolean isPrepared;

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    protected abstract @LayoutRes
    int getLayoutId();

    public abstract P createPresenter();

    public interface OnFragmentCreateViewListener {
        void onFragmentViewCreated(boolean isVisible);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myActivity = getActivity();
        view = inflater.inflate(getLayoutId(), null);
        presenter = createPresenter();
        progressDialog = ProgressDialog.getInstance(myActivity);

        // can't call the onFragmentViewCreated synchronously, the child has not finished onCreateView yet!
        new Handler(Looper.getMainLooper()).post(() -> {
            if (onFragmentCreateViewListener != null) {
                onFragmentCreateViewListener.onFragmentViewCreated(isVisible);
            }
        });

        isPrepared = true;
        lazyLoad();

        return view;
    }

    public void setOnFragmentCreateViewListener(OnFragmentCreateViewListener onFragmentCreateViewListener) {
        this.onFragmentCreateViewListener = onFragmentCreateViewListener;
    }

    @Override
    public void showLoading() {
        showLoading(null);
    }

    @SuppressWarnings("unused")
    protected void showLoading(String message) {
        if (myActivity != null && !myActivity.isFinishing() && progressDialog != null && !progressDialog.isShowing()) {
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
        if (myActivity != null && !myActivity.isFinishing() && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showError(String action, String code, String message) {
        if (myActivity != null && !myActivity.isFinishing() && progressDialog != null) {
            progressDialog.show();
            progressDialog.setErrorMessage(message);
        }
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
        return bindUntilEvent(FragmentEvent.DESTROY);
    }

    @Override
    public Observable<FragmentEvent> lifecycle() {
        return this.lifecycleSubject.hide();
    }

    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(this.lifecycleSubject, event);
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(this.lifecycleSubject);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.lifecycleSubject.onNext(FragmentEvent.ATTACH);
        this.myActivity = (FragmentActivity) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.lifecycleSubject.onNext(FragmentEvent.ATTACH);
        this.myActivity = (FragmentActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        this.lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();

    }

    @Override
    public void onStop() {
        this.lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroyView() {
        this.lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        this.lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        this.lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
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
