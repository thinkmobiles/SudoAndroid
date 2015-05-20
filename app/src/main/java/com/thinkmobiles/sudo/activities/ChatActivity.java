package com.thinkmobiles.sudo.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.thinkmobiles.sudo.R;
import com.thinkmobiles.sudo.global.Network;
import com.thinkmobiles.sudo.utils.ToolbarManager;
import com.thinkmobiles.sudo.adapters.ChatListAdapter;
import com.thinkmobiles.sudo.core.APIConstants;
import com.thinkmobiles.sudo.core.rest.RetrofitAdapter;
import com.thinkmobiles.sudo.global.App;
import com.thinkmobiles.sudo.global.Constants;
import com.thinkmobiles.sudo.models.DefaultResponseModel;
import com.thinkmobiles.sudo.models.chat.CompanionModel;
import com.thinkmobiles.sudo.models.chat.MessageModel;
import com.thinkmobiles.sudo.utils.ContactManager;
import com.thinkmobiles.sudo.utils.Utils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by omar on 28.04.15.
 */
public class ChatActivity extends ActionBarActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    private ListView mChatList;
    private EditText etMessage;
    private Button btnSend;
    private String message;
    private RelativeLayout contentRoot, rlAddComment;
    private ChatListAdapter mListAdapter;
    private Callback<List<MessageModel>> mMessagesCB;
    private Callback<DefaultResponseModel> mSendMessageCB;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Toolbar toolbar;

    private String mOwnerNumber;
    private String mCompanionNumber;
    private List<MessageModel> mMessageModelList;
    private Socket mSocket;
    private int drawingStartLocation;
    private boolean selectMode = false;
    private MenuItem menuItemDelete;
    private boolean[] selectionArray;
    private int mPageCount = 0;
    private int mLength = 6;
    private int mFocusView = 0;


    {
        try {
            mSocket = IO.socket(APIConstants.SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFocusView() {
        if (mListAdapter.getCount() > 1) mFocusView = mListAdapter.getCount() - 1;

    }


    private MessageModel mSendMessageModel;
    private MessageModel mFirstMessageModel;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!selectMode) menuItemDelete.setVisible(false);


        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);//Menu Resource, Menu
        menuItemDelete = menu.findItem(R.id.action_detele);

        return true;
    }


    public static final String BUNDLE = "bundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
        loadContent();
        initListeners();
        setSwipeRefrechLayoutListenerAndColor();
        initGetMessageCB();
        initSendMessageCB();
        getMessagesPages();
        initSocked();
        ToolbarManager.getInstance(this).changeToolbarTitleAndIcon("Chat", 0);


        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }

        mSocket.connect();
    }


    private void initSocked() {
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("receiveMessage", onReceive);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uId", App.getuId());
            mSocket.emit("authorize", jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            Gson gson = new Gson();
            final MessageModel message = gson.fromJson(data.toString(), MessageModel.class);
            Log.d("socket", "received");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListAdapter.addNewMessage(message);
                }
            });

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("receiveMessage", onReceive);
        mSocket.disconnect();
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Connection problims ", Toast.LENGTH_LONG).show();
                }
            });


        }
    };

    private void initSendMessageCB() {
        mSendMessageCB = new Callback<DefaultResponseModel>() {
            @Override
            public void success(DefaultResponseModel defaultResponseModel, Response response) {
                mListAdapter.addNewMessage(mSendMessageModel);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
    }

    private void initGetMessageCB() {
        mMessagesCB = new Callback<List<MessageModel>>() {
            @Override
            public void success(List<MessageModel> messageModel, Response response) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (messageModel.size() > 0) {
                    if (mPageCount < 2) {
                        mFirstMessageModel = messageModel.get(0);
                    }
                    mMessageModelList.addAll(messageModel);
                    mListAdapter.reloadContent(mMessageModelList, mOwnerNumber);



                }

            }

            @Override
            public void failure(RetrofitError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ChatActivity.this, "Error sending message", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onBackPressed() {
        contentRoot.animate().translationY(Utils.getScreenHeight(this)).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }
        }).start();
    }

    private void scrollListViewToPosition(final int newListSize) {
        /*mFocusView += newListSize;*/
        mChatList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mChatList.setSelection( mFocusView);
             }
        });
    }

    private void initComponents() {
        setContentView(R.layout.activity_chat);
        mChatList = (ListView) findViewById(R.id.lvChatList);
        contentRoot = (RelativeLayout) findViewById(R.id.rlMain_AC);
        rlAddComment = (RelativeLayout) findViewById(R.id.rlAddComment_AC);
        mChatList.setDivider(null);
        mChatList.setDividerHeight(0);

        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        mListAdapter = new ChatListAdapter(this);
        mChatList.setAdapter(mListAdapter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_AC);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void loadContent() {
        loadChat();
    }

    private void loadChat() {
        mOwnerNumber = getIntent().getExtras().getBundle(BUNDLE).getString(Constants.FROM_NUMBER);
        mCompanionNumber = getIntent().getExtras().getBundle(BUNDLE).getString(Constants.TO_NUMBER);
        mMessageModelList = new ArrayList<>();
    }


    private void initListeners() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = String.valueOf(etMessage.getText());
                if (Utils.checkString(message)) sendMessage(message);
                etMessage.setText("");
                stopSelectionMode();
            }
        });
        mChatList.setOnItemLongClickListener(this);
        mChatList.setOnItemClickListener(this);
        mChatList.setOnScrollListener(this);

    }

    private void sendMessage(final String _message) {
        RetrofitAdapter.getInterface().senMessage(mOwnerNumber, mCompanionNumber, _message, "sms", mSendMessageCB);
        setSendMessageModel(mOwnerNumber, mCompanionNumber, _message);
    }

    private void setSendMessageModel(String _mOwnerNumber, String _mCompanionNumber, String _message) {
        mSendMessageModel = new MessageModel();
        mSendMessageModel.setCompanion(createCompanion(_mCompanionNumber, mFirstMessageModel));
        mSendMessageModel.setOwner(createCompanion(_mOwnerNumber, mFirstMessageModel));
        mSendMessageModel.setPostedDate(Utils.getDateServerStyle());
        mSendMessageModel.setBody(_message);
    }

    public static void launch(final Activity activity, final String _ownerNumber, final String _companionNumber, int[] _startingLocation) {
        final Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.ARG_DRAWING_START_LOCATION, _startingLocation[1]);
        Bundle b = new Bundle();
        if (ContactManager.isMyNumber(_ownerNumber)) {
            b.putString(Constants.FROM_NUMBER, _ownerNumber);
            b.putString(Constants.TO_NUMBER, _companionNumber);
        } else {
            b.putString(Constants.FROM_NUMBER, _companionNumber);
            b.putString(Constants.TO_NUMBER, _ownerNumber);
        }
        intent.putExtra(BUNDLE, b);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);

    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {

        if (!selectMode) menuItemDelete.setVisible(false);
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                if (!selectMode) {
                    onBackPressed();
                } else {
                    stopSelectionMode();
                }

                break;
            case R.id.action_detele:
                deleteChatItems();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void getMessagesPages() {
        mPageCount++;

        RetrofitAdapter.getInterface().getConversationPages(mOwnerNumber, mCompanionNumber, mPageCount, mLength, mMessagesCB);
    }

    private CompanionModel createCompanion(String number, MessageModel messageModel) {

        CompanionModel companionModel = new CompanionModel();
        companionModel.setNumber(number);

        if (messageModel != null) {

            if (number.equalsIgnoreCase(messageModel.getCompanion().getNumber()))
                companionModel.setAvatar(messageModel.getCompanion().getAvatar());
            else companionModel.setAvatar(messageModel.getOwner().getAvatar());
        }

        return companionModel;
    }


    private void startIntroAnimation() {
        ViewCompat.setElevation(toolbar, 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        rlAddComment.setTranslationY(200);

        contentRoot.animate().scaleY(1).setDuration(200).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewCompat.setElevation(toolbar, Utils.dpToPx(8));
                animateContent();

            }
        }).start();
    }

    private void animateContent() {
        rlAddComment.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(200).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Network.isInternetConnectionAvailable(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        startSelectionMode();
        controlSelection(selectionArray.length - 1 - position);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (selectMode) {

            controlSelection(selectionArray.length - 1 - position);


        }
    }

    private void controlSelection(int position) {

        selectionArray[position] = !selectionArray[position];

        if (isEmptySelection()) {
            stopSelectionMode();
            return;
        }

        mListAdapter.setSelection(selectMode, selectionArray);

    }

    private void startSelectionMode() {
        selectMode = true;
        selectionArray = new boolean[mListAdapter.getList().size()];
        invalidateOptionsMenu();


    }

    private void stopSelectionMode() {
        selectMode = false;
        selectionArray = null;
        mListAdapter.setSelection(selectMode, selectionArray);
        invalidateOptionsMenu();


    }

    private void deleteChatItems() {

        if (!isEmptySelection()) {
            List<MessageModel> mListMessages = mListAdapter.getList();
            for (int i = 0; i < selectionArray.length - 1; i++) {
                if (selectionArray[i]) mListMessages.remove(i);
            }
            mListAdapter.reloadContent(mListMessages, mOwnerNumber);

        }

        stopSelectionMode();
    }

    private boolean isEmptySelection() {
        if (selectionArray == null || selectionArray.length == 0) return true;

        for (int i = 0; i < selectionArray.length - 1; i++) {
            if (selectionArray[i]) return false;
        }

        return true;
    }

    private void setSwipeRefrechLayoutListenerAndColor() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setFocusView();
                getMessagesPages();
                stopSelectionMode();
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if (canScrollUp(mChatList)) {
            mSwipeRefreshLayout.setEnabled(false);

        } else {
            mSwipeRefreshLayout.setEnabled(true);
        }

    }

    public boolean canScrollUp(View view) {

        return ViewCompat.canScrollVertically(view, -1);

    }


}
