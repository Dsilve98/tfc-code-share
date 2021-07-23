package pt.ulusofona.tfc.trabalho.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import pt.ulusofona.tfc.trabalho.ExcelExporter
import pt.ulusofona.tfc.trabalho.dao.Institution
import pt.ulusofona.tfc.trabalho.dao.Researcher
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.*
import pt.ulusofona.tfc.trabalho.form.AddResearcherForm
import pt.ulusofona.tfc.trabalho.form.DisseminationForm
import pt.ulusofona.tfc.trabalho.form.ResearcherForm
import pt.ulusofona.tfc.trabalho.repository.*
import java.io.File
import java.io.InputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import kotlin.collections.ArrayList


@Controller
@RequestMapping("/admin")
class AdminController(val researcherRepository: ResearcherRepository,
                      val institutionRepository: InstitutionRepository,
                      val disseminationRepository: DisseminationRepository,
                      val disseminationResearcherRepository: DisseminationResearcherRepository,
                      val disseminationInstitutionRepository: DisseminationInstitutionRepository,
                      val publicationRepository: PublicationRepository,
                      val publicationResearcherRepository: PublicationResearcherRepository,
                      val projectRepository: ProjectRepository,
                      val projectResearcherRepository: ProjectResearcherRepository,
                      val projectInstitutionRepository: ProjectInstitutionRepository,
                      val otherScientificActivityRepository: OtherScientificActivityRepository,
                      val otherScientificActivityResearcherRepository: OtherScientificActivityResearcherRepository,
                      val otherScientificActivityInstitutionRepository: OtherScientificActivityInstitutionRepository){

    @GetMapping(value = ["/searches"])
    fun showSearches(model: ModelMap): String{
        return "admin-section/searches"
    }

    @GetMapping(value = ["/researcher-management"])
    fun showResearcherManagement(model: ModelMap): String{
        val researchers = researcherRepository.findAll()
        model["researchers"] = researchers
        return "admin-section/researcher-management"
    }

    @GetMapping(value = ["/export-excel"])
    fun showExportButtonPage(model: ModelMap): String {
        return "admin-section/export-excel"
    }

    @GetMapping(value = ["/export-excel-accept"])
    fun exportToExcel(response: HttpServletResponse): String {
        response.contentType = "application/octet-stream"
        val headerKey = "Content-Disposition"

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd_HH:mm")
        val currentDateTime = dateFormatter.format(Date())
        val fileName = "dados-ceied_$currentDateTime.xlsx"
        val headerValue = "attachement; filename = $fileName"

        response.setHeader(headerKey,headerValue)

        val researchers = researcherRepository.findAll()

        //Disseminations
        val listDissemination = disseminationRepository.findAll()
        val mapDissemination = mutableMapOf<String,MutableList<Dissemination>>()

        for (dissemination in listDissemination){
            val connectedTable = disseminationResearcherRepository.findByDisseminationId(dissemination.id)
            val researcher = researcherRepository.findById(connectedTable.get().researcherId)
            val cienciaID = researcher.get().cienciaId
            mapDissemination.getOrPut(cienciaID,::mutableListOf).add(dissemination)
        }

        //OtherScientificActivity
        val listOtherScientificActivity = otherScientificActivityRepository.findAll()
        val mapOtherScientificActivity = mutableMapOf<String,MutableList<OtherScientificActivity>>()

        for (otherScientificActivity in listOtherScientificActivity){
            val connectedTable = otherScientificActivityResearcherRepository.findByOtherScientificActivityId(otherScientificActivity.id)
            val researcher = researcherRepository.findById(connectedTable.get().researcherId)
            val cienciaID = researcher.get().cienciaId
            mapOtherScientificActivity.getOrPut(cienciaID,::mutableListOf).add(otherScientificActivity)
        }

        //Project
        val listProject = projectRepository.findAll()
        val mapProject = mutableMapOf<String,MutableList<Project>>()

        for (project in listProject){
            val connectedTable = projectResearcherRepository.findByProjectId(project.id)
            val researcher = researcherRepository.findById(connectedTable.get().researcherId)
            val cienciaID = researcher.get().cienciaId
            mapProject.getOrPut(cienciaID,::mutableListOf).add(project)
        }

        //Publication
        val listPublication = publicationRepository.findAll()
        val mapPublication = mutableMapOf<String,MutableList<Publication>>()

        for (publication in listPublication){
            val connectedTable = publicationResearcherRepository.findByPublicationId(publication.id)
            val researcher = researcherRepository.findById(connectedTable.get().researcherId)
            val cienciaID = researcher.get().cienciaId
            mapPublication.getOrPut(cienciaID,::mutableListOf).add(publication)
        }

        //Institutions
        val listInstitution = institutionRepository.findAll()
        val mapDissInst = mutableMapOf<Institution,MutableList<Long>>()
        val mapProjectInst = mutableMapOf<Institution,MutableList<Long>>()
        val mapOtherSAInst = mutableMapOf<Institution,MutableList<Long>>()

        for(institution in listInstitution){
            // in disseminations
            val connectedTablesDiss = disseminationInstitutionRepository.findByInstitutionId(institution.id)
            if (connectedTablesDiss.isNotEmpty()){
                val dissIds = mutableListOf<Long>()
                connectedTablesDiss.forEach { i -> dissIds.add(i.disseminationId)  }
                dissIds.forEach { id -> mapDissInst.getOrPut(institution,::mutableListOf).add(id) }
            }

            // in projects
            val connectedTablesProject = projectInstitutionRepository.findByInstitutionId(institution.id)
            if (connectedTablesProject.isNotEmpty()){
                val projectsIds = mutableListOf<Long>()
                connectedTablesProject.forEach { i -> projectsIds.add(i.projectId) }
                projectsIds.forEach { id -> mapProjectInst.getOrPut(institution,::mutableListOf).add(id) }
            }

            // in otherSA
            val connectedTablesOtherSA = otherScientificActivityInstitutionRepository.findByInstitutionId(institution.id)
            if (connectedTablesOtherSA.isNotEmpty()){
                val otherSAIds = mutableListOf<Long>()
                connectedTablesOtherSA.forEach { i -> otherSAIds.add(i.otherScientificActivityId) }
                otherSAIds.forEach { id-> mapOtherSAInst.getOrPut(institution,::mutableListOf).add(id) }
            }
        }

        val excelExporter = ExcelExporter(
                researchers,
                mapDissemination,
                mapOtherScientificActivity,
                mapProject,
                mapPublication,
                mapDissInst,
                mapOtherSAInst,
                mapProjectInst
        )

        excelExporter.export(response)

        return "redirect:/admin-section/export-excel"
    }


    @GetMapping(value = ["/user/{orcid}"])
    fun viewResearcherProfile(@PathVariable("orcid") orcid : String, model: ModelMap): String{

        val researcherOptional = researcherRepository.findById(orcid)
        if (researcherOptional.isPresent) {
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
            return "admin-section/personal-information"
        }else{
            return "not-found/researcher404"
        }
    }

    @GetMapping(value = ["/add-researcher-form"])
    fun showResearcherForm(model: ModelMap): String{
        model["addResearcherForm"] = AddResearcherForm()
        return "forms-section/add-researcher-form"
    }
    @PostMapping(value = ["/add-researcher-form"])
    fun addResearcher(@Valid @ModelAttribute("addResearcherForm") addResearcherForm: AddResearcherForm,
                         bindingResult: BindingResult,
                         redirectAttributes: RedirectAttributes): String{

        if(bindingResult.hasErrors()){
            return "forms-section/add-researcher-form"
        }

        val orcid = addResearcherForm.orcid!!
        val name = addResearcherForm.name!!

        var alreadyExistOnFile = false
        val inputStream: InputStream = File("src/main/resources/first_time_user_list_test.txt").inputStream()
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
        lineList.forEach{
            if("https://sandbox.orcid.org/${orcid}" == it) {
                alreadyExistOnFile = true
            }
        }
        if(!alreadyExistOnFile) {
            File("src/main/resources/first_time_user_list_test.txt").appendText("\nhttps://sandbox.orcid.org/${orcid}")
        }

        redirectAttributes.addFlashAttribute("message","O Investigador ${name} já pode registar-se no site!")
        return "redirect:/admin/researcher-management"
    }
    @GetMapping(value = ["/user/edit/{orcid}"])
    fun editResearcherProfile(@PathVariable("orcid") orcid : String,
                              model: ModelMap,
                              redirectAttributes: RedirectAttributes): String{

        val researcherOptional = researcherRepository.findById(orcid)
        if (researcherOptional.isPresent) {
            val researcher = researcherOptional.get()
            var isAdminOptional = researcher.isAdmin
            if (isAdminOptional == null){
                isAdminOptional = false
            }
            model["researcherForm"] = ResearcherForm(
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
                isAdmin = isAdminOptional
            )
        }
        return "forms-section/personal-information-form"
    }
    @PostMapping(value = ["/user/edit"])
    fun createResearcher(@Valid @ModelAttribute("researcherForm") researcherForm: ResearcherForm,
                         bindingResult: BindingResult,
                         redirectAttributes: RedirectAttributes): String{


        if(bindingResult.hasErrors()){
           return "forms-section/personal-information-form"
            //return "admin-section/researcher-management"
        }
        var isAdminOptional = researcherForm.isAdmin
        if (isAdminOptional == null){
            isAdminOptional = false
        }
        val researcher = Researcher(
                orcid = researcherForm.orcid!!,
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
                isAdmin = researcherForm.isAdmin!!,
        )

        researcherRepository.save(researcher)

       if(researcher.isAdmin == true) {
           File("src/main/resources/admin_list_test.txt").appendText("\nhttps://sandbox.orcid.org/${researcher.orcid}")
       } else {
           removeRoleFromFile("src/main/resources/admin_list_test.txt", researcher.orcid)
       }

        redirectAttributes.addFlashAttribute("message","Investigador ${researcher.name} editado com sucesso!")
        return "redirect:/admin/researcher-management"
    }

    @GetMapping("/user/delete/{orcid}")
    fun deleteResearcher(@PathVariable orcid: String, redirectAttributes: RedirectAttributes): String{
        if (researcherRepository.findById(orcid).isPresent){

            removeRoleFromFile("src/main/resources/admin_list_test.txt", orcid)
            removeRoleFromFile("src/main/resources/user_list_test.txt", orcid)
            removeRoleFromFile("src/main/resources/first_time_user_list_test.txt", orcid)

            redirectAttributes.addFlashAttribute("message","Investigador ${researcherRepository.findById(orcid).get().name} eliminado com sucesso!")
            researcherRepository.deleteById(orcid)
        }
        return "redirect:/admin/researcher-management"
    }


    @GetMapping(value = ["/scientific-activities/{orcid}"])
    fun viewResearcherSA(@PathVariable("orcid") orcid : String, model: ModelMap): String{

        val researcherOptional = researcherRepository.findById(orcid)

        if (researcherOptional.isPresent) {
            val researcher = researcherOptional.get()
            //Recolha de apenas alguns dados (e não do investigador completo) que queiramos usar na pagina das atividades
            model["researcherInfo"] = mapOf(
                    "orcid" to researcher.orcid,
                    "name" to researcher.name,
                    "email" to researcher.email,
                    "cienciaId" to researcher.cienciaId,
                    "researcherCategory" to researcher.researcherCategory,
                    "isAdmin" to researcher.isAdmin,
            )

            val disseminations = ArrayList<Dissemination>()
            val publications = ArrayList<Publication>()
            val projects = ArrayList<Project>()
            val otherScientificActivities = ArrayList<OtherScientificActivity>()

            val disseminationResearcherlist = disseminationResearcherRepository.findByResearcherId(orcid)
            disseminationResearcherlist
                    .map { disseminationRepository.findById(it.disseminationId) }
                    .filter { it.isPresent }
                    .mapTo(disseminations) { it.get() }
            model["disseminations"] = disseminations

            val publicationResearcherlist = publicationResearcherRepository.findByResearcherId(orcid)
            publicationResearcherlist
                    .map { publicationRepository.findById(it.publicationId) }
                    .filter { it.isPresent }
                    .mapTo(publications) { it.get() }
            model["publications"] = publications


            val projectResearcherlist = projectResearcherRepository.findByResearcherId(orcid)
            projectResearcherlist
                    .map { projectRepository.findById(it.projectId) }
                    .filter { it.isPresent }
                    .mapTo(projects) { it.get() }
            model["projects"] = projects

            val otherScientificActivityResearcherList = otherScientificActivityResearcherRepository.findByResearcherId(orcid)
            otherScientificActivityResearcherList
                    .map { otherScientificActivityRepository.findById(it.otherScientificActivityId) }
                    .filter { it.isPresent }
                    .mapTo(otherScientificActivities) { it.get() }

            val advancedEducations = otherScientificActivities.filter { it.otherType == OtherType.ADVANCED_EDUCATION }
            model["advancedEducations"] = advancedEducations

            val scientificInitOfYoungStudents = otherScientificActivities.filter { it.otherType == OtherType.SCIENTIFIC_INIT_OF_YOUNG_STUDENTS }
            model["scientificInitOfYoungStudents"] = scientificInitOfYoungStudents

            return "admin-section/scientific-activities"
        }else{
            return "not-found/researcher404"
        }
    }

    @GetMapping(value = ["{orcid}/dissemination-form"])
    fun showDisseminationForm(@PathVariable orcid : String,
                              model: ModelMap): String{
        model["disseminationForm"] = DisseminationForm()
        model["orcid"] = orcid
        //model["orcid"] = orcid
        return "forms-section/dissemination-form"
    }

    @PostMapping(value = ["{orcid}/dissemination-form"])
    fun createDissemination(@Valid
                            @ModelAttribute("disseminationForm") disseminationForm: DisseminationForm,
                            @PathVariable orcid : String,
                            bindingResult: BindingResult,
                            redirectAttributes: RedirectAttributes):String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        //--criar Dissemination - sem ID
        val dissemination = Dissemination(
                disseminationCategory = disseminationForm.disseminationCategory!!,
                title = disseminationForm.title!!,
                date = dateFormat.parse(disseminationForm.date!!),
                description = disseminationForm.description!!
        )

        //--save Dissemination - já tenho ID
        disseminationRepository.save(dissemination)

        //--criar ResearcherDissemination com o ID do dissemination e com o orcid do principal
        val disseminationResearcher = DisseminationResearcher(
                disseminationId = dissemination.id,
                researcherId = orcid
        )

        //--save ResearcherDissemination
        disseminationResearcherRepository.save(disseminationResearcher)

        return "redirect:/admin/user/$orcid"
    }

    @GetMapping("/dissemination/delete/{id}")
    fun deleteDissemination(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String{
        var orcid = "ORCID"
        if (disseminationRepository.findById(id).isPresent){

            redirectAttributes.addFlashAttribute("message","Investigador ${disseminationRepository.findById(id).get().title} eliminado com sucesso!")
            orcid = disseminationResearcherRepository.findByDisseminationId(id).get().researcherId
            disseminationResearcherRepository.deleteByDisseminationId(id)
            disseminationRepository.deleteById(id)
        }
        return "redirect:/admin/scientific-activities/$orcid"
    }

    @GetMapping(value = ["/scientific-activity/{type}/{id}"])
    fun viewSingleScientificActivity(@PathVariable("type") type : String,
                                     @PathVariable("id") id : Long,
                                     model: ModelMap): String{

        when(type){
            "advanced-education" ->{
                val advancedEduOptional = otherScientificActivityRepository
                        .findByOtherTypeAndId(OtherType.ADVANCED_EDUCATION,id)
                return if (advancedEduOptional.isPresent){
                    val advancedEdu = advancedEduOptional.get()
                    model["advancedEdu"] = OtherScientificActivity(
                            id = advancedEdu.id,
                            otherCategory = advancedEdu.otherCategory,
                            otherType = advancedEdu.otherType,
                            title = advancedEdu.title,
                            date = advancedEdu.date,
                            description = advancedEdu.description
                    )
                    val otherScientificActivitiesInstitutions = otherScientificActivityInstitutionRepository.findByOtherScientificActivityId(advancedEdu.id)
                    val institutions = mutableListOf<Institution>()
                    for (otherScientificActivityInstitution in otherScientificActivitiesInstitutions) {
                        institutions.add(institutionRepository.findById(otherScientificActivityInstitution.institutionId).get())
                    }
                    model["institutions"] = institutions
                    "researcher-section/scientific-activity"
                }else{
                    "not-found/researcher404"
                }
            }
            "scientific-initiation" ->{
                val scientificInitOptional = otherScientificActivityRepository
                        .findByOtherTypeAndId(OtherType.SCIENTIFIC_INIT_OF_YOUNG_STUDENTS,id)
                return if (scientificInitOptional.isPresent){
                    val scientificInit = scientificInitOptional.get()
                    model["scientificInit"] = OtherScientificActivity(
                            id = scientificInit.id,
                            otherCategory = scientificInit.otherCategory,
                            otherType = scientificInit.otherType,
                            title = scientificInit.title,
                            date = scientificInit.date,
                            description = scientificInit.description
                    )
                    val otherScientificActivitiesInstitutions = otherScientificActivityInstitutionRepository.findByOtherScientificActivityId(scientificInit.id)
                    val institutions = mutableListOf<Institution>()
                    for (otherScientificActivityInstitution in otherScientificActivitiesInstitutions) {
                        institutions.add(institutionRepository.findById(otherScientificActivityInstitution.institutionId).get())
                    }
                    model["institutions"] = institutions
                    "researcher-section/scientific-activity"
                }else{
                    "not-found/researcher404"
                }
            }
            "publication" ->{
                val publicationOptional = publicationRepository.findById(id)
                return if (publicationOptional.isPresent){
                    val publication = publicationOptional.get()
                    model["publication"] = Publication(
                            id = publication.id,
                            publicationCategory = publication.publicationCategory,
                            title = publication.title,
                            publicationDate = publication.publicationDate,
                            descriptor = publication.descriptor,
                            publisher = publication.publisher,
                            authors = publication.authors,
                            indexation = publication.indexation,
                            conferenceName = publication.conferenceName
                    )
                    "researcher-section/scientific-activity"
                }else{
                    "not-found/researcher404"
                }
            }
            "project" ->{
                val projectOptional = projectRepository.findById(id)
                return if (projectOptional.isPresent){
                    val project = projectOptional.get()
                    model["project"] = Project(
                            id = project.id,
                            projectCategory = project.projectCategory,
                            title = project.title,
                            initialDate = project.initialDate,
                            finalDate = project.finalDate,
                            abstract = project.abstract,
                            description = project.description
                    )
                    val projectsInstitutions = projectInstitutionRepository.findByProjectId(project.id)
                    val institutions = mutableListOf<Institution>()
                    for (projectInstitution in projectsInstitutions) {
                        institutions.add(institutionRepository.findById(projectInstitution.institutionId).get())
                    }
                    model["institutions"] = institutions
                    "researcher-section/scientific-activity"
                }else{
                    "not-found/researcher404"
                }
            }
            "dissemination" ->{
                val disseminationOptional = disseminationRepository.findById(id)
                return if (disseminationOptional.isPresent){
                    val dissemination = disseminationOptional.get()
                    model["dissemination"] = Dissemination(
                            id = dissemination.id,
                            disseminationCategory = dissemination.disseminationCategory,
                            title = dissemination.title,
                            date = dissemination.date,
                            description = dissemination.description
                    )
                    val disseminationsInstitutions = disseminationInstitutionRepository.findByDisseminationId(dissemination.id)
                    val institutions = mutableListOf<Institution>()
                    for (disseminationInstitution in disseminationsInstitutions) {
                        institutions.add(institutionRepository.findById(disseminationInstitution.institutionId).get())
                    }
                    model["institutions"] = institutions

                    "researcher-section/scientific-activity"
                }else{
                    "not-found/researcher404"
                }
            }
        }
        return "not-found/researcher404"
    }

    @GetMapping(value = ["/annual-report"])
    fun showAnnualReport(model: ModelMap): String{
        return "admin-section/annual-report"
    }

    fun removeRoleFromFile(path: String, orcid: String) {
        var content = mutableListOf<String>()
        var existOnFile = false
        val inputStream: InputStream = File(path).inputStream()
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it)} }
        lineList.forEach{
            if("orcid" != it) {
                content.add("\n" + it)
            } else {
                content.add(it)
            }
            if("https://sandbox.orcid.org/${orcid}" == it) {
                existOnFile = true
            }
        }
        if(existOnFile) {
            content.remove("\nhttps://sandbox.orcid.org/${orcid}")
            PrintWriter(path).close()
            for(i in content) {
                File(path).appendText(i)
            }
        }
    }


}