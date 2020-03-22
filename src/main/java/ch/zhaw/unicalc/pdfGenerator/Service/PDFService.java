package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ArticleRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.SegmentRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class PDFService {

    private String path = "src/main/temp/";

    @Autowired
    public PDFService() {
    }

    public byte[] generatePDF(OfferRequest offerRequest) {
        Document doc = new Document();
        try {
            // --------------- Create PDF -----------------------------
            String sourcePath = path + offerRequest.getTitle() + ".pdf";
            FileOutputStream outputStream = new FileOutputStream(sourcePath);
            PdfWriter.getInstance(doc, outputStream);
            doc.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
            Chunk title = new Chunk("Offerte <?> Projekt <?>," + LocalDate.now(), font);
            doc.add(title);
            doc.add(new Paragraph("                             "));



            PdfPTable table = new PdfPTable(7);
            addTableHeader(table);
            table.setTotalWidth(doc.left());
            doc.add(table);

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

    private void addTableHeader(PdfPTable table) {
        Stream.of("Nr.", "Name", "Artikel Nr.", "Stückzahl", "Preis", "Rabatt", "Total")
                .forEach(columnHeader -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnHeader));
                    table.addCell(header);
                });
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

    // Dummy Method for testing purposes
    private OfferRequest generateDummy() {
        ArticleRequest articleRequest1 = ArticleRequest.builder()
                .id(100)
                .name("articleName1")
                .unit("m")
                .amount(15)
                .discount(7)
                .price(180.00)
                .description("Description 1")
                .build();

        ArticleRequest articleRequest2 = ArticleRequest.builder()
                .id(200)
                .name("articleName2")
                .unit("h")
                .amount(5)
                .discount(15)
                .price(150.85)
                .description("Description 2")
                .build();

        ArticleRequest articleRequest3 = ArticleRequest.builder()
                .id(300)
                .name("articleName3")
                .unit("m")
                .amount(15)
                .discount(15)
                .price(340.45)
                .description("Description 3")
                .build();

        Set<ArticleRequest> articleRequestList1 = new HashSet<>();
        articleRequestList1.add(articleRequest1);
        Set<ArticleRequest> articleRequestList2 = new HashSet<>();
        articleRequestList2.add(articleRequest2);
        articleRequestList2.add(articleRequest3);

        SegmentRequest segmentRequest1 = SegmentRequest.builder()
                .title("segmentTitle 1")
                .articles(articleRequestList1)
                .build();
        SegmentRequest segmentRequest2 = SegmentRequest.builder()
                .title("segmentTitle 2")
                .articles(articleRequestList2)
                .build();

        Set<SegmentRequest> segmentRequestList = new HashSet<>();
        segmentRequestList.add(segmentRequest1);
        segmentRequestList.add(segmentRequest2);

        OfferRequest offerRequest = OfferRequest.builder()
                .id(1)
                .title("offerTitle")
                .marketBranch("sanitär")
                .segments(segmentRequestList)
                .build();
        return offerRequest;
    }
}
