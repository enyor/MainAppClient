package co.kaizenpro.mainapp.mainapp;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServicioViewHolder>{

    ArrayList<ItemServicio> listaPortafolio;

    public ServiceAdapter(ArrayList<ItemServicio> listaPersonaje) {
        this.listaPortafolio=listaPersonaje;
    }

    @Override
    public ServicioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service,null,false);
        return new ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServicioViewHolder holder, int position) {
        holder.txtNombre.setText(listaPortafolio.get(position).getNombre());
        holder.txtInformacion.setText(listaPortafolio.get(position).getInfo());
        holder.txtprecio.setText(listaPortafolio.get(position).getPrecio());
    }

    @Override
    public int getItemCount() {
        return listaPortafolio.size();
    }

    public class ServicioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre,txtInformacion, txtprecio;

        public ServicioViewHolder(View itemView) {
            super(itemView);
            txtNombre= (TextView) itemView.findViewById(R.id.idNombre);
            txtInformacion= (TextView) itemView.findViewById(R.id.idInfo);
            txtprecio= (TextView) itemView.findViewById(R.id.precio);
        }
    }
}