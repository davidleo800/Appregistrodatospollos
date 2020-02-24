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
        holder.tvGranja.setText(listaServer.get(position).getGranja());
        holder.tvGalpon.setText(listaServer.get(position).getGalpon());
        holder.tvMort.setText(listaServer.get(position).getMortalidad());
    }

    @Override
    public int getItemCount() {
        return listaServer.size();
    }

    public class serverViewHolder extends RecyclerView.ViewHolder {

        TextView tvGranja, tvGalpon, tvMort;

        public serverViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGranja = itemView.findViewById(R.id.tvGranja);
            tvGalpon = itemView.findViewById(R.id.tvGalpon);
            tvMort = itemView.findViewById(R.id.tvMort);
        }
    }
}
