package com.wnexample.a0909_bottom_nav2.ui.start;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wnexample.a0909_bottom_nav2.AfService;
import com.wnexample.a0909_bottom_nav2.FloatingService;
import com.wnexample.a0909_bottom_nav2.MainActivity;
import com.wnexample.a0909_bottom_nav2.R;
import com.wnexample.a0909_bottom_nav2.databinding.FragmentStartBinding;
import com.wnexample.a0909_bottom_nav2.playMusicThreadImpl;

public class StartFragment extends Fragment {

    private FragmentStartBinding binding;
    private StartViewModel startViewModel = null;
    private Button button_startService = null ;
    private Button button_start = null ;
    private Button button_stop = null ;
    private Button button_test = null ;
    playMusicThreadImpl pt = null ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        startViewModel =
                new ViewModelProvider(this).get(StartViewModel.class);

        binding = FragmentStartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textStart;
        startViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        button_startService = root.findViewById( R.id.button_startService );

        button_startService.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textView.setText( homeViewModel.getDate() );
                startViewModel.setText(startViewModel.getDate());
                Log.i("wangning",startViewModel.getText().getValue().toString());
                activeAfService(v);
            }
        } );

        button_stop = root.findViewById( R.id.button_stop );
        button_stop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textView.setText( homeViewModel.getDate() );
                startViewModel.setText(startViewModel.getDate());
                Log.i("wangning",startViewModel.getText().getValue().toString());

                if(AfService.instance == null) {
                    Log.i("wangning","button_stop");
                    Toast.makeText(getActivity(),"please LAUNCH SERVICE first!", 5).show() ;
                    return ;
                }

                // 关闭 hand
                if(FloatingService.instance != null) FloatingService.instance.removeFloatingWindow();

                if(!AfService.instance.stop) {
                    AfService.instance.service_stop();
                    //stopFloatingService();
                    Toast.makeText(getActivity(),"The service is stopped !", 5).show(); ;
                }else{
                    Toast.makeText(getActivity(),"The service has been stopped !", 5).show(); ;
                }

            }
        } );
        button_start = root.findViewById( R.id.button_start );
        button_start.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textView.setText( homeViewModel.getDate() );
                startViewModel.setText(startViewModel.getDate());
                Log.i("wangning",startViewModel.getText().getValue().toString());

                if(AfService.instance == null) {
                    Log.i("wangning","button_start");
                    Toast.makeText(getActivity(),"please LAUNCH SERVICE first!", 5).show(); ;
                    return ;
                }

                if( AfService.instance.stop ) {
                    AfService.instance.readSettings();
                    AfService.instance.service_start();

                    if(FloatingService.instance == null) {
                        startFloatingService();
                    }

                    Toast.makeText(getActivity(),"The service is started !", 5).show(); ;
                }else{
                    Toast.makeText(getActivity(),"The service is starting !", 5).show() ;
                }

                if(FloatingService.instance != null ) {
                    FloatingService.instance.addFloatingWindow();
                }

            }
        } );



        button_test = root.findViewById( R.id.button_reload_setting);
        button_test.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //AccessibilityManager a = getSystemService(getActivity().ACCESSIBILITY_SERVICE);
                //  getSystemService(getActivity(),com.wnexample.a0909_bottom_nav2.AfService.)


                //startFloatingService();


                if(AfService.instance != null){
                    AfService.instance.readSettings();
                }

                /*
                RingtoneManager manager = new RingtoneManager(getActivity()) ;
                Cursor cursor = manager.getCursor();
                Ringtone mRingtone = manager.getRingtone(13) ;

                mRingtone.play();
                try{
                    Thread.sleep(5000);
                }catch (Exception e ){
                }

                mRingtone.stop();


                Integer a = Integer.valueOf("001");
                startViewModel.setText(a.toString());


                pt = new playMusicThreadImpl() ;
                pt.manager = new RingtoneManager(getActivity()) ;
                pt.position = 10;
                pt.timelong = 2000 ;
                pt.start();

                 */


            }
        } );
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
    public void startFloatingService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getActivity())) {
            Toast.makeText(getActivity(), "当前无权限，请授权", Toast.LENGTH_SHORT);
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, 0);
            return;
        }
        if (FloatingService.instance==null) {
            getActivity().startService(new Intent(getActivity(), FloatingService.class));
        }
    }
    public void stopFloatingService() {
        if(FloatingService.instance != null ) {
            Log.i("wangning", "FloatingService.instance.onDestroy();") ;
            FloatingService.instance.onDestroy();
            getActivity().stopService(new Intent(getActivity(), FloatingService.class));
        }


    }
}