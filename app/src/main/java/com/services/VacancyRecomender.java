package com.services;

import com.models.AcademicData;
import com.models.CourseData;
import com.models.Curriculum;
import com.models.Vacancy;

import java.util.List;

public class VacancyRecomender {

    public static int calculateJobScore(Vacancy vacancy, Curriculum curriculum,
                                        List<AcademicData> academicList,
                                        List<CourseData> courseList,
                                        List<String> competences) {
        int score = 0;


        // 📍 Localidade compatível?
        if (vacancy.getUf().equalsIgnoreCase(curriculum.getUf())) {
            score += 20;
        }

        // 🧠 Competências compatíveis
        for (String comp : competences) {
            if (vacancy.getRequirements().toLowerCase().contains(comp.toLowerCase())) {
                score += 5;
            }
        }

        // 🏫 Curso ou formação relacionada
        for (CourseData course : courseList) {
            if (vacancy.getRequirements().toLowerCase().contains(course.getCourseName().toLowerCase())) {
                score += 20;
            }
        }

        for (AcademicData academic : academicList) {
            if (vacancy.getRequirements().toLowerCase().contains(academic.getCourseName().toLowerCase())) {
                score += 20;
            }
        }

        return score;
    }
}
