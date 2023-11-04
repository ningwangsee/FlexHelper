package com.wnexample.a0909_bottom_nav2;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Interpolator;
import android.graphics.Rect;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.WeakHashMap;

public class AfService extends AccessibilityService  {
    public static AfService instance = null;
    public boolean stop = true;
    Calendar calendar = Calendar.getInstance();
    myThreadImpl  t = null ;
    playMusicThreadImpl pt = null ;
    private boolean mon=true , tue=true , wed=true , thu=true , fri=true , sat=true , sun=true ;
    private String  mon_from="00:00" , tue_from="00:00" , wed_from="00:00" , thu_from="00:00" , fri_from="00:00" , sat_from="00:00" ,
            sun_from="00:00" ;

    private Integer mon_from_hour=0 , mon_from_minute=0 , tue_from_hour=0 , tue_from_minute=0 ,
            wed_from_hour=0 , wed_from_minute=0 , thu_from_hour=0 , thu_from_minute=0,
            fri_from_hour=0 , fri_from_minute=0 , sat_from_hour=0 , sat_from_minute=0,
            sun_from_hour=0 , sun_from_minute=0 ;

    private Integer fresh_interval = null ;

    private String  mon_to="23:59" , tue_to="23:59" , wed_to="23:59" , thu_to="23:59" , fri_to="23:59" , sat_to="23:59" , sun_to="23:59" ;

    private Integer mon_to_hour=0 , mon_to_minute=0 , tue_to_hour=0 , tue_to_minute=0 ,
            wed_to_hour=0 , wed_to_minute=0 , thu_to_hour=0 , thu_to_minute=0,
            fri_to_hour=0 , fri_to_minute=0 , sat_to_hour=0 , sat_to_minute=0,
            sun_to_hour=0 , sun_to_minute=0 ;
    private AccessibilityNodeInfo       block = null ;

    private AccessibilityNodeInfo       root_node =null ;
    private List<AccessibilityNodeInfo> blockList = null;
    private ArrayList<WareHouse>             wareHouseList = new ArrayList<WareHouse>() ;
    private ArrayList<AccessibilityNodeInfo> allblocks = new ArrayList<AccessibilityNodeInfo>() ;
    private ArrayList<DayInfo> day_header_list = new ArrayList<DayInfo>() ;
    private List<AccessibilityNodeInfo> day_header_tmplist = null ;
    private String last_top_day = null , last_bottom_day = null ;
    private List<AccessibilityNodeInfo> totalPayList = null , timeLongList = null , timeFromToList = null , dateMdList = null;
    private CharSequence                totalPay = null ,     timeLong = null ,     timeFromTo     = null , dateMd     = null;
    private Double                      hours_i = null ,      totalPay_i = null ,   payPerHour_i = null;

    public AfService() {
    }

    public class WareHouse {
        public String title;
        public Double totalPrice ;
        public Double unitPrice ;
    }
    public class DayInfo
    {
        public String weekday;
        public String original_str;
        public Double day ;
        public int sort_str;
        public Date date ;

        public DayInfo(String original_str){
            // week day
            String[] date_stringlist = original_str.split(",");

            this.original_str = original_str;
            this.weekday      = date_stringlist[0].trim() ;
            this.day          = Double.valueOf(date_stringlist[1].trim().replace('/','.'));

            String[] month_day = date_stringlist[1].trim().split("/");

            String day_str = null ;
            String mon_str = month_day[0].trim();
            // 12月分特殊处理
            if(calendar.get(Calendar.MONTH)==12 && month_day[0].equals("12")){
                day_str = month_day[1].trim() ;
                this.sort_str = Integer.valueOf(day_str);
            }else {
                if(month_day[1].length()<2) day_str = "0"+month_day[1].trim();
                else day_str = month_day[1].trim() ;
                this.sort_str = Integer.valueOf(mon_str + day_str);
            }


        }
    }

    public class DateInfo
    {
        public Date date ;

        public String ori_day_str;
        public String ori_time_str;
        public String weekday ;
        public String short_weekday;

        public String year;
        public String month;
        public String day;

        public String ori_start ;
        public String ori_end ;

        public String start_24;
        public String end_24;

        public Integer start_hour;
        public Integer start_minute;
        public Integer end_hour;
        public Integer end_minute;

        public DateInfo(String day_str, String time_str){
            ori_day_str = day_str;
            ori_time_str = time_str ;

            String[] weekday_day_list = day_str.split(",");

            weekday = weekday_day_list[0].trim() ;
            short_weekday = getShortWeekday(weekday) ;

            String[] month_day_list = weekday_day_list[1].split("/");

            month = month_day_list[0].trim();
            day = month_day_list[1].trim();

            String[] start_end_list = time_str.split("-");
            ori_start = start_end_list[0].trim();
            ori_end = start_end_list[1].trim();

            start_24 = convertTime12To24(ori_start) ;
            Log.i("wangning","start_24:"+start_24);

            String[] hour_minute_list = start_24.split(":") ;

            this.start_hour = Integer.valueOf(hour_minute_list[0].trim()) ;
            this.start_minute = Integer.valueOf(hour_minute_list[1].trim()) ;

            end_24 = convertTime12To24(ori_end) ;
            Log.i("wangning","start_24:"+end_24);
            hour_minute_list = end_24.split(":") ;

            this.end_hour = Integer.valueOf(hour_minute_list[0].trim()) ;
            this.end_minute = Integer.valueOf(hour_minute_list[1].trim()) ;

            // get year
            SimpleDateFormat fmtyyyy = new SimpleDateFormat("YYYY");
            SimpleDateFormat fmtMM = new SimpleDateFormat("MM");
            Date now = new Date();
            String now_yyyy = fmtyyyy.format(now) ;
            String now_MM = fmtMM.format(now).toString() ;
            if(now_MM.indexOf("0") == 0 ){
                now_MM = now_MM.substring(1) ;
            }

            if(now_MM.equals(month)){
                year = now_yyyy ;
            }else{
                Integer year_int = (Integer.valueOf(now_yyyy)+1);
                year = year_int.toString() ;
            }
        }
        public String convertTime12To24(String time_12)
        {
            // time_12 example 6:35 PM or 6:35 AM  => 18:35 or 6:35

            String time_12_lower = time_12.toLowerCase();

            if( time_12_lower.contains("am") ) {
                time_12_lower = time_12_lower.replace("am","").trim() ;

                if(!time_12_lower.contains(":")){
                    time_12_lower = time_12_lower + ":0" ;
                }
                return time_12_lower ;
            }
            if( time_12_lower.contains("pm"))  {
                time_12_lower = time_12_lower.replace("pm","").trim() ;

                if(!time_12_lower.contains(":")){
                    time_12_lower = time_12_lower + ":0" ;
                }

                String[]  hour_minute_list = time_12_lower.split(":");
                Integer hour = Integer.valueOf(hour_minute_list[0].trim()) + 12 ;

                return hour.toString() + ":" + hour_minute_list[1].trim();
            }

            return time_12 ;
        }
    }

    public class SortPositiveComparator implements Comparator {
        @Override
        public int compare(Object lhs, Object rhs) {
            DayInfo a = (DayInfo) lhs;
            DayInfo b = (DayInfo) rhs;

            return (b.sort_str - a.sort_str);
        }
    }
    public class SortReverseComparator implements Comparator {
        @Override
        public int compare(Object lhs, Object rhs) {
            DayInfo a = (DayInfo) lhs;
            DayInfo b = (DayInfo) rhs;

            return (a.sort_str - b.sort_str);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        //服务开启时，调用
        //setServiceInfo();这个方法同样可以实现xml中的配置信息
        //可以做一些开启后的操作比如点两下返回

        Log.i("wangning","======= start service ====== ");
        Log.i("wangning","======= read settings ====== ");
        readSettings();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        //关闭服务时,调用
        //如果有资源记得释放
        return super.onUnbind(intent);
    }
    private void sleep(int timelong){
        try{
            Thread.sleep(timelong);
        }catch (Exception e ){
        }
    }
    private void ShufflePlayback(int position,int timelong){
        RingtoneManager manager = new RingtoneManager(this) ;
        Cursor cursor = manager.getCursor();
        //int count = cursor.getCount() ;
        //int position = (int)(Math.random()*count) ;
        Ringtone mRingtone = manager.getRingtone(position) ;
        mRingtone.play();
        sleep(timelong) ;
        mRingtone.stop();
    }
    public boolean inDayInfoList(ArrayList<DayInfo> day_header_list,String original_str){
        for(int i=0 ; i < day_header_list.size(); i++){
            if(day_header_list.get(i).original_str.equals(original_str)){
                return true;
            }
        }
        return false;
    }
    public String getShortWeekday(String original_str)
    {
        if(original_str.contains("Monday")) return "mon" ;
        if(original_str.contains("Tuesday")) return "tue" ;
        if(original_str.contains("Wednesday")) return "wed" ;
        if(original_str.contains("Thursday")) return "thu" ;
        if(original_str.contains("Friday")) return "fri" ;
        if(original_str.contains("Saturday")) return "sat" ;
        if(original_str.contains("Sunday")) return "sun" ;
        return "";
    }
    public boolean matchShortWeekday(String original_str)
    {
        if(original_str.contains("Monday")) {
            return mon;
        }
        if(original_str.contains("Tuesday")) {
            return tue ;
        }
        if(original_str.contains("Wednesday")) {
            return wed ;
        }
        if(original_str.contains("Thursday")) {
            return thu ;
        }
        if(original_str.contains("Friday")) {
            return fri ;
        }
        if(original_str.contains("Saturday")) {
            return sat ;
        }
        if(original_str.contains("Sunday")) {
            return sun ;
        }
        return true ;
    }

    public boolean matchTimeBeginTo(DateInfo dateInfo)
    {
        Integer from_hour = 0 , from_minute = 0;
        Integer to_hour = 0 , to_minute = 0 ;
        switch (dateInfo.short_weekday){
            case "mon":
                if(!mon) return false ;
                from_hour = mon_from_hour ; from_minute = mon_from_minute ;
                to_hour = mon_to_hour ; to_minute = mon_to_minute ;
                break;
            case"tue":
                if(!tue) return false ;
                from_hour = tue_from_hour ; from_minute = tue_from_minute ;
                to_hour = tue_to_hour ; to_minute = tue_to_minute ;
                break;
            case "wed":
                if(!wed) return false ;
                from_hour = wed_from_hour ; from_minute = wed_from_minute ;
                to_hour = wed_to_hour ; to_minute = wed_to_minute ;
                break;
            case "thu":
                if(!thu) return false ;
                from_hour = thu_from_hour ; from_minute = thu_from_minute ;
                to_hour = thu_to_hour ; to_minute = thu_to_minute ;
                break;
            case "fri":
                if(!fri) return false ;
                from_hour = fri_from_hour ; from_minute = fri_from_minute ;
                to_hour = fri_to_hour ; to_minute = fri_to_minute ;
                break;
            case "sat":
                if(!sat) return false ;
                from_hour = sat_from_hour ; from_minute = sat_from_minute ;
                to_hour = sat_to_hour ; to_minute = sat_to_minute ;
                break;
            case "sun":
                if(!sun) return false ;
                from_hour = sun_from_hour ; from_minute = sun_from_minute ;
                to_hour = sun_to_hour ; to_minute = sun_to_minute ;
                break;
        }
        Log.i("wangning","settings from_hour="+from_hour+" from_minute="+from_minute +
                " to_hour="+to_hour + " to_minute="+to_minute) ;
        Log.i("wangning","start_hour="+dateInfo.start_hour +" start_minute="+dateInfo.start_minute +
                " end_hour="+dateInfo.end_hour + " end_minute="+dateInfo.end_minute) ;

        if( dateInfo.start_hour < from_hour || dateInfo.end_hour > to_hour ) return false;
        if( dateInfo.start_hour == from_hour && dateInfo.start_minute < from_minute) return false;
        if( dateInfo.end_hour == to_hour && dateInfo.end_minute > to_minute) return false;

        return true ;
    }
    public void readSettings(){
        SharedPreferences sharedPreferences= getSharedPreferences("settings", MODE_PRIVATE);
        String names=sharedPreferences.getString("names","");

        String[] name_list = names.split(",");
        String show_string = names;

        if(names == "" || names == null){
            return ;
        }

        wareHouseList.clear();

        for(int i = 0 ; i < name_list.length ; i ++ )
        {
            String total = sharedPreferences.getString(name_list[i]+"_total","");
            String unit  = sharedPreferences.getString(name_list[i]+"_unit","");

            WareHouse wareHouse = new WareHouse();
            wareHouse.title = name_list[i] ;
            wareHouse.totalPrice =  Double.valueOf(total);
            wareHouse.unitPrice = Double.valueOf(unit) ;
            wareHouseList.add(wareHouse) ;
        }
        Log.i("wangning","===== warehouse name list : "+ show_string);

        fresh_interval = sharedPreferences.getInt("fresh_interval",1);
        fresh_interval = ( fresh_interval + 3 ) * 1000 ;
        Log.i("wangning","=====================read fresh interval: "+ fresh_interval.toString());
        //read weekday and time
        mon  = sharedPreferences.getBoolean("mon",true);
        tue  = sharedPreferences.getBoolean("tue",true);
        wed  = sharedPreferences.getBoolean("wed",true);
        thu  = sharedPreferences.getBoolean("thu",true);
        fri  = sharedPreferences.getBoolean("fri",true);
        sat  = sharedPreferences.getBoolean("sat",true);
        sun  = sharedPreferences.getBoolean("sun",true);

        String[] hour_minute_list ;
        mon_from  = sharedPreferences.getString("mon_from","00:00");
        hour_minute_list = mon_from.split(":");
        mon_from_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        mon_from_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        tue_from  = sharedPreferences.getString("tue_from","00:00");
        hour_minute_list = tue_from.split(":");
        tue_from_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        tue_from_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        wed_from  = sharedPreferences.getString("wed_from","00:00");
        hour_minute_list = wed_from.split(":");
        wed_from_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        wed_from_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        thu_from  = sharedPreferences.getString("thu_from","00:00");
        hour_minute_list = thu_from.split(":");
        thu_from_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        thu_from_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        fri_from  = sharedPreferences.getString("fri_from","00:00");
        hour_minute_list = fri_from.split(":");
        fri_from_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        fri_from_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        sat_from  = sharedPreferences.getString("sat_from","00:00");
        hour_minute_list = sat_from.split(":");
        sat_from_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        sat_from_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        sun_from  = sharedPreferences.getString("sun_from","00:00");
        hour_minute_list = sun_from.split(":");
        sun_from_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        sun_from_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        mon_to  = sharedPreferences.getString("mon_to","23:59");
        hour_minute_list = mon_to.split(":");
        mon_to_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        mon_to_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        tue_to  = sharedPreferences.getString("tue_to","23:59");
        hour_minute_list = tue_to.split(":");
        tue_to_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        tue_to_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        wed_to  = sharedPreferences.getString("wed_to","23:59");
        hour_minute_list = wed_to.split(":");
        wed_to_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        wed_to_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        thu_to  = sharedPreferences.getString("thu_to","23:59");
        hour_minute_list = thu_to.split(":");
        thu_to_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        thu_to_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        fri_to  = sharedPreferences.getString("fri_to","23:59");
        hour_minute_list = fri_to.split(":");
        fri_to_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        fri_to_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        sat_to  = sharedPreferences.getString("sat_to","23:59");
        hour_minute_list = sat_to.split(":");
        sat_to_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        sat_to_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        sun_to  = sharedPreferences.getString("sun_to","23:59");
        hour_minute_list = sun_to.split(":");
        sun_to_hour = Integer.valueOf(deleteLeft0(hour_minute_list[0])) ;
        sun_to_minute = Integer.valueOf(deleteLeft0(hour_minute_list[1])) ;

        if(t!=null){
            t.exit = true;
            t=null;
        }
    }
    public void service_stop()
    {
        this.stop = true;
        if(t!=null){
            t.exit = true;
            t=null;
        }
    }
    public void service_start()
    {
        this.stop = false;
        readSettings();
        if(t!=null){
            t.exit = false;
        }else{
            t = new myThreadImpl(new Long(fresh_interval)) ;
            t.start();
        }
    }
    public String deleteLeft0(String str){
        if(str.indexOf("0")==0) str = str.substring(1);
        return str;
    }
    //@Override
    public void onAccessibilityEvent2(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventString =  event.getText().toString();

        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date(System.currentTimeMillis());

        Log.i("wangning", format.format(date) + "====" + Integer.toString(eventType) + "====" + eventString  );
    }

    @RequiresApi(api = 34)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // close and start grab service
        // todo list : 启动，停止

        if(stop) return ;

        AccessibilityNodeInfo root_node = getRootInActiveWindow();

        if(root_node == null ) {
            Log.i("wangning","root_node == null") ;
            return ;
        }

        onAccessibilityEvent2(event);

        Log.i("wangning","======= check  Block unavailable ========");
        List<AccessibilityNodeInfo> block_unavailable_List = root_node.findAccessibilityNodeInfosByText("Block unavailable");
        if(block_unavailable_List != null && block_unavailable_List.size()>0 ) {
            // ShufflePlayback(13,5000) ;
            Log.i("wangning","======= check  Block unavailable  play music ========");
            pt = new playMusicThreadImpl() ;
            pt.manager = new RingtoneManager(this) ;
            pt.position = 13;
            pt.timelong = 2000 ;
            pt.start();
            return ;
        }

        Log.i("wangning","======= check  Offer scheduled ========");
        List<AccessibilityNodeInfo> block_scheduled_List = root_node.findAccessibilityNodeInfosByText("Offer scheduled");
        if(block_scheduled_List != null && block_scheduled_List.size()>0 ) {
            // ShufflePlayback(12,5000) ;
            Log.i("wangning","======= check  Offer scheduled  play music ========");
            pt = new playMusicThreadImpl() ;
            pt.manager = new RingtoneManager(this) ;
            pt.position = 12;
            pt.timelong = 2000 ;
            pt.start();
            return ;
        }

        // ======= book page ========
        List<AccessibilityNodeInfo> offer_details_List = root_node.findAccessibilityNodeInfosByText("OFFER DETAILS");
        if(offer_details_List.size() > 0  ) {
            Log.i("wangning","check I'm not a robot page!");
            List<AccessibilityNodeInfo> robot_button_List = root_node.findAccessibilityNodeInfosByText("I'm not a robot");
            if(robot_button_List.size() > 0){
                Log.i("wangning","======= got robot button and perform click action========");
                robot_button_List.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK) ;
                return ;
            }

            Log.i("wangning","======= book page ========");
            List<AccessibilityNodeInfo> schedule_button_List = root_node.findAccessibilityNodeInfosByText("Schedule");
            if(schedule_button_List.size() > 0){
                Log.i("wangning","======= got button and perform click action========");
                schedule_button_List.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK) ;
                sleep(3000) ;
            }
            return ;
        }

        // ======= find page ========
        List<AccessibilityNodeInfo> offers_List = root_node.findAccessibilityNodeInfosByText("OFFERS");
        if(offers_List.size() > 0) {
            Log.i("wangning","======= find page ========");
            if(t == null) {
                Log.i("wangning","======= active fresh thread ========");
                t = new myThreadImpl(new Long(fresh_interval)) ;
                t.root_node = root_node ;
                t.start();
            }else{
                Log.i("wangning","======= update fresh thread root node ========");
                Log.i("wangning","======= update fresh thread root node ======== stop:"+ this.stop);
                Log.i("wangning","======= update fresh thread root node ======== exit:"+ t.exit);
                t.root_node = root_node ;
            }

            int eventType   = event.getEventType();

            // setting day list ;
            day_header_tmplist =  root_node.findAccessibilityNodeInfosByViewId("com.amazon.rabbit:id/offer_list_day_header_item");
            if(day_header_tmplist.size()!=0) {
                for (int ii = 0; ii < day_header_tmplist.size(); ii++) {
                    boolean find = inDayInfoList(day_header_list,day_header_tmplist.get(ii).getText().toString()) ;
                    if(find == false){
                        DayInfo dayinfo = new DayInfo(day_header_tmplist.get(ii).getText().toString()) ;
                        day_header_list.add(dayinfo) ;
                    }
                }
                Comparator comp = new SortReverseComparator();
                Collections.sort(day_header_list,comp);

                // set last_top_day and last_bottom_day
                // last_top_day    = day_header_tmplist[0]      or ( day_header_tmplist[0] - 1 ) in day_header_list ;
                // last_bottom_day = day_header_tmplist[length] or ( day_header_tmplist[0] + 1 ) in day_header_list ;

                // 根据历史记录，与当前记录，确定之前的日期。
                for(int ii = 0 ; ii < day_header_list.size(); ii++ ){
                    if(day_header_list.get(ii).original_str.equals(day_header_tmplist.get(0).getText().toString()))
                    {
                        if(ii == 0){//如果当前第一个就是历史第一个日期，
                            last_top_day = day_header_list.get(0).original_str ;
                        }else{//否则是当前的上一个
                            last_top_day = day_header_list.get(ii-1).original_str ;
                        }
                    }
                }
            }else{
                //如果当前没有日期，并且上一个日期为空，返回不做操作。
                if(last_top_day == null) return ;
            }

            // get settings and find blocks ;
            for(int whs = 0 ; whs < wareHouseList.size(); whs ++ )
            {
                WareHouse wareHouse = wareHouseList.get(whs) ;
                blockList = root_node.findAccessibilityNodeInfosByText(wareHouse.title);
                if(blockList != null) allblocks.addAll(blockList);

                Log.i("wangning","judge ==== warehouse : "+ wareHouse.title + " allblocks size : " + allblocks.size()) ;

                for(int i = 0 ; allblocks.size() > i ; i++ ){
                    block= allblocks.get(i).getParent();
                    if(block==null) continue;
                    Log.i("wangning","judge ==== 1111111111");
                    totalPayList = block.findAccessibilityNodeInfosByText("$");
                    if(totalPayList!=null && totalPayList.size()>0) totalPay = totalPayList.get(0).getText();

                    // block 带消费，所以totalPay为一个区间，目前不处理这种情况。
                    if(totalPay == null){
                         continue;
                    }
                    if(totalPay.toString().contains("-")){
                        continue;
                    }
                    Log.i("wangning","judge ==== 2222222222");
                    timeLongList = block.findAccessibilityNodeInfosByText("hr");
                    if(timeLongList!=null && timeLongList.size()>0) timeLong = timeLongList.get(0).getText();

                    timeFromToList = block.findAccessibilityNodeInfosByText(":");
                    if(timeFromToList!=null && timeFromToList.size()>0) timeFromTo = timeFromToList.get(0).getText();
                    if(timeFromTo == null ){
                        timeFromToList = block.findAccessibilityNodeInfosByText("AM");
                        if(timeFromToList!=null && timeFromToList.size()>0) timeFromTo = timeFromToList.get(0).getText();
                    }
                    Log.i("wangning","judge ==== 33333333333");
                    if(timeFromTo == null ){
                        timeFromToList = block.findAccessibilityNodeInfosByText("PM");
                        if(timeFromToList!=null && timeFromToList.size()>0) timeFromTo = timeFromToList.get(0).getText();
                    }
                    if(timeFromTo==null || !timeFromTo.toString().contains(":") || timeFromTo.toString().contains("Accept")) continue;

                    /*
                    block  = allblocks.get(i).getParent().getParent().getParent();
                    if(block == null ) break;
                    dateMdList = block.findAccessibilityNodeInfosByText("/");
                    if(dateMdList!=null && dateMdList.size()>0) dateMd = dateMdList.get(0).getText();
                     */

                    dateMd = null ;
                    if(day_header_tmplist.size()==0) { //如果当前没有日期，取上一个日期
                        dateMd = last_top_day ;
                    }else{
                        //否则获取block的位置
                        Rect block_bound = new Rect();
                        block.getBoundsInScreen(block_bound);
                        Log.i("wangning","top:"+block_bound.top+" bottom:"+block_bound.bottom+
                                " left:"+block_bound.left+" right:"+block_bound.right);

                        for(int iii = 0 ; iii < day_header_tmplist.size() ; iii ++){
                            Rect day_header_bound = new Rect();
                            day_header_tmplist.get(iii).getBoundsInScreen(day_header_bound);
                            Log.i("wangning","top:"+day_header_bound.top+" bottom:"+day_header_bound.bottom+
                                    " left:"+day_header_bound.left+" right:"+day_header_bound.right);

                            if(block_bound.top < day_header_bound.top && iii == 0 )
                            {//block的位置在当前第一个日期的上面，取上一个日期
                                dateMd = last_top_day ;
                            }else if(block_bound.top < day_header_bound.top){
                                //在某一个日期的上面，去当前日期的前面日期
                                dateMd = day_header_tmplist.get(iii-1).getText().toString();
                            }
                        }
                        //在所有日期的下面，取最后一个日期
                        if(dateMd==null) dateMd = day_header_tmplist.get(day_header_tmplist.size()-1).getText().toString();;
                    }


                    //deal day_header_tmplist.get(ii)

                    Log.i("wangning","totalPay = "+totalPay+
                            "  timeLong = "+timeLong+
                            "  timeFromTo = "+timeFromTo+
                            "  dateMd ="+ dateMd );

                    if(totalPay != null && timeLong != null && timeFromTo != null ) {
                        // deal with totalpay ;
                        totalPay_i = Double.valueOf(totalPay.subSequence(1,totalPay.length()-1).toString());

                        // deal with timelong ;
                        String timeLong_s = timeLong.toString().trim() ;
                        int min = timeLong_s.indexOf("min"); //if return -1  the string dont have min
                        if(min == -1 ) {
                            hours_i = Double.valueOf(timeLong.subSequence(0, 1).toString());
                        }else{
                            timeLong_s = timeLong.subSequence(0, min).toString();
                            String[] hour_min_stinglist = timeLong_s.split("hr");
                            String hour_s = hour_min_stinglist[0].trim();
                            String min_s  = hour_min_stinglist[1].trim();
                            hours_i = Double.valueOf(hour_s) + Double.valueOf(min_s)/60 ;
                        }

                        // get paid per hour
                        payPerHour_i = totalPay_i / hours_i ;

                        // 判断当前block weekday是否满足
                        // 判断当前block时间是否在时间范围
                        Log.i("wangning","dateMd:"+dateMd.toString()+"  timeFromTo:"+timeFromTo.toString());
                        DateInfo dateInfo = new DateInfo(dateMd.toString(),timeFromTo.toString());
                        if(!matchTimeBeginTo(dateInfo)){
                            Log.i("wangning","matchTimeBeginTo fail") ;
                            continue ;
                        }

                        Log.i("wangning","judge ===== payPerHour_i:"+payPerHour_i+" unitPrice:"+wareHouse.unitPrice
                                +" totalPay_i:"+totalPay_i+ " totalPrice:"+wareHouse.totalPrice+" =======") ;

                        if(payPerHour_i>=wareHouse.unitPrice && totalPay_i >= wareHouse.totalPrice){
                            // go to book page
                            block  = allblocks.get(i).getParent() ;
                            block.performAction(AccessibilityNodeInfo.ACTION_CLICK) ;
                            allblocks.removeAll(allblocks);

                            Log.i("wangning","======= check  Offer scheduled  play music ========");
                            pt = new playMusicThreadImpl() ;
                            pt.manager = new RingtoneManager(this) ;
                            pt.position = 10;
                            pt.timelong = 2000 ;
                            pt.start();


                        }
                    }
                }
                totalPayList = null  ; timeLongList = null ; timeFromToList = null ; dateMdList = null ;
                totalPay     = null  ; timeLong     = null ; timeFromTo = null     ; dateMd     = null ;
                hours_i      = null  ; totalPay_i   = null ; payPerHour_i = null;    allblocks.removeAll(allblocks);
            }

        }else{
            if(t != null){
                t.exit = true ;
                t = null ;
            }
        }
        return;
    }

    @Override
    public void onInterrupt() {
        //当服务要被中断时调用.会被调用多次
        t.exit = true;
    }
}