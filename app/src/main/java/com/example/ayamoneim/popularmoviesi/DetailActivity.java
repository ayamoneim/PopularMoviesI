package com.example.ayamoneim.popularmoviesi;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle data = getIntent().getExtras();
        Movie movie = (Movie) data.getParcelable("Movie");

        ImageView imageView = (ImageView) this.findViewById(R.id.imageView);
        Picasso.with(this).load(movie.getPosterPath()).fit() .into(imageView);

        TextView textView = (TextView) this.findViewById(R.id.textView);
        textView.setText(movie.getOriginalTitle());

        TextView textView2 = (TextView) this.findViewById(R.id.textView2);
        textView2.setText(movie.getOverview());

        TextView textView3 = (TextView) this.findViewById(R.id.textView3);
        textView3.setText(movie.getReleaseDate());

        TextView textView4 = (TextView) this.findViewById(R.id.textView4);
        textView4.setText(movie.getAverageVote());
    }
}
