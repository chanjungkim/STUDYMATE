package com.example.gp62.studymate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.gp62.apprtc.CallActivity;
import com.example.gp62.apprtc.SendCallFcmRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;


// 모임상세화면에서 참여자리스트 내에 참여자 하나를 클릭하면, 유저프로필화면으로 이동한다.

    /*유저의 프로필을 볼 수 있는 화면*/
    /*유저프로필화면에는 상단에는 뒤로가기 버튼, 중앙에서부터 하단까지는 해당 유저의 프사, 닉네임, 성별과 나이대, 영상통화걸기버튼이 있다.*/

public class UserProfileActivity extends AppCompatActivity {


    // View
    ImageView user_profile_user_image;
    TextView user_profile_user_nick, user_profile_user_gender, user_profile_user_age;
    Toolbar user_profile_toolbar;
    Button user_profile_video_call_btn;

    // 프로필 유저 정보
    String Tag = "UserProfileActivity";
    String user_profile_user_image_s;
    String user_profile_user_nick_s;
    String user_profile_user_gender_s;
    String user_profile_user_age_s;
    String user_profile_user_no;

    // 현재 접속 유저 정보
    String user_nick;
    String user_no;

    //connectActivity
    private static final int CONNECTION_REQUEST = 1;
    private static boolean commandLineRun = false;

    String keyprefVideoCallEnabled;
    String keyprefScreencapture;
    String keyprefCamera2;
    String keyprefResolution;
    String keyprefFps;
    String keyprefCaptureQualitySlider;
    String keyprefVideoBitrateType;
    String keyprefVideoBitrateValue;
    String keyprefVideoCodec;
    String keyprefAudioBitrateType;
    String keyprefAudioBitrateValue;
    String keyprefAudioCodec;
    String keyprefHwCodecAcceleration;
    String keyprefCaptureToTexture;
    String keyprefFlexfec;
    String keyprefNoAudioProcessingPipeline;
    String keyprefAecDump;
    String keyprefOpenSLES;
    String keyprefDisableBuiltInAec;
    String keyprefDisableBuiltInAgc;
    String keyprefDisableBuiltInNs;
    String keyprefEnableLevelControl;
    String keyprefDisableWebRtcAGCAndHPF;
    String keyprefDisplayHud;
    String keyprefTracing;
    String keyprefRoomServerUrl;
    String keyprefRoom;
    String keyprefRoomList;
    ArrayList<String> roomList;
    ArrayAdapter<String> adapter;
    String keyprefEnableDataChannel;
    String keyprefOrdered;
    String keyprefMaxRetransmitTimeMs;
    String keyprefMaxRetransmits;
    String keyprefDataProtocol;
    String keyprefNegotiated;
    String keyprefDataId;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // findViewById
        user_profile_user_image = findViewById(R.id.user_profile_user_image); // 유저 사진
        user_profile_user_nick = findViewById(R.id.user_profile_user_nick); // 유저 닉네임
        user_profile_user_gender = findViewById(R.id.user_profile_user_gender); // 유저 성별
        user_profile_user_age = findViewById(R.id.user_profile_user_age); // 유저나이
        user_profile_toolbar = findViewById(R.id.user_profile_toolbar); // 툴바
        user_profile_video_call_btn = findViewById(R.id.user_profile_video_call_btn); // 영상통화버튼

        // 현재 접속 유저 정보
        SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = SP.getString("login_user", "");
        Login_user_info item = gson.fromJson(json, Login_user_info.class);

        user_no = item.getUser_no();
        user_nick = item.getUser_nick();

        //connectActivity
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
        keyprefScreencapture = getString(R.string.pref_screencapture_key);
        keyprefCamera2 = getString(R.string.pref_camera2_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key);
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key);
        keyprefFlexfec = getString(R.string.pref_flexfec_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key);
        keyprefAecDump = getString(R.string.pref_aecdump_key);
        keyprefOpenSLES = getString(R.string.pref_opensles_key);
        keyprefDisableBuiltInAec = getString(R.string.pref_disable_built_in_aec_key);
        keyprefDisableBuiltInAgc = getString(R.string.pref_disable_built_in_agc_key);
        keyprefDisableBuiltInNs = getString(R.string.pref_disable_built_in_ns_key);
        keyprefEnableLevelControl = getString(R.string.pref_enable_level_control_key);
        keyprefDisableWebRtcAGCAndHPF = getString(R.string.pref_disable_webrtc_agc_and_hpf_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefTracing = getString(R.string.pref_tracing_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);
        keyprefEnableDataChannel = getString(R.string.pref_enable_datachannel_key);
        keyprefOrdered = getString(R.string.pref_ordered_key);
        keyprefMaxRetransmitTimeMs = getString(R.string.pref_max_retransmit_time_ms_key);
        keyprefMaxRetransmits = getString(R.string.pref_max_retransmits_key);
        keyprefDataProtocol = getString(R.string.pref_data_protocol_key);
        keyprefNegotiated = getString(R.string.pref_negotiated_key);
        keyprefDataId = getString(R.string.pref_data_id_key);

        // 툴바에 뒤로가기버튼 세팅
        setSupportActionBar(user_profile_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 모임상세화면의 참여자리스트 내에서 클릭된 유저의 정보를 받아온다.
        // 유저정보 : 유저프사주소, 유저닉네임, 유저성별, 유저나이대
        Intent get_user_profile = getIntent();
        user_profile_user_image_s = get_user_profile.getStringExtra("user_image");
        user_profile_user_nick_s =  get_user_profile.getStringExtra("user_nick");
        user_profile_user_gender_s = get_user_profile.getStringExtra("user_gender");
        user_profile_user_age_s = get_user_profile.getStringExtra("user_age");
        user_profile_user_no = get_user_profile.getStringExtra("user_no");

//        Log.e(Tag,"user_image"+user_profile_user_image_s);
//        Log.e(Tag,"user_nick"+user_profile_user_nick_s);
//        Log.e(Tag,"user_gender"+user_profile_user_gender_s);
//        Log.e(Tag,"user_age"+user_profile_user_age_s);


        // 받아온 정보들을 각 뷰에 맞게 세팅해준다.
        // 프로필 사진
        String URL = new Variable().getURL();
        Glide
                .with(this)
//                    .load(mapList.get(position).getPlace_image())
                .load(URL+user_profile_user_image_s)
                .override(200,200) // 이미지 크기 변경
                .placeholder(R.drawable.ic_launcher_background) //이미지 불러오기 전까지 보여질 이미지
                .error(R.drawable.ic_launcher_background) // 이미지로딩이 실패했을시 보여질 이미지
                .into(user_profile_user_image);
        // + 프사 원모양으로 나오게하기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            user_profile_user_image.setBackground(new ShapeDrawable(new OvalShape()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            user_profile_user_image.setClipToOutline(true);
        }

        // 닉네임
        user_profile_user_nick.setText(user_profile_user_nick_s);
        // 성별
        user_profile_user_gender.setText(user_profile_user_gender_s);
        // 나이대
        user_profile_user_age.setText(user_profile_user_age_s);

        //final ConnectActivity connectActivity = new ConnectActivity();

        // 프로필이 자기 자신이면, 영상통화버튼이 안나오게 한다.
        if(user_no.equals(user_profile_user_no)){
            user_profile_video_call_btn.setVisibility(View.GONE);
        }

        // 영상통화버튼을 클릭하면, 영상통화 수신대기화면으로 이동한다.
        user_profile_video_call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수신자 유저 고유번호를 제목으로 방을 만들고 그 방에 들어가기
                Random random = new Random();
                int room_num = random.nextInt(500);
                connectToRoom(String.valueOf(room_num), false, false, false, 0);
                // 수신자 유저에게 영상통화 알림보내기
                getTokenSendFCM(String.valueOf(room_num),user_profile_user_no,user_no,user_nick);
                Log.e(Tag,"수신자 유저에게 알림보냄");
            }
        });
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

    /**영상통화기능**/
    public void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                              boolean useValuesFromIntent, int runTimeMs) {
        this.commandLineRun = commandLineRun;

        Log.e("ConnectActivity","영상통화 기능 메소드 시작");

        // roomId is random for loopback. roomId 는 루프백을 위해 무작위입니다.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        Log.e(Tag,"sharedPref : "+sharedPref);
        Log.e(Tag,"sharedPref : "+sharedPref);

        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Video call enabled flag. 화상 통화 가능 플래그
        boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
                CallActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);

        // Use screen capture option. 화면 캡처 옵션을 사용하십시오.
        boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
                CallActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);

        // Use Camera2 option. 카메라 2옵션을 사용하십시오.
        boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, CallActivity.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValuesFromIntent);

        // Get default codecs. 기본 코덱을 가져옵니다.
        String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
                CallActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
        String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
                CallActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);

        // Check HW codec flag. HW 코덱 플래그를 확인하십시오.
        boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
                CallActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);

        // Check Capture to texture. 텍스처에 캡쳐를 체크하십시오.
        boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
                CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValuesFromIntent);

        // Check FlexFEC. FlexFEC 를 확인하십시오.
        boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
                CallActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);

        // Check Disable Audio Processing flag. 오디오 처리 플래그 비활성화를 선택하십시오.
        boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
                CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValuesFromIntent);

        // Check Disable Audio Processing flag. 오디오 처리 플래그 비활성화를 선택하십시오.
        boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
                CallActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);

        // Check OpenSL ES enabled flag. OpenSL ES 사용 가능 플래그를 확인하십시오.
        boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
                CallActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);

        // Check Disable built-in AEC flag. 내장 AEC 플래그를 "비활성화하십시오"를 선택하십시오.
        boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValuesFromIntent);

        // Check Disable built-in AGC flag. 내장 AGC 플래그 비활성화를 선택하십시오.
        boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValuesFromIntent);

        // Check Disable built-in NS flag. 기본 제공 NS 플래그 비활성화를 선택하십시오.
        boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
                CallActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValuesFromIntent);

        // Check Enable level control. 레벨 제어 사용을 선택하십시오.
        boolean enableLevelControl = sharedPrefGetBoolean(R.string.pref_enable_level_control_key,
                CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                useValuesFromIntent);

        // Check Disable gain control 게인 제어 비활성화 확인
        boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
                R.string.pref_disable_webrtc_agc_and_hpf_key, CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
                R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);

        // Get video resolution from settings. 설정에서 비디오 해상도를 가져옵니다.
        int videoWidth = 0;
        int videoHeight = 0;
        if (useValuesFromIntent) {
            videoWidth = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0);
        }
        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
            Log.e(Tag, "keyprefResolution 이 뭐야!"+keyprefResolution+" 기본은? "+R.string.pref_resolution_default);


            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                    Log.e(Tag, "Wrong video resolution setting: 잘못된 카메라 fps 설정: " + resolution);
                }
            }
        }

        // Get camera fps from settings. 설정 카메라 fps 를 가져옵니다.
        int cameraFps = 0;
        if (useValuesFromIntent) {
            cameraFps = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
            Log.e(Tag, "keyprefFps 이 뭐야!"+keyprefFps+" 기본은? "+R.string.pref_fps_default);
            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                    Log.e(Tag, "Wrong camera fps setting: 잘못된 카메라 fps 설정" + fps);
                }
            }
        }

        // Check capture quality slider flag. 캡처 품질 슬라이더 플래그를 확인하십시오.
        boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
                CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValuesFromIntent);

        // Get video and audio start bitrate. 비디오 및 오디오 시작 비트 전송률을 확보하십시오.
        int videoStartBitrate = 0;
        if (useValuesFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefVideoBitrateValue, getString(R.string.pref_maxvideobitratevalue_default));
                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValuesFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(CallActivity.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = sharedPref.getString(
                        keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option. 통계 표시 옵션을 확인하십시오.
        boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
                CallActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);

        boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, CallActivity.EXTRA_TRACING,
                R.string.pref_tracing_default, useValuesFromIntent);

        // Get datachannel options 데이터 채널 옵션 가져오기
        boolean dataChannelEnabled = sharedPrefGetBoolean(R.string.pref_enable_datachannel_key,
                CallActivity.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValuesFromIntent);
        boolean ordered = sharedPrefGetBoolean(R.string.pref_ordered_key, CallActivity.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValuesFromIntent);
        boolean negotiated = sharedPrefGetBoolean(R.string.pref_negotiated_key,
                CallActivity.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValuesFromIntent);
        int maxRetrMs = sharedPrefGetInteger(R.string.pref_max_retransmit_time_ms_key,
                CallActivity.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValuesFromIntent);
        int maxRetr =
                sharedPrefGetInteger(R.string.pref_max_retransmits_key, CallActivity.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValuesFromIntent);
        int id = sharedPrefGetInteger(R.string.pref_data_id_key, CallActivity.EXTRA_ID,
                R.string.pref_data_id_default, useValuesFromIntent);

        String protocol = sharedPrefGetString(R.string.pref_data_protocol_key,
                CallActivity.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValuesFromIntent);

        // Start AppRTCMobile activity. AppRTCMoblie 활동을 시작합니다.
        Log.e(Tag, "Connecting to room 방에 연결하기 " + roomId + " at URL  URL에" + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, CallActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallActivity.EXTRA_SCREENCAPTURE, useScreencapture);
            intent.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(CallActivity.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
            intent.putExtra(CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, disableWebRtcAGCAndHPF);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallActivity.EXTRA_TRACING, tracing);
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);

            intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

            if (dataChannelEnabled) {
                intent.putExtra(CallActivity.EXTRA_ORDERED, ordered);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
                intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, maxRetr);
                intent.putExtra(CallActivity.EXTRA_PROTOCOL, protocol);
                intent.putExtra(CallActivity.EXTRA_NEGOTIATED, negotiated);
                intent.putExtra(CallActivity.EXTRA_ID, id);
            }

            if (useValuesFromIntent) {
                if (getIntent().hasExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                    String videoFileAsCamera =
                            getIntent().getStringExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA);
                    intent.putExtra(CallActivity.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                    String saveRemoteVideoToFile =
                            getIntent().getStringExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                    int videoOutWidth =
                            getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
                }

                if (getIntent().hasExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                    int videoOutHeight =
                            getIntent().getIntExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                    intent.putExtra(CallActivity.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
                }
            }

            startActivityForResult(intent, CONNECTION_REQUEST);
        }

        Log.e("ConnectActivity","영상통화 끝");
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * 공유 환경 설정 또는 의도에서 값을 가져옵니다.(그렇지 않은 경우)
     * exist the default is used.
     * 존재하는 기본값이 사용됩니다.
     */
    private boolean sharedPrefGetBoolean(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {

        Log.e("ConnectActivity","sharedPrefGetBoolean");

        boolean defaultValue = Boolean.valueOf(getString(defaultId));
        if (useFromIntent) {
            return getIntent().getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getBoolean(attributeName, defaultValue);
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * 공유 환경 설정 또는 의도에서 값을 가져옵니다.(그렇지 않은 경우)
     * exist the default is used.
     * 존재하는 기본값이 사용됩니다.
     */
    private int sharedPrefGetInteger(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {

        Log.e("ConnectActivity","sharedPrefGetInteger");

        String defaultString = getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return getIntent().getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            String value = sharedPref.getString(attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                Log.e(Tag, "Wrong setting for: 잘못된 설정" + attributeName + ":" + value);
                return defaultValue;
            }
        }
    }

    /**
     * Get a value from the shared preference or from the intent, if it does not
     * 공유 환경 설정 또는 의도에서 값을 가져옵니다.(그렇지 않은 경우)
     * exist the default is used.
     * 존재하는 기본값이 사용됩니다.
     */
    private String sharedPrefGetString(
            int attributeId, String intentName, int defaultId, boolean useFromIntent) {

        Log.e("ConnectActivity","sharedPrefGetString");

        String defaultValue = getString(defaultId);
        if (useFromIntent) {
            String value = getIntent().getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = getString(attributeId);
            return sharedPref.getString(attributeName, defaultValue);
        }
    }

    private boolean validateUrl(String url) {

        Log.e("ConnectActivity","validateUrl");

        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

    public void getTokenSendFCM(String room_num, String user_profile_user_no, String user_no, String user_nick){
        Response.Listener<String> responseListener_getTokenSendFCM = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(Tag, "response : "+response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.e(Tag, "getToken_response : "+response);
                    Log.e(Tag, "jsonResponse : "+jsonResponse);

                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                }
            }
        };

        SendCallFcmRequest sendCallFcmRequest = new SendCallFcmRequest(room_num, user_profile_user_no,user_no,user_nick,responseListener_getTokenSendFCM);
        Log.e(Tag,"1");
        // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
        RequestQueue queue = Volley.newRequestQueue(UserProfileActivity.this);
        Log.e(Tag,"2");
        // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
        queue.add(sendCallFcmRequest);
        Log.e(Tag,"3");
    }
}
