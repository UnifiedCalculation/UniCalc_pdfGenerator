package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.CompaniesRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ProjectRequest;
import com.itextpdf.layout.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
