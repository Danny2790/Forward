package com.akash.forward.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.akash.forward.Adapters.FeedAdapter;
import com.akash.forward.Models.Feed;
import com.akash.forward.R;
import com.akash.forward.Utility.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.akash.forward.Constants.ForwardConstant.FIREBASE_DB_REF;

public class FeedActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private String TAG = FeedActivity.class.getSimpleName();
    private ArrayList<Feed> feedList = new ArrayList<>();
    private FeedAdapter feedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        RecyclerView feedRecyclerView = (RecyclerView) findViewById(R.id.rv_feed);
        feedAdapter = new FeedAdapter(this, feedList);

        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.setHasFixedSize(true);
        feedRecyclerView.setAdapter(feedAdapter);
        Button button = (Button) findViewById(R.id.btn_upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchUploadActivity();
            }
        });
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = Utils.getDatabase();
            mFirebaseDatabaseReference = mFirebaseDatabase.getReference(FIREBASE_DB_REF);
            setupFirebaseDbListener();
        }
    }

    private void launchUploadActivity() {
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }

    private void setupFirebaseDbListener() {
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: datanspashot " + " key : " + dataSnapshot.getKey() + " value :" + dataSnapshot.getValue());
                //getAllFeeds(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

        mFirebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: data snapshot value : " + dataSnapshot.getValue(Feed.class));
                addFeedToList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: ");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved: ");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });
    }

    private void addFeedToList(DataSnapshot dataSnapshot) {
        Feed feed = dataSnapshot.getValue(Feed.class);
        feedList.add(feed);
        feedAdapter.notifyItemInserted(feedList.size() - 1);
    }

    private void getAllFeeds(DataSnapshot dataSnapshot) {
        Feed feedItem;
        feedList.clear();
        for (DataSnapshot singleShot : dataSnapshot.getChildren()) {
            Log.d(TAG, "onDataChange: child :  key : " + singleShot.getKey() + " value : " + singleShot.getValue());
            feedItem = singleShot.getValue(Feed.class);
            feedList.add(feedItem);
            feedAdapter.notifyDataSetChanged();
        }
    }
}
