package com.thinkmobiles.sudo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thinkmobiles.sudo.R;
import com.thinkmobiles.sudo.core.APIConstants;
import com.thinkmobiles.sudo.models.BlockNumber;
import com.thinkmobiles.sudo.models.addressbook.NumberModel;
import com.thinkmobiles.sudo.models.addressbook.UserModel;
import com.thinkmobiles.sudo.utils.CircleTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by omar on 19.04.15.
 */
public class BlockNumberExpandableAdapter extends BaseExpandableListAdapter {

    private List<UserModel> contacts;
    private LayoutInflater mInflater;
    private Activity mActivity;
    private HashMap<int[], NumberModel> blockInteractions;

    public BlockNumberExpandableAdapter(Activity context) {
        this.mActivity = context;
        this.contacts = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        blockInteractions = new HashMap<>();
    }


    public void reloadList(List<UserModel> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return contacts.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return contacts.get(i).getNumbers().size();
    }

    @Override
    public UserModel getGroup(int i) {
        return contacts.get(i);
    }

    @Override
    public NumberModel getChild(int i, int i1) {
        return contacts.get(i).getNumbers().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int pos, boolean b, View view, ViewGroup viewGroup) {
        ParentViewHolder holder;
        if (view == null) {
            holder = new ParentViewHolder();
            view = mInflater.inflate(R.layout.list_item_block_number_profile, viewGroup, false);
            holder.ivAvatar = (ImageView) view.findViewById(R.id.ivContactsAvatar);
            holder.tvName = (TextView) view.findViewById(R.id.tvContacstFirstName);
            holder.tvBlockNumber = (TextView) view.findViewById(R.id.tvBlockNumber);
            holder.viewSeparator = view.findViewById(R.id.viewSeparator);
            view.setTag(holder);
        } else {
            holder = (ParentViewHolder) view.getTag();
        }
        holder.ivAvatar.setTag(pos);
        setAvatar(holder.ivAvatar, contacts.get(pos).getAvatar(), pos);
        holder.tvName.setText(contacts.get(pos).getCompanion());

        if (!contacts.get(pos).getNumbers().isEmpty()) {
            holder.tvBlockNumber.setText(contacts.get(pos).getNumbers().get(0).getNumber());
        }
        holder.viewSeparator.setVisibility(View.INVISIBLE);

        if (b) holder.viewSeparator.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public View getChildView(final int g, final int c, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder holder;
        if (view == null) {
            holder = new ChildViewHolder();
            view = mInflater.inflate(R.layout.list_item_block_number, viewGroup, false);
            holder.tvNumber = (TextView) view.findViewById(R.id.tvContactNumber);
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);

            view.setTag(holder);
        } else {
            holder = (ChildViewHolder) view.getTag();
        }
        holder.checkBox.setTag(new int[]{g, c});
        holder.checkBox.setChecked(contacts.get(g).getNumbers().get(c).isBlocked());
        holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int[] tag = (int[]) compoundButton.getTag();
                if (tag == null || tag[0] != g || tag[1] != c) {

                } else {
                    contacts.get(g).getNumbers().get(c).setBlocked(b);
                    blockInteractions.put(tag, contacts.get(g).getNumbers().get(c));
                }
            }
        });


        holder.tvNumber.setText(contacts.get(g).getNumbers().get(c).getNumber());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }


    public class ParentViewHolder {
        ImageView ivAvatar;
        TextView tvBlockNumber;
        TextView tvName;
        public View viewSeparator;
    }

    public class ChildViewHolder {
        TextView tvNumber;
        CheckBox checkBox;
    }


    private void setAvatar(final ImageView imageView, String imageUrl, final int pos) {
        if (imageUrl != null && !imageUrl.equalsIgnoreCase("")) {
            Picasso.with(mActivity).load(APIConstants.SERVER_URL + "/" + imageUrl).transform(new CircleTransform()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    if ((int) imageView.getTag() != pos) {
                        Picasso.with(mActivity).load(R.drawable.ic_man).transform(new CircleTransform()).into(imageView);
                    }
                }

                @Override
                public void onError() {
                    Picasso.with(mActivity).load(R.drawable.ic_man).transform(new CircleTransform()).into(imageView);

                }
            });


        } else {
            Picasso.with(mActivity).load(R.drawable.ic_man).transform(new CircleTransform()).into(imageView);

        }

    }

    public List<BlockNumber> getBlockedNumbers() {
        List<BlockNumber> mListBlockNumbers = new ArrayList<>();
        for (Map.Entry<int[], NumberModel> entry : blockInteractions.entrySet()) {
            String number = entry.getValue().getNumber();
            boolean isBlocked = entry.getValue().isBlocked();
            BlockNumber blockNumber = new BlockNumber(number, isBlocked);
            mListBlockNumbers.add(blockNumber);
        }


        return mListBlockNumbers;
    }


}
