package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import pt.ulusofona.tfc.trabalho.dao.Researcher

interface ResearcherRepository: JpaRepository<Researcher, String> {
}