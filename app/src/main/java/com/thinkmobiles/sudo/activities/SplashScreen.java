package com.thinkmobiles.sudo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.thinkmobiles.sudo.R;
import com.thinkmobiles.sudo.core.rest.RetrofitAdapter;
import com.thinkmobiles.sudo.global.App;
import com.thinkmobiles.sudo.models.AuthenticatedModel;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SplashScreen extends Activity {

    private Callback<AuthenticatedModel> mAuthenticatedCB;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setNextActivity(LoginActivity.class);
        initAuthenticatedCB();
        authenticatedRequest();
    }

    private void initAuthenticatedCB() {
        mAuthenticatedCB = new Callback<AuthenticatedModel>() {
            @Override
            public void success(AuthenticatedModel authenticatedModel, Response response) {

                App.setuId(authenticatedModel.getuId());
                setNextActivity(Main_Activity.class);
                showNewActivity();
            }

            @Override
            public void failure(RetrofitError error) {

                    setNextActivity(LoginActivity.class);
                    showNewActivity();

            }
        };
    }

    private void authenticatedRequest() {
        RetrofitAdapter.getInterface().isAuthenticated(mAuthenticatedCB);
    }

    private void showNewActivity(){
        ScheduledExecutorService worker =
                Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            public void run() {
                openActivity();
            }
        };
        worker.schedule(task, 2, TimeUnit.SECONDS);
    }

    private void openActivity() {
        startActivity(mIntent);
        finish();
    }

    private void setNextActivity(Class _class){
        mIntent = new Intent(this, _class);
    }
}
