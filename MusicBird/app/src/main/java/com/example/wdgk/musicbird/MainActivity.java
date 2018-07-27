package com.example.wdgk.musicbird;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.example.wdgk.musicbird.DataBase.myDB;
import com.example.wdgk.musicbird.Objects.Scroe;
import com.example.wdgk.musicbird.R;
import com.example.wdgk.musicbird.Socket.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.BatchUpdateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> ListView_data;
    private ListView mListView;
    private myDB mydb = new myDB(this);
    private Button start, exit, login;
    private ImageView back, libary, map, friend;
    private TextView title, Score;
    private TextView welcome;
    private ListView listView;
    private LinearLayout linearLayout;
    private Typeface typeface;
    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;
    private RelativeLayout main;
    private List<msg> msgs;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private SocThread socketThread;
    private boolean socketmode = false;
    private RecyclerView recyclerView;
    Handler mhandler;
    Handler mhandlerSend;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && resultCode == 1) {
            int score = intent.getIntExtra("Score", 0);
            title.setTextColor(Color.RED);
            title.setText("You_Lose");
            Score.setText("Your score:" + score);
            animationDrawable.stop();
            back.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            map.setVisibility(View.GONE);
            welcome.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            libary.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            exit.setText("menu");
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                    title.setTextColor(Color.BLACK);
                    title.setText("Music_Bird");
                    animationDrawable.start();
                    start.setVisibility(View.VISIBLE);
                    Score.setText("");
                    libary.setVisibility(View.VISIBLE);
                    map.setVisibility(View.VISIBLE);
                    login.setVisibility(View.VISIBLE);
                    final SharedPreferences sharedPreferences = getSharedPreferences("Bird_login",Context.MODE_PRIVATE);
                    if(sharedPreferences.getBoolean("state",false)){
                        welcome.setVisibility(View.VISIBLE);
                        login.setText("Logout");
                    }else{
                        login.setText("Login");
                    }
                    start.setText("play");
                    exit.setText("exit");
                }
            });
        } else if (resultCode == 2) {
            int score = intent.getIntExtra("Score", 0);
            title.setTextColor(Color.GREEN);
            title.setText("You_Win");
            Score.setText("Your_score:" + score);
            start.setVisibility(View.GONE);
            map.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            animationDrawable.stop();
            linearLayout.setVisibility(View.VISIBLE);
            libary.setVisibility(View.GONE);
            welcome.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            exit.setText("menu");
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                    title.setTextColor(Color.BLACK);
                    back.setVisibility(View.GONE);
                    title.setText("Music_Bird");
                    animationDrawable.start();
                    Score.setText("");
                    map.setVisibility(View.VISIBLE);
                    libary.setVisibility(View.VISIBLE);
                    login.setVisibility(View.VISIBLE);
                    final SharedPreferences sharedPreferences = getSharedPreferences("Bird_login",Context.MODE_PRIVATE);
                    if(sharedPreferences.getBoolean("state",false)){
                        welcome.setVisibility(View.VISIBLE);
                        login.setText("Logout");
                    }else{
                        login.setText("Login");
                    }
                    start.setVisibility(View.VISIBLE);
                    start.setText("play");
                    exit.setText("exit");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            List<String> listRequest = new ArrayList<>();
            //权限申请
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                listRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                listRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                listRequest.add(Manifest.permission.READ_PHONE_STATE);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                listRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                listRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                listRequest.add(Manifest.permission.RECORD_AUDIO);
            if (!listRequest.isEmpty()) {
                String[] permissions = listRequest.toArray(new String[listRequest.size()]);
                ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
            }
        }
        setContentView(R.layout.activity_main);
        back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.GONE);
        exit = (Button) findViewById(R.id.exit);
        login = (Button) findViewById(R.id.login);
        start = (Button) findViewById(R.id.start_game);
        main = (RelativeLayout) findViewById(R.id.main);
        title = (TextView) findViewById(R.id.title);
        title.setTextColor(Color.BLACK);


        welcome = (TextView) findViewById(R.id.welcome);
        welcome.setVisibility(View.GONE);
        welcome.setTypeface(typeface);

        final SharedPreferences sharedPreferences = getSharedPreferences("Bird_login",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        login.setVisibility(View.VISIBLE);
        if(!sharedPreferences.contains("state")){
            editor.putBoolean("state",false);
            editor.putString("id",null);
            editor.putString("name",null);
            welcome.setVisibility(View.GONE);
            login.setText("Login");
            editor.commit();
        }else{
            if(sharedPreferences.getBoolean("state",false)){
                welcome.setText("welcome," + sharedPreferences.getString("name",""));
                welcome.setVisibility(View.VISIBLE);
                login.setText("Logout");
            }else{
                welcome.setVisibility(View.GONE);
                login.setText("Login");
            }
        }
        final String id = sharedPreferences.getString("id",null);

        friend = (ImageView) findViewById(R.id.friend);
        friend.setVisibility(View.VISIBLE);
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_chat);
                recyclerView = (RecyclerView) dialog.findViewById(R.id.rv);
                recyclerView.setAdapter(new RecyclerView.Adapter<VH>() {
                    @Override
                    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
                        FrameLayout frameLayout = new FrameLayout(parent.getContext());
                        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        return new VH(frameLayout);
                    }

                    @Override
                    public void onBindViewHolder(VH holder, int position) {
                        if (msgs.get(position).o) {
                            FrameLayout.LayoutParams dd = (FrameLayout.LayoutParams) holder.cardView.getLayoutParams();
                            dd.gravity = Gravity.RIGHT;
                            holder.cardView.setLayoutParams(dd);
                            holder.msg.setGravity(Gravity.RIGHT);
                            holder.dat.setText("To: " + msgs.get(position).f + " " + msgs.get(position).t);
                            holder.msg.setText(msgs.get(position).m);
                        } else {
                            FrameLayout.LayoutParams dd = (FrameLayout.LayoutParams) holder.cardView.getLayoutParams();
                            dd.gravity = Gravity.LEFT;
                            holder.cardView.setLayoutParams(dd);
                            holder.msg.setGravity(Gravity.RIGHT);
                            holder.dat.setText("From: " + msgs.get(position).f + " " + msgs.get(position).t);
                            holder.msg.setText(msgs.get(position).m);
                        }
                    }

                    @Override
                    public int getItemCount() {
                        return msgs.size();
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                Button button = (Button)dialog.findViewById(R.id.send);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText t = (EditText)dialog.findViewById(R.id.toid);
                        EditText ms = (EditText)dialog.findViewById(R.id.msg);
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String date = sDateFormat.format(new java.util.Date());
                        sendMessage(t.getText().toString(),ms.getText().toString(),date,id);
                    }
                });
                dialog.show();
            }
        });
        if (msgs == null) msgs = new ArrayList<msg>();

        if (socketmode) {
            mhandler = new RecHandler();
            mhandlerSend = new SenHandler();
            startSocket();
        } else {
            if (id != null) {
                startGetMessage(id);
            }
        }


        map = (ImageView) findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, MapActivity.class);
                startActivity(it);
            }
        });
        //自定义字体
        typeface = Typeface.createFromAsset(getAssets(), "BitMicro01.ttf");
        title.setTypeface(typeface);
        start.setTypeface(typeface);
        exit.setTypeface(typeface);
        login.setTypeface(typeface);
        welcome.setTypeface(typeface);

        relativeLayout = (RelativeLayout) findViewById(R.id.main);
        relativeLayout.setBackgroundResource(R.drawable.anim);
        relativeLayout.getBackground().setAlpha(200);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.start();

        Score = (TextView) findViewById(R.id.score);
        libary = (ImageView) findViewById(R.id.viewmusic);
        listView = (ListView) findViewById(R.id.list);
        listView.setVisibility(View.GONE);
        linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
        ListView_data = new ArrayList<>();
        simpleAdapter = new SimpleAdapter(this, ListView_data, R.layout.listview,
                new String[]{"name"},
                new int[]{R.id.name});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    String s = ListView_data.get(position).get("url").toString();  //url
                    intent.putExtra("url", s);
                    startActivityForResult(intent, 1);
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                if (position > 0) {
                    String s = ListView_data.get(position).get("name").toString();  //url
                    ListView_data.remove(position);
                    mydb.delete(s);
                    simpleAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView_data.clear();
                Map<String, Object> temp = new LinkedHashMap<>();
                temp.put("name", "name");
                temp.put("data", "none");
                temp.put("url", "none");
                ListView_data.add(temp);
                Cursor cursor = mydb.query();
                while (cursor.moveToNext()) {
                    int q = cursor.getColumnIndex("name");
                    int p = cursor.getColumnIndex("final_res");
                    int k = cursor.getColumnIndex("url");
                    String song_name = cursor.getString(q);
                    String final_res = cursor.getString(p);
                    String url = cursor.getString(k);
                    temp = new LinkedHashMap<>();
                    temp.put("name", song_name);
                    temp.put("data", final_res);
                    temp.put("url", url);
                    ListView_data.add(temp);
                }
                simpleAdapter.notifyDataSetChanged();
                listView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);

            }
        });

        libary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MusicProvider.class);
                startActivity(intent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.setVisibility(View.GONE);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        //我添加的

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sharedPreferences.getBoolean("state",false)){
                    final Dialog dialog = new Dialog(MainActivity.this);
                    LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                    linearLayout.setPadding(60, 40, 60, 40);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    dialog.setContentView(linearLayout, new ViewGroup.LayoutParams(660, ViewGroup.LayoutParams.MATCH_PARENT));

                    final TextView tv = new TextView(MainActivity.this);
                    tv.setText("Login");
                    tv.setTextSize(28);
                    tv.setTextColor(Color.parseColor("#FF1744"));
                    tv.setTypeface(typeface);

                    linearLayout.addView(tv);

                    final EditText et[] = new EditText[4];

                    final LinearLayout lil = new LinearLayout(MainActivity.this);
                    lil.setOrientation(LinearLayout.VERTICAL);

                    final String ss[] = {"USERNAME:", "PASSWORD:", "PASSWORD:", "NICKNAME:"};

                    for (int i = 0; i < 2; i++) {
                        LinearLayout ll = new LinearLayout(MainActivity.this);
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        TextView tmptv = new TextView(MainActivity.this);
                        tmptv.setText(ss[i]);
                        tmptv.setTypeface(typeface);
                        ll.addView(tmptv, llp);
                        llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        llp.weight = 1;
                        et[i] = new EditText(MainActivity.this);
                        et[i].setSingleLine();
                        if (i == 1)
                            et[i].setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        ll.addView(et[i], llp);
                        lil.addView(ll);
                    }

                    linearLayout.addView(lil);

                    LinearLayout ll = new LinearLayout(MainActivity.this);
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    final Button lll, rrr;
                    lll = new Button(MainActivity.this);
                    lll.setTypeface(typeface);
                    lll.setText("go!");
                    lll.setBackgroundColor(Color.parseColor("#66FFCC"));
                    rrr = new Button(MainActivity.this);
                    rrr.setTypeface(typeface);
                    rrr.setText("regist");
                    rrr.setBackgroundColor(Color.parseColor("#66CCFF"));
                    LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lllp.weight = 1;
                    lllp.setMargins(20, 10, 30, 10);
                    ll.addView(lll, lllp);
                    LinearLayout.LayoutParams rrrp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    rrrp.weight = 1;
                    rrrp.setMargins(30, 10, 20, 10);
                    ll.addView(rrr, rrrp);
                    linearLayout.addView(ll);


                    lll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (lll.getText().toString().equals("go!")) {
                                //登录的情况
                                //et[0] 为 第一个 EditText
                                //et[1] 为 第二个 EditText
                                for (int i = 0; i < 2; i++)
                                    if (et[i].getText().toString().length() == 0) {
                                        Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                final String url = "http://118.89.40.20:10086/login?id=" + et[0].getText().toString() + "&password=" + getMD5Code(et[1].getText().toString());
                                final Boolean state = sharedPreferences.getBoolean("state",false);
                                Runnable networkTask = new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO
                                        // 在这里进行 http request.网络请求相关操作
                                        try {
                                            JSONObject jsonObject = new JSONObject(readParse(url));
                                            // 初始化map数组对象
                                            if (state == false) {
                                                if (jsonObject.getInt("status") == 1) {
                                                    System.out.println("登录");
                                                    JSONObject msg = new JSONObject(jsonObject.getString("msg"));
                                                    editor.putString("id",msg.getString("id"));
                                                    editor.putString("name",msg.getString("name"));
                                                    editor.putBoolean("state",true);
                                                    editor.commit();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dialog.cancel();
                                                            Toast.makeText(MainActivity.this, "登录成功。", Toast.LENGTH_SHORT).show();
                                                            welcome.setText("welcome," + sharedPreferences.getString("name",""));
                                                            welcome.setVisibility(View.VISIBLE);
                                                            login.setText("Logout");
                                                            startGetMessage(sharedPreferences.getString("id",""));
                                                            //welcome.setVisibility(View.VISIBLE);
                                                            //welcome.setText("Welcome,"+name".");
                                                        }
                                                    });
                                                } else {
                                                    if (jsonObject.getInt("status") == 0) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                et[1].setText("");
                                                                Toast.makeText(MainActivity.this, "密码错误。", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    } else {
                                                        System.out.println("state:-1");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                et[0].setText("");
                                                                et[1].setText("");
                                                                Toast.makeText(MainActivity.this, "用户名不存在。", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.cancel();
                                                        Toast.makeText(MainActivity.this, "已经登录过了。", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                new Thread(networkTask).start();
                            } else {
                                //注册的情况
                                //et[0] 为 第一个 EditText
                                //et[1] 为 第二个 EditText
                                //et[2] 为 第三个 EditText
                                //et[3] 为 第四个 EditText
                                for (int i = 0; i < 4; i++)
                                    if (et[i].getText().toString().length() == 0) {
                                        Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                final String url = "http://118.89.40.20:10086/regist?id=" + et[0].getText().toString() + "&password=" + getMD5Code(et[1].getText().toString()) + "&name=" + et[3].getText().toString();
                                Runnable networkTask = new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO
                                        // 在这里进行 http request.网络请求相关操作
                                        try {
                                            JSONObject jsonObject = new JSONObject(readParse(url));
                                            // 初始化map数组对象
                                            final Boolean state = sharedPreferences.getBoolean("state",false);
                                            if (state == false) {
                                                if (jsonObject.getInt("status") == 1) {
                                                    System.out.println("注册");
                                                    editor.putBoolean("state",true);
                                                    editor.putString("id",et[0].getText().toString());
                                                    editor.putString("name",et[2].getText().toString());
                                                    editor.commit();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(MainActivity.this, "注册成功。", Toast.LENGTH_SHORT).show();
                                                            welcome.setText("welcome," + sharedPreferences.getString("name",""));
                                                            welcome.setVisibility(View.VISIBLE);
                                                            login.setText("Logout");
                                                            dialog.cancel();
                                                            //welcome.setVisibility(View.VISIBLE);
                                                            //welcome.setText("Welcome,"+name".");
                                                        }
                                                    });
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            et[0].setText("");
                                                            Toast.makeText(MainActivity.this, "用户名已存在。", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                System.out.println("state:-1");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(MainActivity.this, "系统繁忙", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                if (TextUtils.equals(et[1].getText().toString(), et[2].getText().toString())) {
                                    new Thread(networkTask).start();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "两次密码输入不相同。", Toast.LENGTH_SHORT).show();
                                            //welcome.setVisibility(View.VISIBLE);
                                            //welcome.setText("Welcome,"+name".");
                                        }
                                    });
                                }
                            }
                        }
                    });

                    rrr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (rrr.getText().toString().equals("regist")) {

                                for (int i = 2; i < 4; i++) {
                                    LinearLayout ll = new LinearLayout(MainActivity.this);
                                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    TextView tmptv = new TextView(MainActivity.this);
                                    tmptv.setText(ss[i]);
                                    tmptv.setTypeface(typeface);
                                    ll.addView(tmptv, llp);
                                    llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    llp.weight = 1;
                                    et[i] = new EditText(MainActivity.this);
                                    et[i].setSingleLine();
                                    if (i == 2)
                                        et[i].setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    ll.addView(et[i], llp);
                                    lil.addView(ll);
                                }
                                et[2].setHint("请在此再次输入密码确认");
                                tv.setText("REGIST");

                                lll.setText("REGIST");
                                rrr.setText("CANCEL");
                            } else {
                                dialog.cancel();
                            }
                        }
                    });
                    dialog.show();
                }else{
                    editor.putString("id","");
                    editor.putString("name","");
                    editor.putBoolean("state",false);
                    editor.commit();
                    login.setText("Login");
                    welcome.setVisibility(View.GONE);
                }
            }
        });


        Runnable sendLocation = new Runnable() {
            @Override
            public void run() {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 100);
                    }
                    {
                        while (true) {
                            LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location == null) {
                                location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }
                            readParse("http://118.89.40.20:3001/?id=" + id + "&lnt=" + location.getLongitude() + "&lat=" + location.getLatitude());
                            Thread.sleep(5 * 60 * 1000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(sendLocation).start();

    }

    public static String readParse(String urlPath) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        String s = new String(outStream.toByteArray());
        Log.e("aa", s);
        return s;//通过out.Stream.toByteArray获取到写的数据
    }

    public String getMD5Code(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return strBuf.toString();
        } catch (Exception e) {
            // TODO: handle exception
            return "";
        }
    }

    public class msg {
        public String m;
        public String f;
        public String t;
        public boolean o;

        public msg(String mm, String ff, String tt, boolean oo) {
            m = mm;
            f = ff;
            t = tt;
            o = oo;
        }
    }

    public class VH extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView dat, msg;

        public VH(View view) {
            super(view);
            FrameLayout frameLayout = (FrameLayout) view;
            cardView = new CardView(view.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            cardView.setLayoutParams(params);
            cardView.setPadding(30,20,20,30);
            frameLayout.addView(cardView, params);

            cardView.setRadius(16);
            cardView.setCardBackgroundColor(Color.parseColor("#03A9F4"));
            LinearLayout layout = new LinearLayout(view.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(30,20,30,20);
            cardView.addView(layout,params);

            dat = new TextView(view.getContext());
            msg = new TextView(view.getContext());

            dat.setTextColor(Color.parseColor("#eeeeee"));
            dat.setTextSize(10);
            msg.setTextColor(Color.parseColor("#ffffff"));

            layout.addView(dat);
            layout.addView(msg);
        }
    }

    public class RecHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Log.i("socket", "mhandler接收到msg=" + msg.what);
                if (msg.obj != null) {
                    String s = msg.obj.toString();
                    if (s.trim().length() > 0) {
                        Log.i("socket", "mhandler接收到obj=" + s);
                        Log.i("socket", "开始更新UI");
                        Log.i("socket", "更新UI完毕");
                    } else {
                        Log.i("socket", "没有数据返回不更新");
                    }
                }
            } catch (Exception ee) {
                Log.i("socket", "加载过程出现异常");
                ee.printStackTrace();
            }
        }
    }

    public class SenHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                Log.i("socket", "mhandlerSend接收到msg.what=" + msg.what);
                String s = msg.obj.toString();
            } catch (Exception ee) {
                Log.i("socket", "加载过程出现异常");
                ee.printStackTrace();
            }
        }
    }

    public void startSocket() {
        socketThread = new SocThread(mhandler, mhandlerSend, MainActivity.this);
        socketThread.start();
    }

    private void startGetMessage(final String id) {
        Runnable getMessage = new Runnable() {
            @Override
            public void run() {
                while (true) {

                    try {
                        String res = readParse("http://118.89.40.20:3002/get?id=" + id);
                        JSONObject object = new JSONObject(res);
                        if (object.getInt("status") == 1) {
                            JSONArray jsonArray = new JSONArray(object.getString("res"));
                            ArrayList<msg> ml = new ArrayList<msg>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String f = jsonObject.getString("fro");
                                String t = jsonObject.getString("too");
                                String time = jsonObject.getString("time");
                                String mm = jsonObject.getString("msg");
                                if (f.equals(id)) {
                                    msg m = new msg(mm,t,time,true);
                                    ml.add(m);
                                }
                                else if (t.equals(id)){
                                    msg m = new msg(mm,f,time,false);
                                    ml.add(m);
                                }
                            }
                            msgs = new ArrayList<msg>(ml);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }

                        Thread.sleep(30*1000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(getMessage).start();
    }

    public void sendMessage(String to, String ms,String date,final String id) {
        final String tto, mms, da;
        tto = to;
        mms = ms;
        da = date;
        Runnable sendMessage = new Runnable() {
            @Override
            public void run() {
                    try {

                        Map<String,String> params = new HashMap<String,String>();
                        params.put("id", id);
                        params.put("to", tto);
                        params.put("ms", mms);
                        params.put("da", da);

                        String strUrlPath = "http://118.89.40.20:3003/";
                        String res = HttpUtils.submitPostData(strUrlPath,params, "utf-8");
                        Log.e("post",res);
                        startGetMessage(id);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        };

        new Thread(sendMessage).start();
    }
}
