package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.ProjectInstitution
import java.util.*

interface ProjectInstitutionRepository: JpaRepository<ProjectInstitution, String> {

    fun findByInstitutionId(id: Long): List<ProjectInstitution>

    fun findByProjectId(id: Long): List<ProjectInstitution>

    @Transactional
    fun deleteByProjectId(id: Long)

    @Transactional
    fun deleteByInstitutionId(id: Long)
}