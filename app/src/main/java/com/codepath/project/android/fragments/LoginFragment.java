package com.codepath.project.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.project.android.R;
import com.codepath.project.android.activities.LoadingActivity;
import com.codepath.project.android.activities.LoginActivity;
import com.codepath.project.android.activities.NewLoginActivity;
import com.codepath.project.android.activities.SignUpActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginFragment extends Fragment {

    @BindView(R.id.tvLogIn) TextView tvLogIn;
    @BindView(R.id.btnSignUp) TextView btnSignUp;
    @BindView(R.id.btnFBLogin) TextView btnFBLogin;


    private Unbinder unbinder;

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
        add("user_friends");
    }};

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);

        tvLogIn.setOnClickListener(v1 -> {
            Intent intent = new Intent(getActivity(), NewLoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignUpActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        btnFBLogin.setOnClickListener(v -> {
            ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), mPermissions, (user, err) -> {
                if (err != null) {
                    Log.d("MyApp", "Uh oh. Error occurred" + err.toString());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                    getUserDetailsFromFB(user);
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    startNextActivity();
                }
            });
        });

        return view;
    }

    private void startNextActivity(){
        //Intent intent = new Intent(getActivity(), HomeActivity.class);
        Intent intent = new Intent(getActivity(), LoadingActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getUserDetailsFromFB(ParseUser user) {
        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,cover,picture.type(large)");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                response -> {
                    try {
                        String email = response.getJSONObject().getString("email");
                        String name = response.getJSONObject().getString("name");
                        JSONObject picture = response.getJSONObject().getJSONObject("picture");
                        JSONObject data = picture.getJSONObject("data");
                        String pictureUrl = data.getString("url");
                        String id = response.getJSONObject().getString("id");

                        if(response.getJSONObject().has("cover")) {
                            JSONObject cover = response.getJSONObject().getJSONObject("cover");
                            user.put("coverUrl", cover.getString("source"));
                        }
                        user.put("fbid", id);
                        user.setEmail(email);
                        user.setUsername(email);
                        user.put("firstName", name);
                        user.put("pictureUrl", pictureUrl);
                        user.save();
                        getFriendsDetailsFromFB(user);
                        startNextActivity();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
        ).executeAsync();
    }

    private void getFriendsDetailsFromFB(ParseUser user) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                response -> {
                    try {
                        JSONArray friendsArray = response.getJSONObject().getJSONArray("data");
                        if(friendsArray != null && friendsArray.length() > 0) {
                            ArrayList<String> ids = new ArrayList<>();
                            for(int i=0; i < friendsArray.length();i++) {
                                ids.add(friendsArray.getJSONObject(i).getString("id"));
                            }
                            user.put("fbFriends", ids);
                            user.save();
                            sendPushNotificationToFriends();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
        ).executeAsync();
    }

    public void sendPushNotificationToFriends() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("a", "b");
        ParseCloud.callFunctionInBackground("facebookNotifyFriends", parameters, (mapObject, e) -> {
            if (e == null){
                System.out.println("sent");
            }
        });
    }

}
