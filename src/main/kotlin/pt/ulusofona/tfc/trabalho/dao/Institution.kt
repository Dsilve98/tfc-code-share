package pt.ulusofona.tfc.trabalho.dao

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Institution(
        @Id
        @GeneratedValue
        val id: Long = 0,
        val name: String,
        val country: String
)