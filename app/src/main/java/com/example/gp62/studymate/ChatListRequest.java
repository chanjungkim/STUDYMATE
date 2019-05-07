package com.example.gp62.studymate;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GP62 on 2018-05-11.
 */

public class ChatListRequest extends StringRequest {

    private Map<String, String> parameters;
    String Tag = "ChatListRequest";
    static String URL = new Variable().getURL();

    // 생성자
    // Response.Listener : 응답받는 리스너
    public ChatListRequest(String group_no, Response.Listener<String> listener) {
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST, URL + "ChatList.php", listener, null);
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("group_no", group_no);

        Log.e(Tag, "그룹번호 : " + group_no);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
