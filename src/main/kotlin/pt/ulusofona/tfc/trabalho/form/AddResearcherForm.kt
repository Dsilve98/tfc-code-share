package pt.ulusofona.tfc.trabalho.form

import javax.validation.constraints.*

data class AddResearcherForm (

    @field:Size(min = 19, max = 19, message = "O Orcid ID é inválido")
    var orcid: String? = null,
    @field:NotEmpty(message = "Campo obrigatório")
    var name: String? = null
)