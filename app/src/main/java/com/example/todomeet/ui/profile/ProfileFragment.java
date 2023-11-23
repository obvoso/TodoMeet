package com.example.todomeet.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.todomeet.R;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView userNickname;
    private TextView userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImageView = view.findViewById(R.id.profileImageView);
        userNickname = view.findViewById(R.id.userNickname);
        userEmail = view.findViewById(R.id.userEmail);
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
}};
