package com.id.cash.widget.circle;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by linchen on 2018/5/18.
 */

public class CircleAngleAnimation extends Animation {
    private RingChartView circle;

    private float ring1Percent;
    private float ring2Percent;
    private float ring1Angle;
    private float ring2Angle;

    public CircleAngleAnimation(RingChartView circle, float ring1Percent) {
        this.ring1Percent = ring1Percent;
        this.circle = circle;

        ring1Angle = 180 * ring1Percent;
        ring2Percent = 1 - ring1Percent;
        ring2Angle = 180 * ring2Percent;

        circle.reset();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        // interpolate ring1
        if (interpolatedTime <= ring1Percent) {
            // percent in ring1
            float p = interpolatedTime / ring1Percent;
            float angle = ring1Angle * p;
            circle.setAngle1(angle);
            circle.setDrawRing1(true);
        } else {
            circle.setAngle1(ring1Angle);
            // interpolate ring2
            if (ring2Percent > 0) {
                circle.setDrawRing2(true);
                float p = (interpolatedTime - ring1Percent) / ring2Percent;
                float angle = ring2Angle * p;
                circle.setStartAngle2(180 + ring1Angle);
                circle.setAngle2(angle);
            }
        }

        circle.requestLayout();
    }
}
