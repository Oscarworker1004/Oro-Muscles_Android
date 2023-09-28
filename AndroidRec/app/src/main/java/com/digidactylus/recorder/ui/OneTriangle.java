package com.digidactylus.recorder.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.digidactylus.recorder.R;

public class OneTriangle extends OneBase {

    private Bitmap triangleBitmap;
    Context context;

    public OneTriangle(Context context) {
        this.context = context;
        //triangleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trianglefg);
    }

    public void doDraw(Canvas canvas, DataBuffer buf) {
        super.doDrawBase(canvas);
        triangleBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.trianglefg), canvas.getWidth(), canvas.getHeight(), false);
        Paint redPaintT = new Paint();
        redPaintT.setColor(Color.rgb(255, 120, 0));

        Paint greenPaintT = new Paint();
        greenPaintT.setColor(Color.rgb(30, 255, 42));

        Paint bluePaintT = new Paint();
        bluePaintT.setColor(Color.rgb(90, 90, 255));

        Paint whitePaintT = new Paint();
        whitePaintT.setColor(Color.rgb(255, 255, 255));

        Triangle t = buf.getTriangle(buf.size() - 2);
        int s = 17;
        float ts = 100;

        // Calculate the center of the canvas
        float centerX = canvas.getWidth() / 2f;
        float centerY = canvas.getHeight() / 2f;

        // Calculate the starting position for drawing the triangle
        float x0 = centerX - (m_w / 2) + ts * (float) t.cx();
        float y0 = centerY - (m_h / 3) + ts * (float) t.cy();

        greenPaintT.setStrokeWidth(1 + (float) t.line() / 10);
        redPaintT.setStrokeWidth(1 + (float) t.line() / 10);
        whitePaintT.setStrokeWidth(1 + (float) t.line() / 10);

        // Draw the PNG triangle at appropriate positions
        drawRotatedBitmap(canvas, triangleBitmap, x0, y0, x0 + ts * (float) t.bx(), y0 + ts * (float) t.by(), whitePaintT);
    }


    // This method rotates and draws the bitmap on the canvas
    private void drawRotatedBitmap(Canvas canvas, Bitmap bitmap, float x1, float y1, float x2, float y2, Paint paint) {
        float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        float midX = (x1 + x2) / 2;
        float midY = (y1 + y2) / 2;

        canvas.save();
        canvas.rotate(angle, midX, midY);
        canvas.drawBitmap(bitmap, midX - bitmap.getWidth() / 2, midY - bitmap.getHeight() / 2, paint);
        canvas.restore();
    }
}
