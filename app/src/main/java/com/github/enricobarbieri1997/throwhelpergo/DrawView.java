package com.github.enricobarbieri1997.throwhelpergo;

/**
 * Created by Xxenr on 28/07/2016.
 * Mods by KarlVonBicycle 28/07/2016
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
    /**
     * Status of the view
     */
    private boolean isTouchable;

    /**
     * Paint used for the circle only
     */
    private Paint paintCircle = new Paint();

    /**
     * Paint used for the middle line
     */
    private Paint paintLines1 = new Paint();

    /**
     * Paint used for the lines tangent to the circle
     */
    private Paint paintLines2 = new Paint();

    /**
     * radius of the circumference
     */
    private float radius = 32f;

    /**
     * offset of the circumference relative to the center of the screen
     */
    private float offsetY = 0f;

    /**
     * height of the display
     */
    private int height = 0;

    /**
     * width of the display
     */
    private int width = 0;

    public DrawView(Context context) {
        super(context);

        paintLines1.setColor(Color.CYAN);
        paintLines1.setStrokeWidth(3);

        paintLines2.setColor(Color.RED);
        paintLines2.setStrokeWidth(3);

        paintCircle.setColor(Color.GREEN);
        paintCircle.setStrokeWidth(3);
        paintCircle.setStyle(Paint.Style.STROKE);

        this.height = this.getHeight();
        this.width = this.getWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.height = h;
        this.width = w;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        float centerX = width / 2f;
        float centerY = height / 2f;

        float positionY = centerY + offsetY;


        canvas.drawLine(centerX, 0, centerX, height, paintLines1); // middle vertical line
        canvas.drawLine(0, positionY, width, positionY, paintLines1); // horizontal line relative to the circle's center

        canvas.drawCircle(centerX, positionY, radius, paintCircle); // circle

        /* KarlVonBicycle says:
        m = (-b+-sqrt(b^2-4ac))/2a
        a = r^2 - center_x^2
        b = 2center_x*center_y
        c = r^2-center_y^2

        I got the above formulas by intersecting a circumference with a line from the origin (0,0)
        and putting the delta = 0 (only one point in common).
         */

        double relativeCenterY = height - positionY; // this has to be done because the center is in the top left corner

        double a = Math.pow(radius, 2) - Math.pow(centerX, 2);
        double b = 2 * centerX * relativeCenterY;
        double c = Math.pow(radius, 2) - Math.pow(relativeCenterY, 2);

        double m1 = (-b + Math.sqrt(Math.pow(b,2)-4*a*c))/(2*a);
        double m2 = (-b - Math.sqrt(Math.pow(b,2)-4*a*c))/(2*a);

        // let's get the points in 'width', shall we?
        double y1 = height - m1 * width;
        double y2 = height - m2 * width;

        // draw 'em!!
        canvas.drawLine(0, height, width, (float)y1, paintLines2);
        canvas.drawLine(0, height, width, (float)y2, paintLines2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float centerY = height / 2;
            float centerX = width / 2;

            float x = event.getX();
            float y = event.getY();

            this.offsetY = y - centerY;
            this.radius = Math.abs(x-centerX);

            //System.out.println("dragging!");

            this.invalidate();
        }

        return true;
    }

    /**
     * returns if the view is touchable or not (<-- is it necessary though?)
     * @return true or false
     */
    public boolean isTouchable() {
        return isTouchable;
    }

    /**
     * Sets the ,,touchability'' of the view (<-- is it necessary though?)
     * @param touchable  true or false
     */
    public void setTouchable(boolean touchable) {
        isTouchable = touchable;
    }

    /**
     * Sets the radius of the circumference to be drawn
     * @param radius radius in pixels
     */
    public void setRadius (float radius)
    {
        this.radius = radius;
    }

    /**
     * Sets the position of the circumference to be drawn
     * @param y y position relative to the center of the screen
     */
    public void setOffsetY (float y)
    {
        this.offsetY = y;
    }
}
