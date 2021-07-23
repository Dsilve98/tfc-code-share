package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.ProjectResearcher
import java.util.*

interface ProjectResearcherRepository : JpaRepository<ProjectResearcher, String> {

    fun findByResearcherId(orcid: String): List<ProjectResearcher>

    fun findByProjectId(id:Long): Optional<ProjectResearcher>

    @Transactional
    fun deleteByProjectId(id:Long)

    @Transactional
    fun deleteByResearcherId(orcid: String)
}