package app.com.bipin.android.popularmovies;

/**
 * Created by BIPIN on 5/1/2016.
 */
public class NullJsonStringException extends Exception{
    public NullJsonStringException() {
        super();
    }

    public NullJsonStringException(String detailMessage) {
        super(detailMessage);
    }
}

