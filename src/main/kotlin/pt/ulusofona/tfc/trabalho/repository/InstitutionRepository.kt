package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pt.ulusofona.tfc.trabalho.dao.Institution
import java.util.*

interface InstitutionRepository: JpaRepository<Institution, String> {

    @Query
    fun findById(id:Long): Optional<Institution>
}