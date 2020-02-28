package com.dav.appregistrodatospollos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterServer extends RecyclerView.Adapter<AdapterServer.serverViewHolder> {
    Context context;
    List<Tb_Detalles_Class> listaServer;

    public AdapterServer(Context context, List<Tb_Detalles_Class> listaServer) {
        this.context = context;
        this.listaServer = listaServer;
    }

    @NonNull
    @Override
    public serverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, null, false);
        return new AdapterServer.serverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull serverViewHolder holder, int position) {
        holder.tvFecha.setText(listaServer.get(position).getFecha());
        holder.tvGranja.setText(listaServer.get(position).getGranja());
        holder.tvGalpon.setText(listaServer.get(position).getGalpon());
        holder.tvGalponero.setText(listaServer.get(position).getGalponero());
        holder.tvMort.setText(listaServer.get(position).getMortalidad());
        holder.tvAlimento.setText(listaServer.get(position).getAlimento());
        holder.tvPeso.setText(listaServer.get(position).getPeso());
    }

    @Override
    public int getItemCount() {
        return listaServer.size();
    }

    public class serverViewHolder extends RecyclerView.ViewHolder {

        TextView tvFecha, tvGranja, tvGalpon, tvGalponero, tvMort, tvAlimento, tvPeso;

        public serverViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvGranja = itemView.findViewById(R.id.tvGranja);
            tvGalpon = itemView.findViewById(R.id.tvGalpon);
            tvGalponero = itemView.findViewById(R.id.tvGalponero);
            tvMort = itemView.findViewById(R.id.tvMort);
            tvAlimento = itemView.findViewById(R.id.tvAlimento);
            tvPeso = itemView.findViewById(R.id.tvPeso);
        }
    }
}
