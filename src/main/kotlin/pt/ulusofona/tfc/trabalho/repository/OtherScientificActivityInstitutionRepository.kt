package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.OtherScientificActivityInstitution
import java.util.*

interface OtherScientificActivityInstitutionRepository: JpaRepository<OtherScientificActivityInstitution, String> {

    fun findByInstitutionId(id: Long): List<OtherScientificActivityInstitution>

    fun findByOtherScientificActivityId(id: Long): List<OtherScientificActivityInstitution>

    @Transactional
    fun deleteByOtherScientificActivityId(id: Long)

    @Transactional
    fun deleteByInstitutionId(id: Long)
}