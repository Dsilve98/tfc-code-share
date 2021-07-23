package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class OtherScientificActivityResearcher(
        @Id
        val otherScientificActivityId: Long,
        val researcherId: String //orcid
)