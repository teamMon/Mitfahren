/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.example.jpar4.mitfahren.apprtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jpar4.mitfahren.R;
import com.example.jpar4.mitfahren.app.Myapp;
import com.example.jpar4.mitfahren.service.MyService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Handles the initial setup where the user selects which room to join.
 */
public class ConnectActivity extends Activity {
  private static final String TAG = "ConnectActivity";
  private static final int CONNECTION_REQUEST = 1;
  private static final int REMOVE_FAVORITE_INDEX = 0;
  private static boolean commandLineRun = false;

  private ImageButton connectButton;
  private ImageButton addFavoriteButton;
  private EditText roomEditText;
  private ListView roomListView;
  private SharedPreferences sharedPref;
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

  ImageView profile_pic;
  TextView profile_name;
  String carpool_ID = null;
  String room_ID=null;
  String callRecv = null;
  ImageView btn_disconnect;
  TextView tv_disconnect_label;
  TextView tv_call_text;
  private Vibrator vibrator;

  /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
  MyService mService; //서비스객체
  boolean mBound;
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    Myapp app;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = (Myapp)getApplicationContext();
    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


    // Get setting keys.
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

    roomEditText = (EditText) findViewById(R.id.room_edittext);
    roomEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
          addFavoriteButton.performClick();
          return true;
        }
        return false;
      }
    });



    roomListView = (ListView) findViewById(R.id.room_listview);
    roomListView.setEmptyView(findViewById(android.R.id.empty));
    roomListView.setOnItemClickListener(roomListClickListener);
    registerForContextMenu(roomListView);
    connectButton = (ImageButton) findViewById(R.id.connect_button);
    connectButton.setOnClickListener(connectListener);
    addFavoriteButton = (ImageButton) findViewById(R.id.add_favorite_button);
    addFavoriteButton.setOnClickListener(addFavoriteListener);

    // If an implicit VIEW intent is launching the app, go directly to that URL.
    final Intent intent = getIntent();
    if ("android.intent.action.VIEW".equals(intent.getAction()) && !commandLineRun) {
      boolean loopback = intent.getBooleanExtra(CallActivity.EXTRA_LOOPBACK, false);
      int runTimeMs = intent.getIntExtra(CallActivity.EXTRA_RUNTIME, 0);
      boolean useValuesFromIntent =
          intent.getBooleanExtra(CallActivity.EXTRA_USE_VALUES_FROM_INTENT, false);
      String room = sharedPref.getString(keyprefRoom, "");
      connectToRoom(room, true, loopback, useValuesFromIntent, runTimeMs);
    }else{
       /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
      String profile_pic_string= intent.getStringExtra("profile_pic");
      String prfile_name_string = intent.getStringExtra("prfile_name");
      carpool_ID = intent.getStringExtra("carpool_ID");
      room_ID = intent.getStringExtra("room_ID");
      callRecv = intent.getStringExtra("callRecv");
      roomEditText.requestFocus();
      roomEditText.setText(room_ID);
      profile_pic = (ImageView) findViewById(R.id.profile_pic);
      Picasso.with(this).load("http://ec2-52-78-6-238.ap-northeast-2.compute.amazonaws.com/upload/" + profile_pic_string).into(profile_pic);
      profile_name = (TextView) findViewById(R.id.profile_name);
      profile_name.setText(prfile_name_string);
       btn_disconnect = (ImageView)findViewById(R.id.disconnect_button);
      tv_call_text = (TextView)findViewById(R.id.tv_call_text);

      btn_disconnect.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          vibrator.cancel();
          tv_call_text.setVisibility(View.GONE);
                                    /*---------------------------------------------------------------tcp통신으로 메시지 전송----------------------------------------------------------------------------------*/
          JSONObject sendMsgObj = new JSONObject();
          try {
                                            /*
                                            * 보내는 정보
                                            * 1. 메시지 타입 2. 보내는 사람 이메일 3. 받는 사람 이메일 4. 등록된 카풀정보의 아이디(유일값) 5. 탑승자 출발 도착 좌표
                                            *
                                            *
                                            * */
            //핵심꼬임
            sendMsgObj.put("kindOfmsg", "4"); //    kindOfmsg : 1 = carpool_applying ; 2 : chatting ; 4 : 화상채팅
            sendMsgObj.put("carpool_id", carpool_ID); // 등록된 카풀정보의 아이디(유일값), 방번호 어떻게 할지 생각해보고
            //  sendMsgObj.put("sender", app.getUser_email()); // sender : 보내는 사람
            sendMsgObj.put("sender", app.getUser_email());
            sendMsgObj.put("sender_name", app.getUser_name());
            sendMsgObj.put("sender_pic", app.getUser_photo());
            //   sendMsgObj.put("receiver", item_new_driver_info.getUser_email()); // receiver : 받는 사람
            //   sendMsgObj.put("receiver", "z@z.com");
 /*           String new_time = (new SimpleDateFormat("hhmmss").format(new Date())); //
            room_ID = "roomid" + new_time;*/
            sendMsgObj.put("contents", "reject");

            mService.sendMsg(sendMsgObj.toString());
          }catch (Exception e){
            e.printStackTrace();
          }

          finish();
        }
      });
      tv_disconnect_label =(TextView)findViewById(R.id.tv_disconnect_label);
      if(callRecv != null){ // 받을때만 보여지게
        vibrator.vibrate((1000*60)); // 받았을때 진동시작, 전화를 받거나 거절했을때 진동 끝

        tv_disconnect_label.setVisibility(View.VISIBLE);
        btn_disconnect.setVisibility(View.VISIBLE);
        tv_call_text.setVisibility(View.VISIBLE);
        tv_call_text.setText(prfile_name_string+" 님이 영상통화를 요청합니다.");
      }else{
        tv_disconnect_label.setVisibility(View.GONE);
        btn_disconnect.setVisibility(View.GONE);
        tv_call_text.setVisibility(View.GONE);
      }



       /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.connect_menu, menu);
    return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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
    // Handle presses on the action bar items.
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

  @Override
  public void onPause() {
    super.onPause();
    String room = roomEditText.getText().toString();
    String roomListJson = new JSONArray(roomList).toString();
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString(keyprefRoom, room);
    editor.putString(keyprefRoomList, roomListJson);
    editor.commit();
  }

  @Override
  public void onResume() {
    super.onResume();
    //String room = sharedPref.getString(keyprefRoom, "");
    roomEditText.setText(room_ID);
    roomList = new ArrayList<String>();
    String roomListJson = sharedPref.getString(keyprefRoomList, null);
    if (roomListJson != null) {
      try {
        JSONArray jsonArray = new JSONArray(roomListJson);
        for (int i = 0; i < jsonArray.length(); i++) {
          roomList.add(jsonArray.get(i).toString());
        }
      } catch (JSONException e) {
        Log.e(TAG, "Failed to load room list: " + e.toString());
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
      Log.d(TAG, "Return: " + resultCode);
      setResult(resultCode);
      commandLineRun = false;
      finish();
    }
  }

  /**
   * Get a value from the shared preference or from the intent, if it does not
   * exist the default is used.
   */
  private String sharedPrefGetString(
          int attributeId, String intentName, int defaultId, boolean useFromIntent) {
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
   * exist the default is used.
   */
  private boolean sharedPrefGetBoolean(
          int attributeId, String intentName, int defaultId, boolean useFromIntent) {
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
   * exist the default is used.
   */
  private int sharedPrefGetInteger(
          int attributeId, String intentName, int defaultId, boolean useFromIntent) {
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
        Log.e(TAG, "Wrong setting for: " + attributeName + ":" + value);
        return defaultValue;
      }
    }
  }

  private void connectToRoom(String roomId, boolean commandLineRun, boolean loopback,
                             boolean useValuesFromIntent, int runTimeMs) {
    this.commandLineRun = commandLineRun;

    // roomId is random for loopback.
    if (loopback) {
      roomId = Integer.toString((new Random()).nextInt(100000000));
    }

    String roomUrl = sharedPref.getString(
        keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

    // Video call enabled flag.
    boolean videoCallEnabled = sharedPrefGetBoolean(R.string.pref_videocall_key,
        CallActivity.EXTRA_VIDEO_CALL, R.string.pref_videocall_default, useValuesFromIntent);

    // Use screencapture option.
    boolean useScreencapture = sharedPrefGetBoolean(R.string.pref_screencapture_key,
        CallActivity.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValuesFromIntent);

    // Use Camera2 option.
    boolean useCamera2 = sharedPrefGetBoolean(R.string.pref_camera2_key, CallActivity.EXTRA_CAMERA2,
        R.string.pref_camera2_default, useValuesFromIntent);

    // Get default codecs.
    String videoCodec = sharedPrefGetString(R.string.pref_videocodec_key,
        CallActivity.EXTRA_VIDEOCODEC, R.string.pref_videocodec_default, useValuesFromIntent);
    String audioCodec = sharedPrefGetString(R.string.pref_audiocodec_key,
        CallActivity.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValuesFromIntent);

    // Check HW codec flag.
    boolean hwCodec = sharedPrefGetBoolean(R.string.pref_hwcodec_key,
        CallActivity.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValuesFromIntent);

    // Check Capture to texture.
    boolean captureToTexture = sharedPrefGetBoolean(R.string.pref_capturetotexture_key,
        CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
        useValuesFromIntent);

    // Check FlexFEC.
    boolean flexfecEnabled = sharedPrefGetBoolean(R.string.pref_flexfec_key,
        CallActivity.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValuesFromIntent);

    // Check Disable Audio Processing flag.
    boolean noAudioProcessing = sharedPrefGetBoolean(R.string.pref_noaudioprocessing_key,
        CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
        useValuesFromIntent);

    // Check Disable Audio Processing flag.
    boolean aecDump = sharedPrefGetBoolean(R.string.pref_aecdump_key,
        CallActivity.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValuesFromIntent);

    // Check OpenSL ES enabled flag.
    boolean useOpenSLES = sharedPrefGetBoolean(R.string.pref_opensles_key,
        CallActivity.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValuesFromIntent);

    // Check Disable built-in AEC flag.
    boolean disableBuiltInAEC = sharedPrefGetBoolean(R.string.pref_disable_built_in_aec_key,
        CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
        useValuesFromIntent);

    // Check Disable built-in AGC flag.
    boolean disableBuiltInAGC = sharedPrefGetBoolean(R.string.pref_disable_built_in_agc_key,
        CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
        useValuesFromIntent);

    // Check Disable built-in NS flag.
    boolean disableBuiltInNS = sharedPrefGetBoolean(R.string.pref_disable_built_in_ns_key,
        CallActivity.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
        useValuesFromIntent);

    // Check Enable level control.
    boolean enableLevelControl = sharedPrefGetBoolean(R.string.pref_enable_level_control_key,
        CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
        useValuesFromIntent);

    // Check Disable gain control
    boolean disableWebRtcAGCAndHPF = sharedPrefGetBoolean(
        R.string.pref_disable_webrtc_agc_and_hpf_key, CallActivity.EXTRA_DISABLE_WEBRTC_AGC_AND_HPF,
        R.string.pref_disable_webrtc_agc_and_hpf_key, useValuesFromIntent);

    // Get video resolution from settings.
    int videoWidth = 0;
    int videoHeight = 0;
    if (useValuesFromIntent) {
      videoWidth = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_WIDTH, 0);
      videoHeight = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_HEIGHT, 0);
    }
    if (videoWidth == 0 && videoHeight == 0) {
      String resolution =
          sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
      String[] dimensions = resolution.split("[ x]+");
      if (dimensions.length == 2) {
        try {
          videoWidth = Integer.parseInt(dimensions[0]);
          videoHeight = Integer.parseInt(dimensions[1]);
        } catch (NumberFormatException e) {
          videoWidth = 0;
          videoHeight = 0;
          Log.e(TAG, "Wrong video resolution setting: " + resolution);
        }
      }
    }

    // Get camera fps from settings.
    int cameraFps = 0;
    if (useValuesFromIntent) {
      cameraFps = getIntent().getIntExtra(CallActivity.EXTRA_VIDEO_FPS, 0);
    }
    if (cameraFps == 0) {
      String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
      String[] fpsValues = fps.split("[ x]+");
      if (fpsValues.length == 2) {
        try {
          cameraFps = Integer.parseInt(fpsValues[0]);
        } catch (NumberFormatException e) {
          cameraFps = 0;
          Log.e(TAG, "Wrong camera fps setting: " + fps);
        }
      }
    }

    // Check capture quality slider flag.
    boolean captureQualitySlider = sharedPrefGetBoolean(R.string.pref_capturequalityslider_key,
        CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
        R.string.pref_capturequalityslider_default, useValuesFromIntent);

    // Get video and audio start bitrate.
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

    // Check statistics display option.
    boolean displayHud = sharedPrefGetBoolean(R.string.pref_displayhud_key,
        CallActivity.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValuesFromIntent);

    boolean tracing = sharedPrefGetBoolean(R.string.pref_tracing_key, CallActivity.EXTRA_TRACING,
        R.string.pref_tracing_default, useValuesFromIntent);

    // Get datachannel options
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

    // Start AppRTCMobile activity.
    Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
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
  }

  private boolean validateUrl(String url) {
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

  private final AdapterView.OnItemClickListener roomListClickListener =
      new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          String roomId = ((TextView) view).getText().toString();
          connectToRoom(roomId, false, false, false, 0);
        }
      };

  private final OnClickListener addFavoriteListener = new OnClickListener() {
    @Override
    public void onClick(View view) {
      String newRoom = roomEditText.getText().toString();
      if (newRoom.length() > 0 && !roomList.contains(newRoom)) {
        adapter.add(newRoom);
        adapter.notifyDataSetChanged();
      }
    }
  };
/*커넥트 버튼*/
  private final OnClickListener connectListener = new OnClickListener() {
    @Override
    public void onClick(View view) {

                      /*---------------------------------------------------------------tcp통신으로 방번호  전달하고 전화왔어요 화면 띄어줌 연결----------------------------------------------------------------------------------*/
      if(mBound) {
        if(callRecv !=null){//전화받는거
          tv_disconnect_label.setVisibility(View.GONE);
          btn_disconnect.setVisibility(View.GONE);
          tv_call_text.setVisibility(View.GONE);
          connectToRoom(roomEditText.getText().toString(), false, false, false, 0);
          callRecv = null; // 받고 널로, 통화 걸수 있도록
          vibrator.cancel();
        }else { //전화 거는거
          tv_disconnect_label.setVisibility(View.GONE);
          btn_disconnect.setVisibility(View.GONE);
                                  /*---------------------------------------------------------------tcp통신으로 메시지 전송----------------------------------------------------------------------------------*/
          JSONObject sendMsgObj = new JSONObject();
          try{
                                            /*
                                            * 보내는 정보
                                            * 1. 메시지 타입 2. 보내는 사람 이메일 3. 받는 사람 이메일 4. 등록된 카풀정보의 아이디(유일값) 5. 탑승자 출발 도착 좌표
                                            *
                                            *
                                            * */
            //핵심꼬임
            sendMsgObj.put("kindOfmsg", "4"); //    kindOfmsg : 1 = carpool_applying ; 2 : chatting ; 4 : 화상채팅
            sendMsgObj.put("carpool_id", carpool_ID); // 등록된 카풀정보의 아이디(유일값), 방번호 어떻게 할지 생각해보고
            //  sendMsgObj.put("sender", app.getUser_email()); // sender : 보내는 사람
            sendMsgObj.put("sender", app.getUser_email());
            sendMsgObj.put("sender_name", app.getUser_name());
            sendMsgObj.put("sender_pic", app.getUser_photo());
            //   sendMsgObj.put("receiver", item_new_driver_info.getUser_email()); // receiver : 받는 사람
            //   sendMsgObj.put("receiver", "z@z.com");
            String new_time = (new SimpleDateFormat("hhmmss").format(new Date())); //
            room_ID = "roomid"+new_time;
            sendMsgObj.put("contents", room_ID);
            roomEditText.setText(room_ID);

            mService.sendMsg(sendMsgObj.toString());
            connectToRoom(roomEditText.getText().toString(), false, false, false, 0);
          }catch (Exception e){
            e.printStackTrace();
          }
                       /*---------------------------------------------------------------tcp통신으로 메시지 전송----------------------------------------------------------------------------------*/
        }

      }
                /*---------------------------------------------------------------tcp통신으로 방번호  전달하고 전화왔어요 화면 띄어줌 연결----------------------------------------------------------------------------------*/

    }
  };

  /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/
  private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      MyService.LocalBinder binder = (MyService.LocalBinder) service;
      mService = binder.getService();
      mService.registerCallback(mCallback); //콜백 등록
      mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mBound = false;
    }
  };

  //서비스에서 아래의 콜백 함수를 호출하며, 콜백 함수에서는 액티비티에서 처리할 내용 입력
  private MyService.ICallback mCallback = new MyService.ICallback() {
      @Override
      public String getCarpool_ID() {
          return null;
      }

      @Override
    public void recvRejectCall(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents) {

    }

    @Override
    public void recvRoomID(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents) {

    }

    @Override
    public void changeUnreadMsgNum(String carpool_id) { // ChattingRoomListActivity용

    }

    public void recvData(String txt) {
/*      Log.e("ddd", "ddd"+" "+txt);
      Message message= Message.obtain();
      message.what = 1; //에코채팅
      Bundle bundle = new Bundle();
      //  bundle.putString("friendId",friendId);
      bundle.putString("content",txt);
      //    bundle.putLong("time",time);
      message.setData(bundle);
      chattinghandler.sendMessage(message);*/

    }
    //띠용
    public void recvChattingMsg(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents){ // 채팅 메시지 처리

/*      Log.e("ddd", "ddd"+" recvChattingMsg");
      Message message= Message.obtain();
      message.what = 2; //채팅
      Bundle bundle = new Bundle();
      //  bundle.putString("friendId",friendId);
      bundle.putString("carpool_id",carpool_id);
      bundle.putString("sender_email",sender_email);
      bundle.putString("sender_name",sender_name);
      bundle.putString("sender_pic",sender_pic);
      bundle.putString("contents",contents);

      //    bundle.putLong("time",time);
      message.setData(bundle);
      chattinghandler.sendMessage(message);*/
    }

    @Override
    public void recvChattingImgMsg(String carpool_id, String sender_email, String sender_name, String sender_pic, String contents, String img_file_name) { // 채팅 이미지 메시지 처리
/*      Log.e("ddd", "ddd"+" recvChattingImgMsg");
      Message message= Message.obtain();
      message.what = 3; //채팅이미지 메시지
      Bundle bundle = new Bundle();
      //  bundle.putString("friendId",friendId);
      bundle.putString("carpool_id",carpool_id);
      bundle.putString("sender_email",sender_email);
      bundle.putString("sender_name",sender_name);
      bundle.putString("sender_pic",sender_pic);
      bundle.putString("contents",contents);
      bundle.putString("img_file_name",img_file_name);

      //    bundle.putLong("time",time);
      message.setData(bundle);
      chattinghandler.sendMessage(message);*/
    }
  };

  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = new Intent(this, MyService.class);
    // startService(intent);
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if(mBound){
      unbindService(mConnection);
      mBound=false;
    }

  }
    /*----------------------------------------서비스 바인딩 ----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

}

