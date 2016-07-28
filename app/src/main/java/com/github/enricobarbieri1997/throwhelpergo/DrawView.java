package com.github.enricobarbieri1997.throwhelpergo;

/**
 * Created by Xxenr on 28/07/2016.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.view.View;

public class DrawView extends View {
    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        float height = canvas.getHeight();
        float width = canvas.getWidth();
        canvas.drawLine(width/2,0,width/2,height,paint);
    }

}
