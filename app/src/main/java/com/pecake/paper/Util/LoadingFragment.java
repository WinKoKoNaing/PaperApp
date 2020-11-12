package com.pecake.paper.Util;

import android.app.ProgressDialog;
import android.content.Context;

import com.pecake.paper.R;

public class LoadingFragment {
    Context context;
    ProgressDialog progressDialog;

    public LoadingFragment(Context context) {
        this.context = context;
    }

    public void loading(String str) {
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(str);
        progressDialog.setCancelable(false);

    }
    public void show(){
        progressDialog.show();
    }
    public void hide(){
        progressDialog.hide();
    }
    public boolean isShown(){
        return progressDialog.isShowing();
    }
}
