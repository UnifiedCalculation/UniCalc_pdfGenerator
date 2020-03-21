package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PDFService {

    private String path = "src/main/temp/";

    @Autowired
    public PDFService() {}

    public byte[] generatePDF(OfferRequest offerRequest) {
        Document doc = new Document();
        try {
            // --------------- Create PDF -----------------------------
            String sourcePath = path + "TEST.pdf";
            FileOutputStream outputStream = new FileOutputStream(sourcePath);
            PdfWriter.getInstance(doc, outputStream);
            doc.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Chunk chunk = new Chunk("Test", font);
            doc.add(chunk);
            doc.close();
            outputStream.close();

            // ---------------------------------------------------------

            InputStream inputStream = new FileInputStream(sourcePath);
            byte[] bytes = convertPdfToByte(inputStream);
            inputStream.close();

            File toDelete = new File(sourcePath);
            toDelete.delete();
            return bytes;

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        return  byteArrayOutputStream.toByteArray();
    }
}
