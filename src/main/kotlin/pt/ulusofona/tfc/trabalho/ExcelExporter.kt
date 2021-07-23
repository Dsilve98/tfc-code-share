package pt.ulusofona.tfc.trabalho

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.xml.sax.ext.Attributes2
import pt.ulusofona.tfc.trabalho.dao.Institution
import pt.ulusofona.tfc.trabalho.dao.Researcher
import pt.ulusofona.tfc.trabalho.dao.scientificActivities.*
import java.io.IOException
import javax.servlet.http.HttpServletResponse

class ExcelExporter(
        private var listResearchers: List<Researcher>,
        private var mapDissemination: MutableMap<String, MutableList<Dissemination>>,
        private var mapOtherScientificActivity: MutableMap<String, MutableList<OtherScientificActivity>>,
        private var mapProject: MutableMap<String, MutableList<Project>>,
        private var mapPublication: MutableMap<String, MutableList<Publication>>,
        private var mapDissInst: MutableMap<Institution,MutableList<Long>>,
        private var mapOtherSAInst: MutableMap<Institution,MutableList<Long>>,
        private var mapProjectInst: MutableMap<Institution,MutableList<Long>>
        ) {
    private var workbook: XSSFWorkbook = XSSFWorkbook()
    private var sheetResearchers: XSSFSheet = workbook.createSheet("Investigadores")
    private var sheetScientificActivities: XSSFSheet = workbook.createSheet("Atividades Científicas")
    private var researcherReference = "A2"
    private var SAReference = "A2"


    fun writeHeaderRowResearchers(){
        val row = sheetResearchers.createRow(0)

        row.heightInPoints = 40F

        val cellStyle = workbook.createCellStyle()

        val font = workbook.createFont()
        font.bold = true
        font.fontHeightInPoints = 18
        font.setColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))
        cellStyle.setFont(font)

        //rgb(22, 160, 133) light green
        //rgb(8, 105, 114) dark green

        cellStyle.setFillForegroundColor(XSSFColor(byteArrayOf(8.toByte(), 105.toByte(), 114.toByte()),null))
        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        cellStyle.borderRight = BorderStyle.MEDIUM
        cellStyle.borderLeft = BorderStyle.MEDIUM
        cellStyle.borderTop = BorderStyle.MEDIUM
        cellStyle.borderBottom = BorderStyle.MEDIUM

        cellStyle.setRightBorderColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))
        cellStyle.setLeftBorderColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))
        cellStyle.setTopBorderColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))
        cellStyle.setBottomBorderColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))

        cellStyle.alignment = HorizontalAlignment.CENTER
        cellStyle.verticalAlignment = VerticalAlignment.CENTER

        // Celulas Individuais
        val cellCienciaId = row.createCell(0)
        cellCienciaId.setCellValue("Ciência ID")
        cellCienciaId.cellStyle = cellStyle

        val cellName = row.createCell(1)
        cellName.setCellValue("Nome")
        cellName.cellStyle = cellStyle

        val cellEmail = row.createCell(2)
        cellEmail.setCellValue("Email")
        cellEmail.cellStyle = cellStyle

        val cellUserFCT = row.createCell(3)
        cellUserFCT.setCellValue("Utilizador FCT")
        cellUserFCT.cellStyle = cellStyle

        val cellAssociationKeyFct = row.createCell(4)
        cellAssociationKeyFct.setCellValue("Chave Pública FCT")
        cellAssociationKeyFct.cellStyle = cellStyle

        val cellSiteCeied = row.createCell(5)
        cellSiteCeied.setCellValue("Site CeiED")
        cellSiteCeied.cellStyle = cellStyle

        val cellOrcid = row.createCell(6)
        cellOrcid.setCellValue("ORCID")
        cellOrcid.cellStyle = cellStyle

        val cellOrigin = row.createCell(7)
        cellOrigin.setCellValue("Origem")
        cellOrigin.cellStyle = cellStyle

        val cellPhdYear = row.createCell(8)
        cellPhdYear.setCellValue("Ano de Doutoramento")
        cellPhdYear.cellStyle = cellStyle

        val cellProfessionalStatus = row.createCell(9)
        cellProfessionalStatus.setCellValue("Situação Profissional")
        cellProfessionalStatus.cellStyle = cellStyle

        val cellProfessionalCategory = row.createCell(10)
        cellProfessionalCategory.setCellValue("Categoria Profissional")
        cellProfessionalCategory.cellStyle = cellStyle

        val cellPhoneNumber = row.createCell(11)
        cellPhoneNumber.setCellValue("Telefone")
        cellPhoneNumber.cellStyle = cellStyle
    }

    fun writeDataRowsResearchers(){
        var rowCount = 1

        val cellStyleData = workbook.createCellStyle()
        val fontData = workbook.createFont()
        fontData.fontHeightInPoints = 14
        cellStyleData.setFont(fontData)

        for(researcher in listResearchers){
            val row = sheetResearchers.createRow(rowCount++)

            val cellCienciaId = row.createCell(0)
            cellCienciaId.setCellValue(researcher.cienciaId)

            cellCienciaId.cellStyle = cellStyleData

            val cellName = row.createCell(1)
            cellName.setCellValue(researcher.name)
            cellName.cellStyle = cellStyleData

            val cellEmail = row.createCell(2)
            cellEmail.setCellValue(researcher.email)
            cellEmail.cellStyle = cellStyleData

            val cellUserFCT = row.createCell(3)
            cellUserFCT.setCellValue(researcher.utilizador)
            cellUserFCT.cellStyle = cellStyleData

            val cellAssociationKeyFct = row.createCell(4)
            cellAssociationKeyFct.setCellValue(researcher.associationKeyFct)
            cellAssociationKeyFct.cellStyle = cellStyleData

            val cellSiteCeied = row.createCell(5)
            cellSiteCeied.setCellValue(researcher.siteCeied)
            cellSiteCeied.cellStyle = cellStyleData

            val cellOrcid = row.createCell(6)
            cellOrcid.setCellValue(researcher.orcid)
            cellOrcid.cellStyle = cellStyleData

            val cellOrigin = row.createCell(7)
            cellOrigin.setCellValue(researcher.origin)
            cellOrigin.cellStyle = cellStyleData

            val cellPhdYear = row.createCell(8)
            cellPhdYear.setCellValue(researcher.phdYear.toString())
            cellPhdYear.cellStyle = cellStyleData

            val cellProfessionalStatus = row.createCell(9)
            cellProfessionalStatus.setCellValue(researcher.professionalStatus)
            cellProfessionalStatus.cellStyle = cellStyleData

            val cellProfessionalCategory = row.createCell(10)
            cellProfessionalCategory.setCellValue(researcher.professionalCategory)
            cellProfessionalCategory.cellStyle = cellStyleData
            researcherReference = cellProfessionalCategory.reference

            val cellPhoneNumber = row.createCell(11)
            cellPhoneNumber.setCellValue(researcher.phoneNumber)
            cellPhoneNumber.cellStyle = cellStyleData
            researcherReference = cellPhoneNumber.reference
        }
        sheetResearchers.autoSizeColumn(0)
        sheetResearchers.autoSizeColumn(1)
        sheetResearchers.autoSizeColumn(2)
        sheetResearchers.autoSizeColumn(3)
        sheetResearchers.autoSizeColumn(4)
        sheetResearchers.autoSizeColumn(5)
        sheetResearchers.autoSizeColumn(6)
        sheetResearchers.autoSizeColumn(7)
        sheetResearchers.autoSizeColumn(8)
        sheetResearchers.autoSizeColumn(9)
        sheetResearchers.autoSizeColumn(10)
        sheetResearchers.autoSizeColumn(11)
        sheetResearchers.createFreezePane(0,1)
        sheetResearchers.setAutoFilter(CellRangeAddress.valueOf("A1:$researcherReference"))
    }

    fun writeHeaderRowSA(){
        val row = sheetScientificActivities.createRow(0)
        row.heightInPoints = 40F
        val cellStyle = workbook.createCellStyle()

        val font = workbook.createFont()

        font.bold = true
        font.fontHeightInPoints = 18
        font.setColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))
        cellStyle.setFont(font)

        //rgb(232,29,38) light red
        //rgb(215, 65, 65) dark red
        cellStyle.setFillForegroundColor(XSSFColor(byteArrayOf(215.toByte(), 65.toByte(), 65.toByte()),null))
        cellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        cellStyle.borderRight = BorderStyle.MEDIUM
        cellStyle.borderLeft = BorderStyle.MEDIUM
        cellStyle.borderTop = BorderStyle.MEDIUM
        cellStyle.borderBottom = BorderStyle.MEDIUM

        cellStyle.setRightBorderColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))
        cellStyle.setLeftBorderColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))
        cellStyle.setTopBorderColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))
        cellStyle.setBottomBorderColor(XSSFColor(byteArrayOf(255.toByte(), 255.toByte(), 255.toByte()),null))

        cellStyle.alignment = HorizontalAlignment.CENTER
        cellStyle.verticalAlignment = VerticalAlignment.CENTER

        //Celulas Individuais
        val cellCienciaId = row.createCell(0)
        cellCienciaId.setCellValue("Ciência ID")
        cellCienciaId.cellStyle = cellStyle

        val cellInitialDate = row.createCell(1)
        cellInitialDate.setCellValue("Data de Início")
        cellInitialDate.cellStyle = cellStyle

        val cellFinalDate = row.createCell(2)
        cellFinalDate.setCellValue("Data de Fim")
        cellFinalDate.cellStyle = cellStyle

        val cellActivityType = row.createCell(3)
        cellActivityType.setCellValue("Tipo de Atividade")
        cellActivityType.cellStyle = cellStyle

        val cellActivityCategory = row.createCell(4)
        cellActivityCategory.setCellValue("Categoria de Atividade")
        cellActivityCategory.cellStyle = cellStyle

        val cellDescriptor = row.createCell(5)
        cellDescriptor.setCellValue("Descritor")
        cellDescriptor.cellStyle = cellStyle

        val cellPublisher = row.createCell(6)
        cellPublisher.setCellValue("Editora/Livro")
        cellPublisher.cellStyle = cellStyle

        val cellAuthors = row.createCell(7)
        cellAuthors.setCellValue("Autores")
        cellAuthors.cellStyle = cellStyle

        val cellInstitution = row.createCell(8)
        cellInstitution.setCellValue("Instituição")
        cellInstitution.cellStyle = cellStyle

    }

    fun writeDataRowsSA(){
        var rowCount = 1
        val cellStyleData = workbook.createCellStyle()
        val fontData = workbook.createFont()
        fontData.fontHeightInPoints = 14
        cellStyleData.setFont(fontData)

        val cellStyleDate = cellStyleData
        val createHelper = workbook.creationHelper
        cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"))

        for(researcherPublications in mapPublication){
            for (publication in mapPublication[researcherPublications.key].orEmpty()){
                val row = sheetScientificActivities.createRow(rowCount++)

                val cellCienciaId = row.createCell(0)
                cellCienciaId.setCellValue(researcherPublications.key)
                cellCienciaId.cellStyle = cellStyleData

                val cellDate = row.createCell(1)
                cellDate.setCellValue(publication.publicationDate)
                cellDate.cellStyle = cellStyleDate

                val cellType = row.createCell(3)
                cellType.setCellValue("Produção Científica")
                cellType.cellStyle = cellStyleData

                val cellCategory = row.createCell(4)
                cellCategory.setCellValue(renamePublicationCategoryEnum(publication.publicationCategory))
                cellCategory.cellStyle = cellStyleData

                val cellDescriptor = row.createCell(5)
                cellDescriptor.setCellValue(publication.descriptor)
                cellDescriptor.cellStyle = cellStyleData

                val cellPublisher = row.createCell(6)
                cellPublisher.setCellValue(publication.publisher)
                cellPublisher.cellStyle = cellStyleData
                SAReference = cellPublisher.reference

                val cellAuthors = row.createCell(7)
                cellAuthors.setCellValue(publication.authors)
                cellAuthors.cellStyle = cellStyleData
                SAReference = cellAuthors.reference
            }
        }

        for (researcherProjects in mapProject){
            for (project in mapProject[researcherProjects.key].orEmpty()){
                val row = sheetScientificActivities.createRow(rowCount++)

                val cellCienciaId = row.createCell(0)
                cellCienciaId.setCellValue(researcherProjects.key)
                cellCienciaId.cellStyle = cellStyleData

                val cellInitialDate = row.createCell(1)
                cellInitialDate.setCellValue(project.initialDate)
                cellInitialDate.cellStyle = cellStyleDate

                val cellFinalDate = row.createCell(2)
                cellFinalDate.setCellValue(project.finalDate)
                cellFinalDate.cellStyle = cellStyleDate

                val cellType = row.createCell(3)
                cellType.setCellValue("Projeto")
                cellType.cellStyle = cellStyleData

                val cellCategory = row.createCell(4)
                cellCategory.setCellValue(renameProjectCategoryEnum(project.projectCategory))
                cellCategory.cellStyle = cellStyleData
                SAReference = cellCategory.reference

                for (institution in mapProjectInst){
                    if (institution.value.contains(project.id)){
                        val cellInstitution = row.createCell(8)
                        cellInstitution.setCellValue(institution.key.name)
                        cellInstitution.cellStyle = cellStyleData
                        SAReference = cellInstitution.reference
                        break
                    }
                }


            }
        }

        for (researcherOtherScientificActivities in mapOtherScientificActivity) {
            for(otherScientificActivity in mapOtherScientificActivity[researcherOtherScientificActivities.key].orEmpty()){
                val row = sheetScientificActivities.createRow(rowCount++)

                val cellCienciaId = row.createCell(0)
                cellCienciaId.setCellValue(researcherOtherScientificActivities.key)
                cellCienciaId.cellStyle = cellStyleData

                val cellDate = row.createCell(1)
                cellDate.setCellValue(otherScientificActivity.date)
                cellDate.cellStyle = cellStyleDate

                val cellType = row.createCell(3)

                val type = otherScientificActivity.otherType.toString()
                when (type) {
                    "ADVANCED_EDUCATION" -> {
                        cellType.setCellValue("Formação Avançada")
                    }
                    "SCIENTIFIC_INIT_OF_YOUNG_STUDENTS" -> {
                        cellType.setCellValue("Iniciação Científica de Jovens Estudantes")
                    }
                    else -> {
                        cellType.setCellValue("Tipo de Atividade Desconhecida")
                    }
                }
                cellType.cellStyle = cellStyleData

                val cellCategory = row.createCell(4)
                cellCategory.setCellValue(renameOtherCategoryEnum(otherScientificActivity.otherCategory))
                cellCategory.cellStyle = cellStyleData
                SAReference = cellCategory.reference

                for (institution in mapOtherSAInst){
                    if (institution.value.contains(otherScientificActivity.id)){
                        val cellInstitution = row.createCell(8)
                        cellInstitution.setCellValue(institution.key.name)
                        cellInstitution.cellStyle = cellStyleData
                        SAReference = cellInstitution.reference
                        break
                    }
                }
            }
        }

        for (researcherDisseminations in mapDissemination){

            for (dissemination in mapDissemination[researcherDisseminations.key].orEmpty()){
                val row = sheetScientificActivities.createRow(rowCount++)

                val cellCienciaId = row.createCell(0)
                cellCienciaId.setCellValue(researcherDisseminations.key)
                sheetScientificActivities.autoSizeColumn(0)
                cellCienciaId.cellStyle = cellStyleData

                val cellDate = row.createCell(1)
                cellDate.setCellValue(dissemination.date)
                sheetScientificActivities.autoSizeColumn(1)
                cellDate.cellStyle = cellStyleDate

                val cellType = row.createCell(3)
                cellType.setCellValue("Disseminação")
                cellType.cellStyle = cellStyleData

                val cellCategory = row.createCell(4)
                cellCategory.setCellValue(renameDisseminationCategoryEnum(dissemination.disseminationCategory))
                sheetScientificActivities.autoSizeColumn(4)
                cellCategory.cellStyle = cellStyleData
                SAReference = cellCategory.reference

                for (institution in mapDissInst){
                    if (institution.value.contains(dissemination.id)){
                        val cellInstitution = row.createCell(8)
                        cellInstitution.setCellValue(institution.key.name)
                        cellInstitution.cellStyle = cellStyleData
                        SAReference = cellInstitution.reference
                        break
                    }
                }
            }
        }

        sheetScientificActivities.autoSizeColumn(0)
        sheetScientificActivities.autoSizeColumn(1)
        sheetScientificActivities.autoSizeColumn(2)
        sheetScientificActivities.autoSizeColumn(3)
        sheetScientificActivities.autoSizeColumn(4)
        sheetScientificActivities.autoSizeColumn(5)
        sheetScientificActivities.autoSizeColumn(6)
        sheetScientificActivities.autoSizeColumn(7)
        sheetScientificActivities.autoSizeColumn(8)
        sheetScientificActivities.createFreezePane(0,1)
        sheetScientificActivities.setAutoFilter(CellRangeAddress.valueOf("A1:$SAReference"))
    }

    @Throws(IOException::class)
    fun export(response: HttpServletResponse) {
        writeHeaderRowResearchers()
        writeDataRowsResearchers()
        writeHeaderRowSA()
        writeDataRowsSA()
        val outPutStream = response.outputStream
        workbook.write(outPutStream)
        workbook.close()
        outPutStream.close()
    }

    private fun renameOtherCategoryEnum(otherCategory: OtherCategory):String{
        return when(otherCategory) {
            OtherCategory.AGGREGATION_EXAMS_COMPLETION -> "Realização de Provas de Agregação"
            OtherCategory.PHD_SUPERVISION -> "Orientação de Doutoramento"
            OtherCategory.POST_DOCTORATE_SUPERVISION -> "Orientação de Pós-Doutoramento"
            OtherCategory.POST_DOCTORAL_STUDIES -> "Realização de Pós-Doutoramento"
            OtherCategory.PARTICIPATION_IN_DOCTORAL_JURIES -> "Participação em Júris de Doutoramento"
            OtherCategory.MASTERS_SUPERVISION -> "Orientação de Mestrado"
            OtherCategory.INTERNSHIP_SUPERVISION -> "Orientação de Estágio"
            OtherCategory.OTHER_CATEGORY -> "Outra Formação Avançada"
        }
    }

    private fun renameDisseminationCategoryEnum(disseminationCategory: DisseminationCategory):String{
        return when(disseminationCategory) {
            DisseminationCategory.ORGANISING_COMMITTEE_MEMBER -> "Membro da Comissão Organizadora"
            DisseminationCategory.SCIENTIFIC_COMMITTEE_MEMBER -> "Membro da Comissão Científica"
            DisseminationCategory.KNOWLEDGE_AND_TECH_TRANSFER -> "Transferência de Conhecimento e Tecnologia"
            DisseminationCategory.PROMOTION_OF_CULTURE -> "Promoção de Cultura Científica e Tecnológica"
            DisseminationCategory.ACTIONS_TO_SOCIETY -> "Ações de Especial Relevância para a Sociedade"
            DisseminationCategory.OTHER_DISSEMINATION -> "Outra Disseminação"
        }
    }

    private fun renameProjectCategoryEnum(projectCategory: ProjectCategory):String{
        return when(projectCategory) {
            ProjectCategory.INTERNATIONAL_PROJECT -> "Projeto Internacional"
            ProjectCategory.NATIONAL_PROJECT -> "Projeto Nacional"
        }
    }

    private fun renamePublicationCategoryEnum(publicationCategory: PublicationCategory):String{
        return when(publicationCategory) {
            PublicationCategory.KNOWLEDGE_CONTRIBUTION -> "Contribuição para o Avanço e Aplicação do Conhecimento"
            PublicationCategory.NATIONAL_MAGAZINE -> "Artigo em Revista Científica Nacional"
            PublicationCategory.INTERNATIONAL_MAGAZINE -> "Artigo em Revista Científica Internacional"
            PublicationCategory.BOOK_AUTHORSHIP -> "Autoria e Coautoria de Livro"
            PublicationCategory.BOOK_CHAPTER -> "Capítulo de Livro"
            PublicationCategory.BOOK_EDITING_AND_ORGANISATION -> "Edição/organização de Livro"
            PublicationCategory.INTERNATIONAL_CONFERENCE -> "Comunicação em Conferência Internacional"
            PublicationCategory.NATIONAL_CONFERENCE -> "Comunicação em Conferência Nacional"
            PublicationCategory.OTHER_PUBLICATION -> "Outra Publicação"
        }
    }


}