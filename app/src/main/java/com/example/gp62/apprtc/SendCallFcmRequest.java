package com.example.gp62.apprtc;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.gp62.studymate.Variable;

import java.util.HashMap;
import java.util.Map;

public class SendCallFcmRequest extends StringRequest { // 이메일이 사용가능한지 체크

    static String SERVER_URL = new Variable().getURL();
    final static private String URL = SERVER_URL + "getTokenSendFcm.php";
    String Tag = "SendCallFcmRequest";

    private Map<String, String> parameters;

    // 유저번호, 토큰
    public SendCallFcmRequest(String room_num, String received_user_no,String send_user_no,String send_user_nick,Response.Listener<String> listener){
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST,URL,listener,null);
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("room_num",room_num);
        parameters.put("received_user_no",received_user_no);
        parameters.put("send_user_no",send_user_no);
        parameters.put("send_user_nick",send_user_nick);

        Log.e(Tag,"room_num : "+room_num);
        Log.e(Tag,"received_user_no : "+received_user_no);
        Log.e(Tag,"send_user_no : "+send_user_no);
        Log.e(Tag,"send_user_nick : "+send_user_nick);
        // Log.e(Tag, ""+parameters);
    }

    @Override
    public Map<String,String> getParams() {
        return parameters;
    }
}