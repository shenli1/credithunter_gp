package com.id.cash.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.Space;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.id.cash.R;
import com.id.cash.common.DimenUtil;
import com.id.cash.common.LogUtil;
import com.id.cash.common.TrackUtil;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by linchen on 2018/5/22.
 */

public class SlidingTabBar extends LinearLayout implements ViewPager.OnPageChangeListener, View.OnClickListener, ViewTreeObserver.OnScrollChangedListener {
    private ObservableScrollView scrollView;
    List<View> views;
    private ViewPager viewPager;

    // scroll state control
    private boolean isScrolling = false;
    private Runnable tabAnimSelector;
    private int selectedPosition;
    private ViewTreeObserver.OnScrollChangedListener scrollListener;

    private boolean isClick = false;
    private SlidingTabBar mNextSynchronize;
    private SlidingTabBar mSynchronize;
    private Runnable scrollOffRunnable = () -> SlidingTabBar.this.isClick = false;

    // UI config
    private int actionBarHeight = DimenUtil.dip2px(0);
    private int statusBarHeight;
    private int textAppearance;
    @DrawableRes
    private int tabBackground;
    private int tabPadding;
    private int mode;

    private int indicatorHeight;
    private int indicatorMode;
    private int indicatorOffset;
    private int indicatorWidth;

    private int underLineHeight;

    // drawing
    private Paint indicatorPaint;
    private Paint underLinePaint;

    class TabView extends AppCompatRadioButton {
        final SlidingTabBar slidingTabBar;

        public TabView(SlidingTabBar slidingTabBar, Context context) {
            super(context, null, slidingTabBar.textAppearance);
            this.slidingTabBar = slidingTabBar;
            init();
        }

        private void init() {
            setButtonDrawable(new ColorDrawable(0));
            setGravity(17);
            setTextAppearance(getContext(), this.slidingTabBar.textAppearance);
            if (this.slidingTabBar.tabBackground != 0) {
                setBackgroundResource(this.slidingTabBar.tabBackground);
            } else {
                initBackground(this, new ColorDrawable(0));
            }
            setSingleLine(true);
            setEllipsize(TextUtils.TruncateAt.END);
            setOnClickListener(this.slidingTabBar);
        }

        public void initBackground(View view, Drawable drawable) {
            if (view != null) {
//                int paddingLeft = view.getPaddingLeft();
//                int paddingRight = view.getPaddingRight();
//                int paddingTop = view.getPaddingTop();
//                int paddingBottom = view.getPaddingBottom();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(drawable);
                } else {
                    view.setBackgroundDrawable(drawable);
                }

//                view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }
        }
    }

    public SlidingTabBar(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public SlidingTabBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SlidingTabBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlidingTabBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollView(ObservableScrollView inScrollView, ViewTreeObserver.OnScrollChangedListener onScrollChangedListener, List<String> titleList, List<View> viewList) {
        if (this.scrollView != inScrollView) {
            if (titleList != null) {
                if (viewList != null) {
                    if (!titleList.isEmpty()) {
                        if (!viewList.isEmpty()) {
                            if (titleList.size() != viewList.size()) {
                                throw new IllegalArgumentException("tabs and views should be the same length!");
                            }
                            scrollListener = onScrollChangedListener;
                            scrollView = inScrollView;
                            views = viewList;
                            if (scrollView != null) {
                                scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
                            }
                            initTabs(titleList);
                            if (onScrollChangedListener instanceof SlidingTabBar) {
                                this.mSynchronize = (SlidingTabBar) onScrollChangedListener;
                                this.mSynchronize.mNextSynchronize = this;
                                this.viewPager = this.mSynchronize.getViewPager();
                                this.scrollListener = this.mSynchronize.scrollListener;
                                if (this.viewPager != null) {
                                    this.viewPager.addOnPageChangeListener(this);
                                    return;
                                }
                            }
                            initViewPager(titleList.size());
                            return;
                        }
                    }
                    throw new IllegalArgumentException("tabs and views should not be empty!");
                }
            }
            throw new IllegalArgumentException("tabs and views should not be null!");
        }
    }

    private ViewPager getViewPager() {
        return this.viewPager;
    }

    public void setActionBarHeight(int actionBarHeight) {
        this.actionBarHeight = actionBarHeight;
    }

    public void setCurrentItem(int i) {
        TabView tabView;
        String str;
        if (this.selectedPosition != i) {
            tabView = getTabView(this.selectedPosition);
            if (tabView != null) {
                tabView.setChecked(false);
            }
        }
        this.selectedPosition = i;
        switch (this.selectedPosition) {
            case 0:
                TrackUtil.logEvent("detail_tab_detail_click");
                break;
            case 1:
                TrackUtil.logEvent("details_tab_procedure_click");
                break;
            default:
                break;
        }
        tabView = getTabView(this.selectedPosition);
        if (tabView != null) {
            tabView.setChecked(true);
        }
        animateToTab(i);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (this.tabAnimSelector != null) {
            post(this.tabAnimSelector);
        }
    }

    @Override
    public void onClick(View view) {
        int tag = ((Integer) view.getTag());
        if (this.viewPager != null) {
            synchronizeClickStatus();
            this.viewPager.setCurrentItem(tag, true);
        }
        if (this.scrollView != null) {
            int viewLocation = getViewLocation(tag) + (((-actionBarHeight) - getMeasuredHeight()) - this.statusBarHeight);
            if (tag == 0) {
                viewLocation = viewLocation > 0 ? viewLocation - 1 : viewLocation + 1;
            }

            final int vl = viewLocation;
            this.scrollView.postDelayed(
                    () -> SlidingTabBar.this.scrollView.smoothScrollBy(0, vl),
                    200);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        TabView tabView = getTabView(position);
        TabView tabView2 = getTabView(position + 1);
        if (tabView != null && tabView2 != null) {
            int width = this.indicatorMode == -1 ? tabView.getWidth() : getTextWidth(tabView);
            int width2 = this.indicatorMode == -1 ? tabView2.getWidth() : getTextWidth(tabView2);
            float width3 = this.indicatorMode == -1 ? ((float) (width + width2)) / 2.0f : (float) ((((width / 2) + (tabView.getWidth() / 2)) + (tabView2.getWidth() / 2)) - (width2 / 2));
            float f2 = (float) width;
            width2 = (int) (((((float) (width2 - width)) * positionOffset) + f2) + 0.5f);
            updateIndicator((int) ((this.indicatorMode == -1 ? ((((float) tabView.getLeft()) + (f2 / 2.0f)) + (width3 * positionOffset)) - (((float) width2) / 2.0f) : ((float) ((tabView.getLeft() + (tabView.getWidth() / 2)) - (width / 2))) + (width3 * positionOffset)) + 0.5f),
                    width2);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
    }



    @Override
    public void onScrollChanged() {
        if (this.scrollListener != null) {
            scrollView.setOnScrollChanged(new ObservableScrollView.OnScrollChanged() {
                public void onScroll(int i, int i2, int i3, int i4) {
                }
            });
        }
        if (!isClick) {
            int measuredHeight = (actionBarHeight + getMeasuredHeight()) + statusBarHeight;
            List<Integer> arrayList = new ArrayList<>();
            int size = views.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                arrayList.add(getViewLocation(i2));
            }
            Collections.sort(arrayList);
            if (measuredHeight >= arrayList.get(0)) {
                size = arrayList.size();
                int i3 = 0;
                while (i3 < size) {
                    int i4 = i3 + 1;
                    if (i4 != size) {
                        if (measuredHeight < arrayList.get(i3) || measuredHeight >= arrayList.get(i4)) {
                            i3 = i4;
                        } else if (!scrollView.canScrollVertically(1)) {
                            i = size - 1;
                        }
                    }
                    i = i3;
                    break;
                }
            }
            if (getCurrentIndex() != i) {
                viewPager.setCurrentItem(i, true);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 0) {
            isScrolling = false;
            TextView tabView = getTabView(selectedPosition);
            if (tabView != null) {
                switch (indicatorMode) {
                    case -2:
                        int textWidth = getTextWidth(tabView);
                        updateIndicator((tabView.getLeft() + (tabView.getWidth() / 2)) - (textWidth / 2), textWidth);
                        break;
                    case -1:
                        updateIndicator(tabView.getLeft(), tabView.getMeasuredWidth());
                        break;
                    default:
                        break;
                }
            }
            if (isClick) {
                removeCallbacks(scrollOffRunnable);
                postDelayed(scrollOffRunnable, 220);
                return;
            }
        }
        isScrolling = true;
    }

    public int getCurrentIndex() {
        return this.selectedPosition;
    }

    public int getBarHeight() {
        try {
//            Class cls = Class.forName("com.android.internal.R$dimen");
//            return getResources().getDimensionPixelSize(Integer.parseInt(cls.getField("status_bar_height")
//                    .get(cls.newInstance()).toString()));
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            LogUtil.e(e);
            return 38;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (this.statusBarHeight == 0) {
            this.statusBarHeight = getBarHeight();
        }
    }

    private void addTemporaryTab() {
        int i = 0;
        while (i < 3) {
            TabView tabView;
            LayoutParams layoutParams = null;
            CharSequence title = null;
            if (i != 0) {
                if (i != 1) {
                    if (i == 2) {
                    }
                    tabView = new TabView(this, getContext());
                    tabView.setText(title);
                    tabView.setTag(Integer.valueOf(i));
                    tabView.setChecked(i != 0);
                    if (this.mode == 0) {
                        tabView.setPadding(this.tabPadding, 0, this.tabPadding, 0);
                        layoutParams = new LayoutParams(-2, -1);
                    } else if (this.mode != 1) {
                        layoutParams = new LinearLayout.LayoutParams(0, -1);
                        layoutParams.weight = 1.0f;
                    } else {
                        i++;
                    }
                    addView(tabView, layoutParams);
                    i++;
                }
            }
            title = "";
            tabView = new TabView(this, getContext());
            tabView.setText(title);
            tabView.setTag(i);
            if (i != 0) {
            }
            tabView.setChecked(i != 0);
            if (this.mode == 0) {
                tabView.setPadding(this.tabPadding, 0, this.tabPadding, 0);
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            } else if (this.mode != 1) {
                i++;
            } else {
                layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1.0f;
            }
            addView(tabView, layoutParams);
            i++;
        }
    }

    private TabView getTabView(int i) {
        return (TabView) getChildAt(i);
    }

    private int getViewLocation(int i) {
        if (views != null && views.size() > i) {
            View view = views.get(i);
            if (view != null) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                return location[1];
            }
        }
        return 0;
    }

    private int getTextWidth(TextView textView) {
        return textView.getWidth() / 3;
    }

    private void animateToTab(int tabIndex) {
        final TabView tabView = getTabView(tabIndex);
        if (tabView != null) {
            if (this.tabAnimSelector != null) {
                removeCallbacks(this.tabAnimSelector);
            }

            this.tabAnimSelector = () -> {
                if (!SlidingTabBar.this.isScrolling) {
                    switch (SlidingTabBar.this.indicatorMode) {
                        case -2:
                            int textWidth = SlidingTabBar.this.getTextWidth(tabView);
                            SlidingTabBar.this.updateIndicator((tabView.getLeft() + (tabView.getWidth() / 2)) - (textWidth / 2), textWidth);
                            break;
                        case -1:
                            SlidingTabBar.this.updateIndicator(tabView.getLeft(), tabView.getWidth());
                            break;
                        default:
                            break;
                    }
                }
                SlidingTabBar.this.tabAnimSelector = null;
            };

            post(this.tabAnimSelector);
        }
    }

    private void initViewPager(int size) {
        if (viewPager == null) {
            viewPager = new ViewPager(getContext());
            viewPager.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            View childAt = scrollView.getChildAt(0);
            if (childAt == null) {
                throw new IllegalStateException(" The child view of the ScrollView must be not null!");
            } else if (childAt instanceof ViewGroup) {
                ((ViewGroup) scrollView.getChildAt(0)).addView(viewPager);
                final List<View> arrayList = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    arrayList.add(new Space(getContext()));
                }
                viewPager.setAdapter(new PagerAdapter() {
                    @NonNull
                    @Override
                    public Object instantiateItem(@NonNull View container, int position) {
                        ((ViewGroup) container).addView(arrayList.get(position));
                        return arrayList.get(position);
                    }

                    @Override
                    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
                        ((ViewGroup) container).removeView(arrayList.get(position));
                    }

                    @Override
                    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                        return view == object;
                    }

                    @Override
                    public int getCount() {
                        return arrayList.size();
                    }
                });

                viewPager.addOnPageChangeListener(this);
            } else {
                throw new IllegalStateException(" The child view of the ScrollView must be onItemClickListener ViewGroup!");
            }
        }
    }

    private void initTabs(List<String> titleList) {
        removeAllViews();
        int size = titleList.size();
        if (this.selectedPosition > size) {
            this.selectedPosition = size - 1;
        }
        for (int i = 0; i < size; i++) {
            LayoutParams layoutParams = null;
            String title = titleList.get(i);
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
            TabView tabView = new TabView(this, getContext());
            tabView.setText(title);
            tabView.setTag(i);
            if (this.mode == 0) {
                layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin = this.tabPadding;
                layoutParams.rightMargin = this.tabPadding;
            } else if (this.mode == 1) {
                layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1.0f;
            }
            addView(tabView, layoutParams);
        }
        setCurrentItem(this.selectedPosition);
        requestLayout();
    }

    private void init(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        // TODO setGravity(16)
        setGravity(Gravity.CENTER_HORIZONTAL);
        setHorizontalScrollBarEnabled(false);
        indicatorPaint = new Paint(ANTI_ALIAS_FLAG);
        indicatorPaint.setStyle(Paint.Style.FILL);
        underLinePaint = new Paint(ANTI_ALIAS_FLAG);
        underLinePaint.setStyle(Paint.Style.FILL);
        applyStyle(context, attributeSet, defStyleAttr, defStyleRes);
        if (isInEditMode()) {
            addTemporaryTab();
        }
    }

    private void synchronizeClickStatus() {
        isClick = true;
        if (!(mSynchronize == null || mSynchronize.isClick)) {
            mSynchronize.synchronizeClickStatus();
        }
        if (mNextSynchronize != null && !mNextSynchronize.isClick) {
            mNextSynchronize.synchronizeClickStatus();
        }
    }

    private void applyStyle(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SlidingTabBar, defStyleAttr, defStyleRes);
        try {
            tabPadding = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SlidingTabBar_paddingTab, 12);
            int indicatorColor = obtainStyledAttributes.getColor(R.styleable.SlidingTabBar_indicatorColor, getResources().getColor(R.color.white_bg));
            indicatorMode = obtainStyledAttributes.getInt(R.styleable.SlidingTabBar_indicatorMode, -1);
            indicatorHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SlidingTabBar_indicatorHeight, 2);
            int underlineColor = obtainStyledAttributes.getColor(R.styleable.SlidingTabBar_underlineColor, getResources().getColor(R.color.white_bg));
            underLineHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SlidingTabBar_underlineHeight, 1);
            textAppearance = obtainStyledAttributes.getResourceId(R.styleable.SlidingTabBar_textStyle, 0);
            tabBackground = obtainStyledAttributes.getResourceId(R.styleable.SlidingTabBar_background, 0);
            mode = obtainStyledAttributes.getInteger(R.styleable.SlidingTabBar_mode, 0);

            removeAllViews();
            indicatorPaint.setColor(indicatorColor);
            underLinePaint.setColor(underlineColor);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(0.0f, (float) (getHeight() - this.underLineHeight), (float) getWidth(), (float) getHeight(), this.underLinePaint);
        int paddingLeft = indicatorOffset + getPaddingLeft();
        canvas.drawRect((float) paddingLeft,
                (float) (getHeight() - this.indicatorHeight),
                (float) (paddingLeft + this.indicatorWidth),
                (float) getHeight(), this.indicatorPaint);
        if (isInEditMode()) {
            canvas.drawRect((float) getPaddingLeft(),
                    (float) (getHeight() - this.indicatorHeight),
                    (float) (getPaddingLeft() + getChildAt(0).getWidth()),
                    (float) getHeight(), this.indicatorPaint);
        }
    }

    public void updateIndicator(int indicatorOffset, int indicatorWidth) {
        this.indicatorOffset = indicatorOffset;
        this.indicatorWidth = indicatorWidth;
        invalidate();
    }
}
