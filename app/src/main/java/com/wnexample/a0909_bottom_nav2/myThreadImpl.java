package com.wnexample.a0909_bottom_nav2;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class myThreadImpl extends Thread {
    public volatile boolean exit = false;
    public volatile AccessibilityNodeInfo root_node = null ;
    public Long fresh_interval = null ;

    public myThreadImpl(Long aLong) {
        fresh_interval = aLong ;
    }

    public void run(){

        super.run();
        while (!exit) {
            try {
                Log.i("wangning","==========thread start sleep========");
                Log.i("wangning","==========fresh_internal ========"+fresh_interval.toString());
                Thread.sleep(fresh_interval);
                Log.i("wangning","==========thread end   sleep========");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(root_node==null) continue;

            List<AccessibilityNodeInfo> refresh_List = root_node.findAccessibilityNodeInfosByText("Refresh");
            if (refresh_List != null && refresh_List.size() > 0) {
                Log.i("wangning","==========thread Refresh========");
                refresh_List.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
}
