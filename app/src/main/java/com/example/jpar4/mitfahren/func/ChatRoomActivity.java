/*
package com.example.jpar4.mitfahren.func;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jpar4.mitfahren.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import static android.R.id.message;

public class ChatRoomActivity extends BaseActivity implements DrawLine.sendToActivity,NavigationView.OnNavigationItemSelectedListener {


    //    private ViewPager viewPager;
    private ChatRoomViewPager viewPager;
    String friendId;
    String friendNickname;
    String friendProfile;
    Toolbar toolbar;
    Socket socket;
    EditText sendcontent;
    final private static String LOG = "ChatRoomActivity";
    public Handler handler;
    MessageRoomFragment mf;
    DrawRoomFragment df;
    FragmentCommunicator fragmentCommunicator;
    DrawCommunicator drawCommunicator;
    public String sendName;
    public String sendContent;
    private ChatService mService;
    public SharedPreferences mPref;
    public SharedPreferences.Editor editor;
    String userId;
    String userNickname;
    public MyDatabaseOpenHelper db;
    public int no;
    public HashMap<String, String> NickHash = new HashMap<>();
    public HashMap<String, String> ProfileHash = new HashMap<>();
    BroadcastReceiver NetworkChangeUpdater;
    public ImageButton plusBtn;
    public Button drawModeBtn;
    public Button sendMsgBtn;
    public Button drawChatBtn;
    public ImageButton alarmActive;
    DrawerLayout drawer;

    public int delPosition;

    public static final int MakeGroupActivity = 6839;

    public static final int DeleteImage = 3102;

    public static final int DeleteMessage = 2013;


    MemberListAdapter memberListAdapter;
    ArrayList<MemberListItem> ListData;

    NavigationView navigationView;

    final String upLoadServerUri = "http://vnschat.vps.phps.kr/SendImage.php";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private Uri mImageCaptureUri;

    String alarmKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_nav);


        sendcontent = (EditText) findViewById(R.id.messageContent);

        // Adding Toolbar to the activity
        toolbar = (Toolbar) findViewById(R.id.toolbarChatRoom);
        plusBtn = (ImageButton) findViewById(R.id.plus_btn);
        drawModeBtn = (Button) findViewById(R.id.drawMode_btn);
        sendMsgBtn = (Button) findViewById(R.id.SendMsgBtn);
        drawChatBtn = (Button) findViewById(R.id.drawChatBtn);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
//        actionBar.setDisplayShowTitleEnabled(false);


        db = new MyDatabaseOpenHelper(this, "catchMind", null, 1);
        mPref = getSharedPreferences("login", MODE_PRIVATE);
        editor = mPref.edit();


        userId = mPref.getString("userId", "아이디없음");
        userNickname = mPref.getString("nickname", "닉없음");
        Log.d("chatroomId", userId);
        Intent GI = getIntent();


        friendId = GI.getStringExtra("friendId");
        no = GI.getIntExtra("no", 0);
        if (friendId.equals("noti")) {
            getFriendId(no);
        }


        Log.d("chatroomId2", friendId);
        String nickname = GI.getStringExtra("nickname");


        if (no == 0) {
            Cursor cursor = db.getFriendData(friendId);
            Cursor cursor2 = db.getChatFriendData(friendId);

            if (cursor2.getCount() != 0) {
                cursor2.moveToNext();
                if (nickname.equals("#없음")) {
                    nickname = cursor2.getString(2);
                }
            }

            friendProfile = "";
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                friendProfile = cursor.getString(2);

            }

            friendNickname = nickname;


        } else {
            ResetHash();
        }

        getSupportActionBar().setTitle(nickname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NetworkChangeUpdater = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here
                if (intent != null) {
//                    Toast.makeText(context, "액티비티의 리시버작동!"+intent.toString(), Toast.LENGTH_LONG).show();
                    String networkType = intent.getExtras().getString("wifi");
                    UpdateNetwork(networkType);

                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("receiver.to.activity.transfer");

        registerReceiver(NetworkChangeUpdater, filter);

        // Initializing ViewPager
        viewPager = (ChatRoomViewPager) findViewById(R.id.pagerChatRoom);
        mf = new MessageRoomFragment();
        df = new DrawRoomFragment();
        fragmentCommunicator = (FragmentCommunicator) mf;
        drawCommunicator = (DrawCommunicator) df;
        ChatRoomPagerAdapter pagerAdapter = new ChatRoomPagerAdapter(getSupportFragmentManager(), mf, df, mPref, friendId, no);

        Log.d("chatRoomActivity", userId + "###" + no + "###" + friendId);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    ChatRoomViewPager.DrawMode = false;
                    drawModeBtn.setVisibility(View.GONE);
                    plusBtn.setVisibility(View.VISIBLE);
                    drawChatBtn.setVisibility(View.GONE);
                    sendMsgBtn.setVisibility(View.VISIBLE);

                } else if (position == 1) {
                    ChatRoomViewPager.DrawMode = false;
                    plusBtn.setVisibility(View.GONE);
                    drawModeBtn.setVisibility(View.VISIBLE);
                    sendMsgBtn.setVisibility(View.GONE);
                    drawChatBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Intent serviceIntent = new Intent(this, ChatService.class);
        bindService(serviceIntent, mConnection, this.BIND_AUTO_CREATE);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {


                if (msg.what == 1) {

                    if (no == 0) {
                        String content = msg.getData().getString("content");
                        long time = msg.getData().getLong("time");
                        fragmentCommunicator.passData(friendId, friendNickname, friendProfile, content, time, 1);
                    } else {
                        String friendId = msg.getData().getString("friendId");
                        String content = msg.getData().getString("content");
                        long time = msg.getData().getLong("time");
                        fragmentCommunicator.passData(friendId, NickHash.get(friendId), ProfileHash.get(friendId), content, time, 1);
                    }

                } else if (msg.what == 2) {
                    String content = msg.getData().getString("content");
                    long time = msg.getData().getLong("time");
                    fragmentCommunicator.passData("내아아이디", "내닉네임", "내프로필", content, time, 2);
                } else if (msg.what == 3) {
                    String content = msg.getData().getString("content");
                    long time = msg.getData().getLong("time");
                    fragmentCommunicator.passData("내아아이디", "내닉네임", "내프로필", content, time, 3);
                } else if (msg.what == 77) {
                    fragmentCommunicator.alertChange();
                } else if (msg.what == 10) {
                    String path = msg.getData().getString("path");
                    drawCommunicator.receivePath(path);
                } else if (msg.what == 11) {
                    drawCommunicator.receiveClear();
                } else if (msg.what == 99) {
                    memberListAdapter.notifyDataSetChanged();
                } else if (msg.what == 88) {

                    String friendId = msg.getData().getString("friendId");
                    String content = msg.getData().getString("content");
                    String nickname;
                    if (no == 0) {
                        nickname = friendNickname;
                    } else {
                        nickname = NickHash.get(friendId);
                    }
                    drawCommunicator.drawChat(nickname, content);
                } else if (msg.what == 51) {

                    if (no == 0) {
                        String content = msg.getData().getString("content");
                        long time = msg.getData().getLong("time");
                        fragmentCommunicator.passData(friendId, friendNickname, friendProfile, content, time, 51);
                    } else {
                        String friendId = msg.getData().getString("friendId");
                        String content = msg.getData().getString("content");
                        long time = msg.getData().getLong("time");
                        fragmentCommunicator.passData(friendId, NickHash.get(friendId), ProfileHash.get(friendId), content, time, 51);
                    }

                } else if (msg.what == 52) {

                    String friendId = msg.getData().getString("friendId");
                    String content = msg.getData().getString("content");
                    long time = msg.getData().getLong("time");
                    fragmentCommunicator.passData("내아이디", "내닉네임", "내프로필", content, time, 52);

                } else if (msg.what == 365) {
                    fragmentCommunicator.bottomSelect();
                }


            }
        };


        attachKeyboardListeners();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ListView lv = (ListView) findViewById(R.id.memberList);

        if (no != 0) {

            View header = getLayoutInflater().inflate(R.layout.member_invite_header, null, false);

            header.setOnClickListener(mClickListener);

            lv.addHeaderView(header);

        }

        ListData = new ArrayList<>();

        Cursor cursor;

        if (no == 0) {
            cursor = db.getChatFriendListDataWithMe(friendId);
        } else {
            cursor = db.getChatFriendListByNo(no);
        }

        while (cursor.moveToNext()) {
            MemberListItem addItem = new MemberListItem(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            ListData.add(addItem);
        }


        memberListAdapter = new MemberListAdapter(this, ListData);

        lv.setAdapter(memberListAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("아이템", "" + position);
            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        alarmActive = (ImageButton) findViewById(R.id.alarmImageBtn);

        if (no == 0) {
            alarmKey = friendId;
        } else {
            alarmKey = no + "";
        }
        if (!mPref.getBoolean(alarmKey, true)) {
            alarmActive.setBackgroundResource(R.drawable.alarm_disable_icon);
        } else {
            alarmActive.setBackgroundResource(R.drawable.alarm_active_icon);
        }


//        Menu menu = navigationView.getMenu();
//        Log.d("미쳐씨발",menu.size()+"");
//        if(no == 0) {
//            MenuItem GroupChat = menu.getItem(0);
//            GroupChat.setVisible(false);
//
//        }else{
//            MenuItem P2PChat = menu.getItem(1);
//            P2PChat.setVisible(false);
//        }

    }

    public void activeAlarm(View v) {

        if (mPref.getBoolean(alarmKey, true)) {
            editor.putBoolean(alarmKey, false);
            editor.commit();
            alarmActive.setBackgroundResource(R.drawable.alarm_disable_icon);

        } else {

            editor.putBoolean(alarmKey, true);
            editor.commit();
            alarmActive.setBackgroundResource(R.drawable.alarm_active_icon);


        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {


            Intent intentMakeGroup = new Intent(getApplicationContext(), MakeGroupActivity.class);
            intentMakeGroup.putExtra("FCR", true);
            intentMakeGroup.putExtra("friendId", friendId);
            Log.d("설마", friendId);
            startActivityForResult(intentMakeGroup, MakeGroupActivity);


        }
    };

    public void ResetMemberList() {
        memberListAdapter.clearList();

        Cursor cursor = db.getChatFriendListByNo(no);

        while (cursor.moveToNext()) {
            MemberListItem addItem = new MemberListItem(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            memberListAdapter.addMemberItem(addItem);
            Log.d("귀여워", cursor.getString(1));
        }

        Message message = Message.obtain();
        message.what = 99;

        handler.sendMessage(message);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == MakeGroupActivity) {
                long now = System.currentTimeMillis();
                String content = data.getExtras().getString("content");
                String inviteId = data.getExtras().getString("inviteId");

                mService.sendInvite(no, friendId, content, now, inviteId);

            } else if (requestCode == PICK_FROM_CAMERA) {

                try {

//                    String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
//                    String imgpath = ex_storage + "/tmp.png";


                    String imgpath = data.getExtras().getString("CustomPath");

                    if (imgpath.equals("none")) {
                        Toast.makeText(this, "사진촬영 실패", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d("이미지경로", imgpath);


                    ImageSendThread ist = new ImageSendThread(imgpath);
                    ist.start();

                    ist.join();

                    SetBottomThread sbt = new SetBottomThread();
                    sbt.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (requestCode == PICK_FROM_ALBUM) {

                try {

                    Log.d("이미지경로", getPath(data.getData()));

                    ImageSendThread ist = new ImageSendThread(getPath(data.getData()));

                    ist.start();

                    ist.join();

                    SetBottomThread sbt = new SetBottomThread();
                    sbt.start();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == DeleteImage) {


                int position = data.getExtras().getInt("position");
                fragmentCommunicator.deleteMessage(position);


            } else if (requestCode == DeleteMessage) {


                delPosition = data.getExtras().getInt("position");
                String type = data.getExtras().getString("type");

                if (type.equals("del")) {

                    DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fragmentCommunicator.deleteMessage(delPosition);
                        }

                    };


                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    };


                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("선택한 메시지를 삭제하시겠습니까 \n \n 삭제한 메시지는 내 채팅방에서만 적용되며 상대방의 채팅방에서는 삭제되지 않습니다.")
                            .setPositiveButton("확인", deleteListener)
                            .setNegativeButton("취소", cancelListener)
                            .create();


                    dialog.show();


                    Button deleteBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    deleteBtn.setTextColor(Color.BLACK);

                    Button cancelBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    cancelBtn.setTextColor(Color.BLACK);


                } else if (type.equals("copy")) {


                    String subType = data.getExtras().getString("subType");

                    if (subType.equals("text")) {

                        String content = data.getExtras().getString("content");

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("catchMind", content);
                        clipboard.setPrimaryClip(clip);
                    }


                } else if (type.equals("share")) {


                    String subType = data.getExtras().getString("subType");

                    if (subType.equals("text")) {

                        String content = data.getExtras().getString("content");

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                    } else if (subType.equals("image")) {

                        String content = data.getExtras().getString("content");

                        ImageShareThread ist = new ImageShareThread(content, this);
                        ist.start();


//                        try {
//
//                            String content = data.getExtras().getString("content");
//
//                            Bitmap bitmap = Glide.
//                                    with(this).
//                                    load(content).
//                                    asBitmap().
//                                    into(-1, -1).
//                                    get();
//
//                            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"title", null);
//                            Uri bitmapUri = Uri.parse(bitmapPath);
//
//                            Intent intent = new Intent(Intent.ACTION_SEND);
//                            intent.setType("image*/
/*");
//                            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//                            startActivity(Intent.createChooser(intent, "Share"));
//
//                        }catch (InterruptedException e){
//                            e.printStackTrace();
//                        }catch (ExecutionException e){
//                            e.printStackTrace();
//                        }
                    }


                }

            }

        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.open_drawer, R.string.close_drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        // do things when keyboard is shown
//        Toast.makeText(this,"show",Toast.LENGTH_SHORT).show();
        Log.d("키보드", "show" + keyboardHeight);
        drawCommunicator.resizeSketchBook();
    }

    @Override
    protected void onHideKeyboard() {
        // do things when keyboard is hidden
//        Toast.makeText(this,"hide",Toast.LENGTH_SHORT).show();
        Log.d("키보드", "hide");
        drawCommunicator.resizeSketchBook();
    }


    public void UpdateNetwork(String type) {
        if (type.equals("wifi")) {
            Intent serviceIntent = new Intent(this, ChatService.class);
            bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
            Log.d("담배Net", "UPDATE wifi##" + type);
        } else {
            Intent serviceIntent = new Intent(this, ChatService.class);
            bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
            Log.d("담배Net", "UPDATE nonewifi##" + type);
        }
    }

    public void ResetHash() {


        NickHash = new HashMap<>();
        ProfileHash = new HashMap<>();
        Cursor cursor = db.getChatFriendListByNo(no);

        while (cursor.moveToNext()) {
            Log.d("리시브시발", cursor.getString(1));
            NickHash.put(cursor.getString(1), cursor.getString(2));
            ProfileHash.put(cursor.getString(1), cursor.getString(3));
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        db.updateChatRoomData(no, friendId, System.currentTimeMillis());
        if (mService != null) {
            mService.boundStart = true;
            long now = System.currentTimeMillis();
            mService.sendRead(no, friendId, now);
        }
        fragmentCommunicator.alertChange();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mService.boundStart = false;
    }

    public interface FragmentCommunicator {

        void passData(String friendId, String nickname, String profile, String content, long time, int type);

        void alertChange();

        void changeNo(int sNo);

        void deleteMessage(int position);

        void bottomSelect();

    }

    public interface DrawCommunicator {

        void receivePath(String PATH);

        void resizeSketchBook();

        void MinusWidth();

        void PlusWidth();

        void receiveClear();

        void drawChat(String Nickname, String Content);

    }

//    public void passVal(FragmentCommunicator fragmentCommunicator) {
//        this.fragmentCommunicator = fragmentCommunicator;
//
//    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            ChatService.ChatServiceBinder binder = (ChatService.ChatServiceBinder) service;
            mService = binder.getService(); //서비스 받아옴
            mService.registerCallback(mCallback); //콜백 등록
            mService.boundCheck = true;
            mService.boundStart = true;
            mService.boundedNo = no;
            mService.boundedFriendId = friendId;
            long now = System.currentTimeMillis();
            mService.sendRead(no, friendId, now);

        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };


    private ChatService.ICallback mCallback = new ChatService.ICallback() {

        public void recvData(String friendId, String content, long time) {

            Message message = Message.obtain();
            message.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("friendId", friendId);
            bundle.putString("content", content);
            bundle.putLong("time", time);
            message.setData(bundle);
             handler.sendMessage(message);

        }

        public void recvUpdate() {
            Message message = Message.obtain();
            message.what = 77;
            handler.sendMessage(message);
        }

        public void changeNo(int passNo) {
            no = passNo;
            fragmentCommunicator.changeNo(passNo);
        }

        public void sendMessageMark(String content, long time) {
            Message message = Message.obtain();
            message.what = 2;

            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            bundle.putLong("time", time);

            message.setData(bundle);

            handler.sendMessage(message);
        }

        public void sendInviteMark(String inviteId, String content, long time, boolean resetMemberList) {

            if (resetMemberList) {
                ResetMemberList();
                try {
                    JSONArray jsonArray = new JSONArray(friendId);
                    JSONArray addArray = new JSONArray(inviteId);
                    for (int i = 0; i < addArray.length(); i++) {
                        jsonArray.put(addArray.getString(i));
                    }
                    friendId = jsonArray.toString();
                    Log.d("집중", friendId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ResetHash();
            }

            Message message = Message.obtain();
            message.what = 3;

            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            bundle.putLong("time", time);

            message.setData(bundle);

            handler.sendMessage(message);
        }

        @Override
        public void sendExitMark(String sFriendId, String content, long time) {

            ResetMemberList();

            try {
                JSONParser parser = new JSONParser();
                org.json.simple.JSONArray jarray = (org.json.simple.JSONArray) parser.parse(friendId);
                jarray.remove(sFriendId);
                friendId = jarray.toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ResetHash();

            Message message = Message.obtain();
            message.what = 3;

            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            bundle.putLong("time", time);

            message.setData(bundle);

            handler.sendMessage(message);
        }

        @Override
        public void sendImageMark(String friendId, String content, long time, int kind) {


            Message message = Message.obtain();
            message.what = kind;

            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            bundle.putLong("time", time);
            bundle.putString("friendId", friendId);

            message.setData(bundle);

            handler.sendMessage(message);


        }

        public void resetHash() {
            ResetHash();
            ResetMemberList();
        }

        public String getFriendId() {
            return friendId;
        }

        public void resetToolbar() {
            resetTitle();
        }

        public void receivePath(String PATH) {

            Message message = Message.obtain();
            message.what = 10;

            Bundle bundle = new Bundle();
            bundle.putString("path", PATH);

            message.setData(bundle);

            handler.sendMessage(message);
        }

        @Override
        public void receiveClear() {
            Message message = Message.obtain();
            message.what = 11;

            handler.sendMessage(message);
        }

        @Override
        public void receiveDrawChat(String friendId, String content) {
            Message message = Message.obtain();
            message.what = 88;

            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            bundle.putString("friendId", friendId);

            message.setData(bundle);

            handler.sendMessage(message);
        }
    };

    public void resetTitle() {
        getSupportActionBar().setTitle("그룹채팅 " + no);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.boundCheck = false;
        mService.boundStart = false;
        mService.boundedNo = -1;
        mService.boundedFriendId = "";
        unbindService(mConnection);
        unregisterReceiver(NetworkChangeUpdater);
        ChatRoomViewPager.DrawMode = false;
    }

    @Override
    public void sendPath(String PATH) {
        long now = System.currentTimeMillis();
        mService.sendPATH(no, friendId, PATH, now);
    }

    @Override
    public void sendClear() {
        long now = System.currentTimeMillis();
        mService.sendClear(no, friendId, "just Clear", now);
    }

    public void sendMessage(View v) {


        long now = System.currentTimeMillis();
//        Date nowdate = new Date(now);
//        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
//        String time = sdfNow.format(nowdate);

        String et = sendcontent.getText().toString();
        sendcontent.setText("");

//        db.insertMessageData(userId,no,friendId,et,now,2,true);
        Log.d("sendMessage,db.insert", userId + "####" + friendId + "####" + et);


        mService.SendMessage(message);
    }
}
*/
