package com.id.cash.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.id.cash.R;

/**
 * Created by linchen on 2018/5/22.
 */

public class DashedLineView extends View {
    private Paint paint;
    private Path path;

    public DashedLineView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private Paint getPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.step_divider));
            paint.setStrokeWidth(20.0f);
            paint.setPathEffect(new DashPathEffect(new float[]{10.0f, 10.0f, 10.0f, 10.0f}, 10.0f));
        }
        return paint;
    }

    private Path getPath() {
        if (path == null) {
            path = new Path();
            path.moveTo(0.0f, 0.0f);
            path.lineTo(0.0f, 900.0f);
        }
        return path;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(getPath(), getPaint());
    }
}
