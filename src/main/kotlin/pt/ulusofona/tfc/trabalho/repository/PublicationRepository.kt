package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.Publication
import java.util.*

interface PublicationRepository: JpaRepository<Publication, String> {
    @Query
    fun findById(id:Long): Optional<Publication>

    @Transactional
    fun deleteById(id:Long)
}