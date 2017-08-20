package com.akash.forward.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.akash.forward.Models.Comment;
import com.akash.forward.Models.UserInfo;
import com.akash.forward.R;
import com.akash.forward.Utility.SPManager;
import com.akash.forward.Utility.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.akash.forward.Constants.ForwardConstant.FIREBASE_DB_COMMENTS;
import static com.akash.forward.Constants.ForwardConstant.POST_ID;


public class CommentActivity extends AppCompatActivity {
    String TAG = CommentActivity.class.getSimpleName();
    private EditText editTextComment;
    private Button buttonPostactive;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseComments;
    private String postId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        editTextComment = (EditText) findViewById(R.id.et_comment);
        buttonPostactive = (Button) findViewById(R.id.btn_comment_active);
        postId = getIntent().getStringExtra(POST_ID);

        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = Utils.getDatabase();
            mFirebaseDatabaseComments = mFirebaseDatabase.getReference(FIREBASE_DB_COMMENTS);
            firebaseAuth = FirebaseAuth.getInstance();
        }
        setupComment();
        setupFirebaseComment();
    }

    private void setupFirebaseComment() {
        mFirebaseDatabaseComments.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getValue());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setupComment() {
        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    disablePost();
                } else {
                    enablePost();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonPostactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });


    }

    private void disablePost() {
        buttonPostactive.setTextColor(Color.BLACK);
        buttonPostactive.setBackgroundColor(getResources().getColor(R.color.darkGrey));
        buttonPostactive.setEnabled(false);
    }

    private void enablePost() {
        buttonPostactive.setTextColor(Color.WHITE);
        buttonPostactive.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        buttonPostactive.setEnabled(true);
    }

    private void addComment() {
        String message = editTextComment.getText().toString();
        if (message.length() != 0) {
            UserInfo userInfo = SPManager.getUserInfo(this);
            Comment comment = new Comment();
            comment.setUserId(userInfo.getId());
            comment.setUserName(userInfo.getFirstName());
            comment.setText(message);
            mFirebaseDatabaseComments.child(postId).push().setValue(comment);
            editTextComment.setText(" ");
        }
    }
}
