package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class OtherScientificActivity(
        @Id
        @GeneratedValue
        val id: Long = 0,

        var otherCategory: OtherCategory,
        var otherType: OtherType,

        var title: String,
        var date: Date,
        var description : String
)

enum class OtherCategory {
        //Advanced Education
        AGGREGATION_EXAMS_COMPLETION,
        PHD_SUPERVISION,
        POST_DOCTORATE_SUPERVISION,
        POST_DOCTORAL_STUDIES,
        PARTICIPATION_IN_DOCTORAL_JURIES,

        //Scientific Initiation of Young Students
        MASTERS_SUPERVISION,
        INTERNSHIP_SUPERVISION,

        //Other Categories
        OTHER_CATEGORY
}

enum class OtherType {
        ADVANCED_EDUCATION,
        SCIENTIFIC_INIT_OF_YOUNG_STUDENTS,
        PUBLICATION,
        PROJECT,
        DISSEMINATION
}