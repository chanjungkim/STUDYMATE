package com.example.gp62.studymate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by GP62 on 2018-08-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

     String msg, send_chat_user_no, user_no, newToken, group_no, send_user_no, send_user_nick, room_num;
    String TAG = "MyFirebaseMessagingService";
    Context mContext = this;

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mContext = getApplicationContext();
//    }


    public MyFirebaseMessagingService() {

    }

    public  MyFirebaseMessagingService(Context mContext) {
        this.mContext = mContext;
    }

    // 토큰이 생성되거나 갱신이 될 때, 서버로
    @Override
    public void onNewToken(String s) {
        //super.onNewToken(user_no);
        //Log.e("NEW_TOKEN", s);
// Get updated InstanceID token.

        //String refreshedToken = FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken();
//        String refreshedToken =FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken();

        // user_no 가져오기
//        shared();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                Log.e(TAG, "토큰 생성함, 토큰 : " + newToken);

                //shared();
                // user_no 가져오기
                SharedPreferences SP = mContext.getSharedPreferences("login_user", MODE_PRIVATE);
                String json = SP.getString("login_user", "");
                Gson gson = new Gson();
                Login_user_info item = gson.fromJson(json, Login_user_info.class);
                if (SP.contains("login_user")) {
                    user_no = item.getUser_no(); //유저고유번호
                }
                Log.e(TAG, "현재 user_no22 : " + user_no);

                if (user_no != null) {
                    sendRegistrationToServer(newToken, user_no);
                    Log.e(TAG, "현재 user_no는 null이 아니다!");
                }
            }
        });
        //Log.e(TAG, "Refreshed token: " + refreshedToken);

// If you want to send messages to this application instance or
// manage this apps subscriptions on the server side, send the
// Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);

// [END refresh_token]
    }

    public void sendRegistrationToServer(String token, String user_no) {
// TODO: Implement this method to send token to your app server.
        // volley로 보내기
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "sendRegistrationToServer : 6");
                Log.e("response1 : ", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.e(TAG, "sendRegistrationToServer : 7");
                    boolean success = jsonResponse.getBoolean("success");
                    Log.e("success", "" + success);
                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                }
            }
        };
        // Log.e(TAG, "sendRegistrationToServer : 9");
        // 사용가능한 닉네임인지 검사하기
        //Log.e(TAG, "볼리 내 newToken : "+newToken);
        TokenSaveRequest tokenSaveRequest = new TokenSaveRequest(user_no, newToken, responseListener);
        //Log.e(TAG, "sendRegistrationToServer : 10");
        // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
        RequestQueue queue = Volley.newRequestQueue(mContext);
        //Log.e(TAG, "sendRegistrationToServer : 11");
        // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
        queue.add(tokenSaveRequest);

//        OkHttpClient client = new OkHttpClient();
//        Log.e(TAG,"token : "+token+", user_no : "+user_no);
//        RequestBody body = new FormBody.Builder().add("token", token).build();
//        //RequestBody user_no = new FormBody.Builder().add("token", token).build();
//        String SERVER_URL = new Variable().getURL();
//        String URL = SERVER_URL + "TokenSave.php";
//
//        Log.e(TAG,"URL : "+URL);
//        Request request = new Request.Builder().url(URL).post(body).build();
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived");
// [START_EXCLUDE]
// There are two types of messages data messages and notification messages. Data messages are handled
// here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
// traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
// is in the foreground. When the app is in the background an automatically generated notification is displayed.
// When the user taps on the notification they are returned to the app. Messages containing both notification
// and data payloads are treated as notification messages. The Firebase console always sends notification
// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
// [END_EXCLUDE]

// handleIntent();
// TODO(developer): Handle FCM messages here.
// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());

// Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            //sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"),remoteMessage.getData().get("group_no"));
            if(remoteMessage.getData().get("msg").equals("영상통화가 왔습니다")){
                Log.e(TAG,"영상통화입니다");
                msg = remoteMessage.getData().get("msg");
                send_user_no = remoteMessage.getData().get("send_user_no");
                send_user_nick = remoteMessage.getData().get("send_user_nick");
                room_num = remoteMessage.getData().get("room_num");
                sendNotification_call(room_num, send_user_no, msg, send_user_nick);
            }else{
                msg = remoteMessage.getData().get("msg");
                send_chat_user_no = remoteMessage.getData().get("send_chat_user_no");
                group_no = remoteMessage.getData().get("group_no");
                sendNotification_chat(send_chat_user_no, msg, group_no);
            }
        }

// Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            String newToken = instanceIdResult.getToken();
//            Log.e("토큰 생성함, 토큰 : ", newToken);


//            send_chat_user_no = remoteMessage.getNotification().getTitle();
//            msg = remoteMessage.getNotification().getBody();
//            group_no = remoteMessage.getNotification().get

            //sendNotification(send_chat_user_no, msg, group_no);
        }

        // 이거 추가 하면
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE );
        PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG" );
        wakeLock.acquire(3000);

//        title = remoteMessage.getNotification().getTitle();
//        msg = remoteMessage.getNotification().getBody();
    }
// Also if you intend on generating your own notifications as a result of a received FCM
// message, here is where that should be initiated. See sendNotification method below.

// [END receive_message]

    public void sendNotification_chat(String title, String msg, String group_no) {

       /* //푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성

        final NotificationManager notificationManager =(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


//푸시 알림 터치시 실행할 작업 설정(여기선 MainActivity로 이동하도록 설정)

        final Intent intent = new Intent(this, MainActivity.class);
//Notification 객체 생성
        final Notification.Builder builder = new Notification.Builder(mContext);


//푸시 알림을 터치하여 실행할 작업에 대한 Flag 설정 (현재 액티비티를 최상단으로 올린다 | 최상단 액티비티를 제외하고 모든 액티비티를 제거한다)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //앞서 생성한 작업 내용을 Notification 객체에 담기 위한 PendingIntent 객체 생성
        PendingIntent pendnoti = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        //푸시 알림에 대한 각종 설정

        builder.setSmallIcon(R.drawable.ic_launcher_background).setTicker("Ticker").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(title).setContentText(msg)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendnoti).setAutoCancel(true).setOngoing(true);


        //NotificationManager를 이용하여 푸시 알림 보내기
        notificationManager.notify(1, builder.build());*/

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("chat","chat");
        Log.e(TAG,"채팅알림에서 받은 group_no2: "+group_no);
        intent.putExtra("group_no",group_no);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, channelId)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public void sendNotification_call(String room_num, String send_user_no, String msg, String send_user_nick) {

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("call","call");

//        SharedPreferences SP = mContext.getSharedPreferences("login_user", MODE_PRIVATE);
//        String json = SP.getString("login_user", "");
//        Gson gson = new Gson();
//        Login_user_info item = gson.fromJson(json, Login_user_info.class);
//        if (SP.contains("login_user")) {
//            user_no = item.getUser_no(); //유저고유번호
//        }
        Log.e(TAG,"room_num : "+ room_num);
        intent.putExtra("room_num",room_num);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0  /*Request code*/ , intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, channelId)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(send_user_nick)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
//    private String shared(){
//        Log.e(TAG,"user_no1");
//        SharedPreferences SP1 = getApplicationContext().getSharedPreferences("login_user",Context.MODE_PRIVATE);
//        Log.e(TAG,"user_no2");
//        String json = SP1.getString("login_user", "");
//        Log.e(TAG, "현재 login_user_json : " + json);
//        Gson gson = new Gson();
//        Login_user_info item = gson.fromJson(json, Login_user_info.class);
//        if (SP1.contains("login_user")) {
//            user_no = item.getUser_no(); //유저고유번호
//            Log.e(TAG, "현재 user_no11 : " + user_no);
//        }
//        return user_no;
//    }

    //user_no 가져오기
    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }
}
