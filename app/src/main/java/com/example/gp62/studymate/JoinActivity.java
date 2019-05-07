package com.example.gp62.studymate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {// 회원가입 화면
    //버튼
    Button join_check_btn, // 확인버튼을 누르면, 메인화면으로 넘어간다 MainActivity)
            email_validate_btn, // 이메일 중복 체크 버튼
            nick_validate_btn; // 닉네임 중복 체크 버튼

    String Tag = "JoinActivity";

    //문자열
    private String user_email, // 회원 이메일
            user_nick, // 회원 닉네임
            user_pass, // 회원 비밀번호
            user_pass_check, // 회원 비밀번호 체크
            user_gender, // 회원 성별
            user_age; // 회원연령대

    //체크박스
    AppCompatCheckBox check_use, // 이용약관 동의
            check_info; // 개인정보 동의

    // 연령대스피너
    private ArrayAdapter adapter;
    private Spinner spinner;

    // 다이얼로그 (알림창)
    private AlertDialog dialog;

    private boolean email_validate = false; // 이메일 중복체크버튼을 눌렀는지 확인하는 변수
    private boolean nick_validate = false; // 닉네임 중복체크버튼을 눌렀는지 확인하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        //버튼
        join_check_btn = (Button) findViewById(R.id.join_check_btn); // 회원가입버튼 (회원가입이 되면, 메인액티비티로 간다)
        email_validate_btn = (Button) findViewById(R.id.email_validate_btn); // 이메일 중복체크버튼 (사용가능한 이메일인지 검사한다)
        nick_validate_btn = (Button) findViewById(R.id.nick_validate_btn); // 닉네임 중복체크버튼 (사용가능한 닉네임인지 검사한다)

        //에디트텍스트
        final EditText emailText = (EditText) findViewById(R.id.emailText);
        final EditText nickText = (EditText) findViewById(R.id.nickText);
        final EditText passText = (EditText) findViewById(R.id.passText);
        final EditText pass_checkText = (EditText) findViewById(R.id.pass_checkText);

        //이용약관, 개인정보수집 체크박스
        check_use = (AppCompatCheckBox) findViewById(R.id.check_use);
        check_info = (AppCompatCheckBox) findViewById(R.id.check_info);

        //스피너 (회원연령대)
        spinner = (Spinner) findViewById(R.id.age_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.age, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //라디오그룹 (회원성별)
        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.gender_group);
        // 현재 선택된 genderGroup
        int genderGroupID = genderGroup.getCheckedRadioButtonId();
        // 현재 선택된 genderButton값(여자, 남자)이 user_gender 변수에 넣어줌
        user_gender = ((RadioButton) findViewById(genderGroupID)).getText().toString();
        //라디오버튼을 클릭했을 때, (여자->남자, 남자->여자)
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // genderBtn이란 라디오버튼은 체크된 라디오버튼임
                RadioButton genderBtn = (RadioButton) findViewById(checkedId);
                // 체크된 라디오버튼(genderBtn) 값을 user_gender변수에 넣어줌
                user_gender = genderBtn.getText().toString();
            }
        });

        // 이메일 중복체크버튼
        email_validate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 이메일 받아오기
                Log.e(Tag, "13");
                String user_email = emailText.getText().toString();
                Log.e(Tag, "13-1");
                // 중복체크버튼을 한 번 더 누르게 되는 경우, 다시 false 상태로 감(사용할 수 있는 아이디이면, true가 되고 그렇지 않다면 false로)
                email_validate = false;
                Log.e(Tag, "13-2");

                // 중복체크버튼을 누르지 않은 경우 (validate값이 false인경우!)
                // 이메일이 입력되었는지 확인한다
                // -> 이메일이 입력되지 않았다면 이메일을 입력해달라는 다이얼로그를 띄운다
                if (user_email.equals("")) {
                    Log.e(Tag, "13-3");
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("이메일을 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // -> 이메일이 입력되었다면, 이메일 형식이 맞는지 확인한다
                // -> 이메일 형식이 맞지 않다면, 이메일형식에 맞게 입력해달라는 다이얼로그를 띄운다
                if(!Util.validateEmail(user_email)){
                    Log.e(Tag, "13-4");
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("이메일 형식에 맞게 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // -> 이메일 형식이 맞다면, join_validate_request 생성자를 통해서 사용가능한 이메일인지 확인한다
                // 해당 웹사이트에 접속한 후, 특정한 json응답을 다시 받을 수 있도록 함
                Response.Listener<String> responseListener_email = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            Log.e(Tag, "13-5");
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.e(Tag, "13-6");
                            // success : 해당 과정이 정상적으로 수행 되었는지 response 값
                            boolean success = jsonResponse.getBoolean("success");
                            Log.e(Tag, "13-7");
                            Log.e("success", "" + success);
                            //만약 success가 되었다면 즉, 사용할 수 있는 이메일이라면,  다이얼로그를 띄어 사용가능한 이메일이라고 알린다
                            if (success) {
                                Log.e(Tag, "13-8");
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 이메일입니다")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                email_validate = true;
                                String email = jsonResponse.getString("email");
                                Log.e("email_성공", email);
                                return;
                            } else { // 중복체크에 실패했다면, 즉, 사용할 수 없는 이메일이라면,  다이얼로그를 띄어 중복된 이메일이 있다고 알린다
                                Log.e(Tag, "13-9");
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                dialog = builder.setMessage("이미 사용 중인 이메일입니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                                email_validate = false;
                                String email = jsonResponse.getString("email");
                                Log.e("email_실패", email);
                                return;
                            }
                        } catch (Exception e) { // 오류가 발생했을 경우
                            e.printStackTrace();
                        }
                    }
                };

                // 사용가능한 이메일인지 검사하기
                Log.e(Tag, "13-10");
                EmailValidateRequest emailValidateRequest = new EmailValidateRequest(user_email, responseListener_email);
                // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                Log.e(Tag, "13-11");
                RequestQueue queue = Volley.newRequestQueue(JoinActivity.this);
                // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
                Log.e(Tag, "13-12");
                queue.add(emailValidateRequest);
                Log.e(Tag, "13-13");
            }
        });

        // 닉네임 중복체크버튼
        nick_validate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 닉네임 받아오기
                Log.e(Tag, "1");
                String user_nick = nickText.getText().toString();
                // 중복체크버튼을 한 번 더 누르게 되는 경우, 다시 false 상태로 감(사용할 수 있는 아이디이면, true가 되고 그렇지 않다면 false로)
                Log.e(Tag, "2");
                nick_validate = false;
                Log.e(Tag, "3");

                // 중복체크버튼을 누르지 않은 경우 (validate값이 false인경우!)
                // 닉네임이 입력되었는지 확인한다
                // -> 닉네임이 입력되지 않았다면 닉네임을 입력해달라는 다이얼로그를 띄운다
                if (user_nick.equals("")) {
                    Log.e(Tag, "4");
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("닉네임을 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // -> 닉네임이 입력되었다면, 닉네임글자수가 2~5글자인지 확인한다.
                // -> 2~5글자가 아니라면, 닉네임은 2~5글자여야 한다고 알려준다.
                if((user_nick.length() < 2) || (user_nick.length() > 5)){
                    Log.e(Tag, "5");
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("닉네임은 2~5글자만 가능합니다")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // -> 닉네임 형식이 맞다면, nick_validate_request 생성자를 통해서 사용가능한 닉네임인지 확인한다
                // 해당 웹사이트에 접속한 후, 특정한 json응답을 다시 받을 수 있도록 함
                Response.Listener<String> responseListener_nick = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(Tag, "6");
                       //  Log.e("response", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Log.e(Tag, "7");
                            // success : 해당 과정이 정상적으로 수행 되었는지 response 값
                            boolean success = jsonResponse.getBoolean("success");
                            Log.e("success", "" + success);
                            //만약 success가 되었다면 즉, 사용할 수 있는 이메일이라면,  다이얼로그를 띄어 사용가능한 이메일이라고 알린다
                            if (success) {
                                Log.e(Tag, "7-1");
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 닉네임입니다")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                nick_validate = true;
                                String user_nick = jsonResponse.getString("user_nick");
                                Log.e("nick_성공", user_nick);
                                return;
                            } else { // 중복체크에 실패했다면, 즉, 사용할 수 없는 이메일이라면,  다이얼로그를 띄어 중복된 이메일이 있다고 알린다
                                Log.e(Tag, "8");
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                dialog = builder.setMessage("이미 사용 중인 닉네임입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                                nick_validate = false;
                                String user_nick = jsonResponse.getString("user_nick");
                                Log.e("nick_실패", user_nick);
                                return;
                            }
                        } catch (Exception e) { // 오류가 발생했을 경우
                            e.printStackTrace();
                        }
                    }
                };
                Log.e(Tag, "9");
                // 사용가능한 닉네임인지 검사하기
                NickValidateRequest nickValidateRequest = new NickValidateRequest(user_nick, responseListener_nick);
                Log.e(Tag, "10");
                // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                RequestQueue queue = Volley.newRequestQueue(JoinActivity.this);
                Log.e(Tag, "11");
                // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
                queue.add(nickValidateRequest);
                Log.e(Tag, "12");
            }
        });

        // 회원가입버튼
        join_check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 정보 가져오기
                String user_email = emailText.getText().toString();
                String user_nick = nickText.getText().toString();
                String user_pass = passText.getText().toString();
                String user_pass_check = pass_checkText.getText().toString();
                String user_age = spinner.getSelectedItem().toString();

                // 이메일을 입력했는지 확인한다 -> 입력을 안했다면 입력하라는 다이얼로그를 띄운다
                if (user_email.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("이메일을 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // -> 이메일을 입력했다면, 이메일 형식이 맞는지 확인한다
                // -> 이메일 형식이 맞지않다면 이메일 형식에 맞게 입력해달라는 다이얼로그를 띄운다
                if(!Util.validateEmail(user_email)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("이메일 형식에 맞게 입력해주세요")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // 이메일 중복체크를 했는지 확인한다. 중복체크가 되어있지 않다면, 중복체크를 해달라는 다이얼로그를 띄운다
                // (validate = false라면, 중복체크버튼을 클릭 안 한 것, true라면, 클릭 한 것)
                if (!email_validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("이메일 중복체크를 해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 닉네임을 입력했는지 확인한다 -> 입력을 안했다면 입력하라는 다이얼로그를 띄운다
                if (user_nick.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("닉네임을 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 닉네임 중복체크를 했는지 확인한다. 중복체크가 되어있지 않다면, 중복체크를 해달라는 다이얼로그를 띄운다
                // (validate = false라면, 중복체크버튼을 클릭 안 한 것, true라면, 클릭 한 것)
                if (!nick_validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("닉네임 중복체크를 해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // -> 비밀번호를 입력했는지 확인한다 -> 입력을 안했다면 입력하라는 다이얼로그를 띄운다
                if (user_pass.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("비밀번호를 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // -> 비밀번호가 입력되었다면, 비밀번호 형식에 맞는지 확인한다
                // -> 비밀번호 형식에 맞지 않다면, 비밀번호 형식에 맞게 입력해달라는 다이얼로그를 띄운다
                if(!Util.validatePassword(user_pass)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("비밀번호는 8~12 이내의 영문/숫자/특수문자의 조합입니다")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                // -> 비밀번호 형식이 맞다면, 비밀번호를 한 번 더 입력했는지 확인한다
                // -> 비밀번호를 한 번 더 입력하지 않았다면, 비밀번호를 한 번 더 입력해달라는 다이얼로그를 띄운다
                if (user_pass_check.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("비밀번호를 한 번 더 입력해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // -> 비밀번호와 비밀번호확인이 같은지 확인한다. 같지 않다면, 비밀번호 확인이 일치하지 않는다는 다이얼로그를 띄운다
                if (!user_pass.equals(user_pass_check)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("비밀번호와 비밀번호확인이 일치하지 않습니다")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // -> 비밀번호 확인까지 다 입력했다면, 연령대를 선택했는지 확인한다
                // -> 연령대를 선택하지 않았다면, 연령대를 선택해달라는 다이얼로그를 띄운다
                if (user_age.equals("----")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("연령대를 선택해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // -> 연령대를 선택했다면, 런치메이트 이용약관에 동의했는지 확인한다
                // -> 이용약관에 동의하지 않았다면, 이용약관에 동의해달라는 다이얼로그를 띄운다
                if (!check_use.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("이용약관에 동의해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // -> 이용약관에 동의했다면, 개인정보 취급방침에 동의했는지 확인한다.
                // -> 개인정보 취급방침에 동의하지 않았다면, 개인정보 취급방침에 동의해달라는 다이얼로그를 띄운다

                if (!check_info.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                    dialog = builder.setMessage("개인정보 취급방침에 동의해주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // -> 개인정보 취급방침에 동의했다면! join_request 생성자를 통해서 서버에 회원가입요청을 한다 (회원가입을 진행한다)
                // 해당 웹사이트에 접속한 후, 특정한 json응답을 다시 받을 수 있도록 함
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
                            //만약 success가 되었다면 즉, 회원가입에 성공했다면, 메인화면으로 넘어간다
                            if (success) {

                                String email = jsonResponse.getString("email");
                                Log.e("email", email);
                                String pass = jsonResponse.getString("pass");
                                Log.e("pass", pass);
                                String nick = jsonResponse.getString("nick");
                                Log.e("nick", nick);
                                String gender = jsonResponse.getString("gender");
                                Log.e("gender", gender);
                                String age = jsonResponse.getString("age");
                                Log.e("age", age);
                                // 회원가입 화면을 닫는다
                                Log.e("error", "4");
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                dialog = builder.setMessage("회원가입에 성공했습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 로그인화면으로 넘어간다
                                                Intent go_Login = new Intent(getApplicationContext(), LoginActivity.class);
                                                go_Login.putExtra("항목","이메일입력");
                                                String email = emailText.getText().toString();
                                                go_Login.putExtra("이메일",email);
                                                Log.e(Tag, "로그인화면으로 이메일보내기: "+email);
                                                startActivity(go_Login);
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();

//                                String result = jsonResponse.getString("result");
//                                Log.e("result", result);

                            } else { // 회원등록에 실패했다면, 회원가입에 실패했다는 다이얼로그를 띄운다
                                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                                dialog = builder.setMessage("회원가입에 실패했습니다")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();

                                String email = jsonResponse.getString("email");
                                Log.e("email", email);
                                String pass = jsonResponse.getString("pass");
                                Log.e("pass", pass);
                                String nick = jsonResponse.getString("nick");
                                Log.e("nick", nick);
                                String gender = jsonResponse.getString("gender");
                                Log.e("gender", gender);
                                String age = jsonResponse.getString("age");
                                Log.e("age", age);
                            }
                        } catch (Exception e) { // 오류가 발생했을 경우
                            e.printStackTrace();
                            // Log.e("error", "999");
                        }
                        // Log.e("error", "100");

                    }
                };

                // 회원가입하기
                JoinRequest joinRequest = new JoinRequest(user_email, user_nick, user_pass, user_gender, user_age, responseListener1);
                // 회원가입 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                RequestQueue queue = Volley.newRequestQueue(JoinActivity.this);
                // 큐에 회원가입 큐를 등록한다
                queue.add(joinRequest);
            }
        });
    }

    // 회원가입이 된 후, 회원가입 화면이 꺼지면 onStop이 실행됨
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            // 다이얼로그가 떠있다면, 다이얼로그를 없앰
            dialog.dismiss();
            dialog = null;
        }
    }


    public static class Util {
        // 이메일정규식
        public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        public static boolean validateEmail(String emailStr) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
            return matcher.find();
        }
        //비밀번호정규식
        public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{8,12}$"); // 8자리 ~ 12자리까지 가능
        public static boolean validatePassword(String pwStr) {
            Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
            return matcher.matches();
        }
    }

}
