package co.kaizenpro.mainapp.mainapp;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    Context context;
    ArrayList<ItemReview> listaPortafolio;

    public ReviewAdapter(ArrayList<ItemReview> listaPersonaje) {
        this.listaPortafolio=listaPersonaje;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review,null,false);
        context = parent.getContext();
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.txtNombre.setText(listaPortafolio.get(position).getNombre());
        holder.txtInformacion.setText(listaPortafolio.get(position).getInfo());
        //holder.foto.setImageResource(listaPortafolio.get(position).getImagenId());
        String imageUri = "http://mainapp.kaizenpro.co.uk/assets/"+listaPortafolio.get(position).getImagenId();
        Glide.with(context)

                .load(Uri.parse(imageUri)) // add your image url
                .error(R.mipmap.ic_launcher_round)
                .into(holder.foto);

        holder.Rb.setRating(listaPortafolio.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return listaPortafolio.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre,txtInformacion;
        RatingBar Rb;
        ImageView foto;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            txtNombre= (TextView) itemView.findViewById(R.id.idNombre);
            txtInformacion= (TextView) itemView.findViewById(R.id.idInfo);
            foto= (ImageView) itemView.findViewById(R.id.idImagen);
            Rb = (RatingBar) itemView.findViewById(R.id.ratingBar2);
        }
    }
}