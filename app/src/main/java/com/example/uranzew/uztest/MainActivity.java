package com.example.uranzew.uztest;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    LoginButton loginButton;
    CallbackManager callbackManager;
    TextView textView;
    ImageView imageView;
    Bitmap bitmap;
    ProfilePictureView profilePictureView;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //fucku
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        textView = (TextView)findViewById(R.id.text);
        profilePictureView = (ProfilePictureView)findViewById(R.id.profile);
        loginButton.setReadPermissions(Arrays.asList(EMAIL,"public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code               String userid = loginResult.getAccessToken().getUserId();
                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.v("facebook - profile", currentProfile.getFirstName());
                            profilePictureView.setProfileId(currentProfile.getId());
                            mProfileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getFirstName());
                    profilePictureView.setProfileId(profile.getId());
                }
                AccessToken accessToken = loginResult.getAccessToken();
                //Log.i("fuck", profile.getFirstName());



                Log.i("fuck","sda");
                // profilePictureView.setProfileId(profile.getId());

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                displayUserInfo(object);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name, last_name, email, id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                textView.setText("Login Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        printKeyHash();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        String first_name, last_name, id;
        first_name ="";
        last_name ="";
        id ="";
        Profile profile = Profile.getCurrentProfile();
        if(profile != null){
            profilePictureView.setProfileId(profile.getId());
            first_name = profile.getFirstName();
            last_name = profile.getLastName();
            id = profile.getId();
        }
        textView.setText(first_name+"\n"+ last_name+"\n"+id);
    }

    public void displayUserInfo(JSONObject object){
        String first_name, last_name, email, id,image;
        first_name ="";
        last_name ="";
        email ="";
        image ="";
        id ="";

        try {
            first_name = object.getString("first_name");
            last_name = object.getString("last_name");
            email = object.getString("email");
            id = object.getString("id");


        }catch (JSONException e){
            e.printStackTrace();
        }
        textView.setText(first_name+"\n"+ last_name+"\n"+ email+"\n"+id);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.uranzew.uztest",
                    PackageManager.GET_SIGNATURES);
            for(Signature signature :info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("fuck", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
