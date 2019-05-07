package com.example.gp62.studymate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by GP62 on 2018-05-08.
 */

public class myGroupFragment extends Fragment {

    String TAG = "myGroupFragment";
    private RecyclerView my_group_recyclerView;
    List<GroupItem> myGroupList_member;
    List<GroupItem> myGroupList_leader;
    //MyGroupAdapter myGroupAdapter;
    SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;
    MyGroupSection myGroupSection;
    MyGroupSection myGroupSection1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_group, container, false);
        myGroupList_member = new ArrayList<GroupItem>();
        myGroupList_leader = new ArrayList<GroupItem>();

        Log.e(TAG, "onCreateView");
        /*sectionedRecyclerViewAdapter.notifyDataSetChanged();*/

        sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        myGroupSection1 = new MyGroupSection(getActivity(),"내가 만든 스터디",myGroupList_leader);
        myGroupSection = new MyGroupSection(getActivity(),"가입한 스터디",myGroupList_member);
        sectionedRecyclerViewAdapter.addSection(myGroupSection1);
        sectionedRecyclerViewAdapter.addSection(myGroupSection);

        //myGroupAdapter = new MyGroupAdapter(myGroupList,R.layout.my_group_item);
        my_group_recyclerView = (RecyclerView)view.findViewById(R.id.my_group_recyclerView);
        //my_group_recyclerView.setAdapter(myGroupAdapter);

        my_group_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        my_group_recyclerView.setAdapter(sectionedRecyclerViewAdapter);
        my_group_recyclerView.setItemAnimator(new DefaultItemAnimator());

//        DividerItemDecoration dividerItemDecoration =
//                new DividerItemDecoration(getActivity(),new LinearLayoutManager(getActivity()).getOrientation());
//        my_group_recyclerView.addItemDecoration(dividerItemDecoration);


        /*Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(TAG, response);
                try {
                    //JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject_response = jsonObject.getJSONObject("response");
                    //Log.e(TAG,"jsonObject_response :"+jsonObject_response);
                    //Boolean success = jsonObject.getBoolean("success");
                    //Log.e(TAG,"2");
                    JSONArray jsonArray_leader = jsonObject_response.getJSONArray("leader");
                    //Log.e(TAG,"jsonArray_leader : "+jsonArray_leader);

                    String group_title, group_cate, group_region, group_introduce, group_image;
                    int group_max_num, group_cur_num, group_no;

                    //if(success) {
                        for (int i = 0; i < jsonArray_leader.length(); i++) {
                            JSONObject object = jsonArray_leader.getJSONObject(i);
                            group_title = object.getString("group_title");
                           // Log.e(TAG,"group_title: "+group_title);
                            group_cate = object.getString("group_cate");
                           // Log.e(TAG,"group_cate: "+group_cate);
                            group_cur_num = object.getInt("group_cur_num");
                           // Log.e(TAG,"group_cur_num: "+group_cur_num);
                            group_region = object.getString("group_region");
                           // Log.e(TAG,"group_region: "+group_region);
                            group_introduce = object.getString("group_introduce");
                            //Log.e(TAG,"group_introduce: "+group_introduce);
                            group_max_num = object.getInt("group_max_num");
                            //Log.e(TAG,"group_max_num: "+group_max_num);
                            group_no = object.getInt("group_no");
                            //Log.e(TAG,"group_no: "+group_no);
                            // group_item에 각각의 정보들을 넣어줌
                            *//*이미지 잠시만 빈값으로 *//*
                            group_image = "";
                            GroupItem group_item = new GroupItem(group_no, group_cate, group_title, group_introduce, group_region, group_cur_num, group_max_num, group_image);
                            // 어레이리스트에 추가
                            //Log.e(Tag,"7-15");
                            myGroupList_leader.add(group_item);
                            //Log.e(Tag,"7-16");
                        }
                   // }

                    //JSONArray jsonArray_member = jsonObject.getJSONArray("member");
                    JSONArray jsonArray_member = jsonObject_response.getJSONArray("member");
                    //Log.e(TAG,"jsonArray_member : "+jsonArray_member);
                    for (int i = 0; i < jsonArray_member.length(); i++) {
                        JSONObject object = jsonArray_member.getJSONObject(i);
                        group_title = object.getString("group_title");
                        //Log.e(TAG,"group_title: "+group_title);
                        group_cate = object.getString("group_cate");
                        // Log.e(TAG,"group_cate: "+group_cate);
                        group_cur_num = object.getInt("group_cur_num");
                        //Log.e(TAG,"group_cur_num: "+group_cur_num);
                        group_region = object.getString("group_region");
                        // Log.e(TAG,"group_region: "+group_region);
                        group_introduce = object.getString("group_introduce");
                        //Log.e(TAG,"group_introduce: "+group_introduce);
                        group_max_num = object.getInt("group_max_num");
                        //Log.e(TAG,"group_max_num: "+group_max_num);
                        group_no = object.getInt("group_no");
                        //Log.e(TAG,"group_no: "+group_no);
                        // group_item에 각각의 정보들을 넣어줌

                        *//*이미지 잠시만 빈값으로 *//*
                        group_image = "";
                        GroupItem group_item = new GroupItem(group_no, group_cate, group_title, group_introduce, group_region, group_cur_num, group_max_num, group_image);
                        // 어레이리스트에 추가
                        //Log.e(Tag,"7-15");
                        myGroupList_member.add(group_item);
                        //Log.e(Tag,"7-16");
                    }
                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                    //Log.e(TAG, "response7");
                }
                //myGroupAdapter.notifyDataSetChanged();
                sectionedRecyclerViewAdapter.notifyDataSetChanged();
            }
        };

        SharedPreferences SP = getActivity().getSharedPreferences("login_user", Context.MODE_PRIVATE);
        String json = SP.getString("login_user", "");
        Gson gson = new Gson();
        Login_user_info item = gson.fromJson(json, Login_user_info.class);
        if(SP.contains("login_user")){
            String user_no_s = item.getUser_no(); //유저고유번호
            // 파라미터 : 유저고유번호, 스터디그룹고유번호,리스너
            MyGroupListRequest myGroupListRequest = new MyGroupListRequest(user_no_s, responseListener);
            // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
            queue.add(myGroupListRequest);
        }else {
            Toast.makeText(getActivity(),"로그인이 필요한 서비스입니다.",Toast.LENGTH_SHORT).show();
            Intent go_login = new Intent(getActivity(),LoginActivity.class);
            go_login.putExtra("로그인전상황", "내모임클릭");
            startActivity(go_login);
        }*/

//        my_group_recyclerView.addOnItemTouchListener(new Recycler
//                setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent go_group_view = new Intent(getContext(), GroupViewActivity.class);
//                // 리스트뷰에서 선택된 아이템 위치를 가져온다(get(position)), 그리고 그 아이템의 group_no을 가져온다 -> 공부 더 필요!
//                go_group_view.putExtra("group_no", group_items_list.get(position).getGroup_no());
//                startActivity(go_group_view);
//            }
//        });

        return view;
    }

//  1. (클라) 유저 고유번호(user_no)를 서버로 보내는 코드 작성
//	3. (클라) 리사이클러뷰에 필요한 클래스,xml파일 만들기(어댑터.class아이템홀더.class, 아이템.xml, 내모임탭화면.xml)
//	4. (클라) (서버)에서 받은 스터디그룹정보들을 어댑터를 통해 리사이클러뷰(내스터디목록)에 세팅하기


    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        myGroupList_leader.clear();
        myGroupList_member.clear();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(TAG, response);
                try {
                    //JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject_response = jsonObject.getJSONObject("response");
                    //Log.e(TAG,"jsonObject_response :"+jsonObject_response);
                    //Boolean success = jsonObject.getBoolean("success");
                    //Log.e(TAG,"2");
                    JSONArray jsonArray_leader = jsonObject_response.getJSONArray("leader");
                    Log.e(TAG,"1");
                    //Log.e(TAG,"jsonArray_leader : "+jsonArray_leader);

                    String group_title, group_cate, group_region, group_introduce, group_image;
                    int group_max_num, group_cur_num, group_no;

                    //if(success) {
                    for (int i = 0; i < jsonArray_leader.length(); i++) {
                        JSONObject object = jsonArray_leader.getJSONObject(i);
                        group_title = object.getString("group_title");
                        // Log.e(TAG,"group_title: "+group_title);
                        group_cate = object.getString("group_cate");
                        // Log.e(TAG,"group_cate: "+group_cate);
                        group_cur_num = object.getInt("group_cur_num");
                        // Log.e(TAG,"group_cur_num: "+group_cur_num);
                        group_region = object.getString("group_region");
                        // Log.e(TAG,"group_region: "+group_region);
                        group_introduce = object.getString("group_introduce");
                        //Log.e(TAG,"group_introduce: "+group_introduce);
                        group_max_num = object.getInt("group_max_num");
                        //Log.e(TAG,"group_max_num: "+group_max_num);
                        group_no = object.getInt("group_no");
//                        Log.e(TAG,"group_no: "+group_no);
                        // group_item에 각각의 정보들을 넣어줌
                            /*이미지 잠시만 빈값으로 */
                        group_image = object.getString("group_image");
//                        group_image = "";
                        Log.e(TAG,"내가 모임장 그룹이미지 : "+group_image);
                        GroupItem group_item = new GroupItem(group_no, group_cate, group_title, group_introduce, group_region, group_cur_num, group_max_num, group_image);
                        // 어레이리스트에 추가
                        //Log.e(Tag,"7-15");
                        Log.e(TAG,"3");
                        myGroupList_leader.add(group_item);
                        Log.e(TAG,"4");
                        //Log.e(Tag,"7-16");
                    }
                    // }

                    //JSONArray jsonArray_member = jsonObject.getJSONArray("member");
                    JSONArray jsonArray_member = jsonObject_response.getJSONArray("member");
                    //Log.e(TAG,"5");
                    //Log.e(TAG,"jsonArray_member : "+jsonArray_member);
                    for (int i = 0; i < jsonArray_member.length(); i++) {
                        JSONObject object = jsonArray_member.getJSONObject(i);
                        group_title = object.getString("group_title");
                        //Log.e(TAG,"group_title: "+group_title);
                        group_cate = object.getString("group_cate");
                        // Log.e(TAG,"group_cate: "+group_cate);
                        group_cur_num = object.getInt("group_cur_num");
                        //Log.e(TAG,"group_cur_num: "+group_cur_num);
                        group_region = object.getString("group_region");
                        // Log.e(TAG,"group_region: "+group_region);
                        group_introduce = object.getString("group_introduce");
                        //Log.e(TAG,"group_introduce: "+group_introduce);
                        //Log.e(TAG,"6");
                        group_max_num = object.getInt("group_max_num");
                        //Log.e(TAG,"group_max_num: "+group_max_num);
                        group_no = object.getInt("group_no");
                        group_image = object.getString("group_image");
                        //Log.e(TAG,"내가 모임원 그룹이미지 : "+group_image);
                        //Log.e(TAG,"group_no: "+group_no);
                        // group_item에 각각의 정보들을 넣어줌

                        /*이미지 잠시만 빈값으로 */
                       // Log.e(TAG,"7");
//                        group_image = "";
                        GroupItem group_item = new GroupItem(group_no, group_cate, group_title, group_introduce, group_region, group_cur_num, group_max_num, group_image);
                        // 어레이리스트에 추가
                        //Log.e(Tag,"7-15");
                        myGroupList_member.add(group_item);
                        //Log.e(TAG,"8");
                        //Log.e(Tag,"7-16");
                    }
                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                    Log.e(TAG,"9");
                    //Log.e(TAG, "response7");
                }
//                myGroupAdapter.notifyDataSetChanged();
                sectionedRecyclerViewAdapter.notifyDataSetChanged();
            }
        };

        SharedPreferences SP = getActivity().getSharedPreferences("login_user", Context.MODE_PRIVATE);
        String json = SP.getString("login_user", "");
        Gson gson = new Gson();
        Login_user_info item = gson.fromJson(json, Login_user_info.class);
        if(SP.contains("login_user")){
            String user_no_s = item.getUser_no(); //유저고유번호
            // 파라미터 : 유저고유번호, 스터디그룹고유번호,리스너
            MyGroupListRequest myGroupListRequest = new MyGroupListRequest(user_no_s, responseListener);
            // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
            queue.add(myGroupListRequest);
            //Log.e(TAG,"작업끝1");
        }else {
            Toast.makeText(getActivity(),"로그인이 필요한 서비스입니다.",Toast.LENGTH_SHORT).show();
            Intent go_login = new Intent(getActivity(),LoginActivity.class);
            go_login.putExtra("로그인전상황", "내모임클릭");
            startActivity(go_login);
        }
        //Log.e(TAG,"작업끝2");
        //sectionedRecyclerViewAdapter.notifyDataSetChanged();
    }
}
