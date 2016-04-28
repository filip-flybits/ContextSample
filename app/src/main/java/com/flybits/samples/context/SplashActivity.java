package com.flybits.samples.context;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.context.plugins.activity.ActivityProvider;
import com.flybits.core.api.context.plugins.battery.BatteryLifeProvider;
import com.flybits.core.api.context.plugins.beacon.BeaconProvider;
import com.flybits.core.api.context.plugins.carrier.CarrierProvider;
import com.flybits.core.api.context.plugins.language.LanguageProvider;
import com.flybits.core.api.context.plugins.location.LocationProvider;
import com.flybits.core.api.context.plugins.network.NetworkProvider;
import com.flybits.core.api.exceptions.FeatureNotSupportedException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.models.User;
import com.flybits.core.api.utils.filters.LoginOptions;
import com.flybits.samples.context.utilities.ConnectivityUtils;

public class SplashActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkForLoginPermissions();
    }

    private void checkForLoginPermissions() {

        if (Build.VERSION.SDK_INT < 23) {
            attemptToLogin(true);
        } else {
            if (ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);

            } else if (ContextCompat.checkSelfPermission(SplashActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                attemptToLogin(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                boolean isLocationActivated = (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                attemptToLogin(isLocationActivated);
                return;
            }
        }
    }

    private void attemptToLogin(final boolean isLocationActivated) {
        if (ConnectivityUtils.isOnline(this)) {
            Flybits.include(SplashActivity.this).isUserLoggedIn(true, new IRequestLoggedIn() {
                @Override
                public void onLoggedIn(User user) {
                    activateContext(isLocationActivated);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onNotLoggedIn() {
                    startLogin(isLocationActivated);
                }
            });

        } else {
            showAlertDialog(R.string.errorTitleNoInternet, R.string.errorMessageNoInternet, R.string.errorBtnPositive);
        }
    }

    private void startLogin(final boolean isLocationActivated) {

        LoginOptions filterLogin;
        filterLogin = new LoginOptions.Builder(SplashActivity.this)
                .loginAnonymously()
                .setRememberMeToken()
                .setDeviceOSVersion()
                .build();

        Flybits.include(SplashActivity.this).login(filterLogin, new IRequestCallback<User>() {

            @Override
            public void onSuccess(User me) {
                activateContext(isLocationActivated);

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

    private void activateContext(final boolean isLocationActivated){
        try {
            ActivityProvider provider = new ActivityProvider(SplashActivity.this, 60000);
            Flybits.include(SplashActivity.this).activateContext(null, provider);

            BatteryLifeProvider provider2 = new BatteryLifeProvider(SplashActivity.this, 60000);
            Flybits.include(SplashActivity.this).activateContext(null, provider2);

            CarrierProvider provider3 = new CarrierProvider(SplashActivity.this, 60000);
            Flybits.include(SplashActivity.this).activateContext(null, provider3);

            LanguageProvider provider4 = new LanguageProvider(SplashActivity.this, 60000);
            Flybits.include(SplashActivity.this).activateContext(null, provider4);

            NetworkProvider provider5 = new NetworkProvider(SplashActivity.this, 60000);
            Flybits.include(SplashActivity.this).activateContext(null, provider5);

            if (isLocationActivated){
                LocationProvider provider6 = new LocationProvider(SplashActivity.this, 60000);
                Flybits.include(SplashActivity.this).activateContext(null, provider6);

                BeaconProvider provider7 = new BeaconProvider(SplashActivity.this, 60000);
                Flybits.include(SplashActivity.this).activateContext(null, provider7);
            }
        }catch (FeatureNotSupportedException exception){

        }
    }
}
