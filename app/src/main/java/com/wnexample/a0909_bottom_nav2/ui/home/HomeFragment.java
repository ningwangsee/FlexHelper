package com.wnexample.a0909_bottom_nav2.ui.home;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wnexample.a0909_bottom_nav2.R;
import com.wnexample.a0909_bottom_nav2.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

        private FragmentHomeBinding binding;
        private Calendar mCalendar ;
        private HomeViewModel homeViewModel = null;

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
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;

        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // load time tv and set show timepickerdialog on click listener .
        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("settings", MODE_PRIVATE);


        binding.buttonSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取SharedPreferences对象
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
                //获取Editor对象的引用
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String warehous_name = binding.etName.getText().toString().trim() ;

                Log.i("wangning","warehous_name:" + warehous_name) ;
                if(!warehous_name.equals("")  )
                {
                    //deal wearhouse name
                    String names = sharedPreferences.getString("names", "");
                    Log.i("wangning", "names :"+names);
                    if (names.equals("") || names == null) {
                        editor.putString("names", warehous_name);
                    } else if (names.contains(warehous_name)) {
                        //editor.putString("names", names+","+binding.etName.getText().toString());
                    } else {
                        editor.putString("names", names + "," + warehous_name);
                    }

                    //将获取过来的值放入文件
                    Log.i("wangning", "names :"+names);
                    editor.putString(warehous_name + "_total", binding.etTotal.getText().toString().trim());
                    editor.putString(warehous_name + "_unit", binding.etUnit.getText().toString().trim());
                }

                // 提交数据
                editor.commit();

                homeViewModel.setText(homeViewModel.getDate());
                Log.i("wangning", homeViewModel.getText().getValue().toString());
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