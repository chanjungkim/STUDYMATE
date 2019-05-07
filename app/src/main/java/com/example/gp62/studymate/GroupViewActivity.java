package com.example.gp62.studymate;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GroupViewActivity extends AppCompatActivity { // 모임상세페이지

    // TODO: 2018-05-14 * 수정,삭제 버튼은 작성자만 수정,삭제할 수 있게 하기

    android.support.v7.widget.Toolbar toolbar;
    String Tag = "GroupViewActivity";

    TextView group_title, //모임제목
            group_leader,  //모임장이름
            group_introduce,//모임 한줄 소개
            group_desc, //모임설명
            group_tag, //태그설정
            group_region, // 활동지역
            group_cate, // 모임카테고리
            group_max_num;// 모임정원
    ImageView group_image;
    ImageView leader_image;
    String group_title_s, group_desc_s, group_introduce_s, group_cate_s, group_region_s, group_leader_nick, group_image_s;
    String group_leader_no_s; // 모임장 고유번호
    int group_max_num_i;
    String user_role;
    int chat_num; // 채팅시작번호

    Button group_join_btn, // 모임참여하기 버튼
            group_chat_btn,
            setting_btn;
    int group_no; // 그룹 고유번호
    String participant_user_no = ""; // 스터디참가한 유저 고유번호

    Dialog dialog;

    String TAG = "GroupViewActivity";
    private RecyclerView participant_recyclerView;
    List<participant_info> participantInfoList; // 모임원목록
    ParticipantAdapter participantAdapter;

    int LEADER_MODIFY_NUM = 3333;
    int MEMBER_MODIFY_NUM = 1;

//    TextView homeTab;
//    TextView chatTab;
//    TextView settingTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        Log.e(TAG,"onCreate");

        participantInfoList = new ArrayList<participant_info>();

        participantAdapter = new ParticipantAdapter(getApplicationContext(), participantInfoList, R.layout.participant);
        participant_recyclerView = (RecyclerView) findViewById(R.id.participant_recyclerView);
        participant_recyclerView.setAdapter(participantAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        participant_recyclerView.setHasFixedSize(true);
        participant_recyclerView.setLayoutManager(layoutManager);

        participant_recyclerView.setItemAnimator(new DefaultItemAnimator());

        toolbar = findViewById(R.id.toolbar);
        group_title = findViewById(R.id.group_title);
        group_leader = findViewById(R.id.group_leader);
        group_introduce = findViewById(R.id.group_introduce);
        group_desc = findViewById(R.id.group_desc);
        group_cate = findViewById(R.id.group_cate);
        group_region = findViewById(R.id.group_region);
        group_max_num = findViewById(R.id.group_max_num);
        group_join_btn = findViewById(R.id.group_join_btn);
        group_chat_btn = findViewById(R.id.group_chat_btn);
        setting_btn = findViewById(R.id.btn_group_setting);
        // modify_btn = findViewById(R.id.modify_btn);
        //delete_btn = findViewById(R.id.delete_btn);
        group_image = findViewById(R.id.group_profile_image);
        leader_image = findViewById(R.id.leader_image);
//        homeTab = findViewById(R.id.homeTab);
//        chatTab = findViewById(R.id.chatTab);
//        settingTab = findViewById(R.id.settingTab);

        // 모임고유번호 -> 모임번호에 맞는 모임내용을 뿌려주기
        //Log.e(Tag,"chat : "+group_no_in.getStringExtra("chat"));


        // 툴바에 뒤로가기버튼 세팅
        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent group_no_in = getIntent();
//        String a = group_no_in.getStringExtra("chat");
//        Log.e(Tag,"상세화면으로 넘어온 chat : "+a);
        group_no = group_no_in.getIntExtra("group_no", -1);
        Log.e(Tag, "group_no : " + group_no);

        //Log.e(Tag, "서버에서 내용받아오기 시작");
        new BackgroundTask().execute();
       // Log.e(Tag, "서버에서 내용받아오기 끝");


        SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
        String json = SP.getString("login_user", "");
        Gson gson = new Gson();
        Login_user_info item = gson.fromJson(json, Login_user_info.class);
        // String login_user_no = item.getUser_no();

        if (!SP.contains("login_user")) {
            Log.e(TAG, "로그인한유저있나? :ㄴㄴ");
            group_join_btn.setVisibility(View.GONE);
        }

        //Log.e(TAG, "onCreate");


        /*// 수정버튼
        modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 수정버튼을 누르면, 모임추가화면으로 감
                Intent go_modify_group = new Intent(GroupViewActivity.this, AddGroupActivity.class);
                // '항목'에 값이 '모임수정'일 때만, 모임내용 수정되게 함.
                go_modify_group.putExtra("항목", "모임수정");

                // 1-1 모임추가화면에 기존 저장되어있는 정보들을 intent로 보냄 (7가지인지 확인)
                go_modify_group.putExtra("group_no_i", group_no);
                go_modify_group.putExtra("group_title_s", group_title_s);
                go_modify_group.putExtra("group_desc_s", group_desc_s);
                go_modify_group.putExtra("group_introduce_s", group_introduce_s);
                go_modify_group.putExtra("group_region_s", group_region_s);
                go_modify_group.putExtra("group_cate_s", group_cate_s);
                go_modify_group.putExtra("group_max_num_i", "" + group_max_num_i);

                // startActivityForResult 가 아닌 이유 : onCreate 때, 서버에서 모임정보를 받아옴. 다시 intent로 받아오지 않아도 됨.
                startActivity(go_modify_group);
            }
        });*/


        /*채팅하기 버튼*/
        group_chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences SP = getSharedPreferences("login_user", MODE_PRIVATE);
                String json = SP.getString("login_user", "");
                Gson gson = new Gson();
                Login_user_info item = gson.fromJson(json, Login_user_info.class);
                String user_no_s = item.getUser_no(); //유저고유번호
                String user_nick = item.getUser_nick();// 유저닉네임
               /* String user_photo = item.getUser_photo();// 유저사진*/
                String group_no_s = String.valueOf(group_no);//스터디그룹고유번호
//                Log.e(Tag,"그룹원리스트 : "+participantInfoList);

                Intent go_chat = new Intent(getApplicationContext(), ChatActivity.class);
                go_chat.putExtra("유저번호", user_no_s);
                go_chat.putExtra("스터디그룹번호", group_no_s);
                go_chat.putExtra("유저닉네임", user_nick);
                go_chat.putExtra("채팅시작번호", chat_num);
                go_chat.putExtra("스터디제목", group_title_s);
                go_chat.putExtra("그룹원번호리스트", (Serializable) participantInfoList);

                Log.e(Tag, "유저번호 : " + user_no_s);
                Log.e(Tag, "스터디그룹번호 : " + group_no_s);
                Log.e(Tag, "유저닉네임 : " + user_nick);
                Log.e(Tag, "채팅시작번호 : " + chat_num);
                Log.e(Tag, "스터디제목 : " + group_title_s);
                Log.e(Tag, "그룹원번호리스트11 : " + (Serializable) participantInfoList);
                startActivity(go_chat);
            }
        });

        /*채팅알림을 클릭했을 때, 채팅화면으로 가기*/
        /*Intent go_chat = getIntent();
        Log.e(Tag,"채팅화면으로 가자! 시작!");
        Log.e(Tag,"group_no : "+group_no);

        String a = go_chat.getStringExtra("chat");
        if(a!=null){
            group_chat_btn.performClick();
            Log.e(Tag,"채팅버튼 클릭");
            Log.e(Tag,"채팅화면으로 가자! 끝!");
        }
*/
       /* if(group_no_in.getStringExtra("chat")!=null){
            SharedPreferences SP1 = getSharedPreferences("login_user", MODE_PRIVATE);
            String json1 = SP1.getString("login_user", "");
            Gson gson1 = new Gson();
            Login_user_info item1 = gson1.fromJson(json1, Login_user_info.class);
            String user_no_s = item1.getUser_no(); //유저고유번호
            String user_nick = item1.getUser_nick();// 유저닉네임
               *//* String user_photo = item.getUser_photo();// 유저사진*//*
            String group_no_s = String.valueOf(group_no);//스터디그룹고유번호
            //Log.e(Tag,"그룹원리스트 : "+participantInfoList);

            Intent go_chat = new Intent(getApplicationContext(),ChatActivity.class);
            go_chat.putExtra("유저번호",user_no_s);
            // Log.e(Tag,"유저번호 : "+user_no_s);
            go_chat.putExtra("스터디그룹번호",group_no_s);
            go_chat.putExtra("유저닉네임",user_nick);
            go_chat.putExtra("채팅시작번호",chat_num);
            // Log.e(Tag,"인텐트 채팅개수 : "+chat_num);
            Log.e(Tag,"채팅버튼_스터디제목 : "+group_title_s);
            go_chat.putExtra("스터디제목",group_title_s);
            go_chat.putExtra("그룹원번호리스트",(Serializable)participantInfoList);


//                Log.e(Tag,"그룹원리스트 : "+participantInfoList);
            startActivity(go_chat);

            //group_chat_btn.performClick();
        }*/

        //스터디 참가버튼
        group_join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e(TAG, "response : " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(TAG, "스터디참가버튼_response : " + response);
                            // 값 중복유무, 추가성공유무를 받는 코드 작성하기
                            Boolean overlap = jsonObject.getBoolean("overlap");
                            Log.e(TAG, "overlap :" + overlap);
                            //Log.e(TAG, "overlap : " + overlap);
                            //Log.e(TAG, "순서 : 1");
                            //Log.e(TAG, "response4");
                            // 서버로부터 받은 메세지에 따른 기능 구현하기
                            // 1)중복된 값이 있는지 확인하기
                            if (!overlap) {
                                Log.e(TAG, "overlap : 가입가능!");
                                // 1-1) 중복된 값이 없다면 추가하기
                                // 1-1-1)추가가 완료되면, '참여되었습니다'라는 다이얼로그 띄우기
                                if (jsonObject.getBoolean("success")) {
                                    Boolean success = jsonObject.getBoolean("success");
                                    Log.e(TAG, "success : 참여되었습니다");
                                    chat_num = jsonObject.getInt("chat_num");
                                    Log.e(TAG, "채팅 개수 : " + chat_num);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupViewActivity.this);
                                    dialog = builder.setMessage("참여되었습니다")
                                            // 1-1) 유저가 확인버튼 클릭시, '내 모임' 탭으로 이동하는 코드 작성하기
                                            .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
//                                                    finish();
                                                    Intent go_main_mygroup = new Intent(getApplicationContext(), MainActivity.class);
                                                    go_main_mygroup.putExtra("탭이동", "내모임");
                                                    startActivity(go_main_mygroup);
                                                    finish();

                                                }
                                            })
                                            .create();
                                    dialog.show();
                                } else {
                                    //Log.e(TAG, "success : 참여못했습니다");
                                    // 1-1-2)에러메세지를 받으면, '참여하지 못했습니다. 다시 시도해주세요.'라고 다이얼로그 띄우기
                                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupViewActivity.this);
                                    dialog = builder.setMessage("참여하지 못했습니다. 다시 시도해주세요")
                                            .setNegativeButton("확인", null)
                                            .create();
                                    dialog.show();
                                }
                            } else {
                                //Log.e(TAG, "overlap : 이미 가입된 스터디입니다!");
                                // 1-2) 중복된 값이 있다면 '이미 가입한 스터디입니다'라고 다이얼로그 띄우기
                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupViewActivity.this);
                                dialog = builder.setMessage("이미 가입한 스터디입니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                                group_join_btn.setVisibility(View.GONE);
                            }

                            // 1-1) 중복된 값이 없다면 추가하기
                        } catch (Exception e) { // 오류가 발생했을 경우
                            e.printStackTrace();
                            //Log.e(TAG, "response7");
                        }
                    }
                };

                SharedPreferences SP = getSharedPreferences("login_user", MODE_PRIVATE);
                String json = SP.getString("login_user", "");
                Gson gson = new Gson();
                Login_user_info item = gson.fromJson(json, Login_user_info.class);
                String user_no_s = item.getUser_no(); //유저고유번호
                String group_no_s = String.valueOf(group_no);//스터디그룹고유번호
//                Log.e(Tag,"group_no_s : "+group_no_s);

                // 파라미터 : 유저고유번호, 스터디그룹고유번호,리스너
                JoinGroupRequest joinGroupRequest = new JoinGroupRequest(user_no_s, group_no_s, responseListener);
                // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                RequestQueue queue = Volley.newRequestQueue(GroupViewActivity.this);
                // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
                queue.add(joinGroupRequest);
            }
        });

       /* // 삭제버튼
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupViewActivity.this);
                dialog = builder.setMessage("삭제하시겠습니까").setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // '확인'을 누르면, 다이얼로그, 모임상세화면 닫히고, 삭제가 됨.
                        dialog.cancel();
                        finish();
                        Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Log.e("error", "1");
                                try {
                                    //Log.e("error", "233");
                                    //Log.e("response", response);

                                    JSONObject jsonResponse = new JSONObject(response);
                                    // success : 해당 과정이 정상적으로 수행 되었는지 response값
                                    // Log.e("debug", "2");
                                    boolean success = jsonResponse.getBoolean("success");
                                    // Log.e("error", "3");
                                    if (success) { // 정상적으로 삭제되면, 현재 보이는 상세페이지는 닫힘 & 회원
                                        finish();
                                        group_no = jsonResponse.getInt("group_no");
                                        Log.e("group_no", "" + group_no);

                                    } else { // 모임이 삭제가 안됐다면, 삭제하지 못했다는 다이얼로그를 띄운다
                                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupViewActivity.this);
                                        AlertDialog dialog = builder.setMessage("모임을 삭제하지 못했습니다")
                                                .setNegativeButton("확인", null)
                                                .create();
                                        dialog.show();
                                    }
                                } catch (Exception e) { // 오류가 발생했을 경우
                                    e.printStackTrace();
                                    // Log.e("error", "999");
                                }
                                // Log.e("error", "100");

                            }
                        };

                        DeleteGroup_request deleteGroup_request = new DeleteGroup_request(group_no, responseListener1);
                        // 그룸생성 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                        RequestQueue queue = Volley.newRequestQueue(GroupViewActivity.this);
                        // 큐에 그룹 큐를 등록한다
                        queue.add(deleteGroup_request);

                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                dialog.show();
            }
        });*/


        //모임환경설정 버튼
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences SP = getSharedPreferences("login_user", MODE_PRIVATE);
                String json = SP.getString("login_user", "");
                Gson gson = new Gson();
                Login_user_info item = gson.fromJson(json, Login_user_info.class);
                String login_user_no = item.getUser_no();

                // 로그인한 유저인지 확인
                if (SP.contains("login_user")) {
                    //유저가 모임장인 경우
                    if (login_user_no.equals(group_leader_no_s)) {
                        Log.e(TAG, "세팅_로그인 한 유저 : " + login_user_no);
                        Log.e(TAG, "세팅_모임장번호 : " + group_leader_no_s);
                        Log.e(TAG, "모임장임");
                        Intent go_setting = new Intent(getApplicationContext(), GroupSettingActivity.class);

                        go_setting.putExtra("항목", "모임수정");

                        // 1-1 모임추가화면에 기존 저장되어있는 정보들을 intent로 보냄 (7가지인지 확인)
                        go_setting.putExtra("group_no_i", group_no);
                        go_setting.putExtra("group_title_s", group_title_s);
                        go_setting.putExtra("group_desc_s", group_desc_s);
                        go_setting.putExtra("group_introduce_s", group_introduce_s);
                        go_setting.putExtra("group_region_s", group_region_s);
                        go_setting.putExtra("group_cate_s", group_cate_s);
                        go_setting.putExtra("group_max_num_i", "" + group_max_num_i);
                        go_setting.putExtra("group_image_s", "" + group_image_s);

                        go_setting.putExtra("유저구분", "모임장임");
                        startActivityForResult(go_setting, LEADER_MODIFY_NUM);
                        // 모임장 리퀘스트번호 0;

                    } else {
                        // 모임원인경우
                        if (!participantInfoList.isEmpty()) {
                            for (int i = 0; i < participantInfoList.size(); i++) {
                                participant_user_no = participantInfoList.get(i).getParticipant_no();
                                if (login_user_no.equals(participant_user_no)) { //유저가 모임원인 경우
                                    //Log.e(TAG, "세팅_로그인 한 유저 : " + login_user_no);
                                    //Log.e(TAG, "세팅_모임원번호 : " + participant_user_no);
                                    //Log.e(TAG, "모임원임");
                                    user_role = "모임원임";
                                    break;

                                } else {
                                    //로그인은 했지만, 모임구성원이 아닌경우
                                    //Log.e(TAG, "세팅_로그인 한 유저 : " + login_user_no);
                                    //Log.e(TAG, "모임구성원아님");
                                    user_role = "모임구성원아님";
                                }
                            }
                            Intent go_setting = new Intent(getApplicationContext(), GroupSettingActivity.class);
                            go_setting.putExtra("유저구분", user_role);
                            go_setting.putExtra("group_no_i", group_no);
                            Log.e(TAG, "유저구분 : " + user_role);
                            startActivityForResult(go_setting, MEMBER_MODIFY_NUM);
                        }
                    }

                }
                //로그인 안한 유저
                else {
                    Log.e(TAG, "로그인 안한 유저");
                    Toast.makeText(getApplicationContext(), "로그인 후 이용가능합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        // 채팅탭을 클릭했을 때
//        chatTab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent go_chat = new Intent(getApplicationContext(),StudyViewChatActivity.class);
//                startActivity(go_chat);
//            }
//        });
//
//        // 설정탭을 클릭했을 때
//        settingTab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent go_chat = new Intent(getApplicationContext(),StudyViewSettingActivity.class);
//                startActivity(go_chat);
//            }
//        });


    }

//    public void joinBtn(){
//        // 스터디참가버튼1
////        SharedPreferences SP= getSharedPreferences("login_user", Context.MODE_PRIVATE);
////        // SharedPreferences SP1= getSharedPreferences("group", Context.MODE_PRIVATE);
////        //SharedPreferences.Editor editor = SP1.edit();
////        String json = SP.getString("login_user", "");
////        Gson gson = new Gson();
////        Login_user_info item = gson.fromJson(json, Login_user_info.class);
////        String login_user_no = item.getUser_no();
//
//        // 모임원이거나 모임장인 경우에는 참가하기버튼 숨기기
//        Log.e(TAG, "group_leader_no: "+group_leader_no_s);
//        Log.e(TAG, "login_user_no: "+login_user_no);
//        Log.e(TAG, "participant_user_no: "+participant_user_no);
//        if(SP.contains("login_user")) {
//            if(login_user_no.equals(group_leader_no_s)) {
//                group_join_btn.setVisibility(View.GONE);
//            }else {
//                group_join_btn.setVisibility(View.VISIBLE);
//
//            }
////            editor.clear();
////            editor.apply();
//        }
//    }

    // 뒤로가기 버튼이 눌러졌을 때, 나오는 모임검색화면이 나오게 한다.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 모임정보 받아오기
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String SERVER_URL = new Variable().getURL();
        String target;

        SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
        String json = SP.getString("login_user", "");
        Gson gson = new Gson();
        Login_user_info item = gson.fromJson(json, Login_user_info.class);
        String login_user_no = item.getUser_no();

        @Override
        protected void onPreExecute() {
            try {
                Log.e(Tag, "모임정보 받아오기위해서 보낼 group_no :" + group_no);
                Log.e(Tag, "모임정보 받아오기위해서 보낼 user_no :" + login_user_no);
                target = SERVER_URL + "viewGroup.php?group_no=" + group_no + "&user_no=" + login_user_no;
                // Log.e("target", target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                // 서버에 접속할 수 있도록 연결
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                // 넘어오는 결과값들 저장
                InputStream inputStream = httpURLConnection.getInputStream();
                // inputStream 내용들을 buffer에 담아서 읽을 수 있게함
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                // temp에 내용을 하나씩 읽어와서 문자형태로 저장할 수 있게 함
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                // 버퍼에서 받아온 값을 한줄씩 temp에 넣어서 읽기. null이 나올때까지!
//                while ((temp= bufferedReader.readLine()) != null){
                // stringbuilder에는 한줄씩 추가하면서 temp에 넣음
                temp = bufferedReader.readLine();
                stringBuilder.append(temp + "\n");
                // Log.e("tmp", temp);
//                }

                // 끝나면, bufferedReader와 inputStream은 닫아줌 & 연결도 끊어줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim(); //문자열들 반환


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) { //결과처리
            try {
                Log.e(Tag, "모임정보들을 가져옴");
//                AlertDialog dialog;
//                AlertDialog.Builder builder = new AlertDialog.Builder(GroupViewActivity.this.getApplicationContext());
//                dialog = builder.setMessage(result).setPositiveButton("확인",null).create();
//                dialog.show();
//                Log.e("순서", "1");

                JSONObject jsonObject = new JSONObject(result);
                // reponse 어레이리스트 받아옴
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                Log.e(Tag, "모임정보가져오는 response : "+jsonArray);
                int count = 0;

                // Log.e(TAG, "count = " + count + " length = " + jsonArray.length());
//                while (count < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(count);
                Log.e(Tag, "object : "+object);

                group_title_s = object.getString("group_title");  //모임제목
                Log.e("모임제목 가져옴1", group_title_s);
                group_leader_nick = object.getString("user_nick"); //모임장이름
                Log.e("group_leader_nick 가져옴", group_leader_nick);
                group_desc_s = object.getString("group_desc");  //모임설명
                Log.e("group_desc_s", group_desc_s);
                group_introduce_s = object.getString("group_introduce"); //모임 한줄 소개
                Log.e("group_introduce_s", group_introduce_s);
                group_cate_s = object.getString("group_cate"); //모임카테고리
                Log.e("group_cate_s", group_cate_s);
                group_region_s = object.getString("group_region"); //활동지역
                Log.e("group_region_s", group_region_s);
                group_max_num_i = object.getInt("group_max_num"); //모임정원
                Log.e("group_max_num_i", "" + group_max_num_i);
                group_image_s = object.getString("group_image");
                //Log.e(Tag, "이미지 가져옴 : " + group_image_s); // 모임사진

                //if(object.getInt("chat_num"))
                chat_num = object.getInt("chat_num");
                Log.e(Tag, "chat_num 가져옴 : " + chat_num);

                // 각각에 각각의 정보들을 넣어줌
                String URL = new Variable().getURL();
                //Log.e(Tag,"URL : "+URL);
                Glide
                        .with(getApplicationContext())
//                    .load(mapList.get(position).getPlace_image())
                        .load(URL + group_image_s)
//                        .override(200,100) // 이미지 크기 변경
                        .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                        .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                        .into(group_image);

                //Log.e(Tag,"주소 : "+URL+group_image_s);

                group_title.setText(group_title_s);
                group_leader.setText(group_leader_nick);
                group_desc.setText(group_desc_s);
                group_introduce.setText(group_introduce_s);
                group_cate.setText(group_cate_s);
                group_region.setText(group_region_s);
                group_max_num.setText("" + group_max_num_i);
//                    count++;

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Do something after 100ms
//                        Intent go_chat = getIntent();
//                        Log.e(Tag,"채팅화면으로 가자! 시작!");
//                        Log.e(Tag,"group_no : "+group_no);
//
//                        String a = go_chat.getStringExtra("chat");
//                        if(a!=null){
//                            group_chat_btn.performClick();
//                            Log.e(Tag,"채팅버튼 클릭");
//                            Log.e(Tag,"채팅화면으로 가자! 끝!");
//                        }
//                    }
//                }, 1000);
                getParticipate();


//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 0){
                Log.e(TAG,"여기로 가는건 아니겠지?");
                finish();
            }else if(requestCode== LEADER_MODIFY_NUM){
                Log.e(TAG,"여기로 가긴 가나?");
                if(data != null){
                    if(data.getStringExtra("항목").equals("모임수정완료")) {//use data
                        Log.e(TAG, "여기서부터 안되는건가!");
                        group_no = data.getIntExtra("group_no_i", 0);
                        group_title_s = data.getStringExtra("group_title_s");
                        group_desc_s = data.getStringExtra("group_desc_s");
                        group_introduce_s = data.getStringExtra("group_introduce_s");
                        group_region_s = data.getStringExtra("group_region_s");
                        String group_max_num_s = data.getStringExtra("group_max_num_i");
                        group_max_num_i = Integer.valueOf(group_max_num_s);
                        group_image_s = data.getStringExtra("group_image_s");

                        Log.e(Tag, "받는다 group_no : " + group_no);
                        Log.e(Tag, "받는다 group_title_s : " + group_title_s);
                        Log.e(Tag, "받는다 group_introduce_s : " + group_introduce_s);
                        Log.e(Tag, "받는다 group_cate_s : " + group_cate_s);
                        Log.e(Tag, "받는다 group_region_s : " + group_region_s);
                        Log.e(Tag, "받는다 group_max_num_i : " + group_max_num_i);
                        Log.e(Tag, "받는다 group_image_s : " + group_image_s);

                        String URL = new Variable().getURL();

                        Glide
                                .with(getApplicationContext())
//                    .load(mapList.get(position).getPlace_image())
                                .load(URL + group_image_s)
//                        .override(200,100) // 이미지 크기 변경
                                .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                                .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                                .into(group_image);

                        //Log.e(Tag,"주소 : "+URL+group_image_s);

                        group_title.setText(group_title_s);
                        group_leader.setText(group_leader_nick);
                        group_desc.setText(group_desc_s);
                        group_introduce.setText(group_introduce_s);
                        group_cate.setText(group_cate_s);
                        group_region.setText(group_region_s);
                        group_max_num.setText("" + group_max_num_i);
                    }
                }else{
                    finish();
                    Log.e(Tag,"리더) 모임삭제했으니까 groupView 닫는다");
                }
            }
            else if(requestCode== MEMBER_MODIFY_NUM) {
                if(data==null){
                    finish();
                    Log.e(Tag,"참여자) 모임탈퇴했으니까 groupView 닫는다");
                }
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart");
        new BackgroundTask().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    public void getParticipate() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "참여자리스트가져오는 response : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e(TAG, "첫번째 response : "+jsonObject);
//                    Log.e(TAG, "1");
                    //Boolean success = jsonObject.getBoolean("success");
                    JSONObject object_response = jsonObject.getJSONObject("response");
//                    chat_num = object_response.getInt("chat_num");
//                    Log.e(Tag,"채팅개수 가져옴 : "+chat_num);

                    JSONArray jsonArray_leader = object_response.getJSONArray("leader");
                    JSONObject jsonObject_leader = jsonArray_leader.getJSONObject(0);
                    //Log.e(TAG,"object_response"+object_response);
                    group_leader_no_s = jsonObject_leader.getString("leader_no");
                    //Log.e(TAG,"group_leader_server: "+group_leader_no_s);
                    String leader_nick = jsonObject_leader.getString("leader_nick");
                    String leader_gender = jsonObject_leader.getString("leader_gender");
                    String leader_age = jsonObject_leader.getString("leader_age");
                    String leader_photo = jsonObject_leader.getString("leader_photo");
                    Log.e(TAG,"leader_photo로그보기: "+leader_photo );

                    /*프사 원모양으로 나오게 하기*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        leader_image.setBackground(new ShapeDrawable(new OvalShape()));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        leader_image.setClipToOutline(true);
                    }

                    // 각각에 각각의 정보들을 넣어줌
                    String URL = new Variable().getURL();
                    //Log.e(Tag,"URL : "+URL);
                    //Log.e(Tag,"불러오는 leader_photo : "+leader_photo);
                    Glide
                            .with(getApplicationContext())
//                    .load(mapList.get(position).getPlace_image())
                            .load(URL + leader_photo)
//                        .override(200,100) // 이미지 크기 변경
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                            .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                            .into(leader_image);


                    //Log.e(Tag, "불러오는 leader_photo 주소 : " + URL + leader_photo);
                    //Log.e(Tag, "지금 리더사진에 세팅된 주소 불러오기 : " +leader_image.getDrawable().toString());


                    // group_item에 각각의 정보들을 넣어줌
//                    int leader_photo = R.drawable.crown; //왕관 넣기
                    participant_info leaderInfo = new participant_info(group_leader_no_s, leader_photo, leader_nick, leader_gender, leader_age);
                    //Log.e(TAG,"leaderInfo : "+leaderInfo);

                    SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
                    String json = SP.getString("login_user", "");
                    Gson gson = new Gson();
                    Login_user_info item = gson.fromJson(json, Login_user_info.class);
                    String login_user_no = item.getUser_no();

                    // 리더가 중복되지 않는다면, 어레이리스트에 추가
//                    if (!participantInfoList.contains(leaderInfo)) {
//                        participantInfoList.add(leaderInfo);
//                        Log.e(TAG, "리더가 추가되었다!");
//                    }else{
//                        participantInfoList.remove(leaderInfo);
//                    }
                    if (participantInfoList.isEmpty()) {
                        participantInfoList.add(leaderInfo);
                        //Log.e(TAG, "리더가 추가되었다!");
                    }

                    if (SP.contains("login_user")) {
                        Log.e(TAG,"login_user_no :"+login_user_no);
                        if (login_user_no.equals(group_leader_no_s)) {
                            /*현재 접속한 유저가 모임장인 경우, */
                            //Log.e(TAG,"모임장=유저? :true");

                            /*채팅하기 버튼이 나오게한다 */
                            group_join_btn.setVisibility(View.GONE);
                            group_chat_btn.setVisibility(View.VISIBLE);
//                            group_join_btn.setText("채팅하기");
                            setting_btn.setVisibility(View.VISIBLE);
                        } else {
                            /*현재 접속한 유저가 모임장이 아닌 경우, */
                            // Log.e(TAG,"모임장=유저? :false");
                            // 유저가 모임장이 아니면, 참가하기버튼이 나온다. 그리고 모임환경설정버튼은 안나온다.
                            // 모임환경설정버튼은 참가하기버튼 반대!
                            group_join_btn.setVisibility(View.VISIBLE);
                            group_chat_btn.setVisibility(View.GONE);
                            setting_btn.setVisibility(View.GONE);
                        }
                    }

//
                    JSONArray jsonArray_member = object_response.getJSONArray("participant");
                    //Log.e(TAG,"jsonArray_member : "+jsonArray_member);

                    String user_nick, user_gender, user_age, user_photo;


                    // 참여자가 1명인 경우(리더)에만 참여자들 추가시키기
                    if (participantInfoList.size()==1) {
                        //Log.e(TAG, "group_leader_server: " + group_leader_no_s);
                        //if(success) {
                        for (int i = 0; i < jsonArray_member.length(); i++) {
                            JSONObject object = jsonArray_member.getJSONObject(i);
                            participant_user_no = object.getString("user_no");
                            if (!group_leader_no_s.equals(participant_user_no)) {
                                user_nick = object.getString("user_nick");
                                user_gender = object.getString("user_gender");
                                user_age = object.getString("user_age");
                                user_photo = object.getString("user_photo");
                                Log.e(TAG,"user_photo 로그보기: "+user_photo );

                                // group_item에 각각의 정보들을 넣어줌
                                //user_photo = R.drawable.crown;
//                        int user_photo = R.drawable.camera;
                                participant_info participantInfo = new participant_info(participant_user_no, user_photo, user_nick, user_gender, user_age);
                                // 어레이리스트에 추가
                                // Log.e(TAG,"participantInfo : "+participantInfo);

                                    participantInfoList.add(participantInfo);
                                    //Log.e(TAG, "참여자가 추가되었다!");

                                //Log.e(Tag,"7-16");
//                        SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
//                        // SharedPreferences SP1= getSharedPreferences("group", Context.MODE_PRIVATE);
//                        //SharedPreferences.Editor editor = SP1.edit();
//                        String json = SP.getString("login_user", "");
//                        Gson gson = new Gson();
//                        Login_user_info item = gson.fromJson(json, Login_user_info.class);
//                        String login_user_no = item.getUser_no();

                                //Log.e(TAG, "group_leader_no_s가입버튼 :" + group_leader_no_s);
                                // 모임원이거나 모임장인 경우, 스터디가입하기버튼 안보이게하기
                                //Log.e(TAG, "participant_user_no :(참여자)" + participant_user_no);
                                //Log.e(TAG, "login_user_no :(참여자)" + login_user_no);

                                if (SP.contains("login_user")) {
                                    // Log.e(TAG,"로그인한유저있나? :true(참여자)");
                            /*현재 접속한 유저가 모임원인 경우, */
                                    if (login_user_no.equals(participant_user_no)) {
                                        //Log.e(TAG,"모임원=유저? :true");
                                /*채팅하기 버튼이 나오게한다 */
                                        group_join_btn.setVisibility(View.GONE);
                                        group_chat_btn.setVisibility(View.VISIBLE);
//                                group_join_btn.setText("채팅하기");
                                        setting_btn.setVisibility(View.VISIBLE);
//                                if(login_user_no.equals(participant_user_no)){
//                                    Log.e(TAG,"모임원=유저? :true");
//                                    group_join_btn.setVisibility(View.GONE);
                                    }
//
                                }
                            }
                        }
                    }

                    Intent go_chat = getIntent();
                    //Log.e(Tag, "채팅화면으로 가자! 시작!");
                    Log.e(Tag, "group_no : " + group_no);

                    String a = go_chat.getStringExtra("chat");
                    if (a != null) {
                        group_chat_btn.performClick();
                        //Log.e(Tag, "채팅버튼 클릭");
                        //Log.e(Tag, "채팅화면으로 가자! 끝!");
                    }
//                    else{
//                        Log.e(TAG,"로그인한유저있나? :ㄴㄴ");
//                        group_join_btn.setVisibility(View.GONE);
//                    }
                    //Log.e(TAG,"로그인한유저있나? :"+SP.contains("login_user"));
                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                    //Log.e(TAG, "response7");
                }
                participantAdapter.notifyDataSetChanged();
            }
        };
        // 파라미터 : 스터디그룹고유번호, 리스너
        ParticipantListRequest participantListRequest = new ParticipantListRequest(group_no, responseListener);
        // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
        queue.add(participantListRequest);
    }


}
