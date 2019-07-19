package com.id.cash.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.id.cash.R;

import static com.id.cash.common.DimenUtil.dip2px;

/**
 * Created by linchen on 2018/5/27.
 */

public class TagListView extends ViewGroup implements View.OnClickListener {
    private ArrayList<String> tags = new ArrayList<>();
    private Context context;

    // attributes
    private ColorStateList textColor;
    private int resTextBg;
    private int tagPaddingLeft;
    private int tagPaddingTop;
    private int tagPaddingRight;
    private int tagPaddingBottom;
    private int textSize;
    private int lineSpacing;
    private int tagSpacing;

    private OnTagClickListener tagClickListener;

    public interface OnTagClickListener {
        void onClick(View view, String tag, int i);
    }

    public TagListView(Context context) {
        super(context);
        this.context = context;
    }

    public TagListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagListView);
        try {
            loadStyled(typedArray);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    public TagListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(defStyleAttr, R.styleable.TagListView);
        try {
            loadStyled(typedArray);
        } finally {
            typedArray.recycle();
        }
    }

    private void loadStyled(TypedArray typedArray) {
        this.textSize = typedArray.getDimensionPixelSize(R.styleable.TagListView_tagTextSize, dip2px(12));
        this.textColor = typedArray.getColorStateList(R.styleable.TagListView_tagTextColor);
        this.resTextBg = typedArray.getResourceId(R.styleable.TagListView_tagBackground, 0);
        this.tagPaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.TagListView_tagPaddingLeft, 0);
        this.tagPaddingTop = typedArray.getDimensionPixelOffset(R.styleable.TagListView_tagPaddingTop, 0);
        this.tagPaddingRight = typedArray.getDimensionPixelOffset(R.styleable.TagListView_tagPaddingRight, 0);
        this.tagPaddingBottom = typedArray.getDimensionPixelOffset(R.styleable.TagListView_tagPaddingBottom, 0);
        this.lineSpacing = typedArray.getDimensionPixelOffset(R.styleable.TagListView_lineSpacing, 0);
        this.tagSpacing = typedArray.getDimensionPixelOffset(R.styleable.TagListView_tagSpacing, 0);
    }

    public void setTags(List<String> tags) {
        removeAllViews();
        this.tags.clear();

        if (tags != null) {
            this.tags.addAll(tags);
            for (int i = 0; i < tags.size(); i++) {
                addTag(tags.get(i), i);
            }
        }
    }

    private void addTag(String text, int viewTag) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setBackgroundResource(resTextBg);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.textSize);
        textView.setPadding(tagPaddingLeft, tagPaddingTop, tagPaddingRight, tagPaddingBottom);

        textView.setTag(viewTag);
        textView.setOnClickListener(this);

        addView(textView);
    }

    public void setTagClickListener(OnTagClickListener tagClickListener) {
        this.tagClickListener = tagClickListener;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            if (tagClickListener != null) {
                int tag = (int) v.getTag();
                tagClickListener.onClick(v, tags.get(tag), tag);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = right - left;
        int childWidth = width - getPaddingLeft() - getPaddingRight();

        int currentRight = 0;
        int currentLineHeight = 0;
        int currentTop = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int currentChildWidth = child.getMeasuredWidth();
            int currentChildHeight = child.getMeasuredHeight();

            if (childWidth < (currentChildWidth + currentRight)) {
                currentTop += currentLineHeight;
                currentTop += lineSpacing;

                currentLineHeight = 0;

                // start from the virtual (0 - tagSpacing), makes code consistent
                currentRight = getPaddingLeft() - tagSpacing;
            }

            // position the child
            currentRight += tagSpacing;
            child.layout(currentRight, currentTop, currentChildWidth + currentRight, currentChildHeight + currentTop);
            currentRight += currentChildWidth;

            currentLineHeight = Math.max(currentChildHeight, currentLineHeight);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);

        int childWidth = width - getPaddingLeft() - getPaddingRight();

        int i = 0;
        int currentTop = 0;
        int currentLineHeight = 0;
        int maxWidth = 0;
        int currentRight = 0;
        while (i < getChildCount()) {
            int currentChildWidth = 0;
            int currentChildHeight = 0;

            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            // arrange the tags from left to right
            // & from top to bottom
            // 1. check the line is full
            currentChildWidth = child.getMeasuredWidth();
            currentChildHeight = child.getMeasuredHeight();
            if (childWidth < (currentChildWidth + currentRight)) {
                // if the line is full, move to next line
                currentTop += currentLineHeight;
                currentTop += lineSpacing;

                currentLineHeight = 0;

                // start from the virtual (0 - tagSpacing), makes code consistent
                currentRight = -tagSpacing;
            }

            // position the child
            currentRight += tagSpacing;
            currentRight += currentChildWidth;

            // compute line height
            currentLineHeight = Math.max(currentChildHeight, currentLineHeight);
            maxWidth = Math.max(maxWidth, currentRight);

            i++;
        }

        final int finalHeight = currentTop + currentLineHeight;
        setMeasuredDimension(measureWidth(widthMeasureSpec, maxWidth),
                measureHeight(heightMeasureSpec, finalHeight));
    }

    private int measureHeight(int heightMeasureSpec, int height) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        if (mode != MeasureSpec.EXACTLY) {
            height = height + getPaddingTop() + getPaddingBottom();
            h = (mode == Integer.MIN_VALUE) ? Math.min(height, h) : height;
        }

        return Math.max(h, getSuggestedMinimumHeight());
    }

    private int measureWidth(int widthMeasureSpec, int width) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);

        if (mode != MeasureSpec.EXACTLY) {
            width = width + getPaddingLeft() + getPaddingRight();
            w = (mode == Integer.MIN_VALUE) ? Math.min(width, w) : width;
        }

        return Math.max(w, getSuggestedMinimumWidth());
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public void setTagSpacing(int tagSpacing) {
        this.tagSpacing = tagSpacing;
    }
}
