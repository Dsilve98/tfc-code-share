package pt.ulusofona.tfc.trabalho

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.io.File
import java.io.InputStream
import java.util.*
import javax.servlet.http.HttpServletRequest


@Configuration
class OAuthSecurityConfiguration : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .authorizeRequests()
            //                .antMatchers("/**").authenticated() // Block this
            .antMatchers("/css/**", "/images/**", "/sass/**").permitAll()
            .antMatchers("/ceied-login").permitAll() // Allow this for all
            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and().logout()
            .logoutSuccessUrl("/ceied-login").permitAll()
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .and()
            .oauth2Login()
            .defaultSuccessUrl("/default")
            .userInfoEndpoint()
            .userAuthoritiesMapper(userAuthoritiesMapper())
    }

    private fun userAuthoritiesMapper(): GrantedAuthoritiesMapper {
        return GrantedAuthoritiesMapper { authorities: Collection<GrantedAuthority?> ->
            val mappedAuthorities: MutableSet<GrantedAuthority> = HashSet()
            authorities.forEach { authority ->
                if (authority is OidcUserAuthority) {
                    val oidcUserAuthority = authority
                    throw RuntimeException("Shouldn't be here")

                } else if (authority is OAuth2UserAuthority) {

                    val oauth2UserAuthority = authority
                    val userAttributes = oauth2UserAuthority.attributes

                    val inputStream: InputStream = File("src/main/resources/admin_list_test.txt").inputStream()
                    val inputStream2: InputStream = File("src/main/resources/user_list_test.txt").inputStream()
                    val inputStream3: InputStream = File("src/main/resources/first_time_user_list_test.txt").inputStream()
                    val lineList = mutableListOf<String>()
                    val lineList2 = mutableListOf<String>()
                    val lineList3 = mutableListOf<String>()
                    inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
                    inputStream2.bufferedReader().useLines { lines -> lines.forEach { lineList2.add(it)} }
                    inputStream3.bufferedReader().useLines { lines -> lines.forEach { lineList3.add(it)} }

                    lineList.forEach{
                        if(userAttributes["id"] == it) {
                            mappedAuthorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
                        }
                    }
                    lineList2.forEach{
                        if(userAttributes["id"] == it) {
                            mappedAuthorities.add(SimpleGrantedAuthority("ROLE_USER"))
                        }
                    }
                    lineList3.forEach{
                        if(userAttributes["id"] == it) {
                            mappedAuthorities.add(SimpleGrantedAuthority("ROLE_FIRST_USER"))
                        }
                    }

                }
            }
            mappedAuthorities
        }
    }
}

@Controller
class DefaultController {
    @RequestMapping("/default")
    fun defaultAfterLogin(request: HttpServletRequest): String {
        if (request.isUserInRole("ADMIN") || request.isUserInRole("USER")) {
            return "redirect:/home"
        }
        if(request.isUserInRole("FIRST_USER")) {
            return "redirect:/new-researcher-form"
        }
        return "redirect:/no-permission"
    }
}
