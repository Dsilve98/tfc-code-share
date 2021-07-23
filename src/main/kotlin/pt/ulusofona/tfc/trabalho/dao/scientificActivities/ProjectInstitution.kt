package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ProjectInstitution (
        @Id
        val projectId: Long,
        val institutionId: Long
)