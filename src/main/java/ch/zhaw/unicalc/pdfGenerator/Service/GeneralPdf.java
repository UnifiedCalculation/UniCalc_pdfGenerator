package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.CompaniesRequest;
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
 * Pdf Class to generate general Information that every Document needs. (example Header, footer, etc)
 */
@Service
public class GeneralPdf {

    @Autowired
    public GeneralPdf() {

    }

    /**
     * Generates and adds an Header to the given Document.
     * Header includes name, url and logo of the business.
     *
     * @param doc      The Document to which the Header should be added
     * @param business The BusinessRequest with all needed Information
     */
    public void createHeader(Document doc, CompaniesRequest business) {
        float[] widths = {6, 3};
        Table table = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();
        Cell headerLeft = new Cell(1, 1)
                .add(new Paragraph(business.getName() + "\n" + business.getUrl()))
                .setWidth(widths[0])
                .setFontSize(9)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT);
        table.addCell(headerLeft);
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

        }
        table.setMarginBottom(70);
        doc.add(table);
    }


    /**
     * Generates and adds an Letter Head to the given Document.
     * In it, information like state, zip-code, business-name and contact person is noted.
     *
     * @param doc                The Document to which everything should be added
     * @param business           The BusinessRequest with all the Information about the Business
     * @param projektInformation The ProjektInformationRequest with Information about the contact person
     */
    public void createLetterHead(Document doc, CompaniesRequest business, ProjectRequest projektInformation) {

    }

    /**
     * Converts Given InputStream (The PDF) into byte[]
     *
     * @param stream
     * @return
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
