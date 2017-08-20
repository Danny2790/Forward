package com.akash.forward.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akash.forward.Models.Feed;
import com.akash.forward.R;

import java.util.ArrayList;

/**
 * Created by akash on 8/20/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private ArrayList<Feed> feedList;

    public FeedAdapter(ArrayList<Feed> feedList) {
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
