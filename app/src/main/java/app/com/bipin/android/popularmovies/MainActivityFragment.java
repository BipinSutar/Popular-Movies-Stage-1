package app.com.bipin.android.popularmovies;

/**
 * Created by BIPIN on 5/1/2016.
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivityFragment extends Fragment {

    private ImagePostersAdapter mPosterAdapter;
    private GridView postersGrid;
    private static final int landscape_columns = 5;
    private static final int portrait_columns = 3;
    private String last_sorting_pref;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mPosterAdapter = null;

        postersGrid = (GridView) rootView.findViewById(R.id.posters_grid);
        postersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImagePoster selectedImagePoster = (ImagePoster) mPosterAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, (Serializable) selectedImagePoster);

                startActivity(intent);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        last_sorting_pref = prefs.getString(getString(R.string.pref_sorting_key),getString(R.string.pref_sorting_pop_key));

        if(savedInstanceState==null || !savedInstanceState.containsKey("posters")){
            updateMovies();
        }else{

            ImagePoster[] imagePosters = (ImagePoster[]) savedInstanceState.getParcelableArray("imagePosters");

            List<ImagePoster> imagePosterList = new ArrayList<ImagePoster>();

            for (ImagePoster p : imagePosters) {

                imagePosterList.add(p);
            }

            mPosterAdapter = new ImagePostersAdapter(getActivity(), imagePosterList);
            postersGrid.setAdapter(mPosterAdapter);
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                postersGrid.setNumColumns(landscape_columns);
            }
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                postersGrid.setNumColumns(portrait_columns);
            }
            mPosterAdapter.notifyDataSetChanged();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ImagePoster[] imagePosters = new ImagePoster[mPosterAdapter.getCount()];

        if(imagePosters.length>0) {
            for(int i=0; i< imagePosters.length; i++){
                imagePosters[i] = (ImagePoster) mPosterAdapter.getItem(i);
            }
            outState.putParcelableArray("imagePosters", imagePosters);
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {
        super.onStart();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_sorting_key),getString(R.string.pref_sorting_pop_key));


        if(!sorting.equals(last_sorting_pref)) {
            this.last_sorting_pref = sorting;
            updateMovies();
        }
    }

    private void updateMovies(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_sorting_key),getString(R.string.pref_sorting_pop_key));

        new RetrieveMoviesTask().execute(sorting);
    }


    public class RetrieveMoviesTask extends AsyncTask<String, Void, ImagePoster[]>{
        private static final String PARAM_SORT = "sort_by";
        private static final String PARAM_API_KEY = "api_key";
        private static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w342";
        private static final String PARAM_VOTE_COUNT_GTE = "vote_count.gte";

        private static final String VALUE_PARAM_VOTE_COUNT = "100";
        private String LOG_TAG = MainActivityFragment.class.getSimpleName();

        private ImagePoster[] extractPosters(String jsonString) throws JSONException {
            try {

                ImagePoster[] postersArray = new ImagePoster[MoviesDataParser.getNumberOfMovies(jsonString)];

                int i = 0;
                String movie_title = MoviesDataParser.getTitleByIndex(jsonString, i);
                while (movie_title != null) {
                    String posterPath = MoviesDataParser.getPosterPathByIndex(jsonString, i);
                    double voteAverage = MoviesDataParser.getVoteByIndex(jsonString, i);
                    String overview = MoviesDataParser.getOverviewByIndex(jsonString, i);
                    String releaseDate = MoviesDataParser.getReleaseDateByIndex(jsonString, i);
                    ImagePoster imagePoster = new ImagePoster(movie_title, BASE_IMAGE_URL + posterPath, voteAverage, overview, releaseDate);
                    postersArray[i] = imagePoster;

                    i++;
                    movie_title = MoviesDataParser.getTitleByIndex(jsonString, i);
                }

                return postersArray;
            }catch(NullJsonStringException e){
                return new ImagePoster[0]; //If JSON String is null an empty array is returned
            }
        }

        @Override
        protected ImagePoster[] doInBackground(String... sortingParam) {
            String sorting_param = sortingParam[0];
            Log.d(LOG_TAG,"task started");

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {
                Uri builtUri;
                if(sorting_param.equals(getString(R.string.pref_sorting_vote_key))) {
                    //Add a vote_count.gte useful for avoiding movies with a low number of votes
                    //vote_count.gte parameter is set to 100 by default
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(PARAM_VOTE_COUNT_GTE, VALUE_PARAM_VOTE_COUNT)
                            .appendQueryParameter(PARAM_SORT, sorting_param)
                            .appendQueryParameter(PARAM_API_KEY, getString(R.string.api_key))
                            .build();
                }else{
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(PARAM_SORT, sorting_param)
                            .appendQueryParameter(PARAM_API_KEY, getString(R.string.api_key))
                            .build();
                }


                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.d(LOG_TAG,moviesJsonStr);
            }catch(ProtocolException e){
                e.printStackTrace();
                Log.e(LOG_TAG,"Error",e);
            }catch (IOException e){
                e.printStackTrace();
                Log.e(LOG_TAG,"Error",e);
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return this.extractPosters(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG,e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ImagePoster[] imagePosters) {
            if(imagePosters != null){

                if(mPosterAdapter!=null) {
                    mPosterAdapter.clear();

                    for (ImagePoster p : imagePosters) {
                        mPosterAdapter.add(p);
                    }
                }else{
                    Log.v(LOG_TAG,"adapter null");

                    if(imagePosters.length==0){
                        Toast t = Toast.makeText(getActivity(), getString(R.string.empty_poster_string), Toast.LENGTH_LONG);
                        t.show();
                    }

                    List<ImagePoster> imagePosterList = new ArrayList<ImagePoster>();
                    for(ImagePoster p : imagePosters){
                        imagePosterList.add(p);
                    }

                    mPosterAdapter = new ImagePostersAdapter(getActivity(), imagePosterList);
                    postersGrid.setAdapter(mPosterAdapter);
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                        postersGrid.setNumColumns(landscape_columns);
                    }
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                        postersGrid.setNumColumns(portrait_columns);
                    }
                    mPosterAdapter.notifyDataSetChanged();

                    Log.v(LOG_TAG,"added adapter "+mPosterAdapter.getCount());
                }
                Log.v(LOG_TAG,"adapter Dim"+mPosterAdapter.getCount());
            }


        }
    }
}
