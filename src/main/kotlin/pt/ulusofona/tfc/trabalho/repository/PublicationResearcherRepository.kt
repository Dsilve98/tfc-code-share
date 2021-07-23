package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.PublicationResearcher
import java.util.*

interface PublicationResearcherRepository : JpaRepository<PublicationResearcher, String> {

    fun findByResearcherId(orcid: String): List<PublicationResearcher>

    fun findByPublicationId(id:Long): Optional<PublicationResearcher>

    @Transactional
    fun deleteByPublicationId(id:Long)

    @Transactional
    fun deleteByResearcherId(orcid: String)
}