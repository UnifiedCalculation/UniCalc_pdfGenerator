package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PDFService {

    private String path = "target/temp/";

    @Autowired
    public PDFService() {
    }

    public byte[] generatePDF(OfferRequest offerRequest) {
        // --------------- Create PDF -----------------------------
        try {

            String sourcePath = path + offerRequest.getTitle() + ".pdf";
            File file = new File(sourcePath);
            file.getParentFile().mkdirs();

            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(sourcePath));
            Document doc = new Document(pdfDocument);
            Table table = new Table(UnitValue.createPercentArray(7)).useAllAvailableWidth();

            for (int i = 0; i < 7; i++) {
                table.addCell(Integer.toString(i));
            }

            doc.add(table);
            doc.close();


            InputStream inputStream = new FileInputStream(sourcePath);
            byte[] bytes = convertPdfToByte(inputStream);
            inputStream.close();

            file.delete();
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private byte[] convertPdfToByte(InputStream stream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
