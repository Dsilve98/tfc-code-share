package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Project(
        @Id
        @GeneratedValue
        val id: Long = 0,
        var projectCategory: ProjectCategory,
        var title: String,
        var initialDate: Date,
        var finalDate: Date?,
        var abstract : String,
        @Column(columnDefinition = "TEXT")
        var description : String // ou descriptor
)

enum class ProjectCategory {
        NATIONAL_PROJECT,
        INTERNATIONAL_PROJECT
}