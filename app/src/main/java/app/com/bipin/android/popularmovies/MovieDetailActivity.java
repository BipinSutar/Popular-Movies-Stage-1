package app.com.bipin.android.popularmovies;

/**
 * Created by BIPIN on 5/1/2016.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetailActivityFragment())
                    .commit();
        }
    }

}
