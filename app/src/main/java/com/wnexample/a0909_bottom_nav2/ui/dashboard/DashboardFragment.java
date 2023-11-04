package com.wnexample.a0909_bottom_nav2.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wnexample.a0909_bottom_nav2.R;
import com.wnexample.a0909_bottom_nav2.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.List;

import kotlin.sequences.Sequence;

public class DashboardFragment extends Fragment {
    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter ;
    DashboardViewModel dashboardViewModel ;
    List<News> mNewsList = new ArrayList<>();
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        mRecyclerView = binding.recyclerview;
        init_mRecyclerView();

        binding.buttonClear.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= getActivity().getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                init_mRecyclerView();
            }
        } );

        return root;
    }
    public void init_mRecyclerView()
    {
        DividerItemDecoration mDivider = new
                DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(mDivider);
        load_mNewsList();
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        String show_str = "WareHouse Count : <font color = \'#FFB400\'><b>"+mNewsList.size() +"</b></font>";
        dashboardViewModel.setText(fromHtml(show_str));
    }
    public void load_mNewsList()
    {
        mNewsList = new ArrayList<>();

        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("settings", MODE_PRIVATE);
        String names=sharedPreferences.getString("names","");

        if(names == "" || names == null){
            return ;
        }

        String[] name_list = names.split(",");
        String show_content_string = "" , show_title_string = "" ;
        for(int i = 0 ; i < name_list.length ; i ++ )
        {
            News news = new News();

            String total = sharedPreferences.getString(name_list[i]+"_total","");
            String unit  = sharedPreferences.getString(name_list[i]+"_unit","");

            show_title_string = "WareHouse: <font color = \'#FFB400\'><b>" + name_list[i] + "</b></font> ";
            show_content_string = "Detail: TotalPrice:<font color = \'#FFB400\'><b>" + total
                    + "</b></font>,UnitPrice:<font color = \'#FFB400\'><b>" +  unit + "</b></font> " ;

            news.title = fromHtml(show_title_string) ;
            news.content = fromHtml(show_content_string) ;
            mNewsList.add(news);

        }
    }
    public CharSequence fromHtml(String str)
    {
        CharSequence cs ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            cs = Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY);
        } else {
            cs = Html.fromHtml(str);
        }
        return cs ;
    }
@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public class News {
        public CharSequence title; // 标题
        public CharSequence content; //内容
    }
    class MyViewHoder extends RecyclerView.ViewHolder {
        TextView mTitleTv;
        TextView mTitleContent;

        public MyViewHoder(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.textView);
            mTitleContent = itemView.findViewById(R.id.textView2);
        }
    }
    class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {

        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.warehouse_list, null);
            MyViewHoder myViewHoder = new MyViewHoder(view);
            return myViewHoder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            News news = mNewsList.get(position);
            holder.mTitleTv.setText(news.title);
            holder.mTitleContent.setText(news.content);
        }

        @Override
        public int getItemCount() {
            return mNewsList.size();
        }
    }

}