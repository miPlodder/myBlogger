package l8.cb.com.blogwithfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    RecyclerView rv;
    DatabaseReference database;
    public static final String TAG = "MainActivity";
    private SignInButton signIn;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance().getReference().child("blog");
        database.keepSynced(true);  //this method is used to store the string, integer datatype for offline capabilities
        //.getReference("blog");

        //-----------------------------------------------------------------------------------------

//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//                if (firebaseAuth.getCurrentUser() != null) {
//
//                    startActivity(new Intent(MainActivity.this, PostActivity.class));
//
//                } else {
//
//                }
//
//            }
//        };
//
//        mAuth = FirebaseAuth.getInstance();
//        signIn = (SignInButton) findViewById(R.id.btnSignIn);
//
//        mAuth.addAuthStateListener(authStateListener);
//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//                        Toast.makeText(MainActivity.this, "You got an error", Toast.LENGTH_SHORT).show();
//
//                    }
//                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//
//        signIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                signIn();
//
//            }
//        });
//

        //------------------------------------------------------------------

        FirebaseRecyclerAdapter<BlogPOJO, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogPOJO, BlogViewHolder>(

                BlogPOJO.class,
                R.layout.blog_item,
                BlogViewHolder.class,
                database
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final BlogPOJO model, int position) {

                viewHolder.tvTitle.setText(model.getTitle());
                viewHolder.tvPost.setText(model.getPost());


                //Picasso.with(MainActivity.this).load(model.getImageUri()).into(viewHolder.iv);

                Picasso.with(MainActivity.this).load(model.getImageUri())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(viewHolder.iv, new Callback() {
                            @Override
                            public void onSuccess() {

                                //image retrieved offline
                                Log.d(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onError() {

                                Log.d(TAG, "onError: ");
                                Picasso.with(MainActivity.this).load(model.getImageUri()).into(viewHolder.iv);
                            }
                        });

                Log.d(TAG, "populateViewHolder: " + position);

            }
        };

        rv.setAdapter(firebaseRecyclerAdapter);
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);

                        }

                        // ...
                    }
                });

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPost;
        ImageView iv;

        public BlogViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPost = (TextView) itemView.findViewById(R.id.tvPost);
            iv = (ImageView) itemView.findViewById(R.id.iv);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.taskbar_menu_items, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mAdd:

                startActivity(new Intent(this, PostActivity.class));
                break;

            case R.id.mSetting:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
