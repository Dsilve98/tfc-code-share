package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class Publication (
        @Id
        @GeneratedValue
        val id: Long = 0,

        var publicationCategory: PublicationCategory,
        var title : String,
        var publicationDate : Date,
        var descriptor : String,
        var publisher : String,
        var authors: String,
        var indexation : String,
        var conferenceName : String
)

enum class PublicationCategory {
        KNOWLEDGE_CONTRIBUTION,
        NATIONAL_MAGAZINE,
        INTERNATIONAL_MAGAZINE,
        BOOK_AUTHORSHIP,
        BOOK_CHAPTER,
        BOOK_EDITING_AND_ORGANISATION,
        INTERNATIONAL_CONFERENCE,
        NATIONAL_CONFERENCE,
        OTHER_PUBLICATION
}
