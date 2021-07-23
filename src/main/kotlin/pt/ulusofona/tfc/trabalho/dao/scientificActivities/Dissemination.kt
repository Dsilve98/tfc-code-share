package pt.ulusofona.tfc.trabalho.dao.scientificActivities

import org.springframework.format.annotation.DateTimeFormat
import java.util.Date

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Dissemination(
        @Id
        @GeneratedValue
        val id: Long = 0,

        var disseminationCategory: DisseminationCategory,
        var title: String,
        @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
        var date: Date,
        var description: String,
        )

enum class DisseminationCategory {
        ORGANISING_COMMITTEE_MEMBER,
        SCIENTIFIC_COMMITTEE_MEMBER,
        KNOWLEDGE_AND_TECH_TRANSFER,
        PROMOTION_OF_CULTURE,
        ACTIONS_TO_SOCIETY,
        OTHER_DISSEMINATION
}