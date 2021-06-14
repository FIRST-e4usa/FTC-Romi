package org.firstinspires.ftc.driverstation.internal;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import org.firstinspires.ftc.robotcore.internal.ui.PaintedPathDrawable;

public class StopWatchDrawable extends PaintedPathDrawable {
    final float bigDiam;
    final float bigHand;
    final float height;
    final float littleHand;
    final float smallDiam;
    final float stem;
    final float stroke;
    final float width;

    public StopWatchDrawable() {
        this(-16711936);
    }

    public StopWatchDrawable(int i) {
        super(i);
        this.stroke = 6.0f;
        this.smallDiam = 12.5f;
        this.stem = 15.0f;
        this.bigDiam = 67.0f;
        this.bigHand = 20.1f;
        this.littleHand = 13.400001f;
        this.height = 100.5f;
        this.width = 73.0f;
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public int getIntrinsicWidth() {
        return Math.round(73.0f);
    }

    public int getIntrinsicHeight() {
        return Math.round(100.5f);
    }

    /* access modifiers changed from: protected */
    public void computePath(Rect rect) {
        float min = Math.min(((float) rect.width()) / 73.0f, ((float) rect.height()) / 100.5f);
        this.paint.setStrokeWidth(6.0f * min);
        this.path = new Path();
        float f = 3.0f * min;
        RectF rectF = new RectF();
        rectF.set(((float) rect.left) + f, ((float) rect.bottom) - (67.0f * min), ((float) rect.right) - f, ((float) rect.bottom) - f);
        this.path.addOval(rectF, Path.Direction.CCW);
        float exactCenterX = rect.exactCenterX();
        float f2 = rectF.top - (15.0f * min);
        this.path.moveTo(exactCenterX, rectF.top);
        this.path.lineTo(exactCenterX, f2);
        RectF rectF2 = new RectF();
        float f3 = 6.25f * min;
        rectF2.set(exactCenterX - f3, f2 - (12.5f * min), exactCenterX + f3, f2);
        this.path.addOval(rectF2, Path.Direction.CCW);
        this.path.moveTo(rectF.centerX(), rectF.centerY());
        this.path.rLineTo(0.0f, -20.1f * min);
        this.path.moveTo(rectF.centerX(), rectF.centerY());
        this.path.rLineTo(min * 13.400001f, 0.0f);
    }
}
