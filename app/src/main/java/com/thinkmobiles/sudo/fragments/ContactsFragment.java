package com.thinkmobiles.sudo.fragments;

/**
 * Created by njakawaii on 14.04.2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinkmobiles.sudo.R;
import com.thinkmobiles.sudo.MainToolbarManager;
import com.thinkmobiles.sudo.activities.BaseProfileActivity;
import com.thinkmobiles.sudo.activities.ProfileAddActivity;
import com.thinkmobiles.sudo.activities.ProfileEditActivity;
import com.thinkmobiles.sudo.activities.ProfileViewActivity;
import com.thinkmobiles.sudo.adapters.ContactsListAdapter;
import com.thinkmobiles.sudo.callbacks.ContactsFragmentCallback;
import com.thinkmobiles.sudo.core.rest.RetrofitAdapter;
import com.thinkmobiles.sudo.global.App;
import com.thinkmobiles.sudo.models.DefaultResponseModel;
import com.thinkmobiles.sudo.models.addressbook.UserModel;
import com.thinkmobiles.sudo.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ContactsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Activity mActivity;
    private FloatingActionButton mBTNAddFriend;
    private View mView;

    public List<UserModel>  mContactsList;
    private Callback<List<UserModel>> mContactsCB;

    private StickyListHeadersListView stickyList;
    private ContactsListAdapter mStickyListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.contacts, container, false);
        findUI();
        initList();
        setListener();
        initGetContactsCB();
        MainToolbarManager.getCustomInstance(mActivity).changeToolbarTitleAndIcon(App.getCurrentUser().getEmail(), App.getCurrentUser().getEmail());
        return mView;
    }

    private void findUI() {
        mBTNAddFriend = (FloatingActionButton) mView.findViewById(R.id.btnAddFriend_CF);
        stickyList = (StickyListHeadersListView) mView.findViewById(R.id.lwContactsList);
    }

    private void makeGetUserRequest() {
        RetrofitAdapter.getInterface().getContacts(mContactsCB);
    }

    @Override
    public void onResume() {
        super.onResume();
        makeGetUserRequest();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    private void initList() {
        stickyList.setDivider(null);
        stickyList.setDividerHeight(0);
        mStickyListAdapter = new ContactsListAdapter(mActivity);
        stickyList.setAdapter(mStickyListAdapter);
    }


    public void reloadList(List<UserModel> contacts) {

        mStickyListAdapter.reloadList(contacts);
    }

    private void setListener() {
        mBTNAddFriend.setOnClickListener(this);
        stickyList.setOnItemClickListener(this);
    }

    private void initGetContactsCB() {
        mContactsCB = new Callback<List<UserModel>>() {
            @Override
            public void success(List<UserModel> userModels, Response response) {
                mContactsList = userModels;
                App.setCurrentContacts(String.valueOf(mContactsList.size()));

                reloadList(userModels);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddFriend_CF:
                addFriendActivity();
                break;

        }
    }

    private void addFriendActivity() {
        Intent intent = new Intent(mActivity, ProfileAddActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        UserModel tempModel = mStickyListAdapter.getItem(i);
        ImageView transitionView = ((ContactsListAdapter.ViewHolder) view.getTag()).getAvatarIV();
        startProfileViewActivity(tempModel, transitionView);
    }


    public void searchContactsList(String querry) {
        List<UserModel> tempContactsArrayList = new ArrayList<>();
        if (mContactsList != null) {
            for (UserModel userModel : mContactsList ) {
                if (Utils.stringContains(userModel.getCompanion(), querry)) tempContactsArrayList.add(userModel);
            }
            reloadList(tempContactsArrayList);
        }
    }

    public void reloadCurrentList() {
        if (mContactsList  != null) reloadList(mContactsList );
    }

    private void startProfileViewActivity(UserModel userModel, View view) {
        ProfileViewActivity.launch(mActivity, view, userModel);

    }

}
