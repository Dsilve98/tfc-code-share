package pt.ulusofona.tfc.trabalho.form

import org.springframework.format.annotation.DateTimeFormat
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.DisseminationCategory
import javax.validation.constraints.NotEmpty

data class DisseminationForm (

        var disseminationCategory: DisseminationCategory? = null,

        @field:NotEmpty(message = "Campo obrigatório")
        var title: String? = null,

        @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
        var date: String? = null,

        @field:NotEmpty(message = "Campo obrigatório")
        var description: String? = null,

        var researcherOrcid: String? = null
)