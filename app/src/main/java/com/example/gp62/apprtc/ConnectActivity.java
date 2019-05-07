/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.example.gp62.apprtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gp62.studymate.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Handles the initial setup where the user selects which room to join.
 * 사용자가 참가할 방을 선택하는 초기 설정을 처리합니다.
 */
public class ConnectActivity extends Activity {
  public static Context mcontext;

  private static final String TAG = "ConnectActivity";
  private static final int CONNECTION_REQUEST = 1;
  private static final int REMOVE_FAVORITE_INDEX = 0;
  private static boolean commandLineRun = false;

  private ImageButton connectButton; //영상통화를 시작하는 버튼 (수화기 아이콘)
  private ImageButton addFavoriteButton; // 전화번호를 등록하는 버튼 (플러스 아이콘)
  private EditText roomEditText; // 전화번호를 입력하는 EditText
  private ListView roomListView; //등록한 전화번호들을 보여주는 리스트뷰
  private SharedPreferences sharedPref; //어떤 쉐어드인지 알아보자

  /**res -> values -> strings 에 있는 키 값들**/
  private String keyprefVideoCallEnabled;
  private String keyprefScreencapture;
  private String keyprefCamera2;
  private String keyprefResolution;
  private String keyprefFps;
  private String keyprefCaptureQualitySlider;
  private String keyprefVideoBitrateType;
  private String keyprefVideoBitrateValue;
  private String keyprefVideoCodec;
  private String keyprefAudioBitrateType;
  private String keyprefAudioBitrateValue;
  private String keyprefAudioCodec;
  private String keyprefHwCodecAcceleration;
  private String keyprefCaptureToTexture;
  private String keyprefFlexfec;
  private String keyprefNoAudioProcessingPipeline;
  private String keyprefAecDump;
  private String keyprefOpenSLES;
  private String keyprefDisableBuiltInAec;
  private String keyprefDisableBuiltInAgc;
  private String keyprefDisableBuiltInNs;
  private String keyprefEnableLevelControl;
  private String keyprefDisableWebRtcAGCAndHPF;
  private String keyprefDisplayHud;
  private String keyprefTracing;
  private String keyprefRoomServerUrl;
  private String keyprefRoom;
  private String keyprefRoomList;
  private ArrayList<String> roomList;
  private ArrayAdapter<String> adapter;
  private String keyprefEnableDataChannel;
  private String keyprefOrdered;
  private String keyprefMaxRetransmitTimeMs;
  private String keyprefMaxRetransmits;
  private String keyprefDataProtocol;
  private String keyprefNegotiated;
  private String keyprefDataId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.e("ConnectActivity","OnCreate");

    // Get setting keys. 설정 키를 얻으십시오.
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

    setContentView(R.layout.activity_connect);

    Log.e("ConnectActivity","Key 입력 끝");

    /**전화번호를 입력하는 Edit Text**/
    roomEditText = (EditText) findViewById(R.id.room_edittext);
    roomEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
          addFavoriteButton.performClick();


          Log.e("ConnectActivity","전화번호를 입력하는 Edit Text");
          return true;
        }

        Log.e("ConnectActivity","전화번호 입력 조건문 무시");
        return false;
      }
    });
    roomEditText.requestFocus();

    roomListView = (ListView) findViewById(R.id.room_listview);

    roomListView.setEmptyView(findViewById(android.R.id.empty));

    /**리스트뷰 아이템 클릭 이벤트**/
    roomListView.setOnItemClickListener(roomListClickListener);
    registerForContextMenu(roomListView);

    /**수화기 아이콘 클릭 이벤트**/
    connectButton = (ImageButton) findViewById(R.id.connect_button);
    connectButton.setOnClickListener(connectListener);

    /**플러스 아이콘 클릭 이벤트**/
    addFavoriteButton = (ImageButton) findViewById(R.id.add_favorite_button);
    addFavoriteButton.setOnClickListener(addFavoriteListener);

//    // If an implicit VIEW intent is launching the app, go directly to that URL.
//    //암시 적 VIEW 인텐트가 앱을 실행하는 경우 해당 URL 로 바로 이동합니다.
//    final Intent intent = getIntent();
//    if ("android.intent.action.VIEW".equals(intent.getAction()) && !commandLineRun) {
//
//      Log.e("ConnectActivity","암시적 View 인텐트가 앱을 실행하는 경우");
//
//      String room = sharedPref.getString(keyprefRoom, "");
//
//      boolean loopback = intent.getBooleanExtra(CallActivity.EXTRA_LOOPBACK, false);
//
//      boolean useValuesFromIntent = intent.getBooleanExtra(CallActivity.EXTRA_USE_VALUES_FROM_INTENT, false);
//
//      int runTimeMs = intent.getIntExtra(CallActivity.EXTRA_RUNTIME, 0);
//
//      Log.e("ConnectActivity","영상토화 메소드 시작");
//      /**영상통화 메소드**/
//      connectToRoom(room, true, loopback, useValuesFromIntent, runTimeMs);
//    }
    mcontext = this;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.e("ConnectActivity","OnCreateOptionsMenu");
    getMenuInflater().inflate(R.menu.connect_menu, menu);
    return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    Log.e("ConnectActivity","onCreateContextMenu");
    if (v.getId() == R.id.room_listview) {
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
      menu.setHeaderTitle(roomList.get(info.position));
      String[] menuItems = getResources().getStringArray(R.array.roomListContextMenu);
      for (int i = 0; i < menuItems.length; i++) {
        menu.add(Menu.NONE, i, i, menuItems[i]);
      }
    } else {
      super.onCreateContextMenu(menu, v, menuInfo);
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {

    Log.e("ConnectActivity","onContextItemSelected");

    if (item.getItemId() == REMOVE_FAVORITE_INDEX) {
      AdapterView.AdapterContextMenuInfo info =
          (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
      roomList.remove(info.position);
      adapter.notifyDataSetChanged();
      return true;
    }

    return super.onContextItemSelected(item);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.e("ConnectActivity","onOptionsItmeSelected");
    // Handle presses on the action bar items. 작업 표시 줄 항목의 프레스를 처리합니다.
    if (item.getItemId() == R.id.action_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
      return true;
    } else if (item.getItemId() == R.id.action_loopback) {
      connectToRoom(null, false, true, false, 0);
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  /**ListView의 등록한 룸 이름를 저장한다.**/
  @Override
  public void onPause() {
    super.onPause();
    Log.e("ConnectActivity","OnPause");
    String room = roomEditText.getText().toString();
    String roomListJson = new JSONArray(roomList).toString();
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString(keyprefRoom, room);
    editor.putString(keyprefRoomList, roomListJson);
    editor.commit();
  }

  /**ListView의 등록한 룸 이름을 불러온다.**/
  @Override
  public void onResume() {
    super.onResume();

    Log.e("ConnectActivity","OnResume");
    String room = sharedPref.getString(keyprefRoom, "");
    roomEditText.setText(room);
    roomList = new ArrayList<String>();
    String roomListJson = sharedPref.getString(keyprefRoomList, null);
    if (roomListJson != null) {
      try {
        JSONArray jsonArray = new JSONArray(roomListJson);
        for (int i = 0; i < jsonArray.length(); i++) {
          roomList.add(jsonArray.get(i).toString());
        }
      } catch (JSONException e) {
        Log.e(TAG, "Failed to load room list: 방 목록을 로드하지 못했습니다. " + e.toString());
      }
    }
    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomList);
    roomListView.setAdapter(adapter);
    if (adapter.getCount() > 0) {
      roomListView.requestFocus();
      roomListView.setItemChecked(0, true);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    if (requestCode == CONNECTION_REQUEST && commandLineRun) {
      Log.e("ConnectActivity","onActivityResult"+ requestCode);
      Log.d(TAG, "Return: 리턴 " + resultCode);

      setResult(resultCode);
      commandLineRun = false;
      finish();
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
        Log.e(TAG, "Wrong setting for: 잘못된 설정" + attributeName + ":" + value);
        return defaultValue;
      }
    }
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

    Log.e(TAG,"sharedPref : "+sharedPref);
    Log.e(TAG,"sharedPref : "+sharedPref);

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
      Log.e(TAG, "keyprefResolution 이 뭐야!"+keyprefResolution+" 기본은? "+R.string.pref_resolution_default);


      String[] dimensions = resolution.split("[ x]+");
      if (dimensions.length == 2) {
        try {
          videoWidth = Integer.parseInt(dimensions[0]);
          videoHeight = Integer.parseInt(dimensions[1]);
        } catch (NumberFormatException e) {
          videoWidth = 0;
          videoHeight = 0;
          Log.e(TAG, "Wrong video resolution setting: 잘못된 카메라 fps 설정: " + resolution);
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
      Log.e(TAG, "keyprefFps 이 뭐야!"+keyprefFps+" 기본은? "+R.string.pref_fps_default);
      String[] fpsValues = fps.split("[ x]+");
      if (fpsValues.length == 2) {
        try {
          cameraFps = Integer.parseInt(fpsValues[0]);
        } catch (NumberFormatException e) {
          cameraFps = 0;
          Log.e(TAG, "Wrong camera fps setting: 잘못된 카메라 fps 설정" + fps);
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
    Log.e(TAG, "Connecting to room 방에 연결하기 " + roomId + " at URL  URL에" + roomUrl);
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

  /**리스트뷰 아이템 클릭 이벤트**/
  private final AdapterView.OnItemClickListener roomListClickListener =
      new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

          Log.e("ConnectActivity","리스트뷰 아이템 클릭 이벤트");

          String roomId = ((TextView) view).getText().toString();
          connectToRoom(roomId, false, false, false, 0);
        }
      };

      /**플러스 버튼 클릭이벤트**/
  private final OnClickListener addFavoriteListener = new OnClickListener() {
    @Override
    public void onClick(View view) {
      String newRoom = roomEditText.getText().toString();
      if (newRoom.length() > 0 && !roomList.contains(newRoom)) {

        Log.e("ConnectActivity","플러스 버튼 클릭 이벤트");

        adapter.add(newRoom);
        adapter.notifyDataSetChanged();
      }
    }
  };

  /**수화기 버튼 클릭 이벤트**/
  private final OnClickListener connectListener = new OnClickListener() {
    @Override
    public void onClick(View view) {

      Log.e("ConnectActivity","수화기 버튼 클릭 이벤트");

      connectToRoom(roomEditText.getText().toString(), false, false, false, 0);
    }
  };

}
