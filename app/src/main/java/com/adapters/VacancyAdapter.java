package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.activities.R;
import com.fragments.ApplicationsBottomSheetFragment;
import com.fragments.VacancyDetailsFragment;
import com.models.Vacancy;

import java.util.List;

public class VacancyAdapter extends RecyclerView.Adapter<VacancyAdapter.VagaViewHolder> {
    private List<Vacancy> listaVagas;
    private OnVerMaisClickListener listener;
    private Context context;

    // Interface para o evento de "Ver Mais"
    public interface OnVerMaisClickListener {
        void onVerMaisClick(Vacancy vaga);
    }

    // Construtores
    public VacancyAdapter(List<Vacancy> listaVagas) {
        this.listaVagas = listaVagas;
    }

    public VacancyAdapter(List<Vacancy> listaVagas, Context context, OnVerMaisClickListener listener) {
        this.listaVagas = listaVagas;
        this.context = context;
        this.listener = listener; // Passando o listener
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
        holder.tvEmpresa.setText(vaga.getCompanyName());
        holder.tvModalidade.setText(vaga.getModality());

        // Evento de clique para "Ver Mais"
        holder.btnVerMais.setOnClickListener(v -> {
            // Criar o fragmento de candidaturas, passando o ID da vaga
            VacancyDetailsFragment fragment = VacancyDetailsFragment.newInstance(vaga);
            fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), fragment.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return listaVagas.size();
    }

    public static class VagaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvEmpresa, tvModalidade;
        AppCompatButton btnVerMais;

        public VagaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
            tvModalidade = itemView.findViewById(R.id.tvModalidade);
            btnVerMais = itemView.findViewById(R.id.seeMoreDetails); // Botão "Ver Mais"
        }
    }
}
