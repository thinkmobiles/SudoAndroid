package com.thinkmobiles.sudo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thinkmobiles.sudo.R;
import com.thinkmobiles.sudo.models.Contacts;
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
    private Context context;


    public ContactsListAdapter(Context context) {
        this.context = context;
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
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public long getHeaderId(int i) {
        return contacts.get(i).getCompanion().subSequence(0, 1).charAt(0);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.contact_item, viewGroup, false);
            holder.ivAvatar = (ImageView) view.findViewById(R.id.iwContactsAvatar);
            holder.tvFirstName = (TextView) view.findViewById(R.id.twContacstFirstName);
            holder.tvLastName = (TextView) view.findViewById(R.id.twContacstLastName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvFirstName.setText(contacts.get(i).getCompanion());

        setAvatar(holder.ivAvatar, contacts.get(i).getAvatar());


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
        String headerText = "" + contacts.get(i).getCompanion().subSequence(0, 1).charAt(0);
        holder.text.setText(headerText);
        return convertView;
    }


    private class ViewHolder {
        ImageView ivAvatar;
        TextView tvFirstName, tvLastName;
    }

    private class HeaderViewHolder {

        TextView text;
    }


    private void setAvatar(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.equalsIgnoreCase("")) {
            int dimen = (int) context.getResources().getDimension(R.dimen.sc_avatar_size);
            Picasso.with(context)
                    .load(imageUrl)
                    .resize(dimen, dimen)
                    .into(imageView);


        } else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            imageView.setImageBitmap(bm);
        }

    }

}