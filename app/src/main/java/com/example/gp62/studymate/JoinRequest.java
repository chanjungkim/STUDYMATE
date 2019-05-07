package com.example.gp62.studymate;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GP62 on 2018-04-27.
 */

public class JoinRequest extends StringRequest{// url에 회원가입해달라 요청

    static String SERVER_URL = new Variable().getURL();
    final static private String URL = SERVER_URL + "join.php";

    private Map<String, String> parameters;

    // 생성자
    // Response.Listener : 응답받는 리스너
    public JoinRequest(String user_email, String user_nick, String user_pass, String user_gender, String user_age, Response.Listener<String> listener){
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST,URL,listener,null);
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("user_email",user_email);
        parameters.put("user_nick",user_nick);
        parameters.put("user_pass",user_pass);
        parameters.put("user_gender",user_gender);
        parameters.put("user_age",user_age);
        Log.e("join_request", ""+user_email);
        Log.e("join_request", ""+user_nick);
        Log.e("join_request", ""+user_pass);
        Log.e("join_request", ""+user_gender);
        Log.e("join_request", ""+user_age);
    }

    @Override
    public Map<String,String> getParams() {
        return parameters;
    }

}

