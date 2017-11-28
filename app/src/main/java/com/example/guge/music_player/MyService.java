package com.example.guge.music_player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.Toast;

public class MyService extends Service {

    //定义一个Ibinder，类型是自己创建的MyBinder（规定传入的code是某个数值是要做的东西）
    private IBinder mBinder = new MyBinder();

    //声明一个MediaPlayer，用来进行音乐的播放
    public static MediaPlayer mp = new MediaPlayer();

    //使用一个boolean变量控制事件的调用
    public static boolean flag = false;

    public MyService() {
        try{
            mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/melt.mp3");
            mp.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    //重写Mybinder中的
    public class MyBinder extends Binder{
        @Override
        protected boolean onTransact(int code, Parcel data,Parcel reply,int flags) throws RemoteException{
            switch (code){
                case 101:
                    //播放按钮
                    play();
                    break;
                case 102:
                    //停止按钮
                    stop();
                    break;
                case 103:
                    //退出按钮
                    break;
                case 104:
                    //界面刷新
                    break;
                case 105:
                    //拖动进度条
                    break;
            }
            return super.onTransact(code,data,reply,flags);
        }
    }

    //播放或暂停操作
    private void play(){
            if(mp.isPlaying()){
                mp.pause();
            }
            else{
                mp.start();
            }

    }

    //停止操作
    private void stop(){
        if(mp!=null){
            mp.stop();
            try {
                mp.prepare();
                mp.seekTo(0);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
