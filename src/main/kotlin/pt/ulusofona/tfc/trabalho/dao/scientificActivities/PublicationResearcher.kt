package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class PublicationResearcher(
        @Id
        val publicationId: Long,
        val researcherId: String //orcid
)