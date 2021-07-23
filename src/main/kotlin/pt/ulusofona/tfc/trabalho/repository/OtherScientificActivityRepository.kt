package pt.ulusofona.tfc.trabalho.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.OtherScientificActivity
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.OtherType
import java.util.*

interface OtherScientificActivityRepository:JpaRepository<OtherScientificActivity, String> {

    @Query
    fun findById(id:Long): Optional<OtherScientificActivity>
    fun findByOtherType(type : OtherType): Optional<OtherScientificActivity>
    fun findByOtherTypeAndId(type : OtherType, id: Long): Optional<OtherScientificActivity>

    @Transactional
    fun deleteById(id:Long)
}