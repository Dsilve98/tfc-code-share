package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class OtherScientificActivityInstitution (
        @Id
        val otherScientificActivityId: Long,
        val institutionId: Long
)