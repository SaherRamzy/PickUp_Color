package com.apps.saher.pickTheColor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by saher on 5/12/2016.
 */
public class Circleview extends View {
    int circleColor;
    Paint paint;

    public Circleview(Context context) {
        super(context);
        paint = new Paint();
        setupPaint();
    }

    public Circleview(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setupPaint();
    }

    public Circleview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        setupPaint();
    }

    public void setCircleColor(int circleColor)
    {
        this.circleColor = circleColor;
        invalidate();
    }

    public int getCircleColor()
    {
        return circleColor;
    }

    private void setupPaint() {

        paint.setColor(circleColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        int usableWidth = w - (pl + pr);
        int usableHeight = h - (pt + pb);

        int radius = Math.min(usableWidth, usableHeight) / 2;
        int cx = pl + (usableWidth / 2);
        int cy = pt + (usableHeight / 2);

//        paint.setColor(Color.BLUE);
//        canvas.drawCircle(50,50,50,paint);
        canvas.drawCircle(cx,cy,radius,paint);
        invalidate();

    }
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        width=MeasureSpec.getSize(widthMeasureSpec);
//        hight=MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(width,hight);
//    }
}
