package com.services;

import com.models.Vacancy;

import java.util.ArrayList;
import java.util.List;

public class VacancyService {

    private List<Vacancy> vacancies;

    public boolean registerVacancy(Vacancy vacancy){
        return true;
    }

    public VacancyService (){

        vacancies = new ArrayList<Vacancy>();

        vacancies.add(new Vacancy( "Front-end", "Desenvolvedor", "Facebook", "Vale alimentação", "Formado", "Remoto", "São Paulo", "SP", "facebook@gmail.com", "R$12.000,00", "Pleno", 1));
        vacancies.add(new Vacancy( "Back-end", "Desenvolvedor", "Facebook", "Vale alimentação", "Formado", "Remoto", "São Paulo", "SP", "facebook@gmail.com", "R$12.000,00", "Pleno", 1));
        vacancies.add(new Vacancy( "Fullstack", "Desenvolvedor", "Facebook", "Vale alimentação", "Formado", "Remoto", "São Paulo", "SP", "facebook@gmail.com", "R$12.000,00", "Pleno", 1));

    }

    public List<Vacancy> getVacancies(){
        return vacancies;
    }
}
