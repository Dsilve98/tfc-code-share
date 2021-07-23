package pt.ulusofona.tfc.trabalho.form

import javax.validation.constraints.*

data class ResearcherForm(

        @field:Size(min = 19, max = 19, message = "O Orcid ID é inválido" )
        var orcid: String? = null,

        @field:NotEmpty(message = "Campo obrigatório")
        var name: String? = null,

        //@field:NotEmpty(message = "Erro: O utilizador tem que estar preenchido")
        var utilizador: String? = null,

        @field:NotEmpty(message = "Campo obrigatório")
        var email: String? = null,

        @field:Size(min = 14, max = 14, message = "O Ciência ID é inválido")
        var cienciaId: String? = null,

        //@field:NotEmpty(message = "Erro: A chave de associação tem que estar preenchida")
        var associationKeyFct: String? = null,

        /*@field:NotEmpty(message = "Campo obrigatório")*/
        var researcherCategory: String? = null,

        //@field:NotEmpty(message = "Erro: A origem tem que estar preenchida")
        var origin: String? = null,

        @field:NotEmpty(message = "Campo obrigatório")
        var phoneNumber: String? = null,

        //@field:NotEmpty(message = "Erro: O site CeiED tem que estar preenchido")
        var siteCeied: String? = null,

        @field:NotEmpty(message = "Campo obrigatório")
        var professionalStatus: String? = null,

        //@field:NotEmpty(message = "Campo obrigatório")
        var professionalCategory: String? = null,

        //@field:NotEmpty(message = "Erro: O ano de doutoramento tem que estar preenchida")
        var phdYear: Int? = null,

        //@field:NotEmpty(message = "Erro: A categoria tem que estar preenchida")
        var isAdmin: Boolean? = null
)