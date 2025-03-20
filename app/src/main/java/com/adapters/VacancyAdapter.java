package com.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.activities.R;
import com.models.Vacancy;

import java.util.List;

public class VacancyAdapter extends RecyclerView.Adapter<VacancyAdapter.VagaViewHolder> {
    private List<Vacancy> listaVagas;

    public VacancyAdapter(List<Vacancy> listaVagas) {
        this.listaVagas = listaVagas;
    }

    @NonNull
    @Override
    public VagaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.activities.R.layout.vacancy_card, parent, false);
        return new VagaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VagaViewHolder holder, int position) {
        Vacancy vaga = listaVagas.get(position);
        holder.tvTitulo.setText(vaga.getTitle());
        holder.tvEmpresa.setText(vaga.getAboutCompany());
        holder.tvLocalizacao.setText(vaga.getLocality());
    }

    @Override
    public int getItemCount() {
        return listaVagas.size();
    }

    public static class VagaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvEmpresa, tvLocalizacao;

        public VagaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
            tvLocalizacao = itemView.findViewById(R.id.tvLocalizacao);
        }
    }
}
