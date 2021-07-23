package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DisseminationInstitution (
        @Id
        val disseminationId: Long,
        val institutionId: Long
)
