# Context Sample
This is a sample application that is designed to demonstrate the context plugins featured inside the Flybits SDK. Through this sample, a developer will gain an understanding on how the context plugins of the Flybits SDK are managed.

# Features
This sample demonstrates numerous Flybits features that allow application developers to fully understand the Flybits capabilities. Below, a list of these features is discussed in detail, and code examples are provided to help with any integration. Additionally, comments have been included in the code itself with further explanation.

###1. Setup
The setup of the Flybits SDK is performed in 2-steps. The first step is adding the Flybits SDK to the build.gradle file within your project, as well as the module that will be using the Flybits SDK. How the SDK is included in both files is demonstrated below;

Project's gradle.build file:

```
allprojects {
  repositories {
    maven {
      url "https://flybits-maven.s3.amazonaws.com/releases/"
    }
    jcenter()
  }
}
```

Module's gradle.build file:
```
dependencies {
  ...
  compile 'com.flybits.fbcore:library:2.4.2'
  ...
}
```

###2. Initialization of SDK
The Flybits SDK is initialized within the `Application` class of the application. In this application case the `Application` class can be in the [SampleApplication](../master/app/src/main/java/com/flybits/samples/context/utilities/SampleApplication.java) class. The code snippet can be seen below:
```java
FlybitsOptions options = new FlybitsOptions.Builder(this)
  //Indicate whether or not exceptions/network traffic should be displayed in the logcat
  .setDebug(true)
  .build();

//Initialize the FlybitsOptions
Flybits.include(this).initialize(options);
```

Additional options may be added to the `FlybitsOptions` builder object, however, for this sample it is not necessary.

### 3. Login
Once the SDK is set up and initialized, applications will need to log the device into the Flybits server. This is required because all contextual information is linked to a specific user within the Flybits ecosystem. In most cases, application should login **anonymously** as the identity of the user is not needed. This sample application logs the user into the Flybits system inside the [SplashActivity](../master/app/src/main/java/com/flybits/samples/context/SplashActivity.java) class. A small code snippet of this process can be seen below.
```java
//Log the application into Flybits anonymously
LoginOptions filterLogin = new LoginOptions.Builder(SplashActivity.this)
    .loginAnonymously()
    .setRememberMeToken()
    .setDeviceOSVersion()
    .build();

Flybits.include(SplashActivity.this).login(filterLogin, new IRequestCallback<User>() {

    @Override
    public void onSuccess(User me) {}

    @Override
    public void onFailed(String s) {}

    @Override
    public void onException(Exception e) {}

    @Override
    public void onCompleted() {}
});
```

### 4. Context Registration
The purpose of this sample application is to demonstrate how context plugins can be activated within the Flybits SDK. The process of activating context plugins is done in 2 steps. The first step includes the registration of the context plugin within the application's [AndroidManifest](../master/app/src/main/AndroidManifest.xml).
All plugins must be registered here. An example of how to activate a plugin can be seen below with the **Network Connectivity** plugin.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.flybits.samples.context" >

    <!-- Network Permissions -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    ...
    <application... >
        ...
        <!-- Services for Network -->

        <!-- Mandatory: Background mode is enabled (less frequent) -->
        <service android:name="com.flybits.core.api.context.v2.plugins.network.NetworkBackgroundService"
            android:permission="com.google.android.gms.permission.BIND_NETWORK_TASK_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="com.google.android.gms.gcm.ACTION_TASK_READY"/>
            </intent-filter>
        </service>

        <!-- Optional: Only if foreground mode is enabled (more frequent) -->
        <service android:name="com.flybits.core.api.context.v2.plugins.network.NetworkForegroundService" />
        ...
    </application>

</manifest>
```

For more information on how the remaining Context Plugins are registered, please visit the [AndroidManifest](../master/app/src/main/AndroidManifest.xml) file.

The second phase of context activation is activating the context plugin within the application itself. This must occur when the application has successfully registered as demonstrated in the [SplashActivity](../master/app/src/main/java/com/flybits/samples/context/SplashActivity.java) class. The corresponding **Network Connectivity** plugin can be
seen below.
```java

FlybitsContextPlugin pluginNetwork = new FlybitsContextPlugin.Builder()
  .setPlugin(AvailablePlugins.NETWORK_CONNECTIVITY)
  .setRefreshTime(60)
  .setRefreshTimeFlex(60)
  .build();
ContextManager.include(SplashActivity.this).register(pluginNetwork);
```

### 5. Context Retrieval
Once you have registered your contextual plugins within the `AndroidManifest` file, as well as activated them after your application successfully logs in, the Flybits SDK will then periodically update the information needed for each plugin. The application can then use this information to remove/add content based on the needs of the application. New contextual information is obtained through a Broadcast sent by the SDK. In order to receive these, applications will need to register to the broadcast using the following IntentFilter action.

```java
IntentFilter filter = new IntentFilter("CONTEXT_UPDATED");
```

Once you have register the context updated broadcast you will be receive broadcast intents through your **BroadcastReceiver** which will need to be implemented. An example of how a BroadcastReceiver should look like for updating contextual values can be seen in the [MainActivity](../master/app/src/main/java/com/flybits/samples/context/MainActivity.java) class as displayed below.

```java
BroadcastReceiver receiver = new BroadcastReceiver() {
  @Override
  public void onReceive(Context context, Intent intent) {

      Bundle bundle = intent.getExtras();
      FragmentManager manager = getFragmentManager();
      ContextFragment fragment = (ContextFragment) manager.findFragmentByTag(CONTEXT_FRAGMENT_TAG);

      if (fragment != null) {
          if (bundle.containsKey("CONTEXT_TYPE")) {

              if (bundle.getString("CONTEXT_TYPE").equals(AvailablePlugins.BATTERY.getKey())) {
                  BatteryData data = bundle.getParcelable("CONTEXT_OBJ");
                  if (data != null) {
                      Log.d("Testing", data.toString());
                      fragment.onNewData(data);
                  }
              }
              else if (bundle.getString("CONTEXT_TYPE").equals(AvailablePlugins.CARRIER.getKey())) {
                  CarrierData data = bundle.getParcelable("CONTEXT_OBJ");
                  if (data != null) {
                      Log.d("Testing", data.toString());
                      fragment.onNewData(data);
                  }
              }
              else if (bundle.getString("CONTEXT_TYPE").equals(AvailablePlugins.LOCATION.getKey())) {
                  LocationData data = bundle.getParcelable("CONTEXT_OBJ");
                  if (data != null) {
                      Log.d("Testing", data.toString());
                      fragment.onNewData(data);
                  }
              }
              else if (bundle.getString("CONTEXT_TYPE").equals(AvailablePlugins.LANGUAGE.getKey())) {
                  LanguageContextData data = bundle.getParcelable("CONTEXT_OBJ");
                  if (data != null) {
                      Log.d("Testing", data.toString());
                      fragment.onNewData(data);
                  }
              }
              else if (bundle.getString("CONTEXT_TYPE").equals(AvailablePlugins.FITNESS.getKey())) {
                  FitnessContextData data = bundle.getParcelable("CONTEXT_OBJ");
                  if (data != null) {
                      Log.d("Testing", data.toString());
                      fragment.onNewData(data);
                  }
              }
              else if (bundle.getString("CONTEXT_TYPE").equals(AvailablePlugins.ACTIVITY.getKey())) {
                  ActivityData data = bundle.getParcelable("CONTEXT_OBJ");
                  if (data != null) {
                      Log.d("Testing", data.toString());
                      fragment.onNewData(data);
                  }
              }
              else if (bundle.getString("CONTEXT_TYPE").equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey())) {
                  NetworkData data = bundle.getParcelable("CONTEXT_OBJ");
                  if (data != null) {
                      Log.d("Testing", data.toString());
                      fragment.onNewData(data);
                  }
              }
          }
      }
  }
};
```