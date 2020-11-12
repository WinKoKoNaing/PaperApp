package com.pecake.paper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by minthihaaung on 5/17/18.
 */

public class MinPreference extends androidx.preference.Preference {

    private Context ctx;

    public MinPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    public MinPreference(Context context) {
        super(context);
        ctx = context;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        setCustomStyle(holder.itemView);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setCustomStyle(View view) {

        RippleDrawable drawable = (RippleDrawable) ctx.getDrawable(R.drawable.preference_ripple);
        view.setBackground(drawable);

    }

}
