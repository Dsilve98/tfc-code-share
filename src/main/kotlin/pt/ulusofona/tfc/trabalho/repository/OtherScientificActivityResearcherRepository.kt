package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.OtherScientificActivityResearcher
import java.util.*

interface OtherScientificActivityResearcherRepository : JpaRepository<OtherScientificActivityResearcher, String>{

    fun findByResearcherId(orcid: String): List<OtherScientificActivityResearcher>

    fun findByOtherScientificActivityId(id:Long): Optional<OtherScientificActivityResearcher>

    @Transactional
    fun deleteByOtherScientificActivityId(id:Long)

    @Transactional
    fun deleteByResearcherId(orcid: String)
}