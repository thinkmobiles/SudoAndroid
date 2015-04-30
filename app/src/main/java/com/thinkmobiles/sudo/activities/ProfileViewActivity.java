package com.thinkmobiles.sudo.activities;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thinkmobiles.sudo.R;
import com.thinkmobiles.sudo.Utils;
import com.thinkmobiles.sudo.adapters.ProfileViewNumbersAdapter;
import com.thinkmobiles.sudo.custom_views.NonScrollListView;
import com.thinkmobiles.sudo.global.CircleTransform;
import com.thinkmobiles.sudo.models.addressbook.NumberModel;
import com.thinkmobiles.sudo.models.addressbook.UserModel;

import java.util.List;

/**
 * Created by omar on 23.04.15.
 */
public class ProfileViewActivity extends BaseProfileActivity {

    private TextView tvUserFirstName;
    private ImageView ivAvatar;
    private NonScrollListView lvNumbers;
    private ProfileViewNumbersAdapter profileViewNumbersAdapter;

    private UserModel thisUserModel;
    private RelativeLayout rlImage;
    private ScrollView llMain;
    private String firstName, urlAvatar;
    private List<NumberModel> myNumberList;

    public static final String EXTRA_IMAGE = "DetailActivity:image";
    private Target mTarget;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_view_profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadUserModel();
        initComponent();
        loadContent();
        setContent();
        initTarget();
        setImages();



    }

    private void setImages() {
        ViewCompat.setTransitionName(ivAvatar, EXTRA_IMAGE);
        getSupportActionBar().setTitle("");

        Picasso.with(this).load(getIntent().getStringExtra(EXTRA_IMAGE)).transform(new CircleTransform()).into(ivAvatar);
        Picasso.with(this).load(getIntent().getStringExtra(EXTRA_IMAGE)).into(mTarget);

    }

    private void setContent() {
        if (Utils.checkString(firstName)) {

            tvUserFirstName.setText(firstName);
        }

   /*     if (Utils.checkString(lastName)) {
            tvUserLastName.setText(firstName);
        }
*/

        if (Utils.checkList(myNumberList)) {
            profileViewNumbersAdapter = new ProfileViewNumbersAdapter(this, myNumberList);
            lvNumbers.setAdapter(profileViewNumbersAdapter);
        }

    }

    private void loadContent() {
        firstName = thisUserModel.getCompanion();
        urlAvatar = thisUserModel.getAvatar();
        myNumberList = thisUserModel.getNumbers();
    }


    private void initComponent() {
        ivAvatar            = (ImageView) findViewById(R.id.image);
        tvUserFirstName     = (TextView) findViewById(R.id.tvUserFirstName_AVC);
        rlImage             = (RelativeLayout) findViewById(R.id.rlImageProfile_AVP);
        llMain              = (ScrollView) findViewById(R.id.svData_AVP);
        lvNumbers           = (NonScrollListView) findViewById(R.id.lvPhoneNumbersView_AVC);

    }


    private void loadUserModel() {
        thisUserModel = (UserModel) getIntent().getExtras().getBundle(BaseProfileActivity.USER_MODEL).getSerializable(BaseProfileActivity.USER_MODEL);
        Log.d(TAG, thisUserModel.getCompanion());
    }


    public static void launch(Activity activity, View transitionView, UserModel userModel) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);

        Intent intent = new Intent(activity, ProfileViewActivity.class);
        intent.putExtra(EXTRA_IMAGE, userModel.getAvatar());

        Bundle b = new Bundle();
        b.putSerializable(BaseProfileActivity.USER_MODEL, userModel);
        intent.putExtra(BaseProfileActivity.USER_MODEL, b);


        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_view_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        if (id == R.id.action_edit) {
            launchEditProfileActivity();

            return true;

        }
        return super.onOptionsItemSelected(item);

    }

    private void launchEditProfileActivity() {
        ProfileEditActivity.launch(this, thisUserModel);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(" activity result", String.valueOf(resultCode));
        if (resultCode != -1) return;
        if (requestCode == ProfileEditActivity.START_EDIT_PROFILE_ACTIVITY_CODE) {
            reloadUserModel(data);
            loadContent();
            reloadAvatar();
            setContent();

        }

    }

    public void reloadUserModel(Intent intent) {
        thisUserModel = (UserModel) intent
                .getExtras()
                .getBundle(BaseProfileActivity.USER_MODEL)
                .getSerializable(BaseProfileActivity.USER_MODEL);

    }

    public void reloadAvatar() {
        Picasso.with(this).load(urlAvatar).into(ivAvatar);
    }

    private void changeViewColor(final Bitmap _bitmap ) {
        Palette palette = Palette.generate(_bitmap);
        final int initialColor      = getResources().getColor(R.color.colorWhite);
        final int finalColor        = palette.getVibrantColor(0x009900);
        final int stausBarColor     = palette.getDarkVibrantColor(0x009900);

          setStatusBarColor(stausBarColor);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();
                int ImageBlended = blendColors(initialColor, finalColor, position);
                int mainBlended = blendColors(initialColor, finalColor, position);

                // Apply blended color to the view.
                rlImage.setBackgroundColor(ImageBlended);
                llMain.setBackgroundColor(mainBlended);
                llMain.getBackground().setAlpha(60);
            }
        });

        anim.setDuration(2000).start();
    }


    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }


    private void initTarget() {

        mTarget = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                changeViewColor(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {


            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

            ;
        };
    }


    @Override
    public void onDestroy() {
        Picasso.with(this).cancelRequest(mTarget);
        super.onDestroy();
    }

}