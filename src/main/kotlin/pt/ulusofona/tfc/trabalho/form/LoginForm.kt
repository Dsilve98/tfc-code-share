package pt.ulusofona.tfc.trabalho.form

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class LoginForm(

        @field:NotEmpty(message = "Erro: O e-mail tem que estar preenchido")
        var email: String? = null,

        @field:NotEmpty(message = "Erro: A password tem que estar preenchida")
        var password: Int? = null
)