package com.example.guge.music_player;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private IBinder mBinder;
    private ServiceConnection sc;


    //相关的界面控件
    private ImageView imageView;
    private ObjectAnimator animator;
    private SeekBar seekBar;
    private TextView status;
    //当前的播放时间，使用“分：秒”的样式
    private SimpleDateFormat time_now = new SimpleDateFormat("mm:ss");

    private boolean play_pause = true;
    private boolean play_pause1 = true;
    private long mCurrentPlayTime = 0l;

    public static void verify(Activity activity){
        try{
            int permission = ActivityCompat.checkSelfPermission(
                    activity,"android.permission.READ_EXTERNAL_STORAGE"
            );
            if(permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检查权限
        verify(this);

        //实例化ServiceConnection
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Log.d("service","connected");
                mBinder = service;

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                sc = null;
            }
        };


        //开启和MyService的通信，调用bindService来保持通信。
        //当Activity启动时，就绑定Service
        Intent intent = new Intent(this,MyService.class);
        startService(intent);
        bindService(intent,sc, Context.BIND_AUTO_CREATE);

        //通过id找到相关的空间
        imageView = (ImageView) findViewById(R.id.imageView);
        animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360.0f);
        time_n = (TextView)findViewById(R.id.time_now);
        time_e = (TextView)findViewById(R.id.time_end);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        status = (TextView)findViewById(R.id.status);

        //点击拖动seekbar进行的操作
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    MyService.mp.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //播放状态文字的设置
        status.setText("ready to play");
        time_n.setText(time_now.format(MyService.mp.getCurrentPosition()));
        time_e.setText(time_now.format(MyService.mp.getDuration()));


    }

    //点击退出按钮
    public void QuitClick(View view){
        //与服务断开连接
        sc = null;
        try{
            MainActivity.this.finish();
            System.exit(0);
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    //点击播放按钮
    public void PlayClick(View view){
        //向Service发送信息，开始播放音乐
        try{
            int code = 101;
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            mBinder.transact(code,data,reply,0);
        } catch (RemoteException e){
            e.printStackTrace();
        }
        final Button btn_play = (Button)findViewById(R.id.play);
        //图片旋转效果实现


        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        if(play_pause1){
            animator.start();
            play_pause1 = false;
        }



        if(!play_pause){
            play_pause = true;
            btn_play.setText("PLAY");
            status.setText("pause");
            animator.pause();
        }
        else{
            play_pause = false;
            btn_play.setText("PAUSE");
            status.setText("playing...");
            animator.resume();
            handler.post(runnable);
        }



    }

    //点击停止按钮
    public void StopClick(View view){
        try {
            int code = 102;
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            mBinder.transact(code,data,reply,0);
        } catch (RemoteException e){
            e.printStackTrace();
        }
        final Button btn_play = (Button)findViewById(R.id.play);
        play_pause = true;
        btn_play.setText("PLAY");
        status.setText("stop");
        animator.end();
        play_pause1 = true;
    }





    //设置handler更新seekbar

    private TextView time_n;
    private TextView time_e;


    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time_n.setText(time_now.format(MyService.mp.getCurrentPosition()));
            seekBar.setProgress(MyService.mp.getCurrentPosition());
            seekBar.setMax(MyService.mp.getDuration());
            time_e.setText(time_now.format(MyService.mp.getDuration()));
            handler.postDelayed(runnable,300);
        }
    };
}
