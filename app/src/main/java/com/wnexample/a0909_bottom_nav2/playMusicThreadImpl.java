package com.wnexample.a0909_bottom_nav2;

import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.Context;
import java.util.List;

public class playMusicThreadImpl extends Thread {
    public static boolean run_state = false;

    public RingtoneManager manager = null ;
    public int position = 1 , timelong = 5 ;

    public void run(){

        if(run_state==false)
        {
            run_state = true;
        }else{
            return ;
        }

        super.run();
        ShufflePlayback();

        run_state = false ;

    }
    private void ShufflePlayback(){
        if ( manager == null ) return ;

        Cursor cursor = manager.getCursor();
        Ringtone mRingtone = manager.getRingtone(position) ;

        mRingtone.play();
        try{
            Thread.sleep(timelong);
        }catch (Exception e ){
        }

        mRingtone.stop();
    }
}
