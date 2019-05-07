package com.example.gp62.Map;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.gp62.studymate.Variable;

import java.util.HashMap;
import java.util.Map;

public class GoogleMapRequest extends StringRequest { // 이메일이 사용가능한지 체크

    static String SERVER_URL = new Variable().getURL();
    final static private String URL = SERVER_URL + "googleMap.php";
    String Tag = "GoogleMapRequest";

    private Map<String, String> parameters;

    // 생성자
    // Response.Listener : 응답받는 리스너
    // 현재위치의 경도, 위도, 검색어, 반경거리를 내 서버로 보내기
    public GoogleMapRequest(String current_longitude, String current_latitude, String keyword, int radius, Response.Listener<String> listener){
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST,URL,listener,null);
        Log.e(Tag, "1");
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        Log.e(Tag, "2");
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("current_longitude",current_longitude);
        parameters.put("current_latitude",current_latitude);
        parameters.put("keyword",keyword);
        parameters.put("radius",""+radius);
        Log.e(Tag, "3");
        // Log.e(Tag, ""+parameters);
    }

    @Override
    public Map<String,String> getParams() {
        return parameters;
    }
}