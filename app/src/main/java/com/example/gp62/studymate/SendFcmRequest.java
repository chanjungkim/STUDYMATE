package com.example.gp62.studymate;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GP62 on 2018-08-23.
 */

public class SendFcmRequest extends StringRequest {

    private Map<String, String> parameters;

    static String URL = new Variable().getURL()+ "send_fcm.php";

    String Tag = "SendFcmRequest";
    // 생성자
    // Response.Listener : 응답받는 리스너
    public SendFcmRequest(String token, String title, String content, String group_no, Response.Listener<String> listener) {
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST, URL, listener, null);
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("token", token);
        parameters.put("title", title);
        parameters.put("content", content);
        parameters.put("group_no", group_no);

//        Log.e(Tag, "token : " + token);
//        Log.e(Tag, "title : " + title);
//        Log.e(Tag, "content : " + content);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
