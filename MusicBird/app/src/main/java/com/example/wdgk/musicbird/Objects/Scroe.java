package com.example.wdgk.musicbird.Objects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.wdgk.musicbird.R;

/**
 * Created by wdgk on 2017/12/30.
 */

public class Scroe {
    private int score;
    private String ss=score+"";
    private int screen_w;
    private int screen_h;
    public Scroe(Context context) {
        super();
    }

    public void setScreen(int w,int h){
        screen_h=h;
        screen_w=w;
    }
    public void setScore(int s){
        score=s;
        ss=score+"";
    }
    public void draw(Canvas canvas, Paint paint){
        canvas.drawText("Score："+ss,200,screen_h-250,paint);
    }
}
