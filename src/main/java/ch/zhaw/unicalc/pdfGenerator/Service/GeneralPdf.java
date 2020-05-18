package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.CompanyRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ProjectRequest;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Pdf Class to generate general Information that every Document needs. (example: Header, footer, etc)
 */
@Service
public class GeneralPdf {

    @Autowired
    public GeneralPdf() {

    }

    /**
     * Generates and adds an Header to the given Document.
     * Header includes name, url and logo of the company.
     *
     * @param doc      The Document to which the Header should be added
     * @param business The BusinessRequest with all needed Information
     */
    public void createHeader(Document doc, CompanyRequest business) throws MalformedURLException {
        float[] widths = {6, 3};
        Table table = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();
        Cell headerLeft = new Cell(1, 1)
                .add(new Paragraph(business.getName() + "\n" + business.getUrl()))
                .setWidth(widths[0])
                .setFontSize(9)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT);
        table.addCell(headerLeft);
        if (business.getLogo() != null) {
            try {
                Image image = new Image(ImageDataFactory.create(business.getLogo())).setMaxHeight(30).setPadding(0);
                Cell headerLogo = new Cell(1, 1)
                        .add(new Paragraph().add(image))
                        .setWidth(widths[1])
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(null);
                table.addCell(headerLogo);
            } catch (MalformedURLException e) {
                throw new MalformedURLException(e.toString());
            }
        }
        table.setMarginBottom(50);
        doc.add(table);
    }


    /**
     * Generates and adds an Letter Head to the given Document.
     * In it, information like state, zip-code, company-name and contact person is noted.
     *
     * @param doc                The Document to which everything should be added
     * @param projectInformation The ProjektInformationRequest with Information about the company and customer
     */
    public void createLetterHead(Document doc, ProjectRequest projectInformation) {
        float[] widths = {5, 1, 2};
        Table table = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();
        Cell customer = new Cell(1, 1)
                .add(new Paragraph(projectInformation.getCustomer().getCompanyName() + "\n"
                        + "C/O " + projectInformation.getCustomer().getName() + "\n"
                        + projectInformation.getCustomer().getDepartment() + "\n"
                        + projectInformation.getCustomer().getAddress() + "\n"
                        + projectInformation.getCustomer().getLand() + "-" + projectInformation.getCustomer().getZip() + " " + projectInformation.getCustomer().getCity()))
                .setFontSize(8)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT);
        table.addCell(customer);
        Cell company = new Cell(1, 1)
                .add(new Paragraph("Sachbearbeiter \nE-Mail \nTelefon"))
                .setFontSize(7)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT);
        table.addCell(company);
        Cell companyInfo = new Cell(1, 1)
                .add(new Paragraph(projectInformation.getCompany().getContactPerson() + "\n"
                        + projectInformation.getCompany().getMail() + "\n"
                        + projectInformation.getCompany().getPhone()))
                .setFontSize(7)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT);
        table.addCell(companyInfo);
        table.setMarginBottom(50);
        doc.add(table);


    }

    /**
     * Converts Given InputStream (The PDF) into byte[]
     *
     * @param stream The stream to the generated PDF
     * @return to PDF-file converted into byte[]
     * @throws IOException
     */
    public byte[] convertPdfToByte(InputStream stream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
