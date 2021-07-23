package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.PublicationInstitution
import java.util.*

interface PublicationInstitutionRepository: JpaRepository<PublicationInstitution, String> {

    fun findByInstitutionId(id: Long): List<PublicationInstitution>

    fun findByPublicationId(id: Long): Optional<PublicationInstitution>

    @Transactional
    fun deleteByPublicationId(id: Long)

    @Transactional
    fun deleteByInstitutionId(id: Long)
}