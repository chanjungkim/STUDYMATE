package com.example.gp62.studymate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by GP62 on 2018-05-08.
 */

public class searchGroupFragment extends Fragment { // 모임검색화면

    String Tag = "searchGroupFragment";

    android.support.v7.widget.Toolbar toolbar;
    //Button group_add_btn; // 모임추가 버튼
    com.melnykov.fab.FloatingActionButton group_add_btn;
    Spinner region_spinner; // 툴바 내 지역검색 스피너
    Spinner cate_spinner; // 툴바 내 카테고리검색 스피너
    ArrayList<GroupItem> group_items_list; // 그룹어레이리스트
    GroupAdapter group_adapter; // 어댑터
    ListView search_group_listView; // 모임리스트뷰
    Boolean spinneFlag = false; // 모임검색화면을 볼러올 때, 스피너가 2개라서 모임리스트가 2번 불러와지는 오류가 생김. 그래서 플래그를 만들어 오류해결

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_group, container, false);

        // 툴바
        toolbar = (android.support.v7.widget.Toolbar) view.findViewById(R.id.toolbar);
        group_add_btn = (com.melnykov.fab.FloatingActionButton) view.findViewById(R.id.group_add_btn);

        // 툴바 내 지역검색 스피너 -> 선택된 지역에 따라 리스트뷰 내용이 정렬됨
        region_spinner = (Spinner) view.findViewById(R.id.region_spinner);
        ArrayAdapter region_arrayAdapter = new ArrayAdapter(((AppCompatActivity) getActivity()), R.layout.custom_spinner_region,
                getResources().getStringArray(R.array.region));
        region_arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region_spinner.setAdapter(region_arrayAdapter);
        region_spinner.setOnItemSelectedListener(SpinnerListener);
        search_group_listView = (ListView) view.findViewById(R.id.search_group_listView);

        // 툴바 내 카테고리검색 스피너 -> 선택된 카테고리에 따라 리스트뷰 내용이 정렬됨
        cate_spinner = (Spinner) view.findViewById(R.id.cate_spinner);
        ArrayAdapter cate_arrayAdapter = new ArrayAdapter(((AppCompatActivity) getActivity()), R.layout.custom_spinner_region,
                getResources().getStringArray(R.array.cate));
        cate_arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cate_spinner.setAdapter(cate_arrayAdapter);
        cate_spinner.setOnItemSelectedListener(SpinnerListener);
//        cate_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0) {
//                    group_items_list.clear();
//                    new BackgroundTask().execute();
//                    cate_spinner.setSelection(position);
//                    if(position == 0){
//                        group_items_list.clear();
//                        new BackgroundTask().execute();
//                    }
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        // 모임추가버튼
        //group_add_btn = (Button) view.findViewById(R.id.group_add_btn);
        group_add_btn.attachToListView(search_group_listView);

        SharedPreferences SP = getActivity().getSharedPreferences("login_user", Context.MODE_PRIVATE);
        if (SP.contains("login_user")) {
            group_add_btn.setVisibility(View.VISIBLE);
        }else{
            group_add_btn.setVisibility(View.GONE);
        }

        // 모임추가 버튼을 누르면, 모임추가화면으로 감
        group_add_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences SP = getActivity().getSharedPreferences("login_user", Context.MODE_PRIVATE);

                        if (SP.contains("login_user")) {
                            Intent go_addgroup = new Intent(getContext(), AddGroupActivity.class);
                            startActivity(go_addgroup);
                        } else {

                            Toast.makeText(getActivity(),"로그인이 필요한 서비스입니다.",Toast.LENGTH_SHORT).show();
                            Intent go_login = new Intent(getContext(), LoginActivity.class);
                            go_login.putExtra("로그인전상황","모임추가중로그인");
                            startActivity(go_login);
                        }
                    }
                });

        // 모임리스트
        group_items_list = new ArrayList<>();
        group_adapter = new GroupAdapter(getActivity(), group_items_list);
        search_group_listView = (ListView) view.findViewById(R.id.search_group_listView);
        search_group_listView.setAdapter(group_adapter);
        search_group_listView.setDivider(null);

        search_group_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent go_group_view = new Intent(getContext(), GroupViewActivity.class);
                // 리스트뷰에서 선택된 아이템 위치를 가져온다(get(position)), 그리고 그 아이템의 group_no을 가져온다 -> 공부 더 필요!
                go_group_view.putExtra("group_no", group_items_list.get(position).getGroup_no());
                startActivity(go_group_view);
            }
        });

        //Log.e(Tag, "onCreateView");

        return view;
    }

    // 지역, 카테고리 스피너 선택시 이벤트
    Spinner.OnItemSelectedListener SpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedRegion = region_spinner.getSelectedItem().toString();
            String selectedCate = cate_spinner.getSelectedItem().toString();
            // //Log.e(Tag,"1");
            if (selectedRegion.equals("지역") && selectedCate.equals("카테고리")) {
                if (spinneFlag) {
                    group_items_list.clear();
                    new BackgroundTask().execute();
                    spinneFlag = false;
                    ////Log.e(Tag,"2");
                }
            } else {
                group_items_list.clear();
                new BackgroundTask().execute();
                spinneFlag = true;
                // //Log.e(Tag,"3");
            }
            //Log.e(Tag,"spinneFlag : "+spinneFlag);
//            group_items_list.clear();
//            new BackgroundTask().execute();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    // 모임리스트 데이터 받아오기
    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String SERVER_URL = new Variable().getURL();
        String target;

        @Override
        protected void onPreExecute() {
            try {
                // //Log.e(Tag,"4");
                target = SERVER_URL + "selectGroup.php?group_region=" + URLEncoder.encode(region_spinner.getSelectedItem().toString(), "UTF-8") +
                        "&group_cate=" + URLEncoder.encode(cate_spinner.getSelectedItem().toString(), "UTF-8");

//                target = SERVER_URL + "selectGroup.php?group_region=" + region_spinner.getSelectedItem().toString() +
//                        "&group_cate=" + cate_spinner.getSelectedItem().toString();

                //Log.e(Tag,"region : "+URLEncoder.encode(region_spinner.getSelectedItem().toString(), "UTF-8"));
                //Log.e(Tag,"region : "+region_spinner.getSelectedItem().toString());
                //Log.e(Tag,"cate : "+URLEncoder.encode(cate_spinner.getSelectedItem().toString(), "UTF-8"));

                // target = SERVER_URL + "selectGroup.php?group_region=지역&group_cate=카테고리";

                //Log.e(Tag,"4-1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                //Log.e(Tag,"5");
                URL url = new URL(target);
                //Log.e(Tag,"5-1");
                // 서버에 접속할 수 있도록 연결
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //Log.e(Tag,"5-2");
                // 넘어오는 결과값들 저장
                InputStream inputStream = httpURLConnection.getInputStream();
                ////Log.e(Tag,"5-3");
                // inputStream 내용들을 buffer에 담아서 읽을 수 있게함
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                ////Log.e(Tag,"5-4");
                // temp에 내용을 하나씩 읽어와서 문자형태로 저장할 수 있게 함
                String temp;
                ////Log.e(Tag,"5-5");
                StringBuilder stringBuilder = new StringBuilder();
                //Log.e(Tag,"5-6");
                // 버퍼에서 받아온 값을 한줄씩 temp에 넣어서 읽기. null이 나올때까지!


                while ((temp = bufferedReader.readLine()) != null) {
                    //Log.e(Tag,"5-7");
                    // stringbuilder에는 한줄씩 추가하면서 temp에 넣음
                    stringBuilder.append(temp + "\n");
                    //Log.e(Tag,"5-8");
                    // //Log.e(Tag,"temp :"+temp);
                }
                // 끝나면, bufferedReader와 inputStream은 닫아줌 & 연결도 끊어줌
                //Log.e(Tag,"5-9");
                bufferedReader.close();
                //Log.e(Tag,"5-10");
                inputStream.close();
                //Log.e(Tag,"5-11");
                httpURLConnection.disconnect();
                //Log.e(Tag,"5-12");
                return stringBuilder.toString().trim(); //문자열들 반환

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            //Log.e(Tag,"6");
        }

        @Override
        protected void onPostExecute(String result) { //결과처리
            try {
                //Log.e(Tag,"7");
//                AlertDialog dialog;
//                AlertDialog.Builder builder = new AlertDialog.Builder(searchGroupFragment.this.getContext());
//                dialog = builder.setMessage(result).setPositiveButton("확인",null).create();
//                dialog.show();
                //Log.e(Tag,"result : "+result);
                JSONObject jsonObject = new JSONObject(result);
                // reponse 어레이리스트 받아옴
                //Log.e(Tag,"7-1");
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                //Log.e(Tag,"jsonArray : "+jsonArray);
                int count = 0;
                //Log.e(Tag,"7-3");
                String group_title, group_cate, group_region, group_introduce, group_image;
                //Log.e(Tag,"7-4");
                int group_max_num, group_cur_num, group_no;
                //Log.e(Tag,"7-5");


                while (count < jsonArray.length()) {
                    //Log.e(Tag,"7-6");
                    JSONObject object = jsonArray.getJSONObject(count);
                    // Log.e(Tag,"object : "+object);
                    group_title = object.getString("group_title");
                    //Log.e(Tag,"7-8");
                    // //Log.e(Tag,group_title);
                    group_cate = object.getString("group_cate");
                    //Log.e(Tag,"7-9");1
                    ////Log.e(Tag,group_cate);
                    group_cur_num = object.getInt("group_cur_num");
                    //Log.e(Tag,"7-10");
                    //Log.e(Tag,""+group_cur_num);
                    group_region = object.getString("group_region");
                    //Log.e(Tag,"7-11");
                    ////Log.e(Tag,"group_region");
                    group_introduce = object.getString("group_introduce");
                    //Log.e(Tag,"7-12");
                    //  //Log.e(Tag,"group_introduce");
                    //group_max_num = Integer.parseInt(object.getString("group_max_num")); //-> 에러
                    group_max_num = object.getInt("group_max_num"); //-> 에러
                    //Log.e(Tag,"7-13");
                    //  //Log.e(Tag,""+group_max_num);
                    group_no = object.getInt("group_no");
                    //Log.e(Tag,"7-14");
                    //  //Log.e(Tag,""+group_no);
                    // group_item에 각각의 정보들을 넣어줌
                    group_image = object.getString("group_image");
                    //Log.e(Tag,""+group_image);

                    GroupItem group_item = new GroupItem(group_no, group_cate, group_title, group_introduce, group_region, group_cur_num, group_max_num, group_image);
                    // 어레이리스트에 추가
                    //Log.e(Tag,"7-15");
                    if (group_cur_num < group_max_num) {
                        group_items_list.add(group_item);
                    }
                    //Log.e(Tag,"7-16");
                    count++;
                }

                //   //Log.e(Tag,"하이4");
            } catch (Exception e) {
                e.printStackTrace();
            }
            group_adapter.notifyDataSetChanged();
            //Log.e(Tag,"7-17");
        }
    }


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        group_items_list.clear();
//        new BackgroundTask().execute();
//        //Log.e(Tag,"onCreate");
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.e(Tag, "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        group_items_list.clear();
        new BackgroundTask().execute();
        //Log.e(Tag, "onResume");
    }
}
