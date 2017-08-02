package com.example.bittu.dipin.drawer;


import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bittu.dipin.Favorites;
import com.example.bittu.dipin.Platforms;
import com.example.bittu.dipin.R;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_HOME = 1;
    public static final int DRAWER_MENU_ITEM_PLATFORMS = 2;
    public static final int DRAWER_MENU_ITEM_FAVORITES = 3;
    public static final int DRAWER_MENU_ITEM_RATE_US = 4;
    public static final int DRAWER_MENU_ITEM_FEEDBACK = 5;
    public static final int DRAWER_MENU_ITEM_SHARE_APP = 6;
    public static final int DRAWER_MENU_ITEM_LOGOUT = 7;
    public static final int DRAWER_MENU_ITEM_BREAK = 8;
    public static final int DRAWER_MENU_ITEM_SETTINGS = 9;

    private int mMenuPosition;
    private Context mContext;
    private DrawerCallBack mCallBack;

    @View(R.id.mainView)
    LinearLayout mainView;

    @View(R.id.itemNameTxt)
    private TextView itemNameTxt;

    @View(R.id.itemIcon)
    private ImageView itemIcon;

    @View(R.id.line_break)
    private android.view.View lineBreak;

    public DrawerMenuItem(Context context, int menuPosition) {
        mContext = context;
        mMenuPosition = menuPosition;
    }

    @Resolve
    private void onResolved() {
        switch (mMenuPosition) {
            case DRAWER_MENU_ITEM_HOME:
                lineBreak.setVisibility(android.view.View.GONE);
                itemNameTxt.setText(R.string.menu_item_home);
                itemIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_home_black_18dp));
                break;
            case DRAWER_MENU_ITEM_PLATFORMS:
                lineBreak.setVisibility(android.view.View.GONE);
                itemNameTxt.setText(R.string.menu_item_platform);
                itemIcon.setImageDrawable(mContext.getDrawable(R.drawable.icons8_news));
                break;
            case DRAWER_MENU_ITEM_FAVORITES:
                lineBreak.setVisibility(android.view.View.GONE);
                itemNameTxt.setText(R.string.menu_item_favorites);
                itemIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_favorite_black_18dp));
                break;
            case DRAWER_MENU_ITEM_RATE_US:
                lineBreak.setVisibility(android.view.View.GONE);
                itemNameTxt.setText(R.string.menu_item_rate_us);
                itemIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_star_rate_black_18dp));
                break;
            case DRAWER_MENU_ITEM_FEEDBACK:
                lineBreak.setVisibility(android.view.View.GONE);
                itemNameTxt.setText(R.string.menu_item_feedback);
                itemIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_feedback_black_18dp));
                break;
            case DRAWER_MENU_ITEM_SHARE_APP:
                lineBreak.setVisibility(android.view.View.GONE);
                itemNameTxt.setText(R.string.menu_item_share_app);
                itemIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_share_black_18dp));
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                lineBreak.setVisibility(android.view.View.GONE);
                itemIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_exit_to_app_black_18dp));
                itemNameTxt.setText(R.string.menu_item_logout);
                break;
            case DRAWER_MENU_ITEM_BREAK:
                mainView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
                itemIcon.setVisibility(android.view.View.GONE);
                itemNameTxt.setVisibility(android.view.View.GONE);
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                lineBreak.setVisibility(android.view.View.GONE);
                itemIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_settings_black_18dp));
                itemNameTxt.setText(R.string.menu_item_settings);
                break;
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemClick() {
        switch (mMenuPosition) {
            case DRAWER_MENU_ITEM_PLATFORMS:
                mContext.startActivity(new Intent(mContext, Platforms.class));
                if (mCallBack != null) mCallBack.onPlatformsSelected();
                break;
            case DRAWER_MENU_ITEM_FAVORITES:
                mContext.startActivity(new Intent(mContext, Favorites.class));
                if (mCallBack != null) mCallBack.onFavoritesSelected();
                break;
        }
    }

    public void setDrawerCallBack(DrawerCallBack callBack) {
        mCallBack = callBack;
    }

    public interface DrawerCallBack {

        void onPlatformsSelected();

        void onFavoritesSelected();

    }
}