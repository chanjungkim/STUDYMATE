package com.example.gp62.studymate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

public class GroupSettingActivity extends AppCompatActivity {

    String Tag = "GroupSettingActivity";
    android.support.v7.widget.Toolbar toolbar;

    //그룹정보
    int group_no;
    String group_title_s;
    String group_desc_s;
    String group_introduce_s;
    String group_region_s;
    String group_cate_s;
    String group_max_num_i;
    String group_image_s;

    // 유저가 스터디원인 경우 설정화면에서 안나와야하는 것
    LinearLayout Linear_group_manage; // 그룹정보설정
    LinearLayout Linear_join_alarm; // 가입알림
    LinearLayout Linear_member_manage; // 멤버관리섹션

    // 유저가 스터디원인 경우 수정되어야 하는 텍스트
    // 그룹삭제 -> 그룹탈퇴
    TextView group_delete;

    // 기본 세팅
    TextView group_modify; // 그룹정보수정하기
    TextView group_leader_entrust;// 리더위임하기
    TextView group_member_out;// 멤버강퇴관리

    // 인텐트 번호관리
    int LEADER_MODIFY_NUM = 3333;
    int MEMBER_MODIFY_NUM = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);

        Log.e(Tag, "");
        Linear_group_manage = findViewById(R.id.Linear_group_manage);
        Linear_join_alarm = findViewById(R.id.Linear_join_alarm);
        Linear_member_manage = findViewById(R.id.Linear_member_manage);
        group_delete = findViewById(R.id.group_delete);
        group_modify = findViewById(R.id.group_modify);
        group_leader_entrust = findViewById(R.id.group_leader_entrust);
        group_member_out = findViewById(R.id.group_member_out);

        toolbar = findViewById(R.id.group_setting_toolbar);

        // 툴바에 뒤로가기버튼 세팅
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra("유저구분")) {
            group_no = intent.getIntExtra("group_no_i", 0);
            Log.e(Tag, "그룹번호 :  " + group_no);
            Log.e(Tag, "유저구분 : " + intent.getStringExtra("유저구분"));
            if (intent.getStringExtra("유저구분").equals("모임장임")) { // 모임장인 경우,
                Log.e(Tag, "모임장임");

            } else if (intent.getStringExtra("유저구분").equals("모임원임")) { // 모임원인 경우
                Log.e(Tag, "모임원임");
                Linear_group_manage.setVisibility(View.GONE);
                Linear_join_alarm.setVisibility(View.GONE);
                Linear_member_manage.setVisibility(View.GONE);
                group_delete.setText("모임탈퇴");

            } else if (intent.getStringExtra("유저구분").equals("모임구성원아님")) { //모임구성원이 아닌 경우
                Log.e(Tag, "모임구성원아님");
            }
        }

        // 그룹정보수정을 위해, 정보들 받기
        if (intent.hasExtra("항목")) {
            String modify_s = intent.getStringExtra("항목");
            if (modify_s.equals("모임수정")) {
                group_no = intent.getIntExtra("group_no_i", 0);
                group_title_s = intent.getStringExtra("group_title_s");
                group_desc_s = intent.getStringExtra("group_desc_s");
                group_introduce_s = intent.getStringExtra("group_introduce_s");
                group_region_s = intent.getStringExtra("group_region_s");
                group_cate_s = intent.getStringExtra("group_cate_s");
                group_max_num_i = intent.getStringExtra("group_max_num_i");
                group_image_s = intent.getStringExtra("group_image_s");

//                Log.e(Tag, "group_no : " + group_no);
//                Log.e(Tag, "group_title_s : " + group_title_s);
//                Log.e(Tag, "group_region_s : " + group_region_s);
//                Log.e(Tag, "group_cate_s : " + group_cate_s);
//                Log.e(Tag, "group_max_num_i : " + group_max_num_i);
//                Log.e(Tag, "group_image_s : " + group_image_s);
            }
        }

        // 그룹정보수정버튼을 누르면, 그룹정보수정화면(그룹추가수정화면)으로 간다.
        group_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_modify = new Intent(getApplicationContext(), AddGroupActivity.class);
                go_modify.putExtra("항목", "모임수정");

                // 그룹상세화면에 저장되어있는 정보들을 intent로 보냄
                go_modify.putExtra("group_no_i", group_no);
                go_modify.putExtra("group_title_s", group_title_s);
                go_modify.putExtra("group_desc_s", group_desc_s);
                go_modify.putExtra("group_introduce_s", group_introduce_s);
                go_modify.putExtra("group_region_s", group_region_s);
                go_modify.putExtra("group_cate_s", group_cate_s);
                go_modify.putExtra("group_max_num_i", "" + group_max_num_i);
                go_modify.putExtra("group_image_s", "" + group_image_s);
                startActivityForResult(go_modify,LEADER_MODIFY_NUM);
            }
        });

        //리더위임하기 항목을 누르면, 멤버선택화면으로 간다.
        group_leader_entrust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_leader_entrust = new Intent(getApplicationContext(), MemberManageActivity.class);
                go_leader_entrust.putExtra("멤버관리", "리더위임하기");
                startActivity(go_leader_entrust);
            }
        });

        // 스터디원인 경우는 -> 그룹탈퇴
        // 스터디리더인 경우는 -> 그룹삭제
        group_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 스터디원인 경우는 -> 그룹탈퇴
                Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(Tag, "response : " + response);

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            Log.e("success", "" + success);

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingActivity.this);

                                // 모임원이면, 탈퇴되었다는 다이얼로그가 나오게 함
                                Intent modify = getIntent();
                                if (modify.hasExtra("유저구분")) {
                                    String modify_s = modify.getStringExtra("유저구분");
                                    if (modify_s.equals("모임원임")) {
                                        modify.putExtra("항목","삭제");
                                        AlertDialog dialog = builder.setMessage("해당 그룹을 탈퇴하셨습니다")
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                        setResult(RESULT_OK);
                                                        finish();
                                                    }
                                                })
                                                .create();
                                        dialog.show();
                                    } else { // 모임장이면, 삭제되었다는 다이얼로그가 나오게 함
                                        modify.putExtra("항목","삭제");
                                        AlertDialog dialog = builder.setMessage("해당 스터디그룹이 삭제되었습니다")
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                        setResult(RESULT_OK);
                                                        finish();
                                                    }
                                                })
                                                .create();
                                        dialog.show();
                                    }
                                }

////
                            } else { // 탈퇴 혹은 삭제에 실패했다면, 실패했다는 다이얼로그를 띄운다
                                // 수정하기 버튼이면, 모임이 수정되었다는 다이얼로그가 나오게 함
                                Intent modify = getIntent();
                                if (modify.hasExtra("유저구분")) {
                                    String modify_s = modify.getStringExtra("유저구분");
                                    if (modify_s.equals("모임원임")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingActivity.this);
                                        AlertDialog dialog = builder.setMessage("해당 스터디그룹을 탈퇴하지 못했습니다. 관리자에게 문의해주세요")
                                                .setNegativeButton("확인", null)
                                                .create();
                                        dialog.show();
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingActivity.this);
                                        AlertDialog dialog = builder.setMessage("해당 스터디그룹을 삭제하지 못했습니다. 관리자에게 문의해주세요")
                                                .setNegativeButton("확인", null)
                                                .create();
                                        dialog.show();
                                    }
                                }
                            }
                        } catch (Exception e) { // 오류가 발생했을 경우
                            e.printStackTrace();
                            // Log.e("error", "999");
                        }
                        // Log.e("error", "100");

                    }
                };

                // 에디트텍스트 내용들을 가져옴
//                group_title_s = group_title.getText().toString(); //모임제목
//                group_desc_s = group_desc.getText().toString(); //모임설명
//                group_introduce_s = group_introduce.getText().toString(); //모임 한줄 소개
//                group_cate_s = group_cate.getText().toString(); // 모임카테고리
//                group_region_s = group_region.getText().toString(); // 활동지역
//                String group_max_num_s = group_max_num.getText().toString(); // 모임정원

                // 모임원인 경우,
                Intent modify = getIntent();
                if (modify.hasExtra("유저구분")) {
                    String modify_s = modify.getStringExtra("유저구분");
                    if (modify_s.equals("모임원임")) {
                        // 현재 로그인한 회원의 고유번호를 가져와서, 고유번호를 서버로 보낸다.
                        SharedPreferences SP = getSharedPreferences("login_user", MODE_PRIVATE);
                        String json = SP.getString("login_user", "");
                        Gson gson = new Gson();
                        Login_user_info login_user_info = gson.fromJson(json, Login_user_info.class);
                        String member_no_s = login_user_info.getUser_no();
                        int member_no_i = Integer.parseInt(member_no_s);

                        DeleteGroupRequest_member deleteGroupRequest_member = new DeleteGroupRequest_member(group_no, member_no_i, responseListener1);
                        // 그룸생성 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                        RequestQueue queue = Volley.newRequestQueue(GroupSettingActivity.this);
                        // 큐에 그룹 큐를 등록한다
                        queue.add(deleteGroupRequest_member);


                    } else { // 스터디리더인 경우는 -> 그룹삭제
               /*     Intent intent = getIntent();
                    if (intent.hasExtra("유저구분")) {
                        group_no = intent.getIntExtra("group_no_i", 0);
                        Log.e(Tag, "삭제할 그룹번호1 :  " + group_no);
                    }*/
                        Log.e(Tag, "삭제할 그룹번호2 : " + group_no);
                        DeleteGroupRequest_leader deleteGroupRequest_leader = new DeleteGroupRequest_leader(group_no, responseListener1);
                        // 그룸생성 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                        RequestQueue queue = Volley.newRequestQueue(GroupSettingActivity.this);
                        // 큐에 그룹 큐를 등록한다
                        queue.add(deleteGroupRequest_leader);
                    }
                }
                // 스터디리더인 경우는 -> 그룹삭제
            }
        });
    }

    // 뒤로가기 버튼이 눌러졌을 때, 나오는 모임검색화면이 나오게 한다.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                Intent go_view = new Intent();
                go_view.putExtra("항목", "모임수정완료");
                go_view.putExtra("group_no_i", group_no);
                go_view.putExtra("group_title_s", group_title_s);
                go_view.putExtra("group_desc_s", group_desc_s);
                go_view.putExtra("group_introduce_s", group_introduce_s);
                go_view.putExtra("group_region_s", group_region_s);
                go_view.putExtra("group_cate_s", group_cate_s);
                go_view.putExtra("group_max_num_i", "" + group_max_num_i);
                go_view.putExtra("group_image_s", "" + group_image_s);

                Log.e(Tag, "보낸다 group_no_i : "+ group_no);
                Log.e(Tag, "보낸다 group_title_s : "+ group_title_s);
                Log.e(Tag, "보낸다 group_introduce_s : "+ group_introduce_s);
                Log.e(Tag, "보낸다 group_cate_s : "+ group_cate_s);
                Log.e(Tag, "보낸다 group_region_s : "+ group_region_s);
                Log.e(Tag, "보낸다 group_max_num_i : "+ group_max_num_i);
                Log.e(Tag, "보낸다 group_image_s : "+ group_image_s);

                setResult(RESULT_OK,go_view);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== LEADER_MODIFY_NUM && resultCode==RESULT_OK){
            if(data.getStringExtra("항목").equals("모임수정")){//use data
                group_no = data.getIntExtra("group_no_i",0);
                group_title_s = data.getStringExtra("group_title_s");
                group_desc_s = data.getStringExtra("group_desc_s");
                group_introduce_s = data.getStringExtra("group_introduce_s");
                group_region_s = data.getStringExtra("group_region_s");
                group_max_num_i = data.getStringExtra("group_max_num_i");
                group_image_s = data.getStringExtra("group_image_s");
                Log.e(Tag, "받는다 group_no : "+ group_no);
                Log.e(Tag, "받는다 group_title_s : "+ group_title_s);
                Log.e(Tag, "받는다 group_introduce_s : "+ group_introduce_s);
                Log.e(Tag, "받는다 group_cate_s : "+ group_cate_s);
                Log.e(Tag, "받는다 group_region_s : "+ group_region_s);
                Log.e(Tag, "받는다 group_max_num_i : "+ group_max_num_i);
                Log.e(Tag, "받는다 group_image_s : "+ group_image_s);
            }
        }
    }


    public void onBackPressed() { // if child acticity end
        super.onBackPressed();
        Intent go_view1 = getIntent();
        go_view1.putExtra("항목", "모임수정완료");
        go_view1.putExtra("group_no_i", group_no);
        go_view1.putExtra("group_title_s", group_title_s);
        go_view1.putExtra("group_desc_s", group_desc_s);
        go_view1.putExtra("group_introduce_s", group_introduce_s);
        go_view1.putExtra("group_region_s", group_region_s);
        go_view1.putExtra("group_cate_s", group_cate_s);
        go_view1.putExtra("group_max_num_i", "" + group_max_num_i);
        go_view1.putExtra("group_image_s", "" + group_image_s);

        Log.e(Tag, "보낸다 group_no_i : "+ group_no);
        Log.e(Tag, "보낸다 group_title_s : "+ group_title_s);
        Log.e(Tag, "보낸다 group_introduce_s : "+ group_introduce_s);
        Log.e(Tag, "보낸다 group_cate_s : "+ group_cate_s);
        Log.e(Tag, "보낸다 group_region_s : "+ group_region_s);
        Log.e(Tag, "보낸다 group_max_num_i : "+ group_max_num_i);
        Log.e(Tag, "보낸다 group_image_s : "+ group_image_s);

        setResult(RESULT_OK,go_view1);
        finish();
    }
}
