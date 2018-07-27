package com.example.wdgk.musicbird.SurfaceView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.wdgk.musicbird.GameActivity;
import com.example.wdgk.musicbird.Objects.Background;
import com.example.wdgk.musicbird.Objects.Bound;
import com.example.wdgk.musicbird.Objects.Bird;
import com.example.wdgk.musicbird.Interface.EndofGame;
import com.example.wdgk.musicbird.Objects.Scroe;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wdgk on 2017/12/26.
 */

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    //是否继续运行
    public  static boolean flag=true;
    //实例化
    //private List<Integer> myMusic = new ArrayList<>(); //歌曲
    public String url="";
    //public MediaPlayer bound = new MediaPlayer();
    public MediaPlayer bgm = new MediaPlayer();
    SurfaceHolder holder;
    private Paint paint;
    private Paint paint_text;
    private boolean isRunning;
    private int add_score=0;
    Canvas canvas;
    private Background background;
    private Bird bird;
    private Scroe s_score;
    Context context;
    private Bound bound_0,bound_1,bound_2,bound_3,bound_4,bound_5,bound_6,bound_7;
    private int count1=0;
    private Bitmap bitmap;
    int width3,height3;
    int score=0;
    private boolean first = true;
    private boolean pause = false;
    Timer timer;
    static int voice_length;
    static int data_s=0;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MediaPlayer mp = new MediaPlayer();
    private Visualizer visualizer;
    private int lvar = 0;
    private Thread fakebgm,realbgm;

    TimerTask task = new TimerTask(){
        public void run() {
            score++;
        }
    };

    //BGM启动时机
    public boolean startBGM(Bound bound){
        if(bound.getX()<=250&&bound.getX()>=200){
            return true;
        }
        return false;
    };

    public void setBGM(String url){
        this.url = url;
        try{
            Log.e("333","set ? bgm");
            bgm.setDataSource(url);
            bgm.setLooping(false);
            //bgm.setVolume(1,1);
            bgm.setVolume(1,1);
            bgm.prepare();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //释放BGM内存
    public void releaseMediaPlayer(){
        bgm.release();
    }

    public void stopMediaPlayer(){
        bgm.stop();
    }

    public boolean getIsrunning(){
        return isRunning;
    }
    public void setIsrunning(boolean i){
        isRunning=i;
    }
    public int getScore() {
        return score;
    }
    public static boolean isFlag() {
        return flag;
    }
    public static void setFlag(boolean flag) {
        GameSurface.flag = flag;
    }
    protected EndofGame mOnEndOfGame ;
    //callback interface
    public void setOnEndOfGame(EndofGame xOnEndOfGame){
        mOnEndOfGame = xOnEndOfGame;
    }
    //release interface
    //public void releaseInterface(){mOnEndOfGame = null;}
    //构造器
    public GameSurface(Context context, AttributeSet attrs) {
        super(context,attrs);
        //屏幕高度宽度
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float density1 = dm.density;
        width3 = dm.widthPixels;
        height3 = dm.heightPixels;

        holder=getHolder();
        holder.addCallback(this);
        paint = new Paint();
        paint_text = new Paint();
        paint_text.setStrokeWidth(10);
        paint_text.setTextSize(80);
        paint_text.setColor(Color.BLACK);
        bird = new Bird(context);
        bird.setScreen(width3,height3); //设置屏幕的高度宽度
        background = new Background(context);
        background.setScreen(width3,height3);
        this.context=context;

        bound_0=new Bound(context);
        bound_0.setScreen(width3,height3); //设置屏幕的高度宽度

        bound_1=new Bound(context);
        bound_1.setScreen(width3,height3); //设置屏幕的高度宽度

        bound_2=new Bound(context);
        bound_2.setScreen(width3,height3); //设置屏幕的高度宽度

        bound_3=new Bound(context);
        bound_3.setScreen(width3,height3); //设置屏幕的高度宽度

        bound_4=new Bound(context);
        bound_4.setScreen(width3,height3); //设置屏幕的高度宽度

        bound_5=new Bound(context);
        bound_5.setScreen(width3,height3); //设置屏幕的高度宽度

        bound_6=new Bound(context);
        bound_6.setScreen(width3,height3); //设置屏幕的高度宽度

        bound_7=new Bound(context);
        bound_7.setScreen(width3,height3); //设置屏幕的高度宽度

        s_score = new Scroe(context);
        s_score.setScreen(width3,height3);

        //设置障碍物初始位置
        bound_0.setX(width3/2+width3);
        bound_1.setX(width3/2+width3/6+width3);
        bound_2.setX(width3/2+2*width3/6+width3);
        bound_3.setX(width3/2+3*width3/6+width3);
        bound_4.setX(width3/2+4*width3/6+width3);
        bound_5.setX(width3/2+5*width3/6+width3);
        bound_6.setX(width3/2+6*width3/6+width3);
        bound_7.setX(width3/2+7*width3/6+width3);

        timer = new Timer(true);
        timer.schedule(task,1000, 1000);

        //歌曲结束，游戏胜利
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //gameover
                if(mp.isPlaying()){
                    mp.stop();
                }
                mp.release();
                mediaPlayer.release();
                mOnEndOfGame.onEndOfGame(2); //2代表胜利
            }
        });
    }

    //重写sufaceChanged函数，当大小变化时触发(不用)
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}
    //Create函数
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        isRunning=true;
        flag=true;

        try {
            mp.setDataSource(url);
            mp.setLooping(false);
            mp.setVolume((float)1,(float)1);
            mp.prepare();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(false);
            mediaPlayer.setVolume(0.001f,0.001f);
            mediaPlayer.prepare();
            mediaPlayer.start();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Runnable BGM = new Runnable() {
            @Override
            public void run() {
                try {
                    visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
                    visualizer.setCaptureSize(1024);
                    visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                        @Override
                        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) { }

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
                    },Visualizer.getMaxCaptureRate()/2,false, true);
                    visualizer.setEnabled(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
//        Runnable realBGM = new Runnable() {
//            @Override
//            public void run() {
//                mp = new MediaPlayer();
//                try {
//                    mp.setDataSource(url);
//                    mp.setLooping(false);
//                    mp.setVolume((float)1,(float)1);
//                    mp.prepare();
//                    Thread.sleep(1500);
//
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
        fakebgm = new Thread(BGM);
        //realbgm = new Thread(realBGM);
        fakebgm.start();
        //realbgm.start();
        new startThread().start();
    }
    //注销
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        mp.release();
        mediaPlayer.release();
        isRunning=false;
        flag=true;
    }
    //绘图需要在新线程执行，避免和UI中的点击事件冲突
    class startThread extends Thread{
        @Override
        public void run() {
            while (flag){
                while(isRunning){
                    canvas = holder.lockCanvas();
                    if (canvas == null)
                        continue;
                    canvas.drawColor(0xFFFFFFFF);
                    all_draw(canvas);
                    faill(canvas, paint, flag);
                    logic();
                    if(startBGM(bound_0)&&first){
                        Log.e("123","begin");
                        mp.start();
                        first = false;
                    }
                    if(!mp.isPlaying()&&!first){
                        mp.start();
                    }
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas);
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!isRunning){
                    try{
                        mp.pause();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    //三个元素的draw函数
    public void all_draw(Canvas canvas) {
        //super.draw(canvas);
        background.draw(canvas, paint);
        bird.draw(canvas, paint, count1);
        count1++;

        bound_0.draw(canvas, paint);
        bound_1.draw(canvas, paint);
        bound_2.draw(canvas, paint);
        bound_3.draw(canvas, paint);
        bound_4.draw(canvas, paint);
        bound_5.draw(canvas, paint);
        bound_6.draw(canvas, paint);
        bound_7.draw(canvas, paint);

        s_score.draw(canvas, paint_text);
        if (bound_0.isCollection(bird) || bound_1.isCollection(bird) || bound_2.isCollection(bird) || bound_3.isCollection(bird)
                || bound_4.isCollection(bird)|| bound_5.isCollection(bird)|| bound_6.isCollection(bird)|| bound_7.isCollection(bird)) {
            bird.setFlag(false);
            background.setFlag(false);
            bound_0.setFlag(false);
            bound_1.setFlag(false);
            bound_2.setFlag(false);
            bound_3.setFlag(false);
            bound_4.setFlag(false);
            bound_5.setFlag(false);
            bound_6.setFlag(false);
            bound_7.setFlag(false);
            flag = false;
            {
                mediaPlayer.stop();
                mp.stop();
                try {
                    mediaPlayer.prepare();
                    mp.prepare();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                visualizer.setEnabled(false);

            }
        }
        else{
            s_score.setScore(score); //计分
        }
    }
    //三个元素的位置逻辑
    public void logic(){
        background.logic();
        bird.logic();

        bound_0.logic(context,data_s);
        bound_1.logic(context,data_s);
        bound_2.logic(context,data_s);
        bound_3.logic(context,data_s);
        bound_4.logic(context,data_s);
        bound_5.logic(context,data_s);
        bound_6.logic(context,data_s);
        bound_7.logic(context,data_s);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(6);
        canvas.drawLine(0,height3/2+401,width3,height3/2+401,paint);
    }

    //点击事件
    public boolean onTouchEvent(MotionEvent event) {
        bird.move();
        return true;
    }
    //游戏结束/失败
    public void faill(Canvas canvas, Paint paint, Boolean flag){
        if(!flag) {
            //gameover
            mOnEndOfGame.onEndOfGame(1); //1代表失败
        }
    }


}
