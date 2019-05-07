package com.example.gp62.studymate;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class EmailValidateRequest extends StringRequest { // 이메일이 사용가능한지 체크

    static String SERVER_URL = new Variable().getURL();
    final static private String URL = SERVER_URL + "email_validate.php";
    String Tag = "EmailValidateRequest";

    private Map<String, String> parameters;

    // 생성자
    // Response.Listener : 응답받는 리스너
    public EmailValidateRequest(String user_email, Response.Listener<String> listener){
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST,URL,listener,null);
        Log.e(Tag, "1");
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        Log.e(Tag, "2");
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("user_email",user_email);
        Log.e(Tag, "3");
        Log.e("join_validate_request", ""+user_email);
    }

    @Override
    public Map<String,String> getParams() {
        return parameters;
    }
}