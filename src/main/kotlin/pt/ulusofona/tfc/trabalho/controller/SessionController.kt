package pt.ulusofona.tfc.trabalho.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.support.BasicAuthenticationInterceptor
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import pt.ulusofona.tfc.trabalho.dao.Institution
import pt.ulusofona.tfc.trabalho.dao.Researcher
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.*
import pt.ulusofona.tfc.trabalho.form.ResearcherForm
import pt.ulusofona.tfc.trabalho.repository.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid


@Controller
@RequestMapping("")
class SessionController (val researcherRepository: ResearcherRepository,
                         val disseminationRepository: DisseminationRepository,
                         val disseminationResearcherRepository: DisseminationResearcherRepository,
                         val disseminationInstitutionRepository: DisseminationInstitutionRepository,
                         val projectRepository: ProjectRepository,
                         val projectResearcherRepository: ProjectResearcherRepository,
                         val projectInstitutionRepository: ProjectInstitutionRepository,
                         val publicationRepository: PublicationRepository,
                         val publicationResearcherRepository: PublicationResearcherRepository,
                         val publicationInstitutionRepository: PublicationInstitutionRepository,
                         val otherScientificActivityRepository: OtherScientificActivityRepository,
                         val otherScientificActivityResearcherRepository: OtherScientificActivityResearcherRepository,
                         val otherScientificActivityInstitutionRepository: OtherScientificActivityInstitutionRepository,
                         val institutionRepository: InstitutionRepository) {

    @Value("\${ciencia.vitae.token}")
    lateinit var token: String

    @Value("\${ciencia.vitae.secret}")
    lateinit var secret: String

    @GetMapping(value = ["/ceied-login"])
    fun showLoginPage(model: ModelMap): String {
        return "login-page"
    }

    @GetMapping(value = ["/new-researcher-form"])
    fun showPersonalInformationFormPage(model: ModelMap): String {
        model["researcherForm"] = ResearcherForm()
        return "forms-section/new-researcher-form"
    }

    @PostMapping(value = ["/new-researcher-form"])
    fun createResearcher(
        @Valid @ModelAttribute("researcherForm") researcherForm: ResearcherForm,
        bindingResult: BindingResult,
        @ModelAttribute("getId") orcid: String,
        redirectAttributes: RedirectAttributes
    ): String {

        if (bindingResult.hasErrors()) {
            return "forms-section/new-researcher-form"
            //return "admin-section/researcher-management"
        }

        val researcher = Researcher(
            orcid = orcid,
            name = researcherForm.name!!,
            utilizador = researcherForm.utilizador!!,
            email = researcherForm.email!!,
            cienciaId = researcherForm.cienciaId!!,
            associationKeyFct = researcherForm.associationKeyFct!!,
            researcherCategory = researcherForm.researcherCategory!!,
            origin = researcherForm.origin!!,
            phoneNumber = researcherForm.phoneNumber!!,
            siteCeied = researcherForm.siteCeied!!,
            professionalStatus = researcherForm.professionalStatus!!,
            professionalCategory = researcherForm.professionalCategory!!,
            phdYear = researcherForm.phdYear,
            isAdmin = false,
        )

        researcherRepository.save(researcher)

        File("src/main/resources/user_list_test.txt").appendText("\nhttps://sandbox.orcid.org/${researcher.orcid}")

        return "redirect:/accept-sync-cv/${researcher.orcid}"
    }

    @GetMapping(value = ["/no-permission"])
    fun showNoPermissionPage(model: ModelMap): String {
        return "no-permission"
    }

    @GetMapping(value = ["/accept-sync-cv/{orcid}"])
    fun showCvButtonPage(@PathVariable("orcid") orcid: String, model: ModelMap): String {
        val researcherOptional = researcherRepository.findById(orcid)
        val researcher = researcherOptional.get()
        model["researcher"] = Researcher(
            orcid = researcher.orcid,
            name = researcher.name,
            utilizador = researcher.utilizador,
            email = researcher.email,
            cienciaId = researcher.cienciaId,
            associationKeyFct = researcher.associationKeyFct,
            researcherCategory = researcher.researcherCategory,
            origin = researcher.origin,
            phoneNumber = researcher.phoneNumber,
            siteCeied = researcher.siteCeied,
            professionalStatus = researcher.professionalStatus,
            professionalCategory = researcher.professionalCategory,
            phdYear = researcher.phdYear,
            isAdmin = researcher.isAdmin,
        )
        return "sync-cv-page"
    }

    @GetMapping(value = ["/home"])
    fun showHomePage(model: ModelMap): String {
        val researchers = researcherRepository.findAll()
        model["researchers"] = researchers
        return "home-page"
    }

    @GetMapping(value = ["/sync-cv"])
    fun showCienciaVitae(@RequestParam(name = "id") id: String, model: ModelMap, @ModelAttribute("getId") getId: String): String {

        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(BasicAuthenticationInterceptor(token, secret))
        val response = restTemplate.getForEntity("https://vitaeapi.playdev.ulusofona.pt/curriculum/$id", String::class.java)

        if (response.statusCode.is2xxSuccessful) {

            //Drop do cv
            //Dissemination
            val disseminationResearchers = disseminationResearcherRepository.findByResearcherId(getId)
            for (i in disseminationResearchers) {
                disseminationRepository.deleteById(i.disseminationId)
                disseminationInstitutionRepository.deleteByDisseminationId(i.disseminationId)
            }
            disseminationResearcherRepository.deleteByResearcherId(getId)

            //Project
            val projectResearchers = projectResearcherRepository.findByResearcherId(getId)
            for (i in projectResearchers) {
                projectRepository.deleteById(i.projectId)
                projectInstitutionRepository.deleteByProjectId(i.projectId)
            }
            projectResearcherRepository.deleteByResearcherId(getId)

            //Publication
            val publicationResearchers = publicationResearcherRepository.findByResearcherId(getId)
            for (i in publicationResearchers) {
                publicationRepository.deleteById(i.publicationId)
                publicationInstitutionRepository.deleteByPublicationId(i.publicationId)
            }
            publicationResearcherRepository.deleteByResearcherId(getId)

            //OtherSA
            val otherSAResearchers = otherScientificActivityResearcherRepository.findByResearcherId(getId)
            for (i in otherSAResearchers) {
                otherScientificActivityRepository.deleteById(i.otherScientificActivityId)
                otherScientificActivityInstitutionRepository.deleteByOtherScientificActivityId(i.otherScientificActivityId)
            }
            otherScientificActivityResearcherRepository.deleteByResearcherId(getId)

            val mapper = ObjectMapper()
            val root: JsonNode = mapper.readTree(response.body)
            val fullName = root.at("/identifying-info/person-info/full-name").asText()

            val fundingsSize = root.at("/fundings/total").asInt()
            val outputsSize = root.at("/outputs/total").asInt()
            val servicesSize = root.at("/services/total").asInt()
            val count = maxOf(fundingsSize, outputsSize, servicesSize)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")


            for (i in 0..count) {
                //fundings
                if (i < fundingsSize) {
                    if (root.at("/fundings/funding/$i/funding-category/value").asText() == "Projeto") {
                        var initialYear = "1999"
                        var initialMonth = "01"
                        var initialDay = "01"
                        var finalYear = "1999"
                        var finalMonth = "01"
                        var finalDay = "01"

                        //check initialYear
                        if (!root.at("/fundings/funding/$i/start-date-participation/year").isNull) {
                            initialYear = root.at("/fundings/funding/$i/start-date-participation/year").asText()
                        }

                        //check initialMonth
                        if (!root.at("/fundings/funding/$i/start-date-participation/month").isNull) {
                            initialMonth = root.at("/fundings/funding/$i/start-date-participation/month").asText()
                        }

                        //check initialDay
                        if (!root.at("/fundings/funding/$i/start-date-participation/day").isNull) {
                            initialDay = root.at("/fundings/funding/$i/start-date-participation/day").asText()
                        }

                        val initialValidatedDate = "$initialYear-$initialMonth-$initialDay"

                        var finalValidatedDate: Date? = null

                        if (!root.at("/fundings/funding/$i/end-date-participation").isNull) {
                            //check finalYear
                            if (!root.at("/fundings/funding/$i/end-date-participation/year").isNull) {
                                finalYear = root.at("/fundings/funding/$i/end-date-participation/year").asText()
                            }

                            //check finalMonth
                            if (!root.at("/fundings/funding/$i/end-date-participation/month").isNull) {
                                finalMonth = root.at("/fundings/funding/$i/end-date-participation/month").asText()
                            }

                            //check finalDay
                            if (!root.at("/fundings/funding/$i/end-date-participation/day").isNull) {
                                finalDay = root.at("/fundings/funding/$i/end-date-participation/day").asText()
                            }

                            finalValidatedDate = dateFormat.parse("$finalYear-$finalMonth-$finalDay")
                        }

                        var projectCategory = ProjectCategory.INTERNATIONAL_PROJECT

                        if(root.at("/fundings/funding/$i/funding-institutions/institution/0/institution-address/country/code").asText() == "PT") {
                            projectCategory = ProjectCategory.NATIONAL_PROJECT
                        }

                        val project = Project(
                            projectCategory = projectCategory,
                            title = root.at("/fundings/funding/$i/project-title").asText(),
                            initialDate = dateFormat.parse(initialValidatedDate),
                            finalDate = finalValidatedDate,
                            abstract = "",
                            description = root.at("/fundings/funding/$i/project-description").asText()
                        )

                        projectRepository.save(project)

                        val projectResearcher = ProjectResearcher(
                            projectId = project.id,
                            researcherId = getId
                        )

                        projectResearcherRepository.save(projectResearcher)

                        //getInstitutionsSize
                        val institutionsSize = root.at("/fundings/funding/$i/institutions/total").asInt()
                        val allInstitutions = institutionRepository.findAll()
                        //getInstitutions
                        for (i2 in 0 until institutionsSize) {
                            var flagExist = false
                            if(allInstitutions.isNotEmpty()) {
                                for (institution in allInstitutions) {
                                    if (institution.name == root.at("/fundings/funding/$i/institutions/institution/$i2/institution-name").asText()) {
                                        val projectInstitution = ProjectInstitution(
                                            projectId = project.id,
                                            institutionId = institution.id
                                        )
                                        projectInstitutionRepository.save(projectInstitution)
                                        flagExist = true
                                        break
                                    }
                                }
                                if(!flagExist) {
                                    val newInstitution = Institution(
                                        name = root.at("/fundings/funding/$i/institutions/institution/$i2/institution-name").asText(),
                                        country = root.at("/fundings/funding/$i/institutions/institution/$i2/institution-address/country/value").asText()
                                    )
                                    institutionRepository.save(newInstitution)

                                    val projectInstitution = ProjectInstitution(
                                        projectId = project.id,
                                        institutionId = newInstitution.id
                                    )

                                    projectInstitutionRepository.save(projectInstitution)
                                }
                            } else {
                                val newInstitution = Institution(
                                    name = root.at("/fundings/funding/$i/institutions/institution/$i2/institution-name").asText(),
                                    country = root.at("/fundings/funding/$i/institutions/institution/$i2/institution-address/country/value").asText()
                                )
                                institutionRepository.save(newInstitution)

                                val projectInstitution = ProjectInstitution(
                                    projectId = project.id,
                                    institutionId = newInstitution.id
                                )

                                projectInstitutionRepository.save(projectInstitution)
                            }
                        }
                    }
                }

                //outputs
                if (i < outputsSize) {
                    if (root.at("/outputs/output/$i/output-category/value").asText() == "Publicações") {
                        var year = "1999"
                        var month = "01"
                        var day = "01"
                        var identifiers = ""

                        if (root.at("/outputs/output/$i/output-type/value").asText() == "Artigo em revista") {

                            //check year
                            if (!root.at("/outputs/output/$i/journal-article/publication-date/year").isNull) {
                                year = root.at("/outputs/output/$i/journal-article/publication-date/year").asText()
                            }
                            //check month
                            if (!root.at("/outputs/output/$i/journal-article/publication-date/month").isNull) {
                                month = root.at("/outputs/output/$i/journal-article/publication-date/month").asText()
                            }
                            //check day
                            if (!root.at("/outputs/output/$i/journal-article/publication-date/day").isNull) {
                                day = root.at("/outputs/output/$i/journal-article/publication-date/day").asText()
                            }

                            val validatedDate = "$year-$month-$day"

                            var publicationCategory = PublicationCategory.NATIONAL_MAGAZINE

                            if (root.at("/outputs/output/$i/journal-article/publication-location/country/code")
                                    .asText() != "PT"
                            ) {
                                publicationCategory = PublicationCategory.INTERNATIONAL_MAGAZINE
                            }

                            //getIdentifiersSize
                            val identifiersSize =
                                root.at("/outputs/output/$i/journal-article/identifiers/total").asInt()

                            //getIdentifiers
                            for (i2 in 1 until identifiersSize) {
                                identifiers += root.at("/outputs/output/$i/journal-article/identifiers/identifier/$i2/identifier-type/code")
                                    .asText() +
                                        ": " + root.at("/outputs/output/$i/journal-article/identifiers/identifier/$i2/identifier")
                                    .asText() + "\n"
                            }

                            val publication = Publication(
                                publicationCategory = publicationCategory,
                                title = root.at("/outputs/output/$i/journal-article/article-title").asText(),
                                publicationDate = dateFormat.parse(validatedDate),
                                descriptor = identifiers,
                                publisher = "",
                                authors = root.at("/outputs/output/$i/journal-article/authors/citation").asText(),
                                indexation = "",
                                conferenceName = ""
                            )

                            publicationRepository.save(publication)

                            val publicationResearcher = PublicationResearcher(
                                publicationId = publication.id,
                                researcherId = getId
                            )

                            //--save ResearcherDissemination
                            publicationResearcherRepository.save(publicationResearcher)
                        }
                        if (root.at("/outputs/output/$i/output-type/value").asText() == "Livro") {

                            //get year
                            year = root.at("/outputs/output/$i/book/publication-year").asText()

                            val validatedDate = "$year-$month-$day"


                            //getIdentifiersSize
                            val identifiersSize = root.at("/outputs/output/$i/book/identifiers/total").asInt()

                            //getIdentifiers
                            for (i2 in 1 until identifiersSize) {
                                identifiers += root.at("/outputs/output/$i/book/identifiers/identifier/$i2/identifier-type/code")
                                    .asText() +
                                        ": " + root.at("/outputs/output/$i/book/identifiers/identifier/$i2/identifier")
                                    .asText() + "\n"
                            }

                            val publication = Publication(
                                publicationCategory = PublicationCategory.BOOK_AUTHORSHIP,
                                title = root.at("/outputs/output/$i/book/title").asText(),
                                publicationDate = dateFormat.parse(validatedDate),
                                descriptor = identifiers,
                                publisher = root.at("/outputs/output/$i/book/publisher").asText(),
                                authors = root.at("/outputs/output/$i/book/authors/citation").asText(),
                                indexation = "",
                                conferenceName = ""
                            )

                            publicationRepository.save(publication)

                            val publicationResearcher = PublicationResearcher(
                                publicationId = publication.id,
                                researcherId = getId
                            )

                            //--save ResearcherDissemination
                            publicationResearcherRepository.save(publicationResearcher)
                        }

                        if (root.at("/outputs/output/$i/output-type/value").asText() == "Capítulo de livro") {

                            //get year
                            year = root.at("/outputs/output/$i/book-chapter/publication-year").asText()

                            val validatedDate = "$year-$month-$day"


                            //getIdentifiersSize
                            val identifiersSize = root.at("/outputs/output/$i/book-chapter/identifiers/total").asInt()

                            //getIdentifiers
                            for (i2 in 1 until identifiersSize) {
                                identifiers += root.at("/outputs/output/$i/book-chapter/identifiers/identifier/$i2/identifier-type/code")
                                    .asText() +
                                        ": " + root.at("/outputs/output/$i/book-chapter/identifiers/identifier/$i2/identifier")
                                    .asText() + "\n"
                            }

                            val publication = Publication(
                                publicationCategory = PublicationCategory.BOOK_CHAPTER,
                                title = root.at("/outputs/output/$i/book-chapter/chapter-title").asText(),
                                publicationDate = dateFormat.parse(validatedDate),
                                descriptor = identifiers,
                                publisher = root.at("/outputs/output/$i/book-chapter/book-publisher").asText(),
                                authors = root.at("/outputs/output/$i/book-chapter/authors/citation").asText(),
                                indexation = "",
                                conferenceName = ""
                            )

                            publicationRepository.save(publication)

                            val publicationResearcher = PublicationResearcher(
                                publicationId = publication.id,
                                researcherId = getId
                            )

                            //--save ResearcherDissemination
                            publicationResearcherRepository.save(publicationResearcher)
                        }
                        if (root.at("/outputs/output/$i/output-type/value").asText() == "Edição de livro") {

                            //get year
                            year = root.at("/outputs/output/$i/edited-book/publication-year").asText()

                            val validatedDate = "$year-$month-$day"


                            //getIdentifiersSize
                            val identifiersSize = root.at("/outputs/output/$i/edited-book/identifiers/total").asInt()

                            //getIdentifiers
                            for (i2 in 1 until identifiersSize) {
                                identifiers += root.at("/outputs/output/$i/edited-book/identifiers/identifier/$i2/identifier-type/code").asText() +
                                        ": " + root.at("/outputs/output/$i/edited-book/identifiers/identifier/$i2/identifier").asText() + "\n"
                            }

                            val publication = Publication(
                                publicationCategory = PublicationCategory.BOOK_EDITING_AND_ORGANISATION,
                                title = root.at("/outputs/output/$i/edited-book/title").asText(),
                                publicationDate = dateFormat.parse(validatedDate),
                                descriptor = identifiers,
                                publisher = root.at("/outputs/output/$i/edited-book/publisher").asText(),
                                authors = root.at("/outputs/output/$i/edited-book/authors/citation").asText(),
                                indexation = "",
                                conferenceName = ""
                            )

                            publicationRepository.save(publication)

                            val publicationResearcher = PublicationResearcher(
                                publicationId = publication.id,
                                researcherId = getId
                            )

                            //--save ResearcherDissemination
                            publicationResearcherRepository.save(publicationResearcher)
                        }
                        if (root.at("/outputs/output/$i/output-type/value").asText() == "Resumo em conferência") {

                            //check year
                            if (!root.at("/outputs/output/$i/conference-abstract/publication-date/year").isNull) {
                                year = root.at("/outputs/output/$i/conference-abstract/publication-date/year").asText()
                            }
                            //check month
                            if (!root.at("/outputs/output/$i/conference-abstract/publication-date/month").isNull) {
                                month = root.at("/outputs/output/$i/conference-abstract/publication-date/month").asText()
                            }
                            //check day
                            if (!root.at("/outputs/output/$i/conference-abstract/publication-date/day").isNull) {
                                day = root.at("/outputs/output/$i/conference-abstract/publication-date/day").asText()
                            }

                            val validatedDate = "$year-$month-$day"

                            var publicationCategory = PublicationCategory.NATIONAL_CONFERENCE

                            if (root.at("/outputs/output/$i/conference-abstract/conference-location/country/code").asText() != "PT") {
                                publicationCategory = PublicationCategory.INTERNATIONAL_CONFERENCE
                            }

                            //getIdentifiersSize
                            val identifiersSize = root.at("/outputs/output/$i/conference-abstract/identifiers/total").asInt()

                            //getIdentifiers
                            for (i2 in 1 until identifiersSize) {
                                identifiers += root.at("/outputs/output/$i/conference-abstract/identifiers/identifier/$i2/identifier-type/code").asText() +
                                        ": " + root.at("/outputs/output/$i/conference-abstract/identifiers/identifier/$i2/identifier").asText() + "\n"
                            }

                            val publication = Publication(
                                publicationCategory = publicationCategory,
                                title = root.at("/outputs/output/$i/conference-abstract/article-title").asText(),
                                publicationDate = dateFormat.parse(validatedDate),
                                descriptor = identifiers,
                                publisher = "",
                                indexation = "",
                                authors = root.at("/outputs/output/$i/conference-abstract/authors/citation").asText(),
                                conferenceName = root.at("/outputs/output/$i/conference-abstract/conference-name").asText()
                            )

                            publicationRepository.save(publication)

                            val publicationResearcher = PublicationResearcher(
                                publicationId = publication.id,
                                researcherId = getId
                            )

                            //--save ResearcherDissemination
                            publicationResearcherRepository.save(publicationResearcher)
                        }

                    }
                }

                //services
                if (root.at("/services/service/$i/service-category/value").asText() == "Júri de grau académico") {
                    if (root.at("/services/service/$i/graduate-examination/examination-degree/code").asText() == "D") {
                        var year = "1999"
                        var month = "01"
                        var day = "01"

                        //check year
                        if (!root.at("/services/service/$i/graduate-examination/date/year").isNull) {
                            year = root.at("/services/service/$i/graduate-examination/date/year").asText()
                        }

                        //check month
                        if (!root.at("/services/service/$i/graduate-examination/date/month").isNull) {
                            month = root.at("/services/service/$i/graduate-examination/date/month").asText()
                        }

                        //check day
                        if (!root.at("/services/service/$i/graduate-examination/date/day").isNull) {
                            day = root.at("/services/service/$i/graduate-examination/date/day").asText()
                        }

                        val validatedDate = "$year-$month-$day"

                        val otherScientificActivity = OtherScientificActivity(
                            otherType = OtherType.ADVANCED_EDUCATION,
                            otherCategory = OtherCategory.PARTICIPATION_IN_DOCTORAL_JURIES,
                            title = root.at("/services/service/$i/graduate-examination/theme").asText(),
                            date = dateFormat.parse(validatedDate),
                            description = root.at("/services/service/$i/graduate-examination/examination-subject").asText()
                        )

                        otherScientificActivityRepository.save(otherScientificActivity)

                        val otherScientificActivityResearcher = OtherScientificActivityResearcher(
                            otherScientificActivityId = otherScientificActivity.id,
                            researcherId = getId
                        )

                        otherScientificActivityResearcherRepository.save(otherScientificActivityResearcher)

                        //getInstitutionsSize
                        val institutionsSize = root.at("/services/service/$i/graduate-examination/institutions/total").asInt()
                        val allInstitutions = institutionRepository.findAll()
                        //getInstitutions
                        for (i2 in 0 until institutionsSize) {
                            var flagExist = false
                            if(allInstitutions.isNotEmpty()) {
                                for (institution in allInstitutions) {
                                    if (institution.name == root.at("/services/service/$i/graduate-examination/institutions/institution/$i2/institution-name")
                                            .asText()
                                    ) {
                                        val otherScientificActivityInstitution = OtherScientificActivityInstitution(
                                            otherScientificActivityId = otherScientificActivity.id,
                                            institutionId = institution.id
                                        )
                                        otherScientificActivityInstitutionRepository.save(
                                            otherScientificActivityInstitution
                                        )
                                        flagExist = true
                                        break
                                    }
                                }
                                if(!flagExist) {
                                    val newInstitution = Institution(
                                        name = root.at("/services/service/$i/graduate-examination/institutions/institution/$i2/institution-name").asText(),
                                        country = root.at("/services/service/$i/graduate-examination/institutions/institution/$i2/institution-address/country/value").asText()
                                    )
                                    institutionRepository.save(newInstitution)

                                    val otherScientificActivityInstitution = OtherScientificActivityInstitution(
                                        otherScientificActivityId = otherScientificActivity.id,
                                        institutionId = newInstitution.id
                                    )
                                    otherScientificActivityInstitutionRepository.save(
                                        otherScientificActivityInstitution
                                    )
                                }
                            } else {
                                val newInstitution = Institution(
                                    name = root.at("/services/service/$i/graduate-examination/institutions/institution/$i2/institution-name").asText(),
                                    country = root.at("/services/service/$i/graduate-examination/institutions/institution/$i2/institution-address/country/value").asText()
                                )
                                institutionRepository.save(newInstitution)

                                val otherScientificActivityInstitution = OtherScientificActivityInstitution(
                                    otherScientificActivityId = otherScientificActivity.id,
                                    institutionId = newInstitution.id
                                )
                                otherScientificActivityInstitutionRepository.save(otherScientificActivityInstitution)
                            }
                        }
                    }
                }

                if (root.at("/services/service/$i/service-category/value").asText() == "Orientação") {
                    var year = "1999"
                    var month = "01"
                    var day = "01"

                    //check year
                    if (!root.at("/services/service/$i/research-based-degree-supervision/start-date/year").isNull) {
                        year = root.at("/services/service/$i/research-based-degree-supervision/start-date/year").asText()
                    }

                    //check month
                    if (!root.at("/services/service/$i/research-based-degree-supervision/start-date/month").isNull) {
                        month = root.at("/services/service/$i/research-based-degree-supervision/start-date/month").asText()
                    }

                    //check day
                    if (!root.at("/services/service/$i/research-based-degree-supervision/start-date/day").isNull) {
                        day = root.at("/services/service/$i/research-based-degree-supervision/start-date/day").asText()
                    }

                    val validatedDate = "$year-$month-$day"

                    var otherScientificActivityCategory = OtherCategory.OTHER_CATEGORY
                    var otherScientificActivityType = OtherType.ADVANCED_EDUCATION

                    //check Orientação Mestrado
                    if (root.at("/services/service/$i/research-based-degree-supervision/degree-type/code").asText() == "M") {
                        otherScientificActivityCategory = OtherCategory.MASTERS_SUPERVISION
                        otherScientificActivityType = OtherType.SCIENTIFIC_INIT_OF_YOUNG_STUDENTS
                    }

                    //check Orientação de Doutoramento
                    if (root.at("/services/service/$i/research-based-degree-supervision/degree-type/code").asText() == "D") {
                        otherScientificActivityCategory = OtherCategory.PHD_SUPERVISION
                    }


                    val otherScientificActivity = OtherScientificActivity(
                        otherType = otherScientificActivityType,
                        otherCategory = otherScientificActivityCategory,
                        title = root.at("/services/service/$i/research-based-degree-supervision/thesis-title").asText(),
                        date = dateFormat.parse(validatedDate),
                        description = root.at("/services/service/$i/research-based-degree-supervision/degree-subject").asText()
                    )

                    otherScientificActivityRepository.save(otherScientificActivity)

                    val otherScientificActivityResearcher = OtherScientificActivityResearcher(
                        otherScientificActivityId = otherScientificActivity.id,
                        researcherId = getId
                    )

                    otherScientificActivityResearcherRepository.save(otherScientificActivityResearcher)

                    //getInstitutionsSize
                    val institutionsSize = root.at("/services/service/$i/research-based-degree-supervision/academic-institutions/total").asInt()
                    val allInstitutions = institutionRepository.findAll()
                    //getInstitutions
                    for (i2 in 0 until institutionsSize) {
                        var flagExist = false
                        if(allInstitutions.isNotEmpty()) {
                            for (institution in allInstitutions) {
                                if (institution.name == root.at("/services/service/$i/research-based-degree-supervision/academic-institutions/institution/$i2/institution-name")
                                        .asText()
                                ) {
                                    val otherScientificActivityInstitution = OtherScientificActivityInstitution(
                                        otherScientificActivityId = otherScientificActivity.id,
                                        institutionId = institution.id
                                    )
                                    otherScientificActivityInstitutionRepository.save(otherScientificActivityInstitution)
                                    flagExist = true
                                    break
                                }
                            }
                            if(!flagExist) {
                                val newInstitution = Institution(
                                    name = root.at("/services/service/$i/research-based-degree-supervision/academic-institutions/institution/$i2/institution-name").asText(),
                                    country = root.at("/services/service/$i/research-based-degree-supervision/academic-institutions/institution/$i2/institution-address/country/value").asText()
                                )
                                institutionRepository.save(newInstitution)

                                val otherScientificActivityInstitution = OtherScientificActivityInstitution(
                                    otherScientificActivityId = otherScientificActivity.id,
                                    institutionId = newInstitution.id
                                )
                                otherScientificActivityInstitutionRepository.save(otherScientificActivityInstitution)
                            }
                        } else {
                            val newInstitution = Institution(
                                name = root.at("/services/service/$i/research-based-degree-supervision/academic-institutions/institution/$i2/institution-name").asText(),
                                country = root.at("/services/service/$i/research-based-degree-supervision/academic-institutions/institution/$i2/institution-address/country/value").asText()
                            )
                            institutionRepository.save(newInstitution)

                            val otherScientificActivityInstitution = OtherScientificActivityInstitution(
                                otherScientificActivityId = otherScientificActivity.id,
                                institutionId = newInstitution.id
                            )
                            otherScientificActivityInstitutionRepository.save(otherScientificActivityInstitution)
                        }
                    }
                }

                if (root.at("/services/service/$i/service-category/value").asText() == "Organização de evento") {
                    var year = "1999"
                    var month = "01"
                    var day = "01"

                    //check year
                    if (!root.at("/services/service/$i/event-administration/activity-start-date/year").isNull) {
                        year = root.at("/services/service/$i/event-administration/activity-start-date/year").asText()
                    }

                    //check month
                    if (!root.at("/services/service/$i/event-administration/activity-start-date/month").isNull) {
                        month = root.at("/services/service/$i/event-administration/activity-start-date/month").asText()
                    }

                    //check day
                    if (!root.at("/services/service/$i/event-administration/activity-start-date/day").isNull) {
                        day = root.at("/services/service/$i/event-administration/activity-start-date/day").asText()
                    }

                    val validatedDate = "$year-$month-$day"

                    var disseminationCategory = DisseminationCategory.OTHER_DISSEMINATION

                    //check Membro da Comissão Organizadora
                    if (root.at("/services/service/$i/event-administration/administrative-role/code").asText() == "MCO"
                    ) {
                        disseminationCategory = DisseminationCategory.ORGANISING_COMMITTEE_MEMBER
                    }

                    //check Membro da Comissão Científica
                    if (root.at("/services/service/$i/event-administration/administrative-role/code").asText() == "MCC"
                    ) {
                        disseminationCategory = DisseminationCategory.SCIENTIFIC_COMMITTEE_MEMBER
                    }

                    val dissemination = Dissemination(
                        disseminationCategory = disseminationCategory,
                        title = root.at("/services/service/$i/event-administration/event-description").asText(),
                        date = dateFormat.parse(validatedDate),
                        description = root.at("/services/service/$i/event-administration/event-description").asText()
                    )

                    disseminationRepository.save(dissemination)

                    val disseminationResearcher = DisseminationResearcher(
                        disseminationId = dissemination.id,
                        researcherId = getId
                    )

                    //--save ResearcherDissemination
                    disseminationResearcherRepository.save(disseminationResearcher)

                    //getInstitutionsSize
                    val institutionsSize = root.at("/services/service/$i/event-administration/institutions/total").asInt()
                    val allInstitutions = institutionRepository.findAll()
                    //getInstitutions
                    for (i2 in 0 until institutionsSize) {
                        var flagExist = false
                        if(allInstitutions.isNotEmpty()) {
                            for (institution in allInstitutions) {
                                if (institution.name == root.at("/services/service/$i/event-administration/institutions/institution/$i2/institution-name")
                                        .asText()
                                ) {
                                    val disseminationInstitution = DisseminationInstitution(
                                        disseminationId = dissemination.id,
                                        institutionId = institution.id
                                    )
                                    disseminationInstitutionRepository.save(disseminationInstitution)
                                    flagExist = true
                                    break
                                }
                            }
                            if(!flagExist) {
                                val newInstitution = Institution(
                                    name = root.at("/services/service/$i/event-administration/institutions/institution/$i2/institution-name").asText(),
                                    country = root.at("/services/service/$i/event-administration/institutions/institution/$i2/institution-address/country/value").asText()
                                    )
                                    institutionRepository.save(newInstitution)

                                    val disseminationInstitution = DisseminationInstitution(
                                        disseminationId = dissemination.id,
                                        institutionId = newInstitution.id
                                    )
                                    disseminationInstitutionRepository.save(disseminationInstitution)
                            }
                        } else {
                            val newInstitution = Institution(
                                name = root.at("/services/service/$i/event-administration/institutions/institution/$i2/institution-name")
                                    .asText(),
                                country = root.at("/services/service/$i/event-administration/institutions/institution/$i2/institution-address/country/value")
                                    .asText()
                            )
                            institutionRepository.save(newInstitution)

                            val disseminationInstitution = DisseminationInstitution(
                                disseminationId = dissemination.id,
                                institutionId = newInstitution.id
                            )
                            disseminationInstitutionRepository.save(disseminationInstitution)
                        }
                    }
                }

                if (root.at("/services/service/$i/service-category/value").asText() == "Participação em evento") {
                    var year = "1999"
                    var month = "01"
                    var day = "01"

                    //check year
                    if (!root.at("/services/service/$i/event-participation/start-date/year").isNull) {
                        year = root.at("/services/service/$i/event-participation/start-date/year").asText()
                    }

                    //check month
                    if (!root.at("/services/service/$i/event-participation/start-date/month").isNull) {
                        month = root.at("/services/service/$i/event-participation/start-date/month").asText()
                    }

                    //check day
                    if (!root.at("/services/service/$i/event-participation/start-date/day").isNull) {
                        day = root.at("/services/service/$i/event-participation/start-date/day").asText()
                    }

                    val validatedDate = "$year-$month-$day"

                    val dissemination = Dissemination(
                        disseminationCategory = DisseminationCategory.OTHER_DISSEMINATION,
                        title = root.at("/services/service/$i/event-participation/event-name").asText(),
                        date = dateFormat.parse(validatedDate),
                        description = root.at("/services/service/$i/event-participation/event-description").asText()
                    )

                    disseminationRepository.save(dissemination)

                    val disseminationResearcher = DisseminationResearcher(
                        disseminationId = dissemination.id,
                        researcherId = getId
                    )

                    //--save ResearcherDissemination
                    disseminationResearcherRepository.save(disseminationResearcher)

                    //getInstitutionsSize
                    val institutionsSize = root.at("/services/service/$i/event-participation/institutions/total").asInt()
                    val allInstitutions = institutionRepository.findAll()
                    for (i2 in 0 until institutionsSize) {
                        var flagExist = false
                        if(allInstitutions.isNotEmpty()) {
                            for (institution in allInstitutions) {
                                if (institution.name == root.at("/services/service/$i/event-participation/institutions/institution/$i2/institution-name")
                                        .asText()
                                ) {
                                    val disseminationInstitution = DisseminationInstitution(
                                        disseminationId = dissemination.id,
                                        institutionId = institution.id
                                    )
                                    disseminationInstitutionRepository.save(disseminationInstitution)
                                    flagExist = true
                                    break
                                }
                            }
                            if(!flagExist) {
                                val newInstitution = Institution(
                                    name = root.at("/services/service/$i/event-participation/institutions/institution/$i2/institution-name").asText(),
                                    country = root.at("/services/service/$i/event-participation/institutions/institution/$i2/institution-address/country/value").asText()
                                )
                                institutionRepository.save(newInstitution)

                                val disseminationInstitution = DisseminationInstitution(
                                    disseminationId = dissemination.id,
                                    institutionId = newInstitution.id
                                )
                                disseminationInstitutionRepository.save(disseminationInstitution)
                            }
                        } else {
                            val newInstitution = Institution(
                                name = root.at("/services/service/$i/event-participation/institutions/institution/$i2/institution-name")
                                    .asText(),
                                country = root.at("/services/service/$i/event-participation/institutions/institution/$i2/institution-address/country/value")
                                    .asText()
                            )
                            institutionRepository.save(newInstitution)

                            val disseminationInstitution = DisseminationInstitution(
                                disseminationId = dissemination.id,
                                institutionId = newInstitution.id
                            )
                            disseminationInstitutionRepository.save(disseminationInstitution)
                        }
                    }
                }
            }
            model["fullName"] = fullName

            return "cv-update-success"
        }

        return "cv-update-unsuccessfully"
    }


    @GetMapping(value = ["/scientific-activity-form"])
    fun showScientificActivityForm(model: ModelMap): String {
        return "forms-section/scientific-activity-form"
    }

}