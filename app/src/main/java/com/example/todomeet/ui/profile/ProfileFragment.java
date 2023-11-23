package com.example.todomeet.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.todomeet.R;
import com.example.todomeet.login.LoginActivity;
import com.kakao.sdk.user.UserApiClient;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView userNickname;
    private TextView userEmail;
    private Button signoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImageView = view.findViewById(R.id.profileImageView);
        userNickname = view.findViewById(R.id.userNickname);
        userEmail = view.findViewById(R.id.userEmail);
        signoutButton = view.findViewById(R.id.signoutButton);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String profileImageUrl = sharedPref.getString("profileImageUrl", "");
        String userName = sharedPref.getString("userName", "");
        String email = sharedPref.getString("userEmail", "");

        Glide.with(this)
                .load(profileImageUrl)
                .circleCrop()
                .into(profileImageView);

        userNickname.setText(userName);
        userEmail.setText(email);

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().clear().apply();

                UserApiClient.getInstance().logout(error -> {
                    if (error == null) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    return null;
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
}};
