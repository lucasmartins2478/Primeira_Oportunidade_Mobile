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
import com.fragments.VacancyDetailsFragment;
import com.models.Vacancy;

import java.util.List;

public class VacancyAdapter extends RecyclerView.Adapter<VacancyAdapter.VagaViewHolder> {
    private List<Vacancy> listaVagas;
    private OnVerMaisClickListener listener;
    private Context context;


    public interface OnVerMaisClickListener {
        void onVerMaisClick(Vacancy vaga);
    }


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
        holder.tvEmpresa.setText(vaga.getAboutCompany());
        holder.tvLocalizacao.setText(vaga.getLocality());


        holder.btnVerMais.setOnClickListener(v -> {
            // Cria o BottomSheet com os detalhes da vaga
            VacancyDetailsFragment bottomSheet = VacancyDetailsFragment.newInstance(vaga);
            bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
        });
    }

    @Override
    public int getItemCount() {
        return listaVagas.size();
    }

    public static class VagaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvEmpresa, tvLocalizacao;
        AppCompatButton btnVerMais;

        public VagaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
            tvLocalizacao = itemView.findViewById(R.id.tvLocalizacao);
            btnVerMais = itemView.findViewById(R.id.seeMoreDetails);
        }
    }
}
