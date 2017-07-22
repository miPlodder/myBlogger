package l8.cb.com.blogwithfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    ImageView iv;
    public static final int GALLERY_REQUEST = 1;
    public static final String TAG = "PostActivity";
    EditText etText, etPost;
    Button btnSubmit;
    Uri imageUri = null;
    StorageReference mRef;
    ProgressDialog progressDialog;
    DatabaseReference database;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference("blog");

        progressDialog = new ProgressDialog(this);
        iv = (ImageView) findViewById(R.id.iv);
        etText = (EditText) findViewById(R.id.etTitle);
        etPost = (EditText) findViewById(R.id.etPost);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        //-----------------------------------------------------------------------------------------
        /**
         *
         * Firebase Google Auth Code here
         *
        **/

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    startActivity(new Intent(PostActivity.this, MainActivity.class));

                }

            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();

            }
        });


        //-----------------------------------------------------------------------------------------

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("imageUri/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();
            }

        });
    }

    public void startPosting() {

        progressDialog.setProgress(20);
        progressDialog.setMessage("Uploading your Data");

        Log.d(TAG, "startPosting: before getting the strings");

        final String title = etText.getText().toString().trim();
        final String post = etPost.getText().toString().trim();
        //Log.d(TAG, "startPosting: "+title+", "+post+", "+imageUri.toString());

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(post) && imageUri != null) {

            progressDialog.show();

            StorageReference fileRef = mRef.child("blogImages").child(imageUri.getLastPathSegment());

            StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUri = taskSnapshot.getDownloadUrl();

                            //this is my current node with a unique id
                            DatabaseReference currNode = database.push();
                            currNode.child("title").setValue(title);
                            currNode.child("post").setValue(post);
                            currNode.child("imageUri").setValue(downloadUri.toString());

                            Toast.makeText(PostActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(PostActivity.this, MainActivity.class));
                        }


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(PostActivity.this, "Unsuccessful", Toast.LENGTH_SHORT).show();

                        }
                    });

        } else {

            Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData();
            //Log.d(TAG, "onActivityResult: " + data + ", " + data.toString());
            iv.setImageURI(imageUri);


        } else {

            Log.d(TAG, "onFailure");

        }


    }
}
