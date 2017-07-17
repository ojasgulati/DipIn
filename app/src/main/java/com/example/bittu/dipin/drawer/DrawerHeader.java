package com.example.bittu.dipin.drawer;


import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bittu.dipin.mainUi.MainActivity;
import com.example.bittu.dipin.R;
import com.example.bittu.dipin.RoundImageTransform;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader {
    Context mContext;
    public DrawerHeader(Context context){
        mContext = context;
    }

        @View(R.id.profileImageView)
        private ImageView profileImage;

        @View(R.id.nameTxt)
        private TextView nameTxt;

        @View(R.id.emailTxt)
        private TextView emailTxt;

        @Resolve
        private void onResolved() {
            if(MainActivity.mUsername != null)
            nameTxt.setText(MainActivity.mUsername);
            if(MainActivity.mUserEmailId != null)
            emailTxt.setText(MainActivity.mUserEmailId);
            if(MainActivity.mUserPic != null) {
                Glide.with(mContext)
                        .load(MainActivity.mUserPic)
                        .transform(new RoundImageTransform(mContext))
                        .into(profileImage);

            }
        }
    }

