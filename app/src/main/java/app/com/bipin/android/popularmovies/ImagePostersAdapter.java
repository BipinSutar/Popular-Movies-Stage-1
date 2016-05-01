package app.com.bipin.android.popularmovies;

/**
 * Created by DELL on 5/1/2016.
 */
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class ImagePostersAdapter extends BaseAdapter {

    private Context mContext;
    private List<ImagePoster> imagePosterList;

    public ImagePostersAdapter(Context mContext, List<ImagePoster> imagePosterList) {
        this.mContext = mContext;
        this.imagePosterList = imagePosterList;
    }

    @Override
    public int getCount() {
        return imagePosterList.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePosterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.image_poster_layout,null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.poster_image_view);

        ImagePoster imagePoster = (ImagePoster) getItem(position);

        Picasso.with(mContext)
                .load(imagePoster.getImage_url())
                .into(imageView);

        return convertView;
    }

    public void clear() {
        this.imagePosterList.clear();
        notifyDataSetChanged();
    }

    public void add(ImagePoster p) {
        Log.v("Adapter","Added poster");
        this.imagePosterList.add(p);

    }
}

