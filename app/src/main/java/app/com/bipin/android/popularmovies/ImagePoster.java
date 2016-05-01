package app.com.bipin.android.popularmovies;

/**
 * Created by DELL on 5/1/2016.
 */
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class ImagePoster implements Serializable,Parcelable{
    private String title;
    private String image_url;
    private double voteAverage;
    private String overview;
    private String releaseDate;

    public ImagePoster(String title, String image_url, double voteAverage, String overview, String releaseDate) {
        this.title = title;
        this.image_url = image_url;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    protected ImagePoster(Parcel in) {
        title = in.readString();
        image_url = in.readString();
        voteAverage = in.readDouble();
        overview = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<ImagePoster> CREATOR = new Creator<ImagePoster>() {
        @Override
        public ImagePoster createFromParcel(Parcel in) {
            return new ImagePoster(in);
        }

        @Override
        public ImagePoster[] newArray(int size) {
            return new ImagePoster[size];
        }
    };

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image_url);
        dest.writeDouble(voteAverage);
        dest.writeString(overview);
        dest.writeString(releaseDate);
    }
}

