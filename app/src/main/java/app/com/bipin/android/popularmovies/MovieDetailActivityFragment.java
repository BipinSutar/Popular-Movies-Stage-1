package app.com.bipin.android.popularmovies;

/**
 * Created by BIPIN on 5/1/2016.
 */
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        ImagePoster imagePoster = (ImagePoster) intent.getSerializableExtra(Intent.EXTRA_TEXT);

        TextView title_text_view = (TextView) rootView.findViewById(R.id.movie_title_text_view);
        title_text_view.setText(imagePoster.getTitle());

        TextView release_date_text_view = (TextView) rootView.findViewById(R.id.release_date_text_view);
        release_date_text_view.setText(extractReleaseYear(imagePoster.getReleaseDate()));

        TextView user_rating_text_view = (TextView) rootView.findViewById(R.id.user_rating_text_view);
        user_rating_text_view.setText(imagePoster.getVoteAverage() + "/10");

        TextView overview_text_view = (TextView) rootView.findViewById(R.id.overview_text_view);
        overview_text_view.setText(imagePoster.getOverview());


        ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_detail_image_view);
        Picasso.with(getActivity())
                .load(imagePoster.getImage_url())
                .into(imageView);

        return rootView;
    }

    private String extractReleaseYear(String date){


        String [] splittedString = date.split("-");
        return splittedString[0];
    }
}

