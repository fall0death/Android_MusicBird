package com.example.wdgk.musicbird;;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.audiofx.*;
import android.widget.Toast;

import com.example.wdgk.musicbird.DataBase.myDB;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicProvider extends AppCompatActivity {
    private List<Song> list;
    private List<byte[]> bl;
    private String final_res;
    private myDB db = new myDB(this);
    private StringBuilder myres = new StringBuilder();
    private static MediaPlayer mediaPlayer;
    private Visualizer visualizer;

    private String[] color = {"#FF8A80","#FF80AB","#EA80FC","#B388FF","#8C9EFF","#82B1FF","#80D8FF","#84FFFF","#A7FFEB","#B9F6CA","#CCFF90","#F4FF81","#FFFF8D","#FFE57F","#FFD180","#FF9E80"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        getWindow().setTitle("");
        setContentView(linearLayout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mediaPlayer = new MediaPlayer();

        bl = new ArrayList<byte[]>();


        CardView cardView = new CardView(this);


        final Button button = new Button(this);
        button.setBackgroundColor(Color.parseColor("#76FF03"));
        button.setText("选择你所要添加的歌曲");
        button.setTextColor(Color.parseColor("#ffffff"));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        cardView.addView(layout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20,0,20,0);

        linearLayout.addView(button,lp);

        //linearLayout.addView(cardView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 512));

        RecyclerView recyclerView = new RecyclerView(this);
        linearLayout.addView(recyclerView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerView.Adapter<VH>() {
            @Override
            public VH onCreateViewHolder(ViewGroup parent, int viewType) {
                CardView view = new CardView(parent.getContext());
                view.setCardElevation(16);
                view.setRadius(16);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(30,30,30,30);
                view.setLayoutParams(lp);

                return new VH(view);
            }

            @Override
            public void onBindViewHolder(final VH holder, final int position) {
                holder.textView.setText(list.get(position).getFileName());
                CardView cv = (CardView)holder.itemView;
                cv.setCardBackgroundColor(Color.parseColor(color[position%16]));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mediaPlayer.setDataSource(list.get(position).getFileUrl());
                            mediaPlayer.setLooping(false);
                            mediaPlayer.prepare();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        button.setText("add music");
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*
                                if (button.getText().toString().equals("add music")) {
                                    mediaPlayer.start();
                                    visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
                                    visualizer.setCaptureSize(1 );
                                    mediaPlayer.setVolume(1,1);
                                    visualizer.setEnabled(true);
                                    mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                                        @Override
                                        public void onSeekComplete(MediaPlayer mp) {
                                            if (mp.getCurrentPosition() + 500 < mp.getDuration()) {
                                                Runnable getSearchResult = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            button.setText(" " + (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()));
                                                               byte[] w = new byte[1024];
                                                            visualizer.getWaveForm(w);
                                                            bl.add(w);
                                                            Arrays.sort(w);
                                                            myres.append((int)w[0]/127*8);
                                                            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 500);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };
                                                new Thread(getSearchResult).start();
                                            }

                                            else{
                                                //button.setText("complete");
                                                //final_res = myres.toString();

                                            }
                                        }
                                    });
                                    mediaPlayer.seekTo(500);
                                    button.setText("pause");
                                }
                                else {
                                    //visualizer.setEnabled(false);
                                    mediaPlayer.stop();
                                    try{
                                        mediaPlayer.prepare();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    button.setText("play");
                                }
                                */
                                try{
                                    db.insert(holder.textView.getText().toString(),"OK",list.get(position).getFileUrl());  //存到数据库中
                                    Toast.makeText(MusicProvider.this,"add successfully",Toast.LENGTH_SHORT).show();
                                    button.setText("选择你所要添加的歌曲");
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(MusicProvider.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
        list = getAllSongs(this);
        recyclerView.getAdapter().notifyDataSetChanged();


    }

    public class VH extends RecyclerView.ViewHolder {
        public TextView textView;
        public VH (View view) {
            super(view);
            textView = new TextView(view.getContext());
            textView.setText("hello world");
            CardView layout = (CardView)view;
            CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10,20,10,20);
            layout.addView(textView,layoutParams);
        }
    }

    public class Song {

        private String fileName;
        private String title;
        private int duration;
        private String singer;
        private String album;
        private String year;
        private String type;
        private String size;
        private String fileUrl;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public Song() {
            super();
        }

        public Song(String fileName, String title, int duration, String singer,
                    String album, String year, String type, String size, String fileUrl) {
            super();
            this.fileName = fileName;
            this.title = title;
            this.duration = duration;
            this.singer = singer;
            this.album = album;
            this.year = year;
            this.type = type;
            this.size = size;
            this.fileUrl = fileUrl;
        }

        @Override
        public String toString() {
            return "Song [fileName=" + fileName + ", title=" + title
                    + ", duration=" + duration + ", singer=" + singer + ", album="
                    + album + ", year=" + year + ", type=" + type + ", size="
                    + size + ", fileUrl=" + fileUrl + "]";
        }

    }

    public ArrayList<Song> getAllSongs(Context context) {

        ArrayList<Song> songs = null;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA },
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);

        songs = new ArrayList<Song>();

        if (cursor.moveToFirst()) {

            Song song = null;

            do {
                song = new Song();
                // 文件名
                song.setFileName(cursor.getString(1));
                // 歌曲名
                song.setTitle(cursor.getString(2));
                // 时长
                song.setDuration(cursor.getInt(3));
                // 歌手名
                song.setSinger(cursor.getString(4));
                // 专辑名
                song.setAlbum(cursor.getString(5));
                // 年代
                if (cursor.getString(6) != null) {
                    song.setYear(cursor.getString(6));
                } else {
                    song.setYear("未知");
                }
                // 歌曲格式
                if ("audio/mpeg".equals(cursor.getString(7).trim())) {
                    song.setType("mp3");
                } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
                    song.setType("wma");
                }
                // 文件大小
                if (cursor.getString(8) != null) {
                    float size = cursor.getInt(8) / 1024f / 1024f;
                    song.setSize((size + "").substring(0, 4) + "M");
                } else {
                    song.setSize("未知");
                }
                // 文件路径
                if (cursor.getString(9) != null) {
                    song.setFileUrl(cursor.getString(9));
                }
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();

        }
        return songs;
    }

}
