package com.example.wdgk.musicbird;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wdgk.musicbird.DataBase.myDB;
import com.example.wdgk.musicbird.Interface.EndofGame;
import com.example.wdgk.musicbird.SurfaceView.GameSurface;
import com.example.wdgk.musicbird.R;

import java.io.IOException;

/**
 * Created by wdgk on 2017/12/26.
 */

public class GameActivity extends AppCompatActivity implements EndofGame {
    int score;
    ImageView pause;
    GameSurface surface;

    @Override
    public void onEndOfGame(int result) {
        Intent intent = getIntent();
        intent.putExtra("Score",surface.getScore());
        setResult(result,intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.gameactivity);
        surface = (GameSurface)findViewById(R.id.GameSurface);
        pause = (ImageView)findViewById(R.id.pause);

        //设置音频
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        surface.setBGM(url);
        Toast.makeText(GameActivity.this,url,Toast.LENGTH_SHORT).show();
        surface.url = url;



        //暂停按钮
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(surface.getIsrunning()){
                    surface.setIsrunning(false);
                    pause.setImageResource(R.drawable.pause2);
                }
                else if(!surface.getIsrunning()){
                    surface.setIsrunning(true);
                    pause.setImageResource(R.drawable.pause1);
                }
            }
        });
        surface.setOnEndOfGame(this);//传入this，设定自己为回调目标

    }
}
