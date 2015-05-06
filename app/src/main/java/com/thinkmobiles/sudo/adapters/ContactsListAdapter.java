package com.thinkmobiles.sudo.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thinkmobiles.sudo.R;
import com.thinkmobiles.sudo.activities.ProfileViewActivity;
import com.thinkmobiles.sudo.global.CircleTransform;
import com.thinkmobiles.sudo.models.addressbook.NumberModel;
import com.thinkmobiles.sudo.models.addressbook.UserModel;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


/**
 * Created by omar on 19.04.15.
 */
public class ContactsListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<UserModel> contacts;
    private LayoutInflater mInflater;
    private Activity mActivity;


    public ContactsListAdapter(Activity context) {
        this.mActivity = context;
        this.contacts = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }


    public void reloadList(List<UserModel> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public UserModel getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public long getHeaderId(int i) {


        if (contacts.size() > 0) {
            return contacts.get(i).getCompanion().subSequence(0, 1).charAt(0);
        } else return 0;


    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_contact, viewGroup, false);
            holder.ivAvatar = (ImageView) view.findViewById(R.id.ivContactsAvatar);
            holder.tvFirstName = (TextView) view.findViewById(R.id.tvContacstFirstName);
            holder.tvNumber = (TextView) view.findViewById(R.id.tvContactNumber);
            holder.ivOptions = (ImageView) view.findViewById(R.id.ivChatItemOptions);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        contacts.get(i).setAvatar("http://pix.vashdosug.ru/pix/feature/persons/3/21731.jpg");
        final UserModel thisUser = contacts.get(i);
        List<NumberModel> thisNumberModel = thisUser.getNumbers();


        holder.tvFirstName.setText(thisUser.getCompanion());

        if (thisNumberModel != null && thisNumberModel.size() > 0)
            holder.tvNumber.setText(thisNumberModel.get(thisNumberModel.size() - 1).getNumber());

        setAvatar(holder.ivAvatar, thisUser.getAvatar());
        final ImageView transitionView = holder.ivAvatar;

        holder.ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startProfileViewActivity(thisUser, transitionView);

            }
        });


        return view;
    }

    @Override
    public View getHeaderView(int i, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.contacts_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.tvContactsHeader);

            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text


        String headerText;


        if (contacts.size() > 0) headerText = "" + contacts.get(i).getCompanion().subSequence(0, 1).charAt(0);
        else headerText = "";


        holder.text.setText(headerText);
        return convertView;
    }


    public class ViewHolder {
        ImageView ivAvatar, ivOptions;
        TextView tvFirstName, tvNumber;

        public ImageView getAvatarIV() {
            return ivAvatar;
        }

    }

    private class HeaderViewHolder {

        TextView text;
    }


    private void setAvatar(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.equalsIgnoreCase("")) {
            int dimen = (int) mActivity.getResources().getDimension(R.dimen.sc_avatar_size);
            Picasso.with(mActivity).load(imageUrl).transform(new CircleTransform())
                    //                    .resize(dimen, dimen)
                    .into(imageView);


        } else {
            Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_launcher);
            imageView.setImageBitmap(bm);
        }

    }

    private void startProfileViewActivity(UserModel userModel, View view) {

        Log.d("start profile activity", "starting");

        ProfileViewActivity.launch(mActivity, view, userModel);

    }


}
