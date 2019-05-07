package com.example.gp62.studymate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class AddGroupActivity extends AppCompatActivity { // 모임 추가, 수정 화면

    // TODO: 2018-05-14 * 태그설정, 가입방식(운영자허락 or 참가 즉시 바로 가입)은 나중에 추가고려
    // TODO: 2018-05-14 * 모임정원 -> 나중에 숫자스피너 다이얼로그 뜨게 할 예정

    //툴바
    Toolbar toolbar;

    // 모임대표이미지
    ImageView iv_group_profile_image;
    //int CODE_GALLERY_REQUSEST = 9999;
    //Bitmap profile_bitmap;

    String Tag = "AddGroupActivity";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 11;
    private static final int CROP_FROM_iMAGE = 22;
    // 인텐트 번호관리
    int LEADER_MODIFY_NUM = 3333;
    int MEMBER_MODIFY_NUM = 1;
    Bitmap group_photo;

    private Uri mImageCaptureUri;

    // 사용자가 검색할 때 쓰이는 정보 : 활동지역, 모임카테고리
    // 모임리스트에 보여질 내용들 (여기서 모임설명은 제외 -> 모임설명은 상세페이지에서 보여짐)
    EditText group_title, //모임제목
            group_max_num, //모임정원
            group_introduce,//모임 한줄 소개
            group_desc, //모임설명
            group_tag, //태그설정
            group_region, // 활동지역
            group_cate; // 모임카테고리

    // 모임추가버튼
    Button group_add_btn;

    String group_title_s, group_desc_s, group_introduce_s, group_cate_s, group_region_s, group_image_s;
    int group_max_num_i, group_leader_i, group_no;

    // 다이얼로그 (알림창)
    // private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Log.e(Tag,"onCreate");
        // 툴바
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 툴바 뒤로가기버튼
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 모임대표이미지가 들어갈 부분
        iv_group_profile_image = findViewById(R.id.iv_group_profile_image);
        iv_group_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 다이얼로그를 띄운다
                final CharSequence items[] = new CharSequence[]{"카메라", "앨범"};
                final AlertDialog.Builder dialog_choice_image = new AlertDialog.Builder(AddGroupActivity.this);

                dialog_choice_image.setTitle("이미지 불러오기");
                dialog_choice_image.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0: // 카메라
                                doTakePhotoAction();
                                break;
                            case 1: // 앨범
                                // 앨범을 선택한 경우, 저장공간 권한 체크를 한다.
                                doTakeAlbumAction();
                                break;
                            // ActivityCompat.requestPermissions(AddGroupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE_GALLERY_REQUSEST);
                        }
                    }
                });
                AlertDialog alertDialog = dialog_choice_image.create();
                alertDialog.show();

            }
        });


        // 모임제목
        group_title = (EditText) findViewById(R.id.group_title);

        // 모임카테고리클릭시 카테고리 선택창이 뜸
        group_cate = (EditText) findViewById(R.id.group_cate);
        group_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"어학", "취업", "취미", "자격증/시험", "기타"};
                // 다이얼로그창 띄우기
                final AlertDialog.Builder dialog_choice_cate = new AlertDialog.Builder(AddGroupActivity.this);
                dialog_choice_cate.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 카테고리를 선택하면, 모임카테고리 에디트텍스트 창에 입력됨
                        group_cate.setText(items[which]);
                    }
                });
                AlertDialog alertDialog = dialog_choice_cate.create();
                alertDialog.show();
            }
        });

        // 모임정원 -> 나중에 숫자스피너 다이얼로그 뜨게 할 예정
        group_max_num = (EditText) findViewById(R.id.group_max_num);

        // 모임 한줄 소개
        group_introduce = (EditText) findViewById(R.id.group_introduce);

        // 모임 설명
        group_desc = (EditText) findViewById(R.id.group_desc);

        // * 나중에 추가할 부분!
//        // 태그설정
//        group_tag = (EditText) findViewById(R.id.group_tag);

        // 활동지역클릭시 나오는 지역선택창이 뜸
        group_region = (EditText) findViewById(R.id.group_region);
        group_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"서울", "경기", "인천", "강원", "충남", "충북", "대전", "경북", "경남", "대구", "부산", "울산", "전북", "전남", "광주", "제주", "세종"};
                // 다이얼로그창 띄우기
                final AlertDialog.Builder dialog_choice_region = new AlertDialog.Builder(AddGroupActivity.this);
                dialog_choice_region.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 지역을 선택하면, 활동지역 에디트텍스트 창에 입력됨
                        group_region.setText(items[which]);
                    }
                });
                AlertDialog alertDialog = dialog_choice_region.create();
                alertDialog.show();
            }
        });

        // * 가입방식은 추후에 추가할지말지 고려해봄
//        // 가입방식클릭시 가입방식 선택창이 뜸
//        group_join = (EditText) findViewById(R.id.group_join);
//        group_join.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final CharSequence[] items = {"가입신청시 바로 가입할 수 있습니다", "가입신청시 운영진 승인을 거쳐 가입할 수 있습니다."};
//                // 다이얼로그창 띄우기
//                final AlertDialog.Builder dialog_choice_join = new AlertDialog.Builder(AddGroupActivity.this);
//                dialog_choice_join.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // 가입방식을 선택하면, 가입방식 에디트텍스트 창에 입력됨
//                        group_join.setText(items[which]);
//                    }
//                });
//                AlertDialog alertDialog = dialog_choice_join.create();
//                alertDialog.show();
//            }
//        });


        // 모임 추가하기 or 수정하기 버튼
        group_add_btn = (Button) findViewById(R.id.group_add_btn);


        // 수정 intent일 때,제목, 작성자, 모임설명, 모임한줄소개 각 항목에 세팅함, 추가하기버튼-> 수정하기 버튼으로 세팅.
        Intent modify = getIntent();
        if (modify.hasExtra("항목")) {
            String modify_s = modify.getStringExtra("항목");
            Log.e(Tag,"항목은?");
            if (modify_s.equals("모임수정")) {
                Log.e(Tag,"모임수정!");
                group_image_s = modify.getStringExtra("group_image_s");

                // 각각에 각각의 정보들을 넣어줌
                String URL = new Variable().getURL();
                //Log.e(Tag,"URL : "+URL);
                Glide
                        .with(getApplicationContext())
//                    .load(mapList.get(position).getPlace_image())
                        .load(URL+group_image_s)
//                        .override(200,100) // 이미지 크기 변경
                        .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                        .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                        .into(iv_group_profile_image);

                Log.e(Tag,"주소 : "+URL+group_image_s);
                group_no = modify.getIntExtra("group_no_i", 0);
                String group_title_s = modify.getStringExtra("group_title_s");
                group_title.setText(group_title_s); // 제목세팅
                String group_desc_s = modify.getStringExtra("group_desc_s");
                group_desc.setText(group_desc_s); // 모임설명세팅
                String group_introduce_s = modify.getStringExtra("group_introduce_s");
                group_introduce.setText(group_introduce_s); // 모임한줄소개세팅
                String group_region_s = modify.getStringExtra("group_region_s");
                group_region.setText(group_region_s); // 활동지역세팅
                String group_cate_s = modify.getStringExtra("group_cate_s");
                group_cate.setText(group_cate_s); // 모임카테고리세팅
                String group_max_num_i = modify.getStringExtra("group_max_num_i");
                group_max_num.setText(group_max_num_i); // 모임정원세팅
                group_add_btn.setText("수정하기");

                /*com.android.volley.Response.Listener<Bitmap> responseListener = new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Log.e(Tag, "성공" + response);
                        iv_group_profile_image.setImageBitmap(response);
                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(Tag, "실패");
                    }
                };

                SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = SP.getString("login_user", "");
                Login_user_info item = gson.fromJson(json, Login_user_info.class);
                String user_no = item.getUser_no();
                String SERVER_URL = new variable().getURL();
                String URL = SERVER_URL + "/upload/images/user_photo_" + user_no + ".jpeg";
                Log.e(Tag, "저장된 파일 주소" + URL);
                ImageRequest imageRequest = new ImageRequest(URL, responseListener, 0, 0, Bitmap.Config.ARGB_8888, errorListener);

                RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                // 큐에 그룹 큐를 등록한다
                queue1.add(imageRequest);*/
            }
        }

        group_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. 필수내용 다 기입했는지 확인
//                final CharSequence[] items_title = {"모임제목", "카테고리", "활동지역", "정원", "모임한줄소개", "모임설명"};
//                final EditText[] items = {group_title, group_cate, group_max_num, group_region, group_introduce, group_desc};
//                for (int i = 0; i < 6; i++) {
//                    if (items[i].getText().toString().equals("")) {
//                        AlertDialog.Builder dialogBuilder_check = new AlertDialog.Builder(AddGroupActivity.this);
//                        dialogBuilder_check.setMessage(items_title[i] + "을(를) 입력해주세요");
//                        dialogBuilder_check.setPositiveButton("예", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        AlertDialog alertDialog = dialogBuilder_check.create();
//                        alertDialog.show();
//                        break;
//                    }
//                }
//                // 2. 모임 한줄 소개 10자 이상인지 확인 -> 일단 지금은 없앰 (테스트할 때, 글자수 채우기 힘듦)
//                if(group_introduce.getText().toString().length()<=10){
//                    AlertDialog.Builder dialogBuilder_check = new AlertDialog.Builder(AddGroupActivity.this);
//                    dialogBuilder_check.setMessage("모임 한줄 소개 10자 이상 입력해주세요");
//                    dialogBuilder_check.setPositiveButton("예", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    AlertDialog alertDialog = dialogBuilder_check.create();
//                    alertDialog.show();
//                }
//
//                // 3. 모임설명이 100자 이상인지 확인 -> 일단 지금은 없앰 (테스트할 때, 글자수 채우기 힘듦)
//                if(group_desc.getText().toString().length()<=100){
//                    AlertDialog.Builder dialogBuilder_check = new AlertDialog.Builder(AddGroupActivity.this);
//                    dialogBuilder_check.setMessage("모임에 대한 설명을 100자 이상 입력해주세요");
//                    dialogBuilder_check.setPositiveButton("예", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
//                    AlertDialog alertDialog = dialogBuilder_check.create();
//                    alertDialog.show();
//                }

//                // 올릴 이미지가 있으면, 이미지 업로드하기
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }){
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        String imageData = imageToString(profile_bitmap);
//                        params.put("image", imageData);
//
//                        return params;
//                    }
//                };
//
//                RequestQueue requestQueue = Volley.newRequestQueue(AddGroupActivity.this);
//                requestQueue.add(stringRequest);

                // 최종적으로 모임추가, 수정되었다고 다이얼로그 뜸
                // DB에 모임추가,수정하기
                Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("error", "1");
                        try {
                            //Log.e("error", "233");
                            Log.e(Tag, "response : "+response);

                            JSONObject jsonResponse = new JSONObject(response);
                            // success : 해당 과정이 정상적으로 수행 되었는지 response값
                            // Log.e("debug", "2");

                            Log.e(Tag, "jsonResponse : "+jsonResponse);

                            boolean success = jsonResponse.getBoolean("success");
                            if(jsonResponse.has("group_image")){
                                group_image_s = jsonResponse.getString("group_image");
                            }


                             Log.e(Tag,"success : "+success);
                             Log.e(Tag,"group_image_s : "+group_image_s);

                            if (success) {
                                // 모임추가,수정 화면을 닫는다
                                //Log.e("error", "4");
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);

                                // 수정하기 버튼이면, 모임이 수정되었다는 다이얼로그가 나오게 함
                                Intent modify = getIntent();
                                Log.e(Tag,"모임수정1");
                                if (modify.hasExtra("항목")) {
                                    Log.e(Tag,"모임수정2");
                                    String modify_s = modify.getStringExtra("항목");

                                    Log.e(Tag,"모임수정3");
                                    if (modify_s.equals("모임수정")) {
                                        AlertDialog dialog = builder.setMessage("모임이 수정되었습니다")
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // 에디트텍스트 내용들을 가져옴
                                                        group_title_s = group_title.getText().toString(); //모임제목
                                                        group_desc_s = group_desc.getText().toString(); //모임설명
                                                        group_introduce_s = group_introduce.getText().toString(); //모임 한줄 소개
                                                        group_cate_s = group_cate.getText().toString(); // 모임카테고리
                                                        group_region_s = group_region.getText().toString(); // 활동지역
                                                        String group_max_num_s = group_max_num.getText().toString(); // 모임정원
                                                        group_max_num_i = Integer.parseInt(group_max_num_s);

                                                      /* Log.e(Tag, "보낸다 group_title_s : "+ group_title_s);
                                                        Log.e(Tag, "보낸다 group_introduce_s : "+ group_introduce_s);
                                                        Log.e(Tag, "보낸다 group_cate_s : "+ group_cate_s);
                                                        Log.e(Tag, "보낸다 group_region_s : "+ group_region_s);
                                                        Log.e(Tag, "보낸다 group_max_num_s : "+ group_max_num_s);*/
                                                        Log.e(Tag, "보낸다 group_image_s : "+ group_image_s);
                                                       /* Log.e(Tag, "보낸다 group_desc_s : "+ group_desc_s);*/

                                                        Intent modify = new Intent();
                                                        modify.putExtra("항목","모임수정");
                                                        modify.putExtra("group_no_i", group_no);
                                                        modify.putExtra("group_title_s", group_title_s);
                                                        modify.putExtra("group_desc_s", group_desc_s);
                                                        modify.putExtra("group_introduce_s", group_introduce_s);
                                                        modify.putExtra("group_region_s", group_region_s);
                                                        modify.putExtra("group_cate_s", group_cate_s);
                                                        modify.putExtra("group_max_num_i", "" + group_max_num_i);
                                                        modify.putExtra("group_image_s", "" + group_image_s);

                                                        setResult(RESULT_OK,modify);

                                                        dialog.cancel();
                                                        finish();
                                                    }
                                                })
                                                .create();
                                        dialog.show();
                                    }
                                    Log.e(Tag,"모임수정했음11111111");
                                } else { // 모임추가하기 버튼이면 모임이 추가되었다는 알림이 나오게 함
                                    Log.e(Tag,"모임추가했음!");
                                    AlertDialog dialog = builder.setMessage("모임이 등록되었습니다")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // '확인'을 누르면, 모임추가화면 닫히고, 내모임탭 화면으로 감 -> 내 모임에 모임추가가 되었다는 걸 보여줌
                                                    // (수정)'확인'을 누르면, 모임추가화면 닫힘. -> 모임리스트화면이 나옴
                                                    dialog.cancel();
                                                    finish();
                                                }
                                            })
                                            .create();
                                    dialog.show();
                                }

////                                // 잘 보내졌는지 확인
//                                group_title_s = jsonResponse.getString("group_title");
//                                Log.e("group_title", group_title_s);
//                                group_cate_s = jsonResponse.getString("group_cate");
//                                Log.e("group_cate", group_cate_s);
//                                group_region_s = jsonResponse.getString("group_region");
//                                Log.e("group_region", group_region_s);
//                                group_max_num_i = jsonResponse.getInt("group_max_num");
//                                Log.e("group_max_num", "" + group_max_num_i);
//                                group_introduce_s = jsonResponse.getString("group_introduce");
//                                Log.e("group_introduce", group_introduce_s);
//                                group_desc_s = jsonResponse.getString("group_desc");
////                                Log.e("group_desc", group_desc_s); // 3줄 이상되는 내용들이 많아서 Log를 다 차지함 그래서 주석처리함

                            } else { // 등록에 실패했다면, 회원가입에 실패했다는 다이얼로그를 띄운다
                                // 수정하기 버튼이면, 모임이 수정되었다는 다이얼로그가 나오게 함
                                Intent modify = getIntent();
                                if (modify.hasExtra("항목")) {
                                    String modify_s = modify.getStringExtra("항목");
                                    if (modify_s.equals("모임수정")) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
                                        AlertDialog dialog = builder.setMessage("모임을 수정하지 못했습니다")
                                                .setNegativeButton("확인", null)
                                                .create();
                                        dialog.show();
                                    }
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
                                    AlertDialog dialog = builder.setMessage("모임을 등록하지 못했습니다")
                                            .setNegativeButton("확인", null)
                                            .create();
                                    dialog.show();
                                }
                            }
                        } catch (Exception e) { // 오류가 발생했을 경우
                            e.printStackTrace();
                            // Log.e("error", "999");
                        }
                        // Log.e("error", "100");

                    }
                };

                // 에디트텍스트 내용들을 가져옴
                group_title_s = group_title.getText().toString(); //모임제목
                group_desc_s = group_desc.getText().toString(); //모임설명
                group_introduce_s = group_introduce.getText().toString(); //모임 한줄 소개
                group_cate_s = group_cate.getText().toString(); // 모임카테고리
                group_region_s = group_region.getText().toString(); // 활동지역
                String group_max_num_s = group_max_num.getText().toString(); // 모임정원

                // 리더
                Intent modify = getIntent();
                if (modify.hasExtra("항목")) {
                    String modify_s = modify.getStringExtra("항목");
                    Log.e(Tag, "항목은???");
                    if (modify_s.equals("모임수정")) {
                        Log.e(Tag, "항목은 모임수정!!");
                        // 수정하기 버튼을 눌렀을 때 실행
                        Log.e("모임수정_group_no", "" + group_no);

                        group_max_num_i = Integer.parseInt(group_max_num_s);

                        Log.e(Tag, "저장할 이미지이름1 : " + group_image_s);
                        // 이미지가 수정이 된 경우,
                        /*Log.e(Tag, "비트맵 있다없다 : " + group_photo);*/
                        if (group_photo != null) {
                            group_image_s = imageToString(group_photo);//모임사진
                            Log.e(Tag, "이미지가 바뀜!!!!!! 비트맵이미지에서 스트링으로 바꿔야함!");
                        }

                        ModifyGroupRequest modifyGroupRequest = new ModifyGroupRequest(group_no, group_image_s, group_title_s, group_cate_s, group_region_s, group_max_num_i, group_introduce_s, group_desc_s, responseListener1);
                        // 그룸생성 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                        RequestQueue queue = Volley.newRequestQueue(AddGroupActivity.this);
                        // 큐에 그룹 큐를 등록한다
                        queue.add(modifyGroupRequest);
                        Log.e(Tag, "서버에 모임내용 업데이트함");
                    }
                } else { // 추가하기 버튼을 눌렀을 때 실행
                    Log.e(Tag,"항목은 모임추가!!");
                    // 현재 로그인한 회원의 고유번호를 가져와서, 고유번호를 서버로 보낸다.
                    SharedPreferences SP = getSharedPreferences("login_user", MODE_PRIVATE);
                    String json = SP.getString("login_user", "");
                    Gson gson = new Gson();
                    Login_user_info login_user_info = gson.fromJson(json, Login_user_info.class);
                    String group_leader_s = login_user_info.getUser_no();
                    int group_leader_i = Integer.parseInt(group_leader_s);

                    group_max_num_i = Integer.parseInt(group_max_num_s);
                /*    String imageData = imageToString(profile_bitmap);*/
                    group_image_s = imageToString(group_photo);//모임사진

                   /* Log.e(Tag,"group_title_s : "+group_title_s);
                    Log.e(Tag,"group_image_s : "+group_image_s);
                    Log.e(Tag,"group_leader_i : "+group_leader_i);
                    Log.e(Tag,"group_cate_s : "+group_cate_s);
                    Log.e(Tag,"group_region_s : "+group_region_s);
                    Log.e(Tag,"group_max_num_i : "+group_max_num_i);
                    Log.e(Tag,"group_introduce_s : "+group_introduce_s);
                    Log.e(Tag,"group_desc_s : "+group_desc_s);*/

                    AddGroupRequest addGroupRequest = new AddGroupRequest(group_title_s, group_image_s, group_leader_i, group_cate_s, group_region_s, group_max_num_i, group_introduce_s, group_desc_s, responseListener1);
                    // 그룸생성 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                    RequestQueue queue = Volley.newRequestQueue(AddGroupActivity.this);
                    // 큐에 그룹 큐를 등록한다
                    queue.add(addGroupRequest);
                    Log.e(Tag, "서버에 모임추가함");
                }
            }
        });

    }

//    // 권한요청결과
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        // 갤러리에서 권한을 요청했고, 권한 허용된 경우는 앨범앱을 실행한다.
//        if(requestCode == CODE_GALLERY_REQUSEST){
//            if(grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED){
//                Intent go_gallery = new Intent(Intent.ACTION_PICK);
//                go_gallery.setType("image/*");
//                startActivityForResult(Intent.createChooser(go_gallery, "이미지 선택"), CODE_GALLERY_REQUSEST);
//            }else{
//                //권한 허용이 안된 경우는 아래 메세지를 띄운다.
//                Toast.makeText(getApplicationContext(),"권한이 없어 사진을 첨부할 수 없습니다",Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//
//    // 앨범에서 사진을 가지고 왔을 때!
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == CODE_GALLERY_REQUSEST && data != null && resultCode == RESULT_OK){
//            Uri filePath = data.getData();
//
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(filePath);
//                profile_bitmap = BitmapFactory.decodeStream(inputStream);
//                iv_group_profile_image.setImageBitmap(profile_bitmap);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    ;
//    public Map<String, String> getParams() throws AuthFailureError {
//        Map<String, String> params = new HashMap<>();
//        String imageData = imageToString(profile_bitmap);
//        params.put("image", imageData);
//
//        return params;
//    }

    // 이미지를 스트링으로 변환
    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageByte = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByte, Base64.DEFAULT);
        return encodedImage;

    }

    // 뒤로가기 버튼이 눌러졌을 때, 나오는 모임검색화면이 나오게 한다.
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

    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                Log.e("SmartWheel : ", mImageCaptureUri.getPath().toString());
            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

                Intent intent = new Intent("com.android.camera.action.CROP");

                intent.setDataAndType(mImageCaptureUri, "image/*");


                // CROP할 이미지를 200*200 크기로 저장

                intent.putExtra("outputX", 1000); // CROP한 이미지의 x축 크기

                intent.putExtra("outputY", 500); // CROP한 이미지의 y축 크기

                intent.putExtra("aspectX", 2); // CROP 박스의 X축 비율

                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율

                intent.putExtra("scale", true);

                intent.putExtra("return-data", true);

                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동

                break;

            }

            case CROP_FROM_iMAGE:

            {
                Log.e(Tag, "저장1");
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.

                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에

                // 임시 파일을 삭제합니다.

                if (resultCode != RESULT_OK) {
                    Log.e(Tag, "저장2");
                    return;

                }


                final Bundle extras = data.getExtras();


                // CROP된 이미지를 저장하기 위한 FILE 경로

               /* String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/studymate/profile.jpg";*/


                if (extras != null)

                {
                    group_photo = extras.getParcelable("data"); // CROP된 BITMAP
                    iv_group_profile_image.setImageBitmap(group_photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    Log.e(Tag, "저장3");
                    //storeCropImage(user_photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    //SaveBitmapToFileCache(user_photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    /*Log.e(Tag, "filePath :" + filePath);*/
                    //absoultePath = filePath;

                    // 비트맵을 uri로 바꿈
//                    Uri user_photo_uri = BitmapToUri(getContext(),user_photo);

                    // uri로 절대경로 받아옴


                    //Log.e(Tag,"절대경로 : "+absoultePath);
                    break;
                }

                // 임시 파일 삭제

                File f = new File(mImageCaptureUri.getPath());
                if (f.exists())

                {
                    f.delete();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(Tag,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(Tag,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(Tag,"onStop");
    }


}
