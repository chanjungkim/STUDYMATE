package com.example.gp62.studymate;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GP62 on 2018-05-11.
 */

public class ModifyGroupRequest extends StringRequest {

    private Map<String, String> parameters;

    static String URL =  new Variable().getURL();
    String TAG = "ModifyGroupRequest";

    // 생성자
    // Response.Listener : 응답받는 리스너
    public ModifyGroupRequest(int group_no, String group_image, String group_title, String group_cate, String group_region, int group_max_num, String group_introduce, String group_desc, Response.Listener<String> listener) {
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST, URL+"modifyGroup.php", listener, null);
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("group_no", ""+group_no);
        parameters.put("group_image", group_image);
        parameters.put("group_title", group_title);
        parameters.put("group_cate", group_cate);
        parameters.put("group_region", group_region);
        parameters.put("group_max_num", ""+group_max_num);
        parameters.put("group_introduce", group_introduce);
        parameters.put("group_desc", group_desc);
//        Log.e("modify_group_no", "" + group_no);
        //Log.e(TAG, "group_image : " + group_image);
//        Log.e("modify_group_title", "" + group_title);
//        Log.e("modify_group_cate", "" + group_cate);
//        Log.e("modify_group_region", "" + group_region);1
//        Log.e("modify_group_max_num", "" + group_max_num);
//        Log.e("modify_group_introduce", "" + group_introduce);
       // Log.e("addGroup_request", "" + group_desc); // 5줄 이상 넘어가는 내용들이 많아서 Logcat을 다 덮음. 그래서 주석처리
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
