package com.dav.appregistrodatospollos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterLocal extends RecyclerView.Adapter<AdapterLocal.serverViewHolder> {
    Context context;
    List<Tb_Detalles_Class> listaLocal;

    public AdapterLocal(Context context, List<Tb_Detalles_Class> listaLocal) {
        this.context = context;
        this.listaLocal = listaLocal;
    }

    @NonNull
    @Override
    public serverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, null, false);
        return new AdapterLocal.serverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull serverViewHolder holder, int position) {
        holder.tvGranja.setText(listaLocal.get(position).getGranja());
        holder.tvGalpon.setText(listaLocal.get(position).getGalpon());
        holder.tvMort.setText(listaLocal.get(position).getMortalidad());
    }

    @Override
    public int getItemCount() {
        return listaLocal.size();
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
