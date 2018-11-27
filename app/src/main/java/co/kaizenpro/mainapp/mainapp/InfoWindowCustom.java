package co.kaizenpro.mainapp.mainapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;

/**
 * Created by gedica on 30/05/2018.
 */

public class InfoWindowCustom implements GoogleMap.InfoWindowAdapter {
    Context context;
    LayoutInflater inflater;

    public InfoWindowCustom(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.custom_info_window_blank, null);
     /*
       Object tags =  marker.getTag();

       String img = ((Trader) tags).getImagen();
       Float rank = ((Trader) tags).getRate();



        final ImageView avatar = (ImageView) v.findViewById(R.id.imgAvatar);
        TextView title = (TextView) v.findViewById(R.id.info_window_title);
        //TextView subtitle = (TextView) v.findViewById(R.id.info_window_subtitle);
        RatingBar rating = (RatingBar) v.findViewById(R.id.rk);
        //rating.setRating(rank);
        title.setText(marker.getTitle());
        //subtitle.setText(marker.getSnippet());
        ProgressBar progressBar = null;
        progressBar = (ProgressBar) v.findViewById(R.id.pb);
        progressBar.setVisibility(View.VISIBLE);


        progressBar.animate()
                .setDuration(1);


        String imageUri = "https://mainapp.kaizenpro.co.uk/assets/"+img;

        Picasso.with(context)
                .load(imageUri)
                //.placeholder(R.drawable.ic_launcher_background)
                .resize(60,60)
                .centerCrop()
                .transform(new CircleTransformPicasso())
                //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(avatar,new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (this.progressBar != null) {
                            this.progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                ;
*/
      /*  Glide.with(context)
                .load(Uri.parse(imageUri)) // add your image url
                .asBitmap()
                .override(40,40)
                .centerCrop()
                .transform(new CircleTransform(context)) // applying the image transformer
                .into(avatar);*/

          /*    .into(new BitmapImageViewTarget(avatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        avatar.setImageDrawable(circularBitmapDrawable);
                    }
                    });

*/
        //return v;

        return v;
    }

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }

}