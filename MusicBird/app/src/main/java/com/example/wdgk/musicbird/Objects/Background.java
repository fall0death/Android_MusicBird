package com.example.wdgk.musicbird.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.wdgk.musicbird.R;

/**
 * Created by wdgk on 2017/12/26.
 */

public class Background {
    private int x = 0, x2 = 0,begin = 0;
    private int width=0,height=0;
    private int speed;
    Context context;
    private Bitmap bitmap;
    private Bitmap ground;
    private Bitmap ground_b;
    private Rect mSrcRect,mDestRect;
    private boolean flag = true;
    public Background(Context context) {
        super();
        this.context = context;
        speed = 45;
        bitmap = new BitmapFactory().decodeResource(context.getResources(),
                R.drawable.game_background);
        ground = new BitmapFactory().decodeResource(context.getResources(),
                R.drawable.sand);
        ground_b = new BitmapFactory().decodeResource(context.getResources(),
                R.drawable.sand);
    }
    public void setScreen(int w,int h){
        width = w;
        height = h;
        x2 = w;
        x = h;
    }
    public void draw(Canvas canvas, Paint paint) {
        mSrcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        mDestRect = new Rect(0, 0, width, height/2+400);
        canvas.drawBitmap(bitmap, mSrcRect, mDestRect, paint);
        mSrcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        mDestRect = new Rect(begin, x/2+400,begin+width, x);
        canvas.drawBitmap(ground, mSrcRect, mDestRect, paint);
        mSrcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        mDestRect = new Rect(x2, x/2+400,width+x2, x);
        canvas.drawBitmap(ground_b, mSrcRect, mDestRect, paint);
    }
    public void logic() {
        if (flag) {
            x2 -= speed;
            begin -= speed;
            if (x2 < -width) {
                x2 = width-speed;
            }
            if (begin < -width) {
                begin = width-speed;
            }
        }
    }
    public boolean isFlag() {
        return flag;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
