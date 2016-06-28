package com.example.ayamoneim.popularmoviesi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    public static MovieAdapter movieAdapter;
    private final String TOP_RATED = "0";
    private final String MOST_POPULAR = "1";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.popular_movies_gridview);

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie movie = (Movie) movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("Movie", movie);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void updateMovies(){
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sorting_key),
                MOST_POPULAR);

        fetchMoviesTask.execute(sortBy);
    }


    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();



        /**
         * Take the String representing the complete Movie List in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private Movie[] getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_POSTER = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_VOTING = "vote_average";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWN_LIST = "results";
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWN_LIST);


            Movie[] resultStrs = new Movie[moviesArray.length()];
            for(int i = 0; i < moviesArray.length(); i++) {
                
                double averageVote;
                String overview;
                String releaseDate;
                String posterPath;
                String originalTitle;

                // Get the JSON object representing the movie
                JSONObject movie = moviesArray.getJSONObject(i);

                overview = movie.getString(OWM_OVERVIEW);
                averageVote = movie.getDouble(OWM_VOTING);
                releaseDate = movie.getString(OWM_RELEASE_DATE);
                posterPath = "http://image.tmdb.org/t/p/w185/"+movie.getString(OWM_POSTER);
                originalTitle = movie.getString(OWM_ORIGINAL_TITLE);
                resultStrs[i] = new Movie(overview, Double.toString(averageVote), originalTitle, posterPath, releaseDate);
            }
            return resultStrs;

        }

        @Override
        protected Movie[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;


            try {
                // Construct the URL
                String urlStr = "";

                final String POPULAR_MOVIES_URL =
                        "http://api.themoviedb.org/3/movie/popular?";
                final String TOP_RATED_MOVIES_URL =
                        "http://api.themoviedb.org/3/movie/top_rated?";
                final String API_KEY = "api_key";

                if(params[0].equals(TOP_RATED)){
                    urlStr = TOP_RATED_MOVIES_URL;
                }else{
                    urlStr = POPULAR_MOVIES_URL;
                }
                Uri builtUri = Uri.parse(urlStr).buildUpon()
                        .appendQueryParameter(API_KEY, "cc4b67c52acb514bdf4931f7cedfd12b")
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to MovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
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
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the movies.
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                movieAdapter.clear();
                for(Movie movieDataStr : result) {
                    movieAdapter.add(movieDataStr);
                }
                // New data is back from the server.  Hooray!
            }

        }
    }


}
