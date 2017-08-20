package com.akash.forward.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.akash.forward.Models.Feed;
import com.akash.forward.Models.UserInfo;
import com.akash.forward.R;
import com.akash.forward.Utility.SPManager;
import com.akash.forward.Utility.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static com.akash.forward.Constants.ForwardConstant.FIREBASE_DB_REF;
import static com.akash.forward.Constants.ForwardConstant.SELECT_PICTURE;
import static com.akash.forward.Constants.ForwardConstant.SERVER_ERROR;
import static com.akash.forward.Constants.ForwardConstant.STORAGE_REF;
import static com.akash.forward.Constants.ForwardConstant.UPLOAD_FAILED;

public class UploadActivity extends AppCompatActivity {

    private Uri filePath;
    private ImageView imageviewPreview;
    String TAG = UploadActivity.class.getSimpleName();
    FirebaseStorage storage;
    private UploadTask uploadTask;
    private FirebaseDatabase mFirebaseDatabase;
    private ProgressDialog progressDialog;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = Utils.getDatabase();
            mFirebaseDatabaseReference = mFirebaseDatabase.getReference(FIREBASE_DB_REF);
        }
        progressDialog = new ProgressDialog(this);
        storage = FirebaseStorage.getInstance();
        Button buttonGallery = (Button) findViewById(R.id.btn_gallery);
        imageviewPreview = (ImageView) findViewById(R.id.iv_image_preview);
        Button buttonUpload = (Button) findViewById(R.id.bt_upload);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });
    }

    public void uploadImage() {
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference reference = storage.getReference(STORAGE_REF).child(filePath.getLastPathSegment());
        uploadTask = reference.putFile(filePath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Uri downloaduri = taskSnapshot.getDownloadUrl();
                addPost(downloaduri);
                Log.d(TAG, "onSuccess: uri  : " + downloaduri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Utils.showMessage(UploadActivity.this, UPLOAD_FAILED);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
    }

    private void addPost(Uri downloaduri) {
        Feed feed = new Feed();
        UserInfo userInfo = SPManager.getUserInfo(this);
        DatabaseReference dbRef = mFirebaseDatabaseReference.push();
        Log.d(TAG, "addPost:  post key :" + dbRef.getKey());
        feed.setPostId(dbRef.getKey());
        feed.setUserInfo(userInfo);
        feed.setImageUrl(downloaduri.toString());
        dbRef.setValue(feed, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                //if successfull : databaserror = null
                //if fail : database will not null display error message
                Log.d(TAG, "onComplete: " + databaseError);
                if (databaseError == null) {
                    finish();
                } else {
                    String message = SERVER_ERROR;
                    if (databaseError.getMessage() != null) {
                        message = databaseError.getMessage();
                    }
                    Utils.showMessage(UploadActivity.this, message);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
                filePath = data.getData();
                Log.d(TAG, "onActivityResult: " + filePath);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imageviewPreview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
