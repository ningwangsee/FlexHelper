package com.wnexample.a0909_bottom_nav2;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class FloatingService extends Service {
    public static FloatingService instance = null;
    private WindowManager windowManager = null ;
    private WindowManager.LayoutParams layoutParams = null;

    private View view_stop = null ;
    private View view_start = null ;

    private View view_current = null ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FloatingService.instance = this ;
        addFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }
    public void removeFloatingWindow(){
        if(windowManager != null && layoutParams != null){
            if(view_current!=null) {
                windowManager.removeView(view_current);
                view_current = null;
            }
        }
    }
    public void addFloatingWindow(){
        if(windowManager != null && layoutParams != null) {
            removeFloatingWindow();
            if( AfService.instance.stop ) {
                windowManager.addView(view_stop, layoutParams);
                view_current = view_stop ;

            }else{
                windowManager.addView(view_start, layoutParams);
                view_current = view_start ;
            }
        }else{
            showFloatingWindow();
        }
    }
    @SuppressLint("InflateParams")
    private void showFloatingWindow() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            // 设置LayoutParam
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            //宽高自适应
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //显示的位置
            layoutParams.x = 300;
            layoutParams.y = 300;

            // 新建悬浮窗控件
            view_start = LayoutInflater.from(this).inflate(R.layout.fab, null);
            view_start.setOnTouchListener(new FloatingOnTouchListener());

            view_stop = LayoutInflater.from(this).inflate(R.layout.fab_stop, null);
            view_stop.setOnTouchListener(new FloatingOnTouchListener());


            // todo list ;
            // 设置 view.setOnClickListener() ， 弹出菜单，可以启停服务。改变图标颜色

            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(view_start, layoutParams);
            view_current = view_start ;
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;
        private int ori_x;
        private int ori_y;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.i("wangning","onTouch");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    x = (int) event.getRawX();
                    y = (int) event.getRawY();

                    ori_x = x;
                    ori_y = y;
                    break;

                case MotionEvent.ACTION_MOVE:

                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;

                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);

                    break;

                case MotionEvent.ACTION_UP:

                    if(AfService.instance!=null && Math.abs(x-ori_x) < 20 && Math.abs(y-ori_y)< 20){
                        Log.i("wangning","MotionEvent.ACTION_UP") ;

                        if( AfService.instance.stop ){
                            Log.i("wangning","MotionEvent.ACTION_UP stop") ;
                            //tv.setBackgroundColor(Color.WHITE);
                            AfService.instance.service_start();
                            Log.i("wangning","MotionEvent.ACTION_UP stop end") ;
                            windowManager.removeView(view);
                            windowManager.addView(view_start, layoutParams);
                            view_current = view_start ;


                        }else{
                            Log.i("wangning","MotionEvent.ACTION_UP start") ;
                            //tv.setBackgroundColor(Color.RED);
                            AfService.instance.service_stop();
                            Log.i("wangning","MotionEvent.ACTION_UP start end") ;
                            windowManager.removeView(view);
                            windowManager.addView(view_stop, layoutParams);
                            view_current = view_stop ;

                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }


    @Override
    public void onDestroy() {
        FloatingService.instance = null ;
        super.onDestroy();
    }



}
