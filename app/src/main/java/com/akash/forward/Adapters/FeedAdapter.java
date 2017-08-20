package com.akash.forward.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akash.forward.Models.Feed;
import com.akash.forward.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by akash on 8/20/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private ArrayList<Feed> feedList;
    private Context context;
    private String TAG = FeedAdapter.class.getSimpleName();

    public FeedAdapter(Context context, ArrayList<Feed> feedList) {
        this.context = context;
        this.feedList = feedList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feed feed = feedList.get(position);
        holder.textViewUsername.setText(feed.getUserInfo().getFirstName());
        Log.d(TAG, "onBindViewHolder: " + feed.getImageUrl());
        Glide.with(context)
                .load(feed.getImageUrl())
                .into(holder.imageViewFeed);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername, textViewLikeCount, textViewCommentCount;
        ImageView imageViewFeed;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewUsername = (TextView) itemView.findViewById(R.id.tv_user_name);
            textViewLikeCount = (TextView) itemView.findViewById(R.id.tv_like_count);
            textViewCommentCount = (TextView) itemView.findViewById(R.id.tv_comment_count);
            imageViewFeed = (ImageView) itemView.findViewById(R.id.iv_feed_image);
        }
    }
}
