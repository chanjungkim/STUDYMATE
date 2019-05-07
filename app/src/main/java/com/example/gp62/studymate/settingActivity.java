package com.example.gp62.studymate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class settingActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;
    Bitmap user_photo;

    android.support.v7.widget.Toolbar toolbar;
    EditText user_email, user_nick, user_cate, user_region;
    Button user_info_modify_btn;
    ImageView iv_user_photo;
    String Tag = "settingActivity";
    private Uri mImageCaptureUri;
    private String absoultePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Log.e(Tag, "onCreate");
        // xml파일과 연결
        // 툴바
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        iv_user_photo = findViewById(R.id.user_photo);
        user_email = findViewById(R.id.user_email);
        user_nick = findViewById(R.id.user_nick);
        user_cate = findViewById(R.id.user_cate);
        user_region = findViewById(R.id.user_region);
        user_info_modify_btn = (Button) findViewById(R.id.user_info_modify_btn);
       // user_logout = (Button) findViewById(R.id.user_logout);

        /*프사 원모양으로 나오게 하기*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            iv_user_photo.setBackground(new ShapeDrawable(new OvalShape()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv_user_photo.setClipToOutline(true);
        }

        Log.e(Tag, "1");
        /*SharedPreferences SP = getActivity().getSharedPreferences("login_user", Context.MODE_PRIVATE);
        Log.e(Tag, "2");

//        if (SP.contains("login_user")) {
//            // 쉐어드에서 정보 세팅
        Gson gson = new Gson();
        Log.e(Tag, "3");
        String json = SP.getString("login_user", "");
        Login_user_info item = gson.fromJson(json, Login_user_info.class);
        // 이메일
        Log.e(Tag, "4");
        String user_email_s = item.getUser_email();
        user_email.setText(user_email_s);
        Log.e(Tag, user_email_s);
        // 닉네임
        String user_nick_s = item.getUser_nick();
        user_nick.setText(user_nick_s);
        Log.e(Tag, user_nick_s);
        // 카테고리
        String user_cate_s = item.getUser_cate();
        user_cate.setText(user_cate_s);
        Log.e(Tag, user_cate_s);
        // 지역
        String user_region_s = item.getUser_region();
        user_region.setText(user_region_s);*/

        Response.Listener<Bitmap> responseListener = new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.e(Tag, "성공" + response);
                iv_user_photo.setImageBitmap(response);
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
        String SERVER_URL = new Variable().getURL();
        String URL = SERVER_URL + "/upload/images/user_photo_" + user_no + ".jpeg";
        Log.e(Tag, "저장된 파일 주소" + URL);
        ImageRequest imageRequest = new ImageRequest(URL, responseListener, 0, 0, Bitmap.Config.ARGB_8888, errorListener);

        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
        // 큐에 그룹 큐를 등록한다
        queue1.add(imageRequest);

        // 클릭이벤트 지역, 카테고리
        user_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"어학", "취업", "취미", "자격증/시험", "기타"};
                // 다이얼로그창 띄우기
                final AlertDialog.Builder dialog_choice_cate = new AlertDialog.Builder(getApplicationContext());
                dialog_choice_cate.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 카테고리를 선택하면, 모임카테고리 에디트텍스트 창에 입력됨
                        user_cate.setText(items[which]);
                    }
                });
                AlertDialog alertDialog = dialog_choice_cate.create();
                alertDialog.show();
            }
        });

        user_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"서울", "경기", "인천", "강원", "충남", "충북", "대전", "경북", "경남", "대구", "부산", "울산", "전북", "전남", "광주", "제주", "세종"};
                // 다이얼로그창 띄우기
                final AlertDialog.Builder dialog_choice_region = new AlertDialog.Builder(getApplicationContext());
                dialog_choice_region.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 지역을 선택하면, 활동지역 에디트텍스트 창에 입력됨
                        user_region.setText(items[which]);
                    }
                });
                AlertDialog alertDialog = dialog_choice_region.create();
                alertDialog.show();
            }
        });

        //프사클릭시,
        iv_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다이얼로그를 띄운다
                final CharSequence items[] = new CharSequence[]{"카메라", "앨범"};
                final AlertDialog.Builder dialog_choice_image = new AlertDialog.Builder(settingActivity.this);

                dialog_choice_image.setTitle("이미지 불러오기");
                dialog_choice_image.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0: // 카메라
                                doTakePhotoAction();
//                                Intent go_camera = new Intent(getApplicationContext(),CameraActivity.class);
//                                startActivity(go_camera);
                            break;
                            case 1: // 앨범
                                doTakeAlbumAction();
                            break;
                        }
                    }
                });
                AlertDialog alertDialog = dialog_choice_image.create();
                alertDialog.show();

//                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
//                dialog = builder.setMessage("이메일을 입력해주세요")
//                        .setPositiveButton("확인", null)
//                        .create();
//                dialog.show();
            }
        });

        // 프로필 수정하기
        user_info_modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("error", "1");
                        try {
                            Log.e(Tag, "modify_response: " + response);
                            Log.e(Tag, "수정1");
                            JSONObject jsonResponse = new JSONObject(response);
                            // success : 해당 과정이 정상적으로 수행 되었는지 response값
                            Log.e(Tag, "수정2");
                            boolean success = jsonResponse.getBoolean("success");
                            //Log.e(Tag, "3");
                            Log.e(Tag, "" + success);
                            Log.e(Tag, "수정3");
                            if (success) { // 정상적으로 삭제되면, 현재 보이는 상세페이지는 닫힘 & 회원
//                                Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),"수정되었습니다.",Toast.LENGTH_SHORT).show();
                                Log.e(Tag, "수정4");
                                SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
                                Gson gson = new Gson();
                                String json = SP.getString("login_user", "");
                                Login_user_info item = gson.fromJson(json, Login_user_info.class);

                                String user_no = item.getUser_no(); // 유저고유번호
                                String user_email_s = user_email.getText().toString();// 이메일
                                String user_nick_s = user_nick.getText().toString();// 닉네임
                                String user_cate_s = user_cate.getText().toString();// 카테고리
                                String user_region_s = user_region.getText().toString();// 지역

                                String user_photo_s = user_photo.toString();// 포토
                                Log.e(Tag, "쉐어드저장전 유저사진확인 : " + user_photo);

                                String user_gender_s = jsonResponse.getString("user_gender"); // 성별
                                String user_age_s = jsonResponse.getString("user_age"); // 연령대
                                if(jsonResponse.has("user_photo")){
                                    user_photo_s = jsonResponse.getString("user_photo");  // 사진
                                    Log.e(Tag, "서버에서 불러온 유저포토 : " + user_photo);
                                }

                                SharedPreferences.Editor editor = SP.edit();
                                editor.clear();
                                Login_user_info login_user_info = new Login_user_info(user_no, user_email_s, user_photo_s, user_nick_s, user_gender_s, user_age_s, user_region_s, user_cate_s);
                                json = gson.toJson(login_user_info);
                                editor.putString("login_user", json);
                                // Log.e(Tag,json);)s
                                Log.e(Tag, response);
                                editor.apply();

                                //Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();


                            } else { // 수정이 안됐다면, 수정하지 못했다는 토스트를 띄운다
                                Toast.makeText(getApplicationContext(), "수정하지 못했습니다", Toast.LENGTH_SHORT).show();
                                Log.e(Tag, "수정5");
                            }
                        } catch (Exception e) { // 오류가 발생했을 경우
                            e.printStackTrace();
                            Log.e(Tag, "수정6");
                            // Log.e("error", "999");
                        }
                        // Log.e("error", "100");

                    }
                };

                SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = SP.getString("login_user", "");
                Login_user_info item = gson.fromJson(json, Login_user_info.class);

                String user_no = item.getUser_no(); // 유저고유번호
                String user_nick_s = user_nick.getText().toString();
                String user_cate_s = user_cate.getText().toString();
                String user_region_s = user_region.getText().toString();
                String user_photo_s = imageToString(user_photo);
               Log.e(Tag,"user_photo : "+user_photo);
               Log.e(Tag,"user_photo_s : "+user_photo_s);

                Profile_modify_Request profile_modify_request = new Profile_modify_Request(user_no, user_nick_s, user_photo_s, user_cate_s, user_region_s, responseListener1);
                // 그룸생성 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                // 큐에 그룹 큐를 등록한다
                queue.add(profile_modify_request);


                //SharedPreferences SP1 = getSharedPreferences("login_user", Context.MODE_PRIVATE);
                //Log.e(Tag, "resumse2");
                /*if (SP.contains("login_user")) {
                    Log.e(Tag, "resumse3");
                    //Gson gson = new Gson();
                    String json1 = SP.getString("login_user", "");
                    Login_user_info item1 = gson.fromJson(json1, Login_user_info.class);

                    //포토
                    com.android.volley.Response.Listener<Bitmap> responseListener = new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            Log.e(Tag, "성공" + response);
                            iv_user_photo.setImageBitmap(response);
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(Tag, "실패");
                        }
                    };

                    String user_no1 = item1.getUser_no();
                    String SERVER_URL = new variable().getURL();
                    String URL = SERVER_URL + "/upload/images/user_photo_" + user_no1 + ".jpeg";
                    Log.e(Tag, "저장된 파일 주소" + URL);
                    ImageRequest imageRequest = new ImageRequest(URL, responseListener, 0, 0, Bitmap.Config.ARGB_8888, errorListener);

                    RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                    // 큐에 그룹 큐를 등록한다
                    queue1.add(imageRequest);
                }*/

            }
        });
//        } else {
//            Toast.makeText(getActivity(),"로그인이 필요한 서비스입니다.",Toast.LENGTH_SHORT).show();
//            Intent go_login = new Intent(getContext(), LoginActivity.class);
//            go_login.putExtra("로그인전상황", "설정중로그인");
//            startActivity(go_login);
//        }
//        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(Tag, "onResume");
        Log.e(Tag, "resumse1");
        SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
        Log.e(Tag, "resumse2");
        if (SP.contains("login_user")) {
            Log.e(Tag, "resumse3");
            Gson gson = new Gson();
            String json = SP.getString("login_user", "");
            Login_user_info item = gson.fromJson(json, Login_user_info.class);

            /*Log.e(Tag,"유저포토가져오기 : "+item.getUser_photo());*/
            //포토
//            if(item.getUser_photo()!=null){
//                Log.e(Tag,"포토1");
////                String user_photo_s = item.getUser_photo();
////                iv_user_photo.setImageURI(user_photo_uri);
//                //String user_photo_s = item.getUser_photo();
//                iv_user_photo.setImageBitmap(user_photo);
//            }else {
//                Log.e(Tag,"포토2");
//                //iv_user_photo.setImageDrawable(null);
//            }

            //포토


            // 이메일
            String user_email_s = item.getUser_email();
            user_email.setText(user_email_s);
            // 닉네임
            String user_nick_s = item.getUser_nick();
            user_nick.setText(user_nick_s);
            // 카테고리
            String user_cate_s = item.getUser_cate();
            if (user_cate_s.equals("카테고리")) {
                user_cate.setText("카테고리를 선택해주세요");
            } else {
                user_cate.setText(user_cate_s);
            }
            // 지역
            String user_region_s = item.getUser_region();
            if (user_region_s.equals("지역")) {
                user_region.setText("지역을 선택해주세요");
            } else {
                user_region.setText(user_region_s);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(Tag, "onPause");
    }


    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        //Intent intent = new Intent();
        Intent intent = new Intent(this,CameraActivity.class);
        // 임시로 사용할 파일의 경로를 생성
//        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
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
                Log.e(Tag,"mImageCaptureUri : "+mImageCaptureUri);

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // CROP할 이미지를 200*200 크기로 저장

                intent.putExtra("outputX", 300); // CROP한 이미지의 x축 크기

                intent.putExtra("outputY", 300); // CROP한 이미지의 y축 크기

                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율

                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율

                intent.putExtra("scale", true);

                intent.putExtra("return-data", true);

                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동
                break;
            }

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

//                mImageCaptureUri = data.getData();
//                Log.e(Tag,"받은uri : "+mImageCaptureUri);
//
//                Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(mImageCaptureUri, "image/*");
//
//                // CROP할 이미지를 200*200 크기로 저장
//
//                intent.putExtra("outputX", 300); // CROP한 이미지의 x축 크기
//
//                intent.putExtra("outputY", 300); // CROP한 이미지의 y축 크기
//
//                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
//
//                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
//
//                intent.putExtra("scale", true);
//
//                intent.putExtra("return-data", true);
//
//                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동


               // iv_user_photo.setImageBitmap(user_photo);
                String filename = data.getStringExtra("filename");

                File imgFile = new  File(filename);

//                if(imgFile.exists()){

                    user_photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                   // iv_user_photo.setImageBitmap(user_photo);

                Glide.with(this)
                        .load(filename)
                        .centerCrop()
                        .into(iv_user_photo);
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

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/studymate/profile.jpg";


                if (extras != null)

                {
                    user_photo = extras.getParcelable("data"); // CROP된 BITMAP
                    iv_user_photo.setImageBitmap(user_photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    Log.e(Tag, "저장3");
                    storeCropImage(user_photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    //SaveBitmapToFileCache(user_photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    Log.e(Tag, "filePath :" + filePath);
                    absoultePath = filePath;

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

    //비트맵을저장하는부분
    private void storeCropImage(Bitmap bitmap, String filePath) {

        // SmartWheel 폴더를 생성하여 이미지를 저장하는 방식이다.
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/studymate";
        File directory_SmartWheel = new File(dirPath);


        if (!directory_SmartWheel.exists()) // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
            directory_SmartWheel.mkdir();


        File copyFile = new File(filePath);
        BufferedOutputStream outputStream = null;
        Log.e(Tag, "파일경로 = " + dirPath);

       /* try {
            FileOutputStream os = getActivity().openFileOutput(filePath,Context.MODE_PRIVATE);
            os.write(bitmap.getRowBytes());
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            copyFile.createNewFile();
            //copyFile = File.createTempFile(filePath,null,getCacheDir());

            outputStream = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));

            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {

            Log.e(Tag, "절대경로 이미지저장 실패");
            e.printStackTrace();

        }

    }

    private void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;
        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageByte = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageByte, Base64.DEFAULT);
        return encodedImage;
    }


    private Uri BitmapToUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /*//URI로부터 경로 받아오기
    private String getRealPathFromURI(Uri contentUri) {
//        Cursor cursor = null;
//        int column_index = 0;
//        String[] proj = {MediaStore.Images.Media.DATA};
//        cursor = getContentResolver().query(contentUri, proj, null, null, null);
//        if (cursor.moveToFirst()) {
//            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        }
//
//        return cursor.getString(column_index);

        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        try {
            Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);

        } catch (Exception e) {
            result = null;
        }
        return result;
    }*/

}
