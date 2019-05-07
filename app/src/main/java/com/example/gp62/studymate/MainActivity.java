package com.example.gp62.studymate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.gp62.Map.GoogleMapActivity;
import com.example.gp62.apprtc.CallActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int GO_myGroup = 3333;
    String Tag = "MainActivity";
    BottomNavigationView navigation;

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // 각 탭에 맞는 화면 나오게 하기
            switch (item.getItemId()) {
                case R.id.navigation_searchPlace: // 장소검색
                    // 프래그먼트를 searchPlaceFragment 으로 대체한다.
                    //transaction.replace(R.id.content,new searchPlaceFragment()).commit();
                    Intent go_map = new Intent(getApplicationContext(), GoogleMapActivity.class);
                    startActivity(go_map);
                   // navigation.getMenu().getItem(R.id.navigation_searchGroup);
                   // onMenuItemSelected(R.id.navigation_myGroup,item);
                    return true;
                case R.id.navigation_searchGroup: // 모임검색
                    transaction.replace(R.id.content, new searchGroupFragment()).commit();
                    return true;

                case R.id.navigation_myGroup: // 내모임
                    SharedPreferences SP = getSharedPreferences("login_user", Context.MODE_PRIVATE);
                    if (SP.contains("login_user")) {
                        transaction.replace(R.id.content, new myGroupFragment()).commit();
                    } else {
                        transaction.replace(R.id.content, new searchGroupFragment()).commit();
                        Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                        Intent go_login = new Intent(getApplicationContext(), LoginActivity.class);
                        go_login.putExtra("로그인전상황", "내모임클릭");
                        startActivity(go_login);
                        finish();
                    }
                    return true;
                case R.id.navigation_settings: // 설정
                    SharedPreferences SP1 = getSharedPreferences("login_user", Context.MODE_PRIVATE);
                    if (SP1.contains("login_user")) {
                        transaction.replace(R.id.content, new settingsFragment()).commit();
                    } else {
                        transaction.replace(R.id.content, new searchGroupFragment()).commit();
                        Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                        Intent go_login = new Intent(getApplicationContext(), LoginActivity.class);
                        go_login.putExtra("로그인전상황", "설정중로그인");
                        startActivity(go_login);
                        finish();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.e(Tag,"onCreate");

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

        /*채팅알림을 통해 메인화면으로 들어온 경우*/
        Intent in = getIntent();
        String a = in.getStringExtra("chat");
        Log.e(Tag,"chat : "+a);
        if(a!=null){
            String group_no =in.getStringExtra("group_no");
            Log.e(Tag, "채팅알림에서 메인화면으로 넘어온 group_no : "+group_no);
            int group_no_i = Integer.valueOf(group_no);
            Intent intent = new Intent(this,GroupViewActivity.class);
            intent.putExtra("chat","chat");
            intent.putExtra("group_no",group_no_i);
            startActivity(intent);
        }

        /*영상통화알림을 통해 메인화면으로 들어온 경우*/
        String a1 = in.getStringExtra("call");
        Log.e(Tag,"call : "+a1);
        if(a1!=null){
            String room_num =in.getStringExtra("room_num");
            Log.e(Tag, "영상통화 방 제목인 room_num : "+room_num);
            connectToRoom(room_num, false, false, false, 0);
        }

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // 기본 화면
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new searchGroupFragment()).commit();

        Intent move = getIntent();
        if (move.hasExtra("탭이동")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (move.getStringExtra("탭이동").equals("내모임")) {
                //Log.e(Tag,"내모임탭: "+R.id.navigation_myGroup);
                navigation.setSelectedItemId(R.id.navigation_myGroup);
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, new myGroupFragment()).commit();
            } else if (move.getStringExtra("탭이동").equals("장소검색")) {
                navigation.setSelectedItemId(R.id.navigation_searchPlace);
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, new searchPlaceFragment()).commit();
            } else if (move.getStringExtra("탭이동").equals("모임검색")) {
                navigation.setSelectedItemId(R.id.navigation_searchGroup);
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content, new searchGroupFragment()).commit();
            }
        }

//        Intent intent = getIntent();
//        // 모임추가화면에서
//        if(!intent.equals("")){
//            MenuItem m =navigation.getMenu().getItem(3);
//            mOnNavigationItemSelectedListener.onNavigationItemSelected(m);
//                getSupportFragmentManager().beginTransaction().replace(R.id.content, new myGroupFragment()).commit();
//        }

        // 카카오지도api를 호출하기 위해서 앱의 해쉬키를 얻어내ㅐ는 코드
//        try{
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String key = new String(Base64.encode(md.digest(), 0));
//                Log.e("Hash key:", "!!!!!!!"+key+"!!!!!!");
//            }
//        } catch (Exception e){
//            Log.e("name not found", e.toString());
//        }
    }


    // 뒤로가기 두번하면, 앱종료시키기
    private long lastTimeBackPressed;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            // 버튼을 한번 누른 다음, 1.5초 이내로 한 번 더 누르게 되면 앱종료가 됨
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로'버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.e(Tag,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.e(Tag,"onResume");
        if(navigation.getSelectedItemId() ==R.id.navigation_searchPlace){
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.replace(R.id.content, new searchGroupFragment()).commit();
            navigation.setSelectedItemId(R.id.navigation_searchGroup);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.e(Tag,"onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.e(Tag,"onPause");
    }


    @Override
    protected void onStop() {
        super.onStop();
        //Log.e(Tag,"onStop");
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
}

