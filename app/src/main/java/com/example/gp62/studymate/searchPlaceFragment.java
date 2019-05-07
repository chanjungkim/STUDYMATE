package com.example.gp62.studymate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by GP62 on 2018-05-08.
 */

public class searchPlaceFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_place, container, false);

//        mapBtn = view.findViewById(R.id.mapBtn);
//        mapBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent go_map = new Intent(getContext(),GoogleMapActivity.class);
//                startActivity(go_map);
//            }
//        });

//        // 툴바
//        toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_map_black_24dp);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        // 툴바 오른쪽에 지도아이콘 넣기



        return view;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//       // getActivity().getMenuInflater().inflate(R.menu.searchplace_toolbar_menu,menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
}
