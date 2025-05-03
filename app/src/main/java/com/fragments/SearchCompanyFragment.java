package com.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.activities.R;
import com.adapters.CompanyAdapter;
import com.models.Company;
import com.services.CompanyService;

import java.util.ArrayList;
import java.util.List;

public class SearchCompanyFragment extends Fragment {
    private EditText searchInput;
    private RecyclerView recyclerView;
    private CompanyAdapter adapter;
    private List<Company> allCompanies = new ArrayList<>();

    private Spinner spinnerUf, spinnerSegment;
    private boolean isFilterOpen = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_company, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        recyclerView = view.findViewById(R.id.recyclerView);
        spinnerUf = view.findViewById(R.id.uf_spinner);
        spinnerSegment = view.findViewById(R.id.segment_spinner);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));

        // Filtros (UF etc.) – popular como no fragment original
        ArrayAdapter<CharSequence> ufAdapter = ArrayAdapter.createFromResource(getContext(), R.array.uf_options, R.layout.spinner_item_white);
        ufAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerUf.setAdapter(ufAdapter);

        ArrayAdapter<CharSequence> modalityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.segment_options, R.layout.spinner_item_white);
        modalityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSegment.setAdapter(modalityAdapter);
        // fetch e busca
        fetchCompanies();
        setupSearchAndFilters(view);

        return view;
    }

    private void fetchCompanies() {
        CompanyService service = new CompanyService();
        service.fetchAllCompanies(new CompanyService.CompanyListCallback() {
            @Override
            public void onSuccess(List<Company> companies) {
                allCompanies = companies;
                updateCompanyList(searchInput.getText().toString());            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupSearchAndFilters(View view) {
        ImageView filterIcon = view.findViewById(R.id.filterIcon);
        LinearLayout filtersContainer = view.findViewById(R.id.filtersContainer);


        filterIcon.setOnClickListener(v -> {
            isFilterOpen = !isFilterOpen;
            filtersContainer.setVisibility(isFilterOpen ? View.VISIBLE : View.GONE);
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCompanyList(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCompanyList(searchInput.getText().toString());
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerUf.setOnItemSelectedListener(filterListener);
        spinnerSegment.setOnItemSelectedListener(filterListener);
    }

    private void updateCompanyList(String query) {
        String termo = query.toLowerCase().trim();
        String uf = spinnerUf.getSelectedItem().toString();
        String segment = spinnerSegment.getSelectedItem().toString();

        List<Company> filtradas = new ArrayList<>();
        for (Company company : allCompanies) {
            boolean matches = company.getCompanyName().toLowerCase().contains(termo);

            if (!uf.equals("Selecione")) {
                matches &= company.getUf().equalsIgnoreCase(uf);
            }

            if (!segment.equals("Selecione")) {
                matches &= company.getSegment().equalsIgnoreCase(segment);
            }



            if (matches) filtradas.add(company);
        }

        adapter = new CompanyAdapter(filtradas, getContext(), company -> {
            // Clique na empresa
        });

        recyclerView.setAdapter(adapter);
    }

}
