package com.saludactiva.medicalrecords.data

object StudyFilter {
    fun filter(studies: List<MedicalStudy>, query: String): List<MedicalStudy> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isEmpty()) return studies

        return studies.filter { study ->
            study.titulo.contains(normalizedQuery, ignoreCase = true) ||
                study.tipo.contains(normalizedQuery, ignoreCase = true)
        }
    }
}
