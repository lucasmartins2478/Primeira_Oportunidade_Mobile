package com.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.activities.CandidateDetails;
import com.activities.R;
import com.models.Application;
import com.models.Candidate;
import com.services.CandidateService;

import java.util.List;

public class ApplicationsAdapter extends RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder> {

    private List<Application> applications;

    private CandidateService candidateService;


    public ApplicationsAdapter(List<Application> applications) {
        this.applications = applications;
        this.candidateService = new CandidateService();

    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application app = applications.get(position);

        // 🔄 Mostra um texto temporário enquanto carrega
        holder.textCandidateName.setText("Carregando candidato...");

        candidateService.fetchCandidateFromApiByCandidateId(app.getUserId(), new CandidateService.CandidateCallback() {
            @Override
            public void onSuccess(Candidate candidate) {
                holder.textCandidateName.post(() ->
                        holder.textCandidateName.setText(candidate.getName()));
                holder.btnAction.setOnClickListener(v -> {
                    // Exemplo: mostrar Toast
                    Toast.makeText(v.getContext(), "Abrir currículo de " + candidate.getName(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(v.getContext(), CandidateDetails.class);
                    intent.putExtra("candidate_id", String.valueOf(candidate.getId()));
                    v.getContext().startActivity(intent);

                });


            }

            @Override
            public void onFailure(String error) {
                holder.textCandidateName.post(() ->
                        holder.textCandidateName.setText("Erro ao carregar candidato"));
            }
        });


    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView textCandidateName;
        AppCompatButton btnAction;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            textCandidateName = itemView.findViewById(R.id.textCandidateName); // Corrigido aqui
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }

}

