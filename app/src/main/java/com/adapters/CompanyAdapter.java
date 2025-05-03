package com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.activities.R;
import com.models.Company;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {
    private List<Company> companies;
    private Context context;

    public interface OnCompanyClickListener {
        void onCompanyClick(Company company);
    }

    private final OnCompanyClickListener listener;

    public CompanyAdapter(List<Company> companies, Context context, OnCompanyClickListener listener) {
        this.companies = companies;
        this.context = context;
        this.listener = listener;
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView name;

        public CompanyViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(com.activities.R.id.company_logo);
            name = itemView.findViewById(R.id.company_name);
        }
    }

    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_company, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CompanyViewHolder holder, int position) {
        Company company = companies.get(position);
        holder.name.setText(company.getCompanyName());



        holder.itemView.setOnClickListener(v -> listener.onCompanyClick(company));
    }


    @Override
    public int getItemCount() {
        return companies.size();
    }
}
