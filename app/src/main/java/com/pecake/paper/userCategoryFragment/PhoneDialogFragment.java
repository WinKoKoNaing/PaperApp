package com.pecake.paper.userCategoryFragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pecake.paper.R;

public class PhoneDialogFragment extends DialogFragment {
    setPhoneLoginClickListener loginClickListener;
    View rootView;
    EditText editTextPhone;
    public void setLoginClickListener(setPhoneLoginClickListener loginClickListener) {
        this.loginClickListener = loginClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.phone_dialog_fragment, container, false);
        editTextPhone = rootView.findViewById(R.id.etPhone);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnPhoneLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClickListener.onClick(editTextPhone.getText().toString().trim());
            }
        });
    }
    public interface setPhoneLoginClickListener {
        public void onClick(String phone);
    }
}


