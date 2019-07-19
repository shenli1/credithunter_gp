package com.id.cash.widget.circle;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.id.cash.R;

/**
 * Created by linchen on 2018/5/18.
 */
public class RingChartView extends View {
    private final RectF rectF = new RectF();

    private Ring ringBg;
    private Ring ring1;
    private Ring ring2;
    private float strokeWidth;
    private boolean isDrawRing1 = false;
    private boolean isDrawRing2 = false;

    class Ring {
        private final Paint paint;
        private float startAngle;
        private float angle;

        Ring(@ColorInt int color, float strokeWidth) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
            paint.setStrokeWidth(strokeWidth);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setAntiAlias(true);
        }

        void drawArc(Canvas canvas, RectF rectF) {
            canvas.drawArc(rectF, startAngle, angle, false, paint);
        }

        void setAngle(float angle) {
            this.angle = angle;
        }

        void setStartAngle(float startAngle) {
            this.startAngle = startAngle;
        }
    }

    public RingChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RingChartView);

        @ColorInt int bgColor = R.color.ring_bg;
        @ColorInt int color1 = R.color.ring_color1;
        @ColorInt int color2 = R.color.ring_color2;
        float strokeWidth = 14.0f;
        if (typedArray != null) {
            try {
                Resources resources = getResources();
                strokeWidth = typedArray.getDimension(R.styleable.RingChartView_stroke_width, 14.0f);
                bgColor = typedArray.getColor(R.styleable.RingChartView_bg_color, ResourcesCompat.getColor(resources, R.color.ring_bg, null));
                color1 = typedArray.getColor(R.styleable.RingChartView_color1, ResourcesCompat.getColor(resources, R.color.ring_color1, null));
                color2 = typedArray.getColor(R.styleable.RingChartView_color2, ResourcesCompat.getColor(resources, R.color.ring_color2, null));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                typedArray.recycle();
            }
        }

        this.strokeWidth = strokeWidth;
        ringBg = new Ring(bgColor, strokeWidth);
        ringBg.setStartAngle(180);
        ringBg.setAngle(180);
        ring1 = new Ring(color1, strokeWidth);
        ring1.setStartAngle(180);
        ring2 = new Ring(color2, strokeWidth);

        reset();
    }

    public void reset() {
        isDrawRing1 = false;
        isDrawRing2 = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ringBg.drawArc(canvas, rectF);
        if (isDrawRing1) {
            ring1.drawArc(canvas, rectF);
        }

        if (isDrawRing2) {
            ring2.drawArc(canvas, rectF);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // fits the width then calculate the height
        int width, height;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(widthSize, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                width = 100;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(widthSize, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                height = width / 2 + (int) strokeWidth / 2 + 1;
                break;
        }

        final float strokeWidth = getStrokeWidthInPixel();
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, width - strokeWidth / 2, width - strokeWidth / 2);

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private float getStrokeWidthInPixel() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth, dm);
    }

    public void setStartAngle2(float angle) {
        ring2.setStartAngle(angle);
    }

    public void setAngle1(float angle) {
        ring1.setAngle(angle);
    }

    public void setAngle2(float angle2) {
        ring2.setAngle(angle2);
    }

    public void setDrawRing1(boolean drawRing1) {
        isDrawRing1 = drawRing1;
    }

    public void setDrawRing2(boolean drawRing2) {
        isDrawRing2 = drawRing2;
    }
}
