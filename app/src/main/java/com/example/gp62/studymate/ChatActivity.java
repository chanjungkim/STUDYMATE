package com.example.gp62.studymate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import xyz.dev_juyoung.cropicker.CroPicker;
import xyz.dev_juyoung.cropicker.models.Media;


public class ChatActivity extends AppCompatActivity {

    String Tag = "ChatActivity";
    Toolbar group_chat_toolbar;
    TextView txtMessage;

    TextView plusBtn;
    Button btnConnect, btnSend;
    EditText editIp, editPort, editMessage;
    Handler msgHandler; //android.os.Handler
    SocketClient client;
    ReceiveThread receive;
    SendThread send;
    Socket socket;
    // LinkedList<SocketClient> threadList;
    Context context;

    String mac;
    String user_no; // 유저번호
    String user_no_receive; // 서버로부터 받은 번호
    String read_chat_user_nick; // 챗에서 nick
    String read_chat_user_image; // 해당 유저의 이미지
    String group_no; // 스터디그룹번호
    String user_nick; // 유저닉네임
    String chat_title; // 그룹이름

    int chat_num; // 채팅개수

    String byteImage = null; // byte 배열(스트링)로 바꾼 이미지

    ArrayList<participant_info> participantInfoList; // 참여자리스트 (스터디그룹원)
    ArrayList<String> participantNoList; //참여자(스터디그룹원) 고유번호 리스트
    //    ArrayList<ChatItem> ChatItemList; // 채팅서버에 보낼 리스트 (스터디그룹번호,그룹원들번호,유저번호,메세지 내용)
    String first_chat_json; // 채팅서버에 보낼 (스터디그룹번호,그룹원들번호,유저번호,메세지 내용)

    RecyclerView group_chat_recyclerView;
    ArrayList<ChatItem_ui> chatItem_uiList; // 채팅리스트
    GroupChatAdapter groupChatAdapter;

    boolean plusContentCheck = false; // 플러스 버튼을 눌렀을 때, 컨텐츠보이는 창

    // 레이아웃
    LinearLayout plusContent, camera_icon, album_icon, chat_layout;
//    String prticipantNoListJson; // 참여자(스터디그룹원) 고유번호 리스트 json으로 바꾸기


    // 앨범에서 선택한 다중이미지
    ArrayList<Media> album_pics;

    // 이미지 경로
    String ImagePath = null;

    //서버로 보낼 다중 이미지
    JsonArray jsonElements = null;

    // 쓰레드로 채팅서버에 이미지 보낼 때
    String json_image;
    JSONArray chat_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;

        //파이어베이스
        //FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken();

//        btnConnect = findViewById(R.id.btnConnect);
//        editIp = findViewById(R.id.editIp);
//        editPort = findViewById(R.id.editPort);
        group_chat_toolbar = findViewById(R.id.group_chat_toolbar);
        //txtMessage = findViewById(R.id.txtMessage);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        plusBtn = findViewById(R.id.plusBtn);
        plusContent = findViewById(R.id.plusContent);
        camera_icon = findViewById(R.id.camera_icon);
        album_icon = findViewById(R.id.album_icon);
        chat_layout = findViewById(R.id.chat_layout);

        album_pics = new ArrayList<>();

        // 에디트텍스트에 포커스주기
        //editMessage.requestFocus();
//        //키보드 보이게 하는 부분
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        plusContent.setVisibility(View.GONE);
        showItemList();

        // 채팅방 레이아웃 내 채팅입력창 왼쪽에 ‘+버튼’
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showItemList();
                if (plusContentCheck) {
                    //키보드 보이게 하는 부분
                    plusContent.setVisibility(View.GONE);
//                    plusContent.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showItemList();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                    }, 200);
                    plusContentCheck = false;
                    Log.e(Tag, "키보드보여야함");
                } else {
                    showItemList();
//                    // 키보드 안보이게하기
                    InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            plusContent.setVisibility(View.VISIBLE);
                        }
                    }, 200);
                    plusContentCheck = true;
                    Log.e(Tag, "키보드가려야함");
                }
            }
        });

        // 대화입력창이 클릭되면,
        // 1. 사진, 카메라 아이콘이 있는 레이아웃이 안보이게 함
        // 2. 리사이클러뷰 맨 밑 행에 포커스를 줌
        editMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusContent.setVisibility(View.GONE);
                showItemList();
            }
        });

        // 사진 아이콘을 클릭하면, 다중이미지를 선택할 수 있는 앨범이 나온다.
        album_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CroPicker.Options options = new CroPicker.Options();
                options.setNotSelectedMessage("선택한 이미지가 없습니다");
                options.setLimitedExceedMessage("최대 5개까지만 가능합니다");
                options.setLimitedCount(5);
                options.setMessageViewType(CroPicker.MESSAGE_VIEW_TYPE_SNACKBAR);

                CroPicker
                        .init(ChatActivity.this)
                        .withOptions(options) //Optional
                        .start();
            }
        });

        // 어레이리스트 생성
        participantInfoList = new ArrayList<>();
        participantNoList = new ArrayList<>();
        chatItem_uiList = new ArrayList<>();
        Log.e(Tag, "onCreat_chatItem_uiList : " + chatItem_uiList);
//        ChatItemList = new ArrayList<>();

        Intent chat_info = getIntent();
        user_no = chat_info.getStringExtra("유저번호");
        Log.e(Tag, "user_no : " + user_no);
        group_no = chat_info.getStringExtra("스터디그룹번호");
        Log.e(Tag, "group_no : " + group_no);
        user_nick = chat_info.getStringExtra("유저닉네임");
        Log.e(Tag, "user_nick : " + user_nick);
        chat_num = chat_info.getIntExtra("채팅시작번호", 0);
        Log.e(Tag, "chat_num : " + chat_num);
        chat_title = chat_info.getStringExtra("스터디제목");
        Log.e(Tag, "chat_title : " + chat_title);
        participantInfoList = (ArrayList<participant_info>) chat_info.getSerializableExtra("그룹원번호리스트");

        // 툴바에 뒤로가기버튼 세팅
        setSupportActionBar(group_chat_toolbar);
        Log.e(Tag,"그룹이름 받아왔나 : "+chat_title);
        getSupportActionBar().setTitle(chat_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // DB서버에 대화목록 요청
        Response.Listener<String> responseListener_chatList = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("chat_response", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.e("chat_response", response);

                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    //Log.e(Tag, "jsonArray :" + jsonArray);

                    int count = chat_num;

                    String chat_user_no;
                    String chat_message;
                    String chat_msg_type;
                    String chat_user_nick;
                    String chat_user_image;

                    Log.e(Tag, "chat_num : " + chat_num);
                    Log.e(Tag, "jsonArray.length() : " + jsonArray.length());
                    Log.e(Tag, "count : " + count);

                    // 참여자리스트에서 user_nick 가져오기

                    while (count <= jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        chat_user_no = object.getString("chat_user_no");
                        chat_msg_type = object.getString("chat_type");
                        chat_message = object.getString("chat_message");
                        Log.e(Tag, "chat_msg_type :" + chat_msg_type);

                        for (int i = 0; i < participantInfoList.size(); i++) {
                            if (participantInfoList.get(i).getParticipant_no().equals(chat_user_no)) {
                                // 유저 닉네임
                                chat_user_nick = participantInfoList.get(i).getParticipant_nick();
                                // 유저 사진
                                chat_user_image = participantInfoList.get(i).getParticipant_image();
                                //  Log.e(Tag, "chat_user_image : " + chat_user_image);

                                ChatItem_ui chatItem_ui = null;

                                if (chat_msg_type.equals("텍스트")) {
                                    // Log.e(Tag,"텍스트임");
                                    chatItem_ui = new ChatItem_ui(chat_message, chat_user_no, chat_user_nick, chat_user_image, null, false);
                                } else{
                                    //Log.e(Tag,"사진임");
                                    chatItem_ui = new ChatItem_ui(null, chat_user_no, chat_user_nick, chat_user_image, chat_message, false);
                                }
                                chatItem_uiList.add(chatItem_ui);
                                groupChatAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        //Log.e(Tag, "채팅서버의 chat_user_no : " + chat_user_no);
//                        Log.e("chat_message", "" + chat_message);
                        count++;
                    }
                    // success : 해당 과정이 정상적으로 수행 되었는지 response 값
                    // boolean success = jsonResponse.getBoolean("success");
                    // Log.e("success", "" + success);
                    // 대화목록 받아오기 // 메세지리스트에 메세지 추가

//                    //만약 success가 되었다면
//                    if (success) {
//                        String chat_user_no = jsonResponse.getString("chat_user_no");
//                        String chat_message = jsonResponse.getString("chat_message");
//                        Log.e(Tag,"chat_user_no 성공 : "+chat_user_no);
//                        return;
//                    } else {
//                        String chat_user_no = jsonResponse.getString("chat_user_no");
//                        String chat_message = jsonResponse.getString("chat_message");
//                        Log.e(Tag,"chat_user_no 실패");
//                        return;
//                    }
                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                }
            }
        };

        ChatListRequest chatListRequest = new ChatListRequest(group_no, responseListener_chatList);
        // 요청을 실질적으로 보낼 수 있도록 큐를 만든다
        RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);
        // 큐에 사용가능한 이메일인지 검사하는 큐를 등록한다
        queue.add(chatListRequest); // 스터디 그룹번호 전달


        // 스터디그룹원 고유번호 리스트 만들기
        for (int i = 0; i < participantInfoList.size(); i++) {
//            Log.e(Tag,"그룹원번호 : "+participantInfoList.get(i).getParticipant_no());
            participantNoList.add(participantInfoList.get(i).getParticipant_no());
        }

        ChatItem_ToServer first_chat = new ChatItem_ToServer(group_no, user_nick, participantNoList, user_no, "");

        Gson gson = new Gson();
        first_chat_json = gson.toJson(first_chat);
        Log.e(Tag, "first_chat_json : " + first_chat_json);

        // 메세지리스트 어댑터
        groupChatAdapter = new GroupChatAdapter(getApplicationContext(), chatItem_uiList, R.layout.right_chat_item, user_no);

        // 메세지리스트
        group_chat_recyclerView = (RecyclerView) findViewById(R.id.group_chat_recyclerView);
        group_chat_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        group_chat_recyclerView.setAdapter(groupChatAdapter);
        group_chat_recyclerView.setItemAnimator(new DefaultItemAnimator());
//        group_chat_recyclerView.scrollToPosition(chatItem_uiList.size()-1);
//        group_chat_recyclerView.scrollToPosition(groupChatAdapter.getItemCount()-1);
        showItemList();

        /*// 리사이클러뷰 하단 스크롤로 고정시키기
        group_chat_recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!group_chat_recyclerView.canScrollVertically(1)) {
                    Log.i(Tag, "리스트 맨 위쪽");
                } else if (!group_chat_recyclerView.canScrollVertically(1)) {
                    Log.i(Tag, "리스트 맨 아래");
                } else {
                    Log.i(Tag, "리스트 중간");
                }
            }
        });*/


//        participantNoListJson = gson.toJson(participantNoList);
//        Log.e("SocketClient","prticipantNoListJson : "+participantNoListJson );
//        Log.e(Tag,"그룹원번호리스트 : "+prticipantNoList);


//        Log.e(Tag,"유저번호 : "+user_no);
//        Log.e(Tag,"스터디그룹번호 : "+group_no);
//        Log.e(Tag,"유저닉네임 : "+user_nick);
//        Log.e(Tag,"그룹원번호리스트 : "+participantInfoList);
        //핸들러 작성
        msgHandler = new Handler() {
            // 백그라운드 스레드에서 받은 메세지를 처리

            public void handleMessage(Message msg) {
// 채팅서버로부터 수신한 메세지를 텍스트뷰에 추가
                Log.e(Tag, "핸들_현재 유저 번호 :" + user_no);
                String chat_user_nick;
                String chat_user_image;
                for (int i = 0; i < participantInfoList.size(); i++) {
                    if (participantInfoList.get(i).getParticipant_no().equals(user_no)) {
                        // 유저 닉네임
                        chat_user_nick = participantInfoList.get(i).getParticipant_nick();
                        // 유저 사진
                        chat_user_image = participantInfoList.get(i).getParticipant_image();
                        Log.e(Tag, "handle_chat_user_nick : " + chat_user_nick);
                        Log.e(Tag, "handle_user_no : " + user_no);
                        Log.e(Tag, "handle_chat_user_image : " + chat_user_image);

                        ChatItem_ui chatItem_ui = null;
                        if (msg.what == 1111) { // write한 메세지 & 텍스트
                            chatItem_ui = new ChatItem_ui(msg.obj.toString(), user_no, chat_user_nick, chat_user_image, null, true);
                        } else if (msg.what == 2222) { // write한 메세지 & 이미지
                            chatItem_ui = new ChatItem_ui(null, user_no, chat_user_nick, chat_user_image, msg.obj.toString(), true);
                        } else if (msg.what == 3333) { // read한 메세지 & 텍스트
                            chatItem_ui = new ChatItem_ui(msg.obj.toString(), user_no_receive, read_chat_user_nick, read_chat_user_image, null, false);
                        } else if (msg.what == 4444) {// read 메세지 & 이미지
                            chatItem_ui = new ChatItem_ui(null, user_no_receive, read_chat_user_nick, read_chat_user_image, msg.obj.toString(), false);
                        }
                        chatItem_uiList.add(chatItem_ui);
                        Log.e(Tag, "핸들22");
                        groupChatAdapter.notifyDataSetChanged();
                        break;
                    }
                }
//                    ChatItem_ui chatItem_ui = new ChatItem_ui(msg.obj.toString(),user_no,);
//                    Log.e(Tag, "chatItem_ui : " + chatItem_ui);
//                    chatItem_uiList.add(chatItem_ui);
//                    groupChatAdapter.notifyDataSetChanged();
                Log.e(Tag, "chatItem_uiList : " + chatItem_uiList);
            }
        };

//        //서버에 접속버튼
//        btnConnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                client = new SocketClient(editIp.getText().toString(), editPort.getText().toString());
//                client.start();
//            }
//        });

        String ip = new Variable().getIP();
        // 서버에 접속, ip
        client = new SocketClient(ip, "5001");
        /*client = new SocketClient("192.168.1.8", "5001");*/
        /*client = new SocketClient("13.209.65.255", "5001");*/
        client.start();
        Log.e(Tag, "서버에 접속");

        //서버에 메세지 전송버튼
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String message = editMessage.getText().toString();
                if (message != null || !message.equals("")) {
                    send = new SendThread(socket);
                    Log.e(Tag, "보낼 때 소켓 : " + socket);
                    send.start();
                    editMessage.setText("");
                }
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

    //내부 클래스
    class SocketClient extends Thread {
        boolean threadAlive; // 쓰레드의 동작여부
        String ip;
        String port;
        //        String mac;
        OutputStream outputStream = null;
        BufferedReader br = null;
        DataOutputStream output = null;

        public SocketClient(String ip, String port) {
            threadAlive = true;
            this.ip = ip;
            this.port = port;
        }

        public void run() {
            try {
                // 채팅 서버에 접속 -> 접속 대기상태인 서버에서 소켓이 만들어짐 -> 데이터를 주고 받을 수 있는 환경이 됨.
                socket = new Socket(ip, Integer.parseInt(port));
                // 서버에 메세지를 전달하기 위한 스트림 생성
                output = new DataOutputStream(socket.getOutputStream());
                // 메세지 수신용 스레드 생성
                receive = new ReceiveThread(socket);
                receive.start();
                // 식별자로 맥 주소를 가져옴
                // 와이파이 정보 관리자 객체로부터 폰의 mac address를 가져와서 채팅서버에 전달
                // -> 서버에 ip주소와 맥 어드레스가 출력됨
                // -> 서버에 run이 실행되면서, 클라이언트의 정보를 저장함
//                WifiManager mng = (WifiManager) context.getSystemService(WIFI_SERVICE);
//                WifiInfo info = mng.getConnectionInfo();
//                mac = info.getMacAddress(); // 새로 추가

                // 맥전송
                //output.writeUTF(mac);


                output.writeUTF(first_chat_json);

                // 대화리스트에 유저번호, 메세지 추가하기


               /* output.writeUTF(user_no); // 유저번호 전송
                output.writeUTF(user_nick); // 유저닉네임 전송
                output.writeUTF(group_no); // 그룹번호 전송
                output.writeUTF(prticipantNoListJson); // 그룹번호 전송
*/
                // 스터디그룹원리스트에서 그룹원 고유번호 꺼내기
                // 스터디그룹원 고유번호만 또 다른 리스트에 저장하기
                // 서버에 스터디그룹원 고유번호리스트 보내기

               /* Log.e("SocketClient","user_no : "+user_no );
                Log.e("SocketClient","user_nick : "+user_nick );
                Log.e("SocketClient","group_no : "+group_no );
                Log.e("SocketClient","prticipantNoListJson : "+prticipantNoListJson );*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } // SocketClient 끝

    // 내부클래스
    class ReceiveThread extends Thread {
        // 서버에서 받은 메세지를 핸들러에게 보내서 메인스레드에 메세지가 출력되도록함.
        Socket socket = null;
        DataInputStream input = null;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
            try {
                // 채팅서버로부터 메세지를 받기 위한 스트림 생성
                input = new DataInputStream(socket.getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (input != null) {
                    // 채팅 서버로부터 받은 메세지
                    String msg_type = input.readUTF();
                    String user_token = null;
                    if(msg_type.equals("토큰")){
                        user_token = input.readUTF();
                        user_no_receive = input.readUTF();
                    }else{
                        user_no_receive = input.readUTF();
                    }
//                    user_nick_receive = input.readUTF()
//                    if(msg_type.equals("토큰")){
//                       user_token = input.readUTF();
//                    }
                    String msg = input.readUTF();
//                    Log.e(Tag, "msg_type : " + msg_type);
//                    Log.e(Tag, "msg : " + msg);

                    for (int i = 0; i < participantInfoList.size(); i++) {
                        if (participantInfoList.get(i).getParticipant_no().equals(user_no_receive)) {
                            // 유저 닉네임
                            read_chat_user_nick = participantInfoList.get(i).getParticipant_nick();
                            // 유저 사진
                            read_chat_user_image = participantInfoList.get(i).getParticipant_image();
                            //  Log.e(Tag, "chat_user_image : " + chat_user_image);

                            if (msg_type.equals("텍스트")) {
                                Log.e(ACTIVITY_SERVICE, "msg_type : 텍스트");
                                Log.e(Tag, "방금 받은 유저번호 :  user_no_receive : " + user_no_receive);
                                // 핸들러에게 전달한 메세지 객체
                                Message hdmsg = msgHandler.obtainMessage();
                                hdmsg.what = 3333; // 메세지의 식별자
                                hdmsg.obj = msg;
                                // 핸들러에게 메세지 전달 (화면 변경 요청)
                                msgHandler.sendMessage(hdmsg);

                                //Log.e(ACTIVITY_SERVICE, hdmsg.obj.toString());
                            } else if(msg_type.equals("사진")){
                                Log.e(ACTIVITY_SERVICE, "msg_type : 사진");
                                Log.e(Tag, "방금 받은 유저번호 :  user_no_receive : " + user_no_receive);
                                // 핸들러에게 전달한 메세지 객체
                                Message hdmsg = msgHandler.obtainMessage();
                                hdmsg.what = 4444; // 메세지의 식별자
                                hdmsg.obj = msg;

//                                hdmsg.obj = msg; // 메세지 본문
                                // 핸들러에게 메세지 전달 (화면 변경 요청)
                                msgHandler.sendMessage(hdmsg);
                                //Log.e(ACTIVITY_SERVICE, hdmsg.obj.toString());
                            }else if(msg_type.equals("토큰")){

                                //Log.e(Tag, "방금 받은 유저번호 :  user_no_receive : " + user_no_receive);
                                Log.e(Tag, "방금 받은 유저 닉네임 :  read_chat_user_nick : " + read_chat_user_nick);
                                Log.e(Tag, "msg_type : " + msg_type);
                                Log.e(Tag, "token : " + user_token);
                                //Log.e(Tag, "msg : " + msg);
                                //Log.e(Tag, "user_nick : " + user_nick);
                                sendFCM(user_token, user_nick, msg);
                            }
//                            chatItem_uiList.add(chatItem_ui);
//                            groupChatAdapter.notifyDataSetChanged();
                            break;
                        }
                    }



                    /*if (group_no_receive.equals(group_no)) {*/
//                    if (msg_type.equals("텍스트")) {
//                        Log.e(ACTIVITY_SERVICE, "msg_type : 텍스트");
//                        // 핸들러에게 전달한 메세지 객체
//                        Message hdmsg = msgHandler.obtainMessage();
//                        hdmsg.what = 3333; // 메세지의 식별자
//                        hdmsg.obj = msg; // 메세지 본문
//                        // 핸들러에게 메세지 전달 (화면 변경 요청)
//                        msgHandler.sendMessage(hdmsg);
//                        //Log.e(ACTIVITY_SERVICE, hdmsg.obj.toString());
//                    } else {
//                        Log.e(ACTIVITY_SERVICE, "msg_type : 사진");
//                        // 핸들러에게 전달한 메세지 객체
//                        Message hdmsg = msgHandler.obtainMessage();
//                        hdmsg.what = 4444; // 메세지의 식별자
//                        hdmsg.obj = msg; // 메세지 본문
//                        // 핸들러에게 메세지 전달 (화면 변경 요청)
//                        msgHandler.sendMessage(hdmsg);
//                        //Log.e(ACTIVITY_SERVICE, hdmsg.obj.toString());
//                    }
                   /* }*/

                    /*if (msg != null) {
                        Log.e(ACTIVITY_SERVICE, "test");
                        // 핸들러에게 전달한 메세지 객체
                        Message hdmsg = msgHandler.obtainMessage();
                        hdmsg.what = 1111; // 메세지의 식별자
                        hdmsg.obj = msg; // 메세지 본문
                        // 핸들러에게 메세지 전달 (화면 변경 요청)
                        msgHandler.sendMessage(hdmsg);
                        Log.e(ACTIVITY_SERVICE, hdmsg.obj.toString());
                    }*/
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } //ReceiveThread 끝

    // 내부 클래스
    class SendThread extends Thread {
        Socket socket;
        String sendmsg = editMessage.getText().toString();
        OutputStream out;
        DataOutputStream output;
        FileInputStream fin;

        public SendThread(Socket socket) {
            this.socket = socket;
            try {
                //채팅서버로 메세지를 보내기 위한 스트림 생성
                out = socket.getOutputStream(); //서버에 바이트단위로 데이터를 보내는 스트림을 개통합니다.
                output = new DataOutputStream(socket.getOutputStream());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                Log.e(ACTIVITY_SERVICE, "11111");
                String mac = null;

                // 새로 추가한 내용 시작
//                WifiManager mng = (WifiManager)context.getSystemService(WIFI_SERVICE);
//                WifiInfo info = mng.getConnectionInfo();
//                mac = info.getMacAddress();
                // 새로 추가한 내용 끝

                if (output != null) {
                    Log.e(Tag, "초반 sendmsg : " + sendmsg);
                    if (sendmsg != null) {
                        // 채팅서버에 메세지 전달
                        //output.writeUTF(mac + ":"+sendmsg);

                        //새로 추가한 내용
                        //if(!sendmsg.equals("close")){
                        //String msg = user_nick + ":" + sendmsg;

                        //이미지가 있는 경우
                        //Log.e(Tag, "채팅에 이미지가 있는 경우, ImagePath : " + album_pics.get(0).getImagePath());

                        if (jsonElements != null) {
                            Log.e(Tag, "이미지 있다!");
                            //Log.e(Tag,"byteImage1 : " +byteImage);
                            // 이미지파일을 byte로 바꾼다.
//                            Bitmap bitmap = ImagePathToBitmap(ImagePath);
//                            byteImage = BitmapToStringByte(bitmap);
                            // 이미지를 받고 서버에 저장할 php파일을 만든다.
//                            try {
//                            Log.e(Tag, "이미지경로 있나? :" + ImagePath);
                            // php파일로 보낼 request만든다.
                            Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e(Tag, "전체 response : " + response);
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        Log.e(Tag, "요청jsonResponse : " + jsonResponse);
//                                        // success : 해당 과정이 정상적으로 수행 되었는지 response값
                                        //boolean success = jsonResponse.getBoolean("success");

                                        //if (success) { // 이미지 업로드가 되면, 저장된 이미지파일 이름을 가져온다.
                                        Log.e(Tag, "업로드 성공1");
                                        //Log.e(Tag, "저장된 사진메세지파일 이름 : " + jsonResponse);
                                        JSONObject jsonObject1 = jsonResponse.getJSONObject("response");
                                        Log.e(Tag, "jsonObject1 : " + jsonObject1);
                                        chat_photo = jsonObject1.getJSONArray("chat_photo"); // 사진메세지
                                        Log.e(Tag, "chat_photo : " + chat_photo);
//                                        JSONObject object = chat_photo.getJSONObject(0);
//                                        Log.e(Tag, "object : "+object);

                                        // 서브 쓰레드를 통해 채팅서버에 사진올리기
                                        Thread th = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    output.writeUTF("사진");
                                                    output.writeUTF("" + chat_photo);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                jsonElements = null;
                                                Log.e(Tag, "jsonElements은 null");
                                                Log.e(Tag, "서버로 사진전송");
                                            }
                                        });

//                                        for(int i = 0; i<chat_photo.length();i++){
//                                            json_image = chat_photo.getString(i);
//                                            Log.e(Tag,"json_image :"+json_image);
                                        th.start();
//                                        }

                                        /*Chat_Photo_Item chat_photo_item = new Chat_Photo_Item(chat_photo);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(chat_photo_item);*/
                                    } catch (Exception e) { // 오류가 발생했을 경우
                                        e.printStackTrace();
                                        Log.e(Tag, "오류발생 : " + e);
                                    }
                                }
                            };
                            //logLargeString("서버로 보낼 jsonElements:"+jsonElements);

                            chat_photo_Request chat_photo_request = new chat_photo_Request(user_no, group_no, jsonElements, responseListener2);
                            // 그룸생성 요청을 실질적으로 보낼 수 있도록 큐를 만든다
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            // 큐에 그룹 큐를 등록한다
                            queue.add(chat_photo_request);

//                            if (msg != null) { //내가 보낸 메세지
//                                Log.e(ACTIVITY_SERVICE, "test");
//                                // 핸들러에게 전달한 메세지 객체
//                                Message hdmsg = msgHandler.obtainMessage();
//                                hdmsg.what = 1111; // 메세지의 식별자
//                                hdmsg.obj = sendmsg; // 메세지 본문
//                                // 핸들러에게 메세지 전달 (화면 변경 요청)
//                                msgHandler.sendMessage(hdmsg);
//                                Log.e(ACTIVITY_SERVICE, hdmsg.obj.toString());
//                            }


                        } else {// 이미지가 없고 텍스트만 있는 경우
                            Log.e(Tag, "이미지 아니고 텍스트 있다!");
                            String msg = "" + sendmsg;
                            output.writeUTF("텍스트");
                            output.writeUTF(msg);
                            Log.e(Tag, "sendmsg : " + sendmsg);
                            Log.e(Tag, "msg : " + msg);
                            Log.e(Tag, "editMessage.getText : " + editMessage.getText());

                            if (msg != null) { //내가 보낸 메세지
                                Log.e(ACTIVITY_SERVICE, "test");
                                // 핸들러에게 전달한 메세지 객체
                                Message hdmsg = msgHandler.obtainMessage();
                                hdmsg.what = 1111; // 메세지의 식별자
                                hdmsg.obj = sendmsg; // 메세지 본문
                                // 핸들러에게 메세지 전달 (화면 변경 요청)
                                msgHandler.sendMessage(hdmsg);
                                Log.e(ACTIVITY_SERVICE, hdmsg.obj.toString());
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                Log.e(Tag, "Image ::" + ex);

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(Tag, "onResume");
//        SharedPreferences SP = getPreferences(MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = SP.getString(""+group_no,"");
//        Log.e(Tag,"onResume_chatItem_uiList : "+chatItem_uiList);
//        ArrayList<ChatItem_ui> chatItem_uiList_2 = gson.fromJson(json, new TypeToken<ArrayList<ChatItem_ui>>() {
//        }.getType());
//
//        if(chatItem_uiList_2==null){
//            Log.e(Tag,"json==null, onResume_chatItem_uiList : "+chatItem_uiList);
//        }else{
////            ArrayList<ChatItem_ui> chatItem_uiList = gson.fromJson(json, new TypeToken<ArrayList<ChatItem_ui>>() {
////            }.getType());
//            Log.e(Tag,"onResume_json : "+json);
//            Log.e(Tag,"json==null아님, onResume_chatItem_uiList : "+chatItem_uiList);
//            //chatItem_uiList_re  = chatItem_uiList;
//
//            for(int i = 0; i<chatItem_uiList_2.size();i++){
//                chatItem_uiList.add(chatItem_uiList_2.get(i));
//            }
//            groupChatAdapter.notifyDataSetChanged();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(Tag, "onPause");
//        SharedPreferences SP = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = SP.edit();
//        Gson gson = new Gson();
//        Log.e(Tag,"onPause_chatItem_uiList : "+chatItem_uiList);
//        String json = gson.toJson(chatItem_uiList);
//        editor.putString(""+group_no,json);
//        //Log.e(Tag,"onPause");
//        //Log.e(Tag,"onPause"+"group_no : "+group_no+"chatItem_uiList : "+chatItem_uiList.get(0));
//        editor.apply();
    }

    //    @Override
    protected void onStop() {
        super.onStop();
        //소켓종료
        Log.e(Tag, "onStop");
        try {
//            String message = "close";
//            if (message != null || !message.equals("")) {
//                send = new SendThread(socket);
//                Log.e(Tag,"종료시 소켓 : "+socket);
//                send.start();
//            }
//            Log.e(Tag, "socket : "+socket);
            if(socket!=null) {
                socket.close();
                Log.e(Tag, "소켓종료");
            }
            Log.e(Tag, "종료할 소켓이 없음");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showItemList() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //group_chat_recyclerView.scrollToPosition(chatItem_uiList.size() - 1);
                group_chat_recyclerView.scrollToPosition(groupChatAdapter.getItemCount() - 1);
            }
        }, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CroPicker.REQUEST_ALBUM) {
            album_pics = data.getParcelableArrayListExtra(CroPicker.EXTRA_RESULT_IMAGES);
            Log.e(Tag, "album_pics.size : " + album_pics.size());

            ArrayList<String> server_chat_pic = new ArrayList<>();

            // 클라내부
            for (int i = 0; i < album_pics.size(); i++) {
                ImagePath = album_pics.get(i).getImagePath();
                Log.e(Tag, "album_pics.get(" + i + ") : " + ImagePath);
                ChatItem_ui chatItem_ui = new ChatItem_ui(null, user_no, "", "", ImagePath, true);
                chatItem_uiList.add(chatItem_ui);
                groupChatAdapter.notifyDataSetChanged();

                //클라에서 서버로 사진보내기위해 byte배열로 형변환한 이미지를 server_chat_pic에 차곡차곡 넣기
                Bitmap bitmap = ImagePathToBitmap(ImagePath);
                byteImage = BitmapToStringByte(bitmap);
                server_chat_pic.add(byteImage);

                // Log.e(Tag, "byteImage : " + byteImage);
            }


            // 어레이리스트를 jsonarray로 바꾸기
            Log.e(Tag, "jsonElements만듦 : " + jsonElements);
            jsonElements = (JsonArray) new Gson().toJsonTree(server_chat_pic);
            //Log.e(Tag,"jsonElements:"+jsonElements);

            send = new SendThread(socket);
            send.start();

//            Log.e(Tag,"두번째사진"+album_pics.get(1));
//            Log.e(Tag, "핸들_현재 유저 번호 :" + user_no);
//            String chat_user_nick;
//            String chat_user_image;
//            for (int i = 0; i < participantInfoList.size(); i++) {
//                if (participantInfoList.get(i).getParticipant_no().equals(user_no)) {
//                    // 유저 닉네임
//                    chat_user_nick = participantInfoList.get(i).getParticipant_nick();
//                    // 유저 사진
//                    chat_user_image = participantInfoList.get(i).getParticipant_image();
//                    Log.e(Tag, "1111_chat_user_nick : " + chat_user_nick);
//                    Log.e(Tag, "1111_user_no : " + user_no);
//                    Log.e(Tag, "1111_chat_user_image : " + chat_user_image);
//                    ChatItem_ui chatItem_ui = new ChatItem_ui(chat_message, user_no, chat_user_nick, chat_user_image);
//                    chatItem_uiList.add(chatItem_ui);
//                    groupChatAdapter.notifyDataSetChanged();
//                    break;
//                }
//            }
        }
    }

    public Bitmap ImagePathToBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    private String BitmapToStringByte(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageByte = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageByte, Base64.DEFAULT);
        return encodedImage;
    }

    public void sendFCM(String user_token, String title, String msg){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(Tag, "전체 response : " + response);
                try {
                    Log.e(Tag, "전체 response : " + response);
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.e(Tag,"jsonResponse : "+jsonResponse);
                    //boolean success = jsonResponse.getBoolean("success");
                    //Log.e(Tag, "success : "+success);
//                    jsonResponse.get("success")
//                    JSONObject jsonResponse = new JSONObject(response);
//                    Log.e(Tag, "요청jsonResponse : " + jsonResponse);
////                                        // success : 해당 과정이 정상적으로 수행 되었는지 response값
//                    //boolean success = jsonResponse.getBoolean("success");
//
//                    //if (success) { // 이미지 업로드가 되면, 저장된 이미지파일 이름을 가져온다.
//                    Log.e(Tag, "업로드 성공1");
//                    //Log.e(Tag, "저장된 사진메세지파일 이름 : " + jsonResponse);
//                    JSONObject jsonObject1 = jsonResponse.getJSONObject("response");
//                    Log.e(Tag, "jsonObject1 : " + jsonObject1);
//
                } catch (Exception e) { // 오류가 발생했을 경우
                    e.printStackTrace();
                    Log.e(Tag, "오류발생1 : " + e);
                }
            }
        };
        //logLargeString("서버로 보낼 jsonElements:"+jsonElements);

        Log.e(Tag,"알림보낼 group_no :"+ group_no );
        SendFcmRequest sendFcmRequest = new SendFcmRequest(user_token, title, msg, group_no,responseListener);
        // 그룸생성 요청을 실질적으로 보낼 수 있도록 큐를 만든다
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // 큐에 그룹 큐를 등록한다
        queue.add(sendFcmRequest);
    }

//    public void logLargeString(String str) {
//        if (str.length() > 3000) {
//            Log.e(Tag, str.substring(0, 3000));
//            logLargeString(str.substring(3000));
//        } else {
//            Log.e(Tag, str); // continuation
//        }
//    }
}
