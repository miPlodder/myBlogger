package l8.cb.com.blogwithfirebase;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by ip510 feih on 22-07-2017.
 */

public class BlogWithFirebase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);

        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));

        Picasso built = builder.build();
        built.setIndicatorsEnabled(false); // to show the indicator
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

    }

}
