package com.flybits.samples.context;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.context.plugins.activity.ActivityProvider;
import com.flybits.core.api.context.plugins.battery.BatteryLifeProvider;
import com.flybits.core.api.exceptions.FeatureNotSupportedException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.models.User;
import com.flybits.core.api.utils.filters.LoginOptions;
import com.flybits.samples.context.utilities.ConnectivityUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (ConnectivityUtils.isOnline(this)) {
            Flybits.include(SplashActivity.this).isUserLoggedIn(true, new IRequestLoggedIn() {
                @Override
                public void onLoggedIn(User user) {
                    activateContext();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onNotLoggedIn() {
                    startLogin();
                }
            });

        } else {
           showAlertDialog(R.string.errorTitleNoInternet, R.string.errorMessageNoInternet, R.string.errorBtnPositive);
        }
    }

    private void startLogin() {

        LoginOptions filterLogin;

        /*
            Check to see if the Remember Me Token has been saved. If so, use the remember me token
            Otherwise, Prompt the user to Login.
         */

        filterLogin = new LoginOptions.Builder(SplashActivity.this)
                .loginAnonymously()
                .setRememberMeToken()
                .setDeviceOSVersion()
                .build();

        Flybits.include(SplashActivity.this).login(filterLogin, new IRequestCallback<User>() {

            @Override
            public void onSuccess(User me) {
                activateContext();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userID", me.id);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(String s) {}

            @Override
            public void onException(Exception e) {
                showAlertDialog(getString(R.string.errorTitleLogin), e.getMessage(), getString(R.string.errorBtnPositive));
            }

            @Override
            public void onCompleted() {}
        });
    }


    private void showAlertDialog(int title, int message, int buttonPositive){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(buttonPositive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SplashActivity.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showAlertDialog(String title, String message, String buttonPositive){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(buttonPositive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SplashActivity.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void activateContext(){
        try {
            ActivityProvider provider = new ActivityProvider(SplashActivity.this, 60000);
            Flybits.include(SplashActivity.this).activateContext(null, provider);

            BatteryLifeProvider provider2 = new BatteryLifeProvider(SplashActivity.this, 60000);
            Flybits.include(SplashActivity.this).activateContext(null, provider2);
        }catch (FeatureNotSupportedException exception){

        }
    }
}
