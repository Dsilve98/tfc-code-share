package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.DisseminationResearcher
import java.util.*

interface DisseminationResearcherRepository: JpaRepository<DisseminationResearcher, String> {

    fun findByResearcherId(orcid: String): List<DisseminationResearcher>

    fun findByDisseminationId(id:Long): Optional<DisseminationResearcher>

    @Transactional
    fun deleteByDisseminationId(id:Long)

    @Transactional
    fun deleteByResearcherId(orcid: String)
}