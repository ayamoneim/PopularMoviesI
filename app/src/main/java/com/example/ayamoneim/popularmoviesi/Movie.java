package com.example.ayamoneim.popularmoviesi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ayamoneim on 26/06/16.
 */
public class Movie implements Parcelable {
    private String averageVote;
    private String overview;
    private String releaseDate;
    private String posterPath;
    private String originalTitle;

    public Movie(){}
    public Movie(Parcel in){
        this.averageVote = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.originalTitle = in.readString();
        this.releaseDate = in.readString();

    }
    public String getOverview(){
        return this.overview;
    }
    public String getAverageVote(){
        return this.averageVote;
    }
    public String getPosterPath(){
        return this.posterPath;
    }
    public String getReleaseDate(){
        return this.releaseDate;
    }
    public String getOriginalTitle(){
        return this.originalTitle;
    }
    public Movie(String overview, String averageVote, String originalTitle, String posterPath, String releaseDate){
        this.overview = overview;
        this.averageVote = averageVote;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.averageVote);
        dest.writeString(this.posterPath);
        dest.writeString(this.overview);
        dest.writeString(this.originalTitle);
        dest.writeString(this.releaseDate);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
