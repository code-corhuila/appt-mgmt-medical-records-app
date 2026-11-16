package com.saludactiva.medicalrecords.data

import org.junit.Assert.assertEquals
import org.junit.Test

class StudyFilterTest {
    private val studies = listOf(
        MedicalStudy("1", "Hemograma completo", "2026-03-01", "Laboratorio", "Normal"),
        MedicalStudy("2", "Radiografía de tórax", "2026-03-02", "Imagenología", "Sin hallazgos")
    )

    @Test
    fun emptySearchReturnsAllStudies() {
        assertEquals(studies, StudyFilter.filter(studies, "  "))
    }

    @Test
    fun searchMatchesTitleAndTypeIgnoringCase() {
        assertEquals(listOf(studies[0]), StudyFilter.filter(studies, "HEMOGRAMA"))
        assertEquals(listOf(studies[1]), StudyFilter.filter(studies, "imagenología"))
    }

    @Test
    fun unmatchedSearchReturnsNoStudies() {
        assertEquals(emptyList<MedicalStudy>(), StudyFilter.filter(studies, "ecografía"))
    }
}
