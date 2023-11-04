package com.wnexample.a0909_bottom_nav2.ui.setting;

import static android.content.Context.MODE_PRIVATE;

import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wnexample.a0909_bottom_nav2.R;
import com.wnexample.a0909_bottom_nav2.databinding.FragmentSettingBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private Spinner spn_freshtime ;
    private String[] spn_freshtime_data = {"3 sec","4 sec","5 sec","6 sec","7 sec","8 sec"} ;

    private Calendar mCalendar ;
    private SettingViewModel settingViewModel = null;

    private void showTimePickerDialog(TextView v) {
        mCalendar = Calendar.getInstance();
        //mCalendar.set

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                mCalendar.set(Calendar.HOUR_OF_DAY, i);
                mCalendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                v.setText(format.format(mCalendar.getTime()));
                // Toast.makeText(getActivity(), "" + format.format(mCalendar.getTime()), Toast.LENGTH_SHORT).show();
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);

        dialog.show();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("settings", MODE_PRIVATE);

        ArrayAdapter<String> spn_freshtime_adapter = new ArrayAdapter<>(getActivity(),R.layout.spiner_item,spn_freshtime_data) ;
        binding.spinnerFleshtime.setAdapter(spn_freshtime_adapter);
        Integer a = sharedPreferences.getInt("fresh_interval",1) ;
        Log.i("wangning","============== read fresh interval "+ a.toString());
        binding.spinnerFleshtime.setSelection(sharedPreferences.getInt("fresh_interval",1));


        final TextView textView = binding.textSetting;

        settingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // load time tv and set show timepickerdialog on click listener .

        final TextView tvMonFrom = binding.tvMonFrom ;
        final TextView tvTueFrom = binding.tvTueFrom ;
        final TextView tvWedFrom = binding.tvWedFrom ;
        final TextView tvThuFrom = binding.tvThuFrom ;
        final TextView tvFriFrom = binding.tvFriFrom ;
        final TextView tvSatFrom = binding.tvSatFrom ;
        final TextView tvSunFrom = binding.tvSunFrom ;

        final TextView tvMonTo = binding.tvMonTo ;
        final TextView tvTueTo = binding.tvTueTo ;
        final TextView tvWedTo = binding.tvWedTo ;
        final TextView tvThuTo = binding.tvThuTo ;
        final TextView tvFriTo = binding.tvFriTo ;
        final TextView tvSatTo = binding.tvSatTo ;
        final TextView tvSunTo = binding.tvSunTo ;

        final CheckBox cbMon = binding.cbMon ;
        final CheckBox cbTue = binding.cbTue ;
        final CheckBox cbWed = binding.cbWed ;
        final CheckBox cbThu = binding.cbThu ;
        final CheckBox cbFri = binding.cbFri ;
        final CheckBox cbSat = binding.cbSat ;
        final CheckBox cbSun = binding.cbSun ;

        String mon_from=sharedPreferences.getString("mon_from","00:00");
        String tue_from=sharedPreferences.getString("tue_from","00:00");
        String wed_from=sharedPreferences.getString("wed_from","00:00");
        String thu_from=sharedPreferences.getString("thu_from","00:00");
        String fri_from=sharedPreferences.getString("fri_from","00:00");
        String sat_from=sharedPreferences.getString("sat_from","00:00");
        String sun_from=sharedPreferences.getString("sun_from","00:00");

        tvMonFrom.setText(mon_from);
        tvTueFrom.setText(tue_from);
        tvWedFrom.setText(wed_from);
        tvThuFrom.setText(thu_from);
        tvFriFrom.setText(fri_from);
        tvSatFrom.setText(sat_from);
        tvSunFrom.setText(sun_from);

        String mon_to=sharedPreferences.getString("mon_to","23:59");
        String tue_to=sharedPreferences.getString("tue_to","23:59");
        String wed_to=sharedPreferences.getString("wed_to","23:59");
        String thu_to=sharedPreferences.getString("thu_to","23:59");
        String fri_to=sharedPreferences.getString("fri_to","23:59");
        String sat_to=sharedPreferences.getString("sat_to","23:59");
        String sun_to=sharedPreferences.getString("sun_to","23:59");

        tvMonTo.setText(mon_to);
        tvTueTo.setText(tue_to);
        tvWedTo.setText(wed_to);
        tvThuTo.setText(thu_to);
        tvFriTo.setText(fri_to);
        tvSatTo.setText(sat_to);
        tvSunTo.setText(sun_to);

        Log.i("wangning","getString wed_from="+wed_from+" wed_to="+wed_to);

        boolean mon=sharedPreferences.getBoolean("mon",true);
        boolean tue=sharedPreferences.getBoolean("tue",true);
        boolean wed=sharedPreferences.getBoolean("wed",true);
        boolean thu=sharedPreferences.getBoolean("thu",true);
        boolean fri=sharedPreferences.getBoolean("fri",true);
        boolean sat=sharedPreferences.getBoolean("sat",true);
        boolean sun=sharedPreferences.getBoolean("sun",true);

        cbMon.setChecked(mon);
        cbTue.setChecked(tue);
        cbWed.setChecked(wed);
        cbThu.setChecked(thu);
        cbFri.setChecked(fri);
        cbSat.setChecked(sat);
        cbSun.setChecked(sun);

        binding.tvMonFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvMonFrom);
            }
        } );
        binding.tvMonTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvMonTo);
            }
        } );
        binding.tvTueFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvTueFrom);
            }
        } );
        binding.tvTueTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvTueTo);
            }
        } );
        binding.tvWedFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvWedFrom);
            }
        } );
        binding.tvWedTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvWedTo);
            }
        } );
        binding.tvThuFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvThuFrom);
            }
        } );
        binding.tvThuTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvThuTo);
            }
        } );
        binding.tvFriFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvFriFrom);
            }
        } );
        binding.tvFriTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvFriTo);
            }
        } );
        binding.tvSatFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvSatFrom);
            }
        } );
        binding.tvSatTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvSatTo);
            }
        } );
        binding.tvSunFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvSunFrom);
            }
        } );
        binding.tvSunTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(tvSunTo);
            }
        } );




        binding.buttonSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取SharedPreferences对象
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
                //获取Editor对象的引用
                SharedPreferences.Editor editor = sharedPreferences.edit();
/*
                if(!binding.etName.getText().toString().equals(""))
                {
                    //deal wearhouse name
                    String names = sharedPreferences.getString("names", "");
                    Log.i("wangning", names);
                    if (names.equals("") || names == null) {
                        editor.putString("names", binding.etName.getText().toString());
                    } else if (names.contains(binding.etName.getText().toString())) {
                        //editor.putString("names", names+","+binding.etName.getText().toString());
                    } else {
                        editor.putString("names", names + "," + binding.etName.getText().toString().trim());
                    }

                    //将获取过来的值放入文件
                    editor.putString(binding.etName.getText().toString() + "_total", binding.etTotal.getText().toString());
                    editor.putString(binding.etName.getText().toString() + "_unit", binding.etUnit.getText().toString());
                }

 */

                // saving timer
                editor.putBoolean("mon", binding.cbMon.isChecked());
                editor.putBoolean("tue", binding.cbTue.isChecked());
                editor.putBoolean("wed", binding.cbWed.isChecked());
                editor.putBoolean("thu", binding.cbThu.isChecked());
                editor.putBoolean("fri", binding.cbFri.isChecked());
                editor.putBoolean("sat", binding.cbSat.isChecked());
                editor.putBoolean("sun", binding.cbSun.isChecked());

                editor.putString("mon_from",binding.tvMonFrom.getText().toString());
                editor.putString("mon_to",binding.tvMonTo.getText().toString());
                editor.putString("tue_from",binding.tvTueFrom.getText().toString());
                editor.putString("tue_to",binding.tvTueTo.getText().toString());

                Log.i("wangning","putString wed_from="+binding.tvWedFrom.getText().toString());
                Log.i("wangning","putString wed_to="+binding.tvWedTo.getText().toString());

                editor.putString("wed_from",binding.tvWedFrom.getText().toString());
                editor.putString("wed_to",binding.tvWedTo.getText().toString());
                editor.putString("thu_from",binding.tvThuFrom.getText().toString());
                editor.putString("thu_to",binding.tvThuTo.getText().toString());
                editor.putString("fri_from",binding.tvFriFrom.getText().toString());
                editor.putString("fri_to",binding.tvFriTo.getText().toString());
                editor.putString("sat_from",binding.tvSatFrom.getText().toString());
                editor.putString("sat_to",binding.tvSatTo.getText().toString());
                editor.putString("sun_from",binding.tvSunFrom.getText().toString());
                editor.putString("sun_to",binding.tvSunTo.getText().toString());

                Integer time_interval = Integer.valueOf(Long.valueOf(binding.spinnerFleshtime.getSelectedItemId()).toString());
                Log.i("wangning","======================================== time_interval"+ time_interval );

                editor.putInt("fresh_interval",time_interval);
                // 提交数据
                editor.commit();

                settingViewModel.setText(settingViewModel.getDate());

                Log.i("wangning", settingViewModel.getText().getValue().toString());
            }
        } );
/*
        binding.buttonClear.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= getActivity().getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                homeViewModel.setText(homeViewModel.getDate());
                Log.i("wangning",homeViewModel.getText().getValue().toString());
            }
        } );

 */
        return root;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void activeAfService(View view){

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        /*
        boolean b = HomeFragment.isServiceON(requireActivity(), HomeFragment.class.getName());
        if(b==false) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            Toast.makeText(requireActivity(),"AfService is on !",Toast.LENGTH_LONG).show();
        }
        */
    }
    public static boolean isServiceON(Context context, String className){

        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo>
                runningServices = activityManager.getRunningServices(100);

        if (runningServices.size() < 0 ){

            return false;
        }

        for (int i = 0;i<runningServices.size();i++){
            ComponentName service = runningServices.get(i).service;

            if (service.getClassName().contains(className)){
                return true;
            }
        }
        return false;
    }

}