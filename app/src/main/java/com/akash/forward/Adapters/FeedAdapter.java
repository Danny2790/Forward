package com.akash.forward.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akash.forward.Activities.CommentActivity;
import com.akash.forward.Models.Feed;
import com.akash.forward.R;
import com.akash.forward.Utility.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.akash.forward.Constants.ForwardConstant.FIREBASE_DB_LIKES;
import static com.akash.forward.Constants.ForwardConstant.POST_ID;

/**
 * Created by akash on 8/20/2017.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private final FirebaseAuth firebaseAuth;
    private ArrayList<Feed> feedList;
    private Context context;
    private String TAG = FeedAdapter.class.getSimpleName();
    private DatabaseReference mFirebaseDatabaseLikes;
    private Boolean mProcessLike = false;

    public FeedAdapter(Context context, ArrayList<Feed> feedList) {
        this.context = context;
        this.feedList = feedList;
        FirebaseDatabase mFirebaseDatabase = Utils.getDatabase();
        mFirebaseDatabaseLikes = mFirebaseDatabase.getReference(FIREBASE_DB_LIKES);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Feed feed = feedList.get(position);
        holder.textViewUsername.setText(feed.getUserInfo().getFirstName());
        Glide.with(context).load(feed.getImageUrl()).placeholder(R.drawable.placeholder).into(holder.imageViewFeed);
        Log.d(TAG, "onBindViewHolder:  post id " + feed.getPostId());
        holder.setLikeButton(feed.getPostId());
        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProcessLike = true;
                mFirebaseDatabaseLikes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProcessLike) {
                            if (dataSnapshot.child(feed.getPostId()).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                mFirebaseDatabaseLikes.child(feed.getPostId()).child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                mProcessLike = false;
                            } else {
                                mFirebaseDatabaseLikes.child(feed.getPostId()).child(firebaseAuth.getCurrentUser().getUid()).setValue(true);
                                mProcessLike = false;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.imageViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra(POST_ID, feed.getPostId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername, textViewLikeCount, textViewCommentCount;
        ImageView imageViewFeed, imageViewLike, imageViewComments;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewUsername = (TextView) itemView.findViewById(R.id.tv_user_name);
            textViewLikeCount = (TextView) itemView.findViewById(R.id.tv_like_count);
            textViewCommentCount = (TextView) itemView.findViewById(R.id.tv_comment_count);
            imageViewFeed = (ImageView) itemView.findViewById(R.id.iv_feed_image);
            imageViewLike = (ImageView) itemView.findViewById(R.id.iv_like);
            imageViewComments = (ImageView) itemView.findViewById(R.id.iv_comment);
        }

        public void setLikeButton(final String post_id) {
            mFirebaseDatabaseLikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_id).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                        imageViewLike.setImageResource(R.drawable.ic_thumb_up_red_24px);
                    } else {
                        imageViewLike.setImageResource(R.drawable.ic_thumb_up_black_24px);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
