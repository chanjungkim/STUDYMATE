package com.example.gp62.studymate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    //버튼
    Button join_btn; // 회원가입 화면으로 넘어가는 버튼 (이메일 회원가입)
    Button login_btn; // 확인버튼을 누르면, 메인화면으로 넘어간다 MainActivity)
    String user_email, // 회원 이메일
            user_pass; // 회원 비밀번호

    // 다이얼로그
    private AlertDialog dialog;
    String Tag = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        join_btn = (Button) findViewById(R.id.join_btn);
        login_btn = (Button) findViewById(R.id.login_btn); // 로그인버튼 (로그인이 되면, 메인액티비티로 간다)

        //에디트텍스트
        final EditText emailText = (EditText) findViewById(R.id.emailText_login);
        final EditText passText = (EditText) findViewById(R.id.passText_login);

/*
        // 회원가입 후, 로그인화면으로 왔다면 이메일이 입력된다.
        Intent email_intent = getIntent();
        if(email_intent.getStringExtra("항목").equals("이메일입력")){
            String email = email_intent.getStringExtra("이메일");
            emailText.setText(email);
        }*/


        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_join = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(go_join);
            }
        });

        // 로그인버튼
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 이메일,비밀번호 받아오기
                user_email = emailText.getText().toString();
                user_pass = passText.getText().toString();
                // 이메일이 입력되지 않았다면 이메일을 입력해달라는 다이얼로그를 띄운다
                if (user_email.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    dialog = builder.setMessage("이메일을 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // 비밀번호가 입력되지 않았다면 비밀번호를 입력해달라는 다이얼로그를 띄운다
                if (user_pass.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    dialog = builder.setMessage("비밀번호를 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // 이메일과 비밀번호가 DB에 등록되어있는지 확인한다
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log.e(Tag, "response : "+response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            // success : 해당 과정이 정상적으로 수행 되었는지 response 값
                            boolean success = jsonResponse.getBoolean("success");

                            Log.e(Tag, "success : " + success);
                            //만약 success가 되었다면 즉, 로그인 되었다면, 쉐어드에 회원고유번호와 이메일 저장한다 & 메인액티비티로 간다
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("로그인되었습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                // 로그인화면을 끈다
                                                Intent intent = getIntent();

                                                if (intent.hasExtra("로그인전상황")) {
                                                    String situation_login = intent.getStringExtra("로그인전상황");
                                                    if (situation_login.equals("모임추가중로그인")) {
                                                        Intent go_addGroup = new Intent(getApplicationContext(), AddGroupActivity.class);
                                                        startActivity(go_addGroup);
                                                    } else if (situation_login.equals("설정중로그인")) {
//                                                        FragmentManager fragmentManager = getSupportFragmentManager();
//                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                                        fragmentTransaction.replace(R.id.content,new searchPlaceFragment()).commit();
                                                        //  fragmentTransaction.commit();
                                                        Intent go_main = new Intent(getApplicationContext(), MainActivity.class);
                                                        go_main.putExtra("탭이동", "모임검색");
                                                        startActivity(go_main);
                                                    } else if (situation_login.equals("내모임클릭")) {
                                                        Intent go_main = new Intent(getApplicationContext(), MainActivity.class);
                                                        go_main.putExtra("탭이동", "내모임");
                                                        startActivity(go_main);
                                                    }else{
                                                        Intent go_main = new Intent(getApplicationContext(), MainActivity.class);
                                                        go_main.putExtra("탭이동", "모임검색");
                                                        startActivity(go_main);
                                                    }
                                                    finish();
                                                }else{
                                                    Intent go_main = new Intent(getApplicationContext(), MainActivity.class);
                                                    go_main.putExtra("탭이동", "모임검색");
                                                    startActivity(go_main);
                                                }
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();

                                // 서버로부터 해당 회원정보를 받고, 쉐어드에 저장한다.
                                // 회원정보 : 회원의 고유번호, 이메일, 프로필사진, 닉네임, 성별, 연령대, 관심 지역, 관심 카테고리
                                Log.e(Tag, "1");
                                int user_no_i = jsonResponse.getInt("user_no");
                                Log.e(Tag, "user_no : " + user_no_i);
                                String user_no = String.valueOf(user_no_i);
                                Log.e(Tag, "user_no : " + user_no);
                                String user_email = jsonResponse.getString("user_email");
                                Log.e(Tag, "user_email : " + user_email);
                                String user_nick = jsonResponse.getString("user_nick");
                                Log.e(Tag, "user_nick : " + user_nick);
                                String user_gender = jsonResponse.getString("user_gender");
                                Log.e(Tag, "user_gender : " + user_gender);
                                String user_age = jsonResponse.getString("user_age");
                                Log.e(Tag, "user_age : " + user_age);
                               /* String user_cate = jsonResponse.getString("user_cate");
                                Log.e(Tag,"user_cate : "+user_cate);
                                String user_region = jsonResponse.getString("user_region");
                                Log.e(Tag,"user_region : "+user_region);
                                String user_photo = jsonResponse.getString("user_photo");
                                Log.e(Tag,"user_photo : "+user_photo);*/

                                Log.e(Tag, "2");
                                SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = SP.edit();
                                Log.e(Tag, "3");
                                editor.clear();
                                Gson gson = new Gson();
                                Log.e(Tag, "4");
                                Login_user_info login_user_info = new Login_user_info(user_no, user_email, "", user_nick, user_gender, user_age, "지역", "카테고리");
                                String json = gson.toJson(login_user_info);

                                editor.putString("login_user", json);
                                Log.e(Tag, "로그인 : " + json);
                                editor.apply();


                                // 토큰 생성
                                MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService(getApplicationContext());
                                Log.e(Tag,"로그인_토큰생성_user_no : "+user_no);
                                myFirebaseMessagingService.setUser_no(user_no);
                                myFirebaseMessagingService.onNewToken("");
                                Log.e(Tag, "토큰생성");

//                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//                                    @Override
//                                    public void onSuccess(InstanceIdResult instanceIdResult) {
//                                        String newToken = instanceIdResult.getToken();
//                                        Log.e(Tag,"토큰 생성함, 토큰 : "+newToken);
//                                        new MyFirebaseMessagingService().onNewToken(newToken);
//                                        Log.e(Tag,"서버에 토큰 저장하기");
//                                    }
//                                });

                                return;
                            } else { // 등록이 안되어있다면, '아이디 또는 비밀번호를 다시 확인하세요'라는 다이얼로그를 띄운다
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("아이디 또는 비밀번호를 다시 확인해주세요")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                                return;
                            }
                        } catch (Exception e) { // 오류가 발생했을 경우
                            e.printStackTrace();
                        }
                    }
                };

                // 사용가능한 이메일인지 검사하기
                LoginRequest LoginRequest = new LoginRequest(user_email, user_pass, responseListener);
                // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
                queue.add(LoginRequest);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(Tag, "onPause");
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    public void onBackPressed() {
        super.onBackPressed();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.content,new searchPlaceFragment()).commit();
////        fragmentTransaction.commit();
        Intent add_study_ing = getIntent();
        if (add_study_ing.getStringExtra("로그인전상황").equals("모임추가중로그인")) {
            Intent go_main = new Intent(getApplicationContext(), MainActivity.class);
            go_main.putExtra("탭이동", "모임검색");
            startActivity(go_main);
            finish();
        } else {
            Intent go_main = new Intent(getApplicationContext(), MainActivity.class);
            go_main.putExtra("탭이동", "모임검색");
            startActivity(go_main);
            finish();
        }
    }
}

