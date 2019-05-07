package com.example.gp62.studymate;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TokenSaveRequest extends StringRequest { // 이메일이 사용가능한지 체크

    static String SERVER_URL = new Variable().getURL();
    final static private String URL = SERVER_URL + "register_token.php";
    String Tag = "TokenSaveRequest";

    private Map<String, String> parameters;

    // 유저번호, 토큰
    public TokenSaveRequest(String user_no, String user_token, Response.Listener<String> listener){
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST,URL,listener,null);
        Log.e(Tag, "1");
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        Log.e(Tag, "2");
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("user_no",user_no);
        parameters.put("user_token",user_token);
        Log.e(Tag, "3");
        // Log.e(Tag, ""+parameters);
    }

    @Override
    public Map<String,String> getParams() {
        return parameters;
    }
}