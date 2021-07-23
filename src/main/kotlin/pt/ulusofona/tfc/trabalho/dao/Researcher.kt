package pt.ulusofona.tfc.trabalho.dao

import javax.persistence.*

@Entity
data class Researcher(

        @Id
        var orcid: String,
        var name: String,
        var utilizador: String,
        var email: String,
        var cienciaId: String,
        var associationKeyFct: String,
        var researcherCategory: String,
        var origin: String,
        var phoneNumber: String,
        var siteCeied: String,
        var professionalStatus: String,
        var professionalCategory:String,
        var phdYear: Int?,
        var isAdmin: Boolean?
        )