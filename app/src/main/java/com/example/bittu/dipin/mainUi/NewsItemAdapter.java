package com.example.bittu.dipin.mainUi;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bittu.dipin.R;
import com.example.bittu.dipin.Website;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.NewsItemViewHolder>{
    Context mContext;
    List<Website> mWebsites;
    DrawerLayout mDrawerLayout;
    public class NewsItemViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.platform_image)
        ImageView platformImage;
        @InjectView(R.id.platform_title)
        TextView platformText;

        public NewsItemViewHolder(android.view.View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    public NewsItemAdapter(Context context, List<Website> websites, DrawerLayout drawerLayout){
        mContext = context;
        mWebsites = websites;
        mDrawerLayout = drawerLayout;
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        android.view.View view = LayoutInflater.from(mContext).inflate(R.layout.news_drawer_item, parent, false);
        return new NewsItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewsItemViewHolder holder, int position) {
        final Website currentWebsite = mWebsites.get(position);
        holder.platformImage.setImageDrawable(currentWebsite.getPlatformImage());
        holder.platformText.setText(currentWebsite.getPlatformTitle());
        holder.platformImage.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                String urlString = currentWebsite.getPlatformTitle().toLowerCase().replace(" ", "-");
                String baseUrl = "https://newsapi.org/v1/articles?source=" + urlString + "&apiKey=839b127083e848e188382abfc1e8ee16";
                SharedPreferences sharedPref = mContext.getSharedPreferences("sharedPlatform", 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(mContext.getString(R.string.pref_shared_website), baseUrl);
                editor.commit();
                mDrawerLayout.closeDrawer(Gravity.END);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mWebsites.size();
    }


}