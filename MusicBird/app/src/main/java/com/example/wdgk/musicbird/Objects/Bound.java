package com.example.wdgk.musicbird.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.Log;

import com.example.wdgk.musicbird.GameActivity;
import com.example.wdgk.musicbird.R;
import com.example.wdgk.musicbird.SurfaceView.GameSurface;

import java.io.IOException;

/**
 * Created by wdgk on 2017/12/26.
 */

public class Bound {
    public MediaPlayer mediaPlayer;
    private int x;
    private int rand_down=0;
    private int speed=35;
    private Bitmap bound_down;
    private Bitmap bound_down_g;
    private Bitmap bound_down_res;
    private Bitmap bound_up;
    private boolean flag=true;
    private int screen_wid;
    private int screen_hei;
    private int count=0;
    private int changedBound;  //障碍物边界
    private boolean glass=false;

    static boolean complete_flag;
    public Visualizer visualizer;
    static int data_s=0;
    static int voice_length;

    public int getData_s() {return data_s;}

    public void setData_s(int data) {data_s = data;}
    //释放内存
    public void releaseMediaPlayer(){
        mediaPlayer.release();
    }
    //初始化音乐
    public void setMediaPlayer(String url){
        try{
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);
            mediaPlayer.setVolume((float)0.01,(float)0.01);
            visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            System.out.println("here");
            visualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        @Override
                        public void onWaveFormDataCapture(Visualizer visualizer,
                                                          byte[] waveform, int samplingRate) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onFftDataCapture(Visualizer arg0, byte[] fft,
                                                     int arg2) {//返回的经过FFT处理后的数据
                            // TODO Auto-generated method stub
                            try {
                                int sum=0;
                                for(int i=0;i<512;i++){
                                    //if(waveform[i]>0)sum++;
                                    sum+=fft[i];
                                }
                                if(sum>voice_length){//这里修改判断条件
                                    System.out.println("height:");
                                    if(data_s<8){
                                        data_s++;
                                    }else{
                                        data_s=8;
                                    }
                                }else{
                                    System.out.println("     ");
                                    if(data_s>0){
                                        data_s--;
                                    }else{
                                        data_s=0;
                                    }
                                }
                                voice_length=sum;
                                System.out.println(data_s);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, false, true);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    visualizer.setEnabled(false);
                    complete_flag=true;
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }//gai

    public  boolean isP(){
        if(mediaPlayer.isPlaying()){
            return true;
        }
        return false;
    }

    public void startMediaPlayer(){
        try{
            mediaPlayer.start();
            visualizer.setEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void stopMediaPlayer(){
        try{
            mediaPlayer.stop();
            mediaPlayer.release();
            visualizer.setEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void pauseMediaPlayer(){
        try{
            mediaPlayer.pause();
            visualizer.setEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //获取屏幕高度宽度
    public void setScreen(int wid,int hei){
        this.screen_wid=wid;
        this.screen_hei=hei;
    }
    public int getSpeed(){
        return speed;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public Bound(Context context) {
        super();
        bound_down = BitmapFactory.decodeResource(context.getResources(), R.drawable.tree);
        bound_up = BitmapFactory.decodeResource(context.getResources(), R.drawable.bound_down);
        bound_down_g = BitmapFactory.decodeResource(context.getResources(), R.drawable.glass);
        bound_down_res = BitmapFactory.decodeResource(context.getResources(), R.drawable.xianrenzhang);
    }
    //x位置需要移动
    public int getWidth() {
        return bound_down.getWidth();
    }
    public int getHeight() {
        return bound_down.getHeight();
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }

    //画上下边界
    public void draw(Canvas canvas, Paint paint){
        Rect mSrcRect,mDestRect;
        //    isdown=0;
        //下边界
        changedBound = screen_hei/2+400-rand_down;
        mSrcRect = new Rect(0, 0, bound_down_res.getWidth(), bound_down_res.getHeight());
        mDestRect = new Rect(x,changedBound ,x+screen_wid/6, screen_hei/2+400);
        //isdown=1;
        canvas.drawBitmap(bound_down_res, mSrcRect, mDestRect, paint);
        //上边界
        mSrcRect = new Rect(0, 0, bound_up.getWidth(),bound_up.getHeight());
        mDestRect = new Rect(x, 0,x+screen_wid/6, changedBound-1000);
        canvas.drawBitmap(bound_up, mSrcRect, mDestRect, paint);
    }
    //设置边界高度
    public void setY(int rand) {
        this.rand_down=rand;
    }

    //边界位置逻辑（结合音频）
    public void logic(Context context,int i){
        if(flag){
            x-=speed;
            if(x<=-bound_down.getWidth()){
                count++;
                x=screen_wid-speed;
                //根据音乐生成障碍
                setY(i*55);
            }
        }
    }
    //碰撞检测
    public boolean isCollection(Bird bird){
        int easy = 30;//放宽碰撞条件
        int left=200;
        int right=200+bird.getBitmap().getWidth();
        int top=bird.getY();
        int bottom=bird.getY()+bird.getBitmap().getHeight();
        if(bottom>=screen_hei/2+400){
            return false;
        }
        if (right < x+ easy ){
            return false;
        }
        if (x + bound_down.getWidth() - easy < left){
            return false;
        }
        if (bottom < changedBound + easy && top > changedBound-1000 - easy){
            return false;
        }
        return true;
    }
}
