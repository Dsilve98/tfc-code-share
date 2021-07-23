package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.DisseminationInstitution
import java.util.*

interface DisseminationInstitutionRepository: JpaRepository<DisseminationInstitution, String> {

    fun findByInstitutionId(id: Long): List<DisseminationInstitution>

    fun findByDisseminationId(id: Long): List<DisseminationInstitution>

    @Transactional
    fun deleteByDisseminationId(id: Long)

    @Transactional
    fun deleteByInstitutionId(id: Long)
}