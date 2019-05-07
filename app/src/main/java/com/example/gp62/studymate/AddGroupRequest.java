package com.example.gp62.studymate;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GP62 on 2018-05-11.
 */

public class AddGroupRequest extends StringRequest {

    private Map<String, String> parameters;
    String Tag = "AddGroupRequest";
    static String URL = new Variable().getURL();

    // 생성자
    // Response.Listener : 응답받는 리스너
    public AddGroupRequest(String group_title, String group_image, int group_leader_num, String group_cate, String group_region, int group_max_num, String group_introduce, String group_desc, Response.Listener<String> listener) {
        // 해당 url에 post방식으로 파라미터를 보낸다
        super(Method.POST, URL + "addGroup.php", listener, null);
        // 각 값(파라미터)들을 넣을 수 있도록 해쉬맵을 만듦
        parameters = new HashMap<>();
        // 각 값들을 파라미터로 매칭시켜줌
        parameters.put("group_title", group_title);
        parameters.put("group_image", group_image);
        parameters.put("group_leader", "" + group_leader_num);
        parameters.put("group_cate", group_cate);
        parameters.put("group_region", group_region);
        parameters.put("group_max_num", "" + group_max_num);
        parameters.put("group_introduce", group_introduce);
        parameters.put("group_desc", group_desc);

//        Log.e(Tag, "group_title : " + group_title);
//        Log.e(Tag, "group_image : " + group_image);
//        Log.e(Tag, "group_leader_num : " + group_leader_num);
//        Log.e(Tag, "group_cate : " + group_cate);
//        Log.e(Tag, "group_region : " + group_region);
//        Log.e(Tag, "group_max_num : " + group_max_num);
//        Log.e(Tag, "group_introduce : " + group_introduce);
//        Log.e(Tag, "group_desc : " + group_desc);

        //Log.e("AddgroupRequest: ",parameters.get("group_leader"));
        //parameters.put("group_desc", group_desc);
        //Log.e(Tag, "이미지 : " + group_image);
        //Log.e(Tag, group_title + group_image + group_leader_num + group_cate + group_region + group_max_num + group_introduce + group_desc);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }

}
