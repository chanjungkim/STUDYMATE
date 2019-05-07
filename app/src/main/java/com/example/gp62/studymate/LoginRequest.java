package com.example.gp62.studymate;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {// 서버에 로그인 요청함

    static String SERVER_URL = new Variable().getURL();
    final static private String URL = SERVER_URL + "login.php";

    private Map<String, String> parameters;
    String Tag = "LoginRequest";

    // Response.Listener : 응답받는 리스너
    public LoginRequest(String user_email, String user_pass, Response.Listener<String> listener) {
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST, URL, listener, null);
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("user_email", user_email);
        parameters.put("user_pass", user_pass);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
