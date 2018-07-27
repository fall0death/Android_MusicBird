package com.example.wdgk.musicbird.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.wdgk.musicbird.R;

/**
 * Created by wdgk on 2017/12/26.
 */

public class Bird {
    private int x=200,y=600,speed=0;
    Context context;
    private Bitmap bitmap,bitmap2,bitmap3;
    private boolean flag=true;
    private int screen_wid;
    private int screen_hei;
    private boolean ground=true;

    public void setScreen(int wid,int hei){
        this.screen_wid=wid;
        this.screen_hei=hei;
    }

    public Bird(Context context) {
        super();
        this.context=context;
        bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.fly_bird_0);
        bitmap2= BitmapFactory.decodeResource(context.getResources(), R.drawable.fly_bird_1);
        bitmap3= BitmapFactory.decodeResource(context.getResources(), R.drawable.fly_bird_2);
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public void draw(Canvas canvas, Paint paint, int count1){
        //canvas.drawBitmap(bitmap, 200, y, paint);
        switch(count1%3){
            case 0:
                if(!ground){
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fly_bird_0);
                }
                else{
                    bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.w_bird_0);
                }
                canvas.drawBitmap(bitmap, 200, y, paint);
                break;
            case 1:
                if(!ground){
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fly_bird_1);
                }
                else{
                    bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.w_bird_1);
                }
                canvas.drawBitmap(bitmap, 200, y, paint);
                break;
            case 2:
                if(!ground){
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fly_bird_2);
                }
                else{
                    bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.w_bird_2);
                }
                canvas.drawBitmap(bitmap, 200, y, paint);
        }
    }

    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getWidth(){return bitmap.getWidth();}
    public int getHeight(){return bitmap.getHeight();}
    public void logic(){
        if(flag){
            y+=speed;
            speed+=7;
            if(y-(screen_hei/2+400-bitmap.getHeight())>=5){
                y=screen_hei/2+400-bitmap.getHeight()-5;
                ground=true;  //落地
            }
            if(y<0){
                y=0;
            }
        }
        else{
            //gameover
            bitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.dead_bird_2);
            bitmap2= BitmapFactory.decodeResource(context.getResources(), R.drawable.dead_bird_2);
            bitmap3= BitmapFactory.decodeResource(context.getResources(), R.drawable.dead_bird_2);
        }
    }
    public boolean isFlag() {
        return flag;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public void move(){
        if(ground){  //起飞
            speed=-20;
            ground=false;
        }
        else{         //续航
            speed=-20;
        }
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
}
