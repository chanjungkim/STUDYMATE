package com.example.gp62.studymate;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.Map;

public class chat_photo_Request extends StringRequest {// 서버에 로그인 요청함

    static String SERVER_URL = new Variable().getURL();
    final static private String URL = SERVER_URL + "ChatPhoto.php";

    private Map<String, String> parameters;
    String Tag = "chat_photo_Request";

    // Response.Listener : 응답받는 리스너
    public chat_photo_Request(String user_no, String group_no, JsonArray chat_photo, Response.Listener<String> listener) {
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST, URL, listener, null);
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        // 각 값들을 파라미터로 매칭시켜줌

        parameters.put("user_no", user_no);
        parameters.put("group_no", group_no);
        parameters.put("chat_photo",""+chat_photo);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
