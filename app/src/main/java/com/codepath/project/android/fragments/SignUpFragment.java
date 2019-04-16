package com.codepath.project.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.project.android.R;
import com.codepath.project.android.network.ParseHelper;
import com.codepath.project.android.utils.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SignUpFragment extends Fragment {

    @BindView(R.id.etFirstName) EditText etFirstName;
    @BindView(R.id.etLastName) EditText etLastName;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etConfirmPassword) EditText etConfirmPassword;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.btnSignUp) Button btnSignUp;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        unbinder = ButterKnife.bind(this, view);
        btnSignUp.setOnClickListener(v -> {
            ParseHelper.newUserSignUp(etFirstName.getText().toString(),
                    etPassword.getText().toString(),
                    etEmail.getText().toString());
            GeneralUtils.showSnackBar(getView(),
                    ParseHelper.PARSE_SIGNUP_SUCCESS_SNACKTOAST,
                    getActivity().getColor(R.color.colorGreen),
                    getActivity().getColor(R.color.colorGray));
            getActivity().finish();
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
