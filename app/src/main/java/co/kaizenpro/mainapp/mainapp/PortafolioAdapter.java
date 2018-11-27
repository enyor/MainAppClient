package co.kaizenpro.mainapp.mainapp;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;





public class PortafolioAdapter extends RecyclerView.Adapter<PortafolioAdapter.PortafolioViewHolder>{
    Context context;
    ArrayList<ItemPortafolio> listaPortafolio;

    public PortafolioAdapter(ArrayList<ItemPortafolio> listaPersonaje) {

        this.listaPortafolio=listaPersonaje;
    }

    @Override
    public PortafolioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_portafolio,null,false);
        context = parent.getContext();
        return new PortafolioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PortafolioViewHolder holder, int position) {
        holder.txtNombre.setText(listaPortafolio.get(position).getNombre());
        holder.txtInformacion.setText(listaPortafolio.get(position).getInfo());
        //holder.foto.setImageResource(listaPortafolio.get(position).getImagenId());
        String imageUri = "http://mainapp.kaizenpro.co.uk/assets/"+listaPortafolio.get(position).getImagenId();
        Glide.with(context)
                .load(Uri.parse(imageUri)) // add your image url
                .into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return listaPortafolio.size();
    }

    public class PortafolioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre,txtInformacion;
        ImageView foto;

        public PortafolioViewHolder(View itemView) {
            super(itemView);
            txtNombre= (TextView) itemView.findViewById(R.id.idNombre);
            txtInformacion= (TextView) itemView.findViewById(R.id.idInfo);
            foto= (ImageView) itemView.findViewById(R.id.idImagen);
        }
    }
}