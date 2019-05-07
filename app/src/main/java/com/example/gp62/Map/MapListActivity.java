package com.example.gp62.Map;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gp62.studymate.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by GP62 on 2018-06-08.
 */


public class MapListActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar map_toolbar;

    // 현재위치
    Location mCurrentLocatiion;

    RecyclerView rv_mapList;
    MapListAdapter mapListAdapter;
    ArrayList<MapItem> mapItemList;
    TextView tv_no_room;
    String Tag = "MapListActivity";

    //정렬버튼
    TextView tv_order_accuracy;
    TextView tv_order_distance;

    MapItem[] arr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);

        tv_no_room = findViewById(R.id.tv_no_room);
        map_toolbar = findViewById(R.id.map_toolbar);

        tv_order_accuracy = findViewById(R.id.tv_order_accuracy);
        tv_order_accuracy.setTextColor(Color.RED);

        tv_order_distance = findViewById(R.id.tv_order_distance);

        // 리사이클러뷰 기본 세팅
        rv_mapList = findViewById(R.id.rv_mapList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rv_mapList.setLayoutManager(gridLayoutManager);
        //mapItemList = new ArrayList<>();
        //mapListAdapter = new MapListAdapter(this, mapItemList, R.layout.map_list_item);

        // 툴바에 뒤로가기버튼 세팅
        setSupportActionBar(map_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*인텐트로 스터디룸정보(이미지, 이름, 등등)을 가져옴*/
        //Intent curPosition = getIntent();
        mapItemList = (ArrayList<MapItem>)getIntent().getSerializableExtra("주변스터디룸"); // 인텐트로 어레이리스트 받기
        Log.e(Tag, "기본_리스트내용 : " + mapItemList);
        Log.e(Tag, "기본_사이즈 : " + mapItemList.size());

        mapListAdapter = new MapListAdapter(getApplicationContext(), mapItemList, R.layout.map_list_item);
        rv_mapList.setAdapter(mapListAdapter);

            /* 배열로 스터디룸정보리스트 저장하기 (나중에 정확도순을 클릭할 때, 정보들을 순서대로 보여주기위해) */
        arr = new MapItem[mapItemList.size()];
        for (int i = 0; i < mapItemList.size(); i++) {
            arr[i] = mapItemList.get(i);
        }
//        /* --------------------------------------------------------------------------------------
////         < 인텐트로 현재 위치 받아오기 >
////         그리고 현재 위치 주변에 있는 스터디룸 정보를 구글api에 요청하기*/
//
//        Intent curPosition = getIntent();
//        if (curPosition.hasExtra("현재위치")) {
//            //Log.e(Tag, "기본_스터디룸목록받아옴");
//
//            mCurrentLocatiion = getIntent().getParcelableExtra("현재위치");
//            Log.e(Tag,"현재위치 :"+mCurrentLocatiion);
//
//            // 스터디룸 리스트 가져오기
//            kakaoMap(mCurrentLocatiion);
//            googleMap(mCurrentLocatiion);
//
//            /*mapItemList = (ArrayList<MapItem>) getIntent().getSerializableExtra("주변스터디룸");*/ // 인텐트로 어레이리스트 받기
//            //Log.e(Tag, "기본_리스트내용 : " + mapItemList);
//            Log.e(Tag, "기본_사이즈 : " + mapItemList.size());
////            if (mapItemList == null) {
////                Log.e(Tag, "맵아이템리스트 null이다");
////            }
////
//            mapListAdapter = new MapListAdapter(getApplicationContext(),mapItemList, R.layout.map_list_item);
//            rv_mapList.setAdapter(mapListAdapter);
//
//            /* 배열로 스터디룸정보리스트 저장하기 (나중에 정확도순을 클릭할 때, 정보들을 순서대로 보여주기위해) */
//            arr = new MapItem[mapItemList.size()];
//            for (int i=0;i<mapItemList.size();i++){
//                arr[i]=mapItemList.get(i);
//            }
//
//            tv_no_room.setVisibility(View.GONE);
//            rv_mapList.setVisibility(View.VISIBLE);
//
//
//        } else { // 목록 못 받아오면, '검색된 스터디룸이 없습니다'띄우기
//            tv_no_room.setVisibility(View.VISIBLE);
//            rv_mapList.setVisibility(View.GONE);
//        }
//       /* --------------------------------------------------------------------------------------*/




        /*정확도순 버튼을 눌렀을 때,*/
        tv_order_accuracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 색바꾸기
                tv_order_accuracy.setTextColor(Color.RED);
                tv_order_distance.setTextColor(Color.BLACK);

                // 어레이리스트에 정확도순으로 목록 세팅
                mapItemList.clear();
                mapItemList.addAll(Arrays.asList(arr));
                mapListAdapter.notifyDataSetChanged();
            }
        });

        /* 거리순버튼을 눌렀을 때,*/
        tv_order_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 색바꾸기
                tv_order_accuracy.setTextColor(Color.BLACK);
                tv_order_distance.setTextColor(Color.RED);
                Comparator<MapItem> noAsc = new Comparator<MapItem>() {
                    @Override
                    public int compare(MapItem item1, MapItem item2) {
                        int ret;
                        if (item1.getDistance() < item2.getDistance()) ret = -1;
                        else if (item1.getDistance() == item2.getDistance()) ret = 0;
                        else ret = 1;
                        return ret;
                        // 위의 코드를 간단히 만드는 방법.
                        // return (item1.getNo() - item2.getNo()) ;
                    }
                };
                Collections.sort(mapItemList, noAsc);
                mapListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

//       /* --------------------------------------------------------------------------------------
//         < 인텐트로 주변 스터디룸 목록 받아오기 >
//         목록 받아오면, 리사이클러뷰에 받아온 목록 넣기*/
//        Intent room_list = getIntent();
//        if (room_list.hasExtra("현재위치")) {
//            //Log.e(Tag, "기본_스터디룸목록받아옴");
//            tv_no_room.setVisibility(View.GONE);
//            rv_mapList.setVisibility(View.VISIBLE);
//
//            /*mapItemList = (ArrayList<MapItem>) getIntent().getSerializableExtra("주변스터디룸");*/ // 인텐트로 어레이리스트 받기
//            //Log.e(Tag, "기본_리스트내용 : " + mapItemList);
//            Log.e(Tag, "기본_사이즈 : " + mapItemList.size());
////            if (mapItemList == null) {
////                Log.e(Tag, "맵아이템리스트 null이다");
////            }
////
//            mapListAdapter = new MapListAdapter(getApplicationContext(),mapItemList, R.layout.map_list_item);
//            rv_mapList.setAdapter(mapListAdapter);
//
//            /* 배열로 어레이리스트 저장하기 -> 정확도순에서 꺼내 세팅하기 */
//            arr = new MapItem[mapItemList.size()];
//            for (int i=0;i<mapItemList.size();i++){
//                arr[i]=mapItemList.get(i);
//            }
//
//
//        } else { // 목록 못 받아오면, '검색된 스터디룸이 없습니다'띄우기
//            tv_no_room.setVisibility(View.VISIBLE);
//            rv_mapList.setVisibility(View.GONE);
//        }
//       /* --------------------------------------------------------------------------------------*/

    }

    // 뒤로가기 버튼이 눌러졌을 때, 현재 화면이 꺼진다
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

//    public void googleMap(Location location) {
//        //////////////////////////////////////////////////////////////////////////////////////////////////////
//        // 내 서버를 통해 카카오맵 api로 부터 주변 스터디룸 정보 받아오기
//
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //logLargeString("google_response : "+response);
///*주석시작*/
//                try {
//                    Log.e(Tag, "onResponse : 1");
//                    //Log.e(Tag,"response1");
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
//                    Log.e(Tag,"받아오는값"+jsonObject1);
//                    //logLargeString("jsonObject : " + jsonObject);
//
//                    /*JSONArray jsonResponse = jsonObject1.getJSONArray("room_photo");*/
//                    //Log.e(Tag, "jsonResponse.length() : "+jsonResponse.length());
//                    //logLargeString("jsonResponse : "+jsonResponse);
////                    JSONObject jsonObject1 = new JSONObject(response);
////                    logLargeString("jsonObject1 : "+jsonObject);
////                    JSONArray jsonResponse_photo = jsonObject1.getJSONArray("room_photo");
////                    //Log.e(Tag,"results결과 : "+(JSONArray)jsonResponse);
//                    /*Log.e(Tag,"onResponse : 2");*/
//
////                    Log.e(Tag, "여기도 photo : "+photo);
//
//////                    Log.e(Tag, response);
//                    String study_latitude; // 위도
//                    String study_longitude; // 경도
//                    String place_name; // 이름
//                    String address_name; // 주소
//                    final String phone; // 전화번호
//                    String place_url; // url
//                    double distance; // 중심좌표까지의 거리
//                    String place_id;
//
//                    JSONArray jsonResponse_info = jsonObject1.getJSONArray("info");
//                    /*logLargeString("jsonResponse_info : "+jsonResponse_info);*/
//                    // 만약 success가 되었다면 즉 주변 스터디룸 정보들을 받아올 수 있다면,
//                    // 받아온 주변 스터디룸 정보들을 마커에 세팅한다
//                    // success : 해당 과정이 정상적으로 수행 되었는지 response 값
//                    // boolean success = jsonObject.getBoolean("success");
//                    //if (success) {
//                    Log.e(Tag, "jsonResponse_info.length : " + jsonResponse_info.length());
//                    Log.e(Tag, "구글1");
////                    /*장소 기본 정보 가져오기*/
//                    for (int i = 0; i < jsonResponse_info.length(); i++) {
//                        // Log.e(Tag,"response5");
//                        JSONObject Object = jsonResponse_info.getJSONObject(i);
//                        //Log.e(Tag, "jsonResponse_info: 1");
//                        JSONObject geometry = Object.getJSONObject("geometry");
//                        //Log.e(Tag, "geometry: " + geometry);
//                        JSONObject location = geometry.getJSONObject("location");
//                        //Log.e(Tag, "location: " + location);
//                        study_latitude = location.optString("lat");
//                        study_longitude = location.optString("lng");
//                        //Log.e(Tag,"study_latitude: "+study_latitude);
//                        //Log.e(Tag,"study_longitude: "+study_longitude);
//
//                        place_name = Object.optString("name");
//                        address_name = Object.optString("vicinity");
//
//                        // 두 위치 사이 거리 구하기
//
//                        // 내 위치
//                        Location location_cur = new Location("pointA");
//                        location_cur.setLatitude(mCurrentLocatiion.getLatitude());
//                        location_cur.setLongitude(mCurrentLocatiion.getLongitude());
//
//                        // 스터디룸 위치
//                        double study_latitude_d = Double.valueOf(study_latitude);
//                        double study_longitude_d = Double.valueOf(study_longitude);
//
//                        Location location_room = new Location("pointB");
//                        location_room.setLatitude(study_latitude_d);
//                        location_room.setLongitude(study_longitude_d);
//
//                        distance = location_cur.distanceTo(location_room);
//                        //Log.e(Tag,"두 지점사이의 거리 : "+distance);
//
//////                        Log.e(Tag,"344");
//                            /*구글맵 주변 스터디룸 리스트*/
//                        MapItem mapItem = new MapItem("", place_name, "", address_name, "", "", study_longitude_d, study_latitude_d, distance, "", "", "");
//
////                        Log.e(Tag,"35551");
//                        mapItemList.add(mapItem);
//                        //Log.e(Tag,"스터디룸리스트 넣어짐");
//                        //Log.e(Tag,"어레이리스트내용 : "+mapItemList);
//                    }
//                    Log.e(Tag, "구글2");
//                    String name;
//                    String photo = "";
//
//                    JSONArray jsonResponse = jsonObject1.getJSONArray("room_photo");
//                    Log.e(Tag, "구글3");
//                    // 사진url 가져오기
//                    for (int i = 0; i < jsonResponse.length(); i++) {
//                        Log.e(Tag, "구글4-1");
//                        JSONObject Object = jsonResponse.getJSONObject(i);
//                        Log.e(Tag, "구글4-2");
//                        name = Object.optString("name");
//                        photo = Object.optString("photo");
////                        Log.e(Tag, "name : " + name);
//                        //Log.e(Tag, "photo : " + photo);
//                        Log.e(Tag, "구글4-3");
//                        //Log.e(Tag, "스터디리스트사이즈 : " + mapItemList.size());
//
//                        for (int j = 0; j < mapItemList.size(); j++) {
//                            if (mapItemList.get(j).getPlace_name().equals(name)) {
//                                mapItemList.get(j).setPlace_image(photo);
////                                Log.e(Tag,"이름가져오기 : "+name);
//                                //Log.e(Tag, j + "번 사진가져오기 : " + photo);
//                                //Log.e(Tag, "사진_ : " + mapItemList.get(j).getPlace_image());
//                                break;
//                            } else { //스터디룸 사진이 없다면, 기본 이미지가 나오게 하기
////                                mapItemList.get(j).setPlace_image("");
//                                //Log.e(Tag, j + "번 사진없음");
//                            }
//                        }
//                    }
//                    Log.e(Tag, "구글5");
//
//                    for (int j = 0; j < mapItemList.size(); j++) {
//                        Log.e(Tag,j+"번 이미지 : "+mapItemList.get(j).getPlace_image());
//                    }
////                    }
////                     }else { // 주변 스터디룸 정보들을 받아올 수 없다면,
////                    Log.e(Tag,"카카오 : 주변스터디룸 정보 받아올 수 없음!");
////                     }
//
//                } catch (Exception e) { // 오류가 발생했을 경우
//                    e.printStackTrace();
//                    Log.e(Tag, "구글에서 스터디룸 정보가져오기 실패");
//                }
//                /*주석끝*/
//            }
//        };
//        String keyword = "스터디룸";
//        int radius = 5000;
//        String x = String.valueOf(location.getLongitude());
//        String y = String.valueOf(location.getLatitude());
//        // 현재위치의 경도, 위도, 검색어, 반경거리를 내 서버로 보내기
//        // Log.e(Tag, "googleMap_search_1");
//        GoogleMapRequest googleMapRequest = new GoogleMapRequest(x, y, keyword, radius, responseListener);
//        // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
//        RequestQueue queue = Volley.newRequestQueue(MapListActivity.this);
//        // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
//        queue.add(googleMapRequest);
//        // Log.e(Tag, "googleMap_search_2");
//
//    }
//
//    public void kakaoMap(Location location) {
//        //////////////////////////////////////////////////////////////////////////////////////////////////////
//        // 내 서버를 통해 카카오맵 api로 부터 주변 스터디룸 정보 받아오기
//
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
////                Log.e(Tag, "response : "+response);
//
//                try {
//                    //Log.e(Tag,"response1");
//                    JSONObject jsonObject = new JSONObject(response);
//                    // Log.e(Tag,"response2");
//                    JSONArray jsonResponse = jsonObject.getJSONArray("documents");
//                    // Log.e(Tag,"response3");
////                    Log.e(Tag, response);
//                    double study_latitude; // 위도
//                    double study_longitude; // 경도
//                    String place_name; // 이름
//                    String address_name; // 주소
//                    String phone; // 전화번호
//                    String place_url; // url
//                    double distance; // 중심좌표까지의 거리
//                    String place_id;
//
//                    //Log.e(Tag, "response4");
//                    // 만약 success가 되었다면 즉 주변 스터디룸 정보들을 받아올 수 있다면,
//                    // 받아온 주변 스터디룸 정보들을 마커에 세팅한다
//                    // success : 해당 과정이 정상적으로 수행 되었는지 response 값
//                    // boolean success = jsonObject.getBoolean("success");
//                    //Log.e(Tag, "response4-1");
//                    //if (success) {
//                    for (int i = 0; i < response.length(); i++) {
//                        // Log.e(Tag,"response5");
//                        JSONObject jObject = jsonResponse.getJSONObject(i);
//                        place_name = jObject.optString("place_name");
//                        address_name = jObject.optString("address_name");
//                        phone = jObject.optString("phone");
//                        //place_id = jObject.optString("id");
//                        //Log.e(Tag,"id"+place_id);
//                        study_longitude = Double.valueOf(jObject.optString("x"));
//                        study_latitude = Double.valueOf(jObject.optString("y"));
//                        place_url = jObject.optString("place_url");
//                        distance = Double.valueOf(jObject.optString("distance"));
//                        //Log.e(Tag, "경도(x):" + study_longitude + " 위도(y):" + study_latitude + " 이름:" + place_name + " 주소:" + address_name + " 전화번호:" + phone);
//
////                        Log.e(Tag,"344");
//                            /*카카오맵 주변 스터디룸 리스트*/
//                        MapItem mapItem = new MapItem("", place_name, place_url, address_name, "", phone, study_longitude, study_latitude, distance, "", "", "");
//
////                        Log.e(Tag,"35551");
//                        mapItemList.add(mapItem);
//                        //Log.e(Tag,"스터디룸리스트 넣어짐");
//                        //Log.e(Tag,"어레이리스트내용 : "+mapItemList);
//                    }
//                    // }else { // 주변 스터디룸 정보들을 받아올 수 없다면,
//                    Log.e(Tag, "카카오 : 주변스터디룸 정보 받아올 수 없음!");
//                    // }
//                } catch (Exception e) { // 오류가 발생했을 경우
//                    e.printStackTrace();
//                    Log.e(Tag, "카카오 오류");
//                }
//            }
//        };
//        String keyword = "스터디룸";
//        int radius = 2000;
//        String x = String.valueOf(location.getLongitude());
//        String y = String.valueOf(location.getLatitude());
//        // 현재위치의 경도, 위도, 검색어, 반경거리를 내 서버로 보내기
//        Log.e(Tag, "+11");
//        KaKaoMapRequest kaKaoMapRequest = new KaKaoMapRequest(x, y, keyword, radius, responseListener);
//        Log.e(Tag, "+12");
//        // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
//        Log.e(Tag, "+13");
//        RequestQueue queue = Volley.newRequestQueue(MapListActivity.this);
//        // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
//        Log.e(Tag, "+14");
//        queue.add(kaKaoMapRequest);
//        Log.e(Tag, "+15");
//    }

}
