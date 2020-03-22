package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ArticleRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.SegmentRequest;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
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
    private String[] header = {"Nr.", "Name", "Artikel Nr.", "Stückzahl", "Preis", "Rabatt", "Total"};
    private float[] width = {1, 5, 3, 3, 3, 3, 3};

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

            Table table = new Table(UnitValue.createPercentArray(width)).useAllAvailableWidth();
            createHeader(table);
            createContent(table, offerRequest);


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

    /**
     *  Creates Header for the Table
     * @param table
     */
    private void createHeader(Table table) {
        for(int i = 1; i < 8; i++) {
            Cell cell = new Cell(1,1)
                    .add(new Paragraph(header[i-1]))
                    .setWidth(width[i-1])
                    .setFontSize(12)
                    .setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY))
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell);
        }
    }

    /**
     * Creates the content from the JSON
     * @param table
     */
    private void createContent(Table table, OfferRequest offerRequest) {
        double i = 1;
        for(SegmentRequest segment: offerRequest.getSegments()) {
            Cell cell1 = new Cell(1,1)
                    .add(new Paragraph(Integer.toString((int) i)))
                    .setFontSize(12)
                    .setBackgroundColor(DeviceGray.GRAY)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell1);
            Cell cell2 = new Cell(1,6)
                    .add(new Paragraph(segment.getTitle()))
                    .setFontSize(12)
                    .setBackgroundColor(DeviceGray.GRAY)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell2);
            boolean even = false;
            double j = 0.01;

            for(ArticleRequest article: segment.getArticles()) {
                Cell nr = new Cell().add(new Paragraph(j + i + "")).setFontSize(11);
                Cell name = new Cell().add(new Paragraph(article.getName())).setFontSize(11);
                Cell articleNr = new Cell().add(new Paragraph("")).setFontSize(11);
                Cell amount;
                if(article.getUnit().equals("m")) {
                    amount = new Cell().add(new Paragraph( "" + article.getAmount())).setFontSize(11);
                } else {
                    amount = new Cell().add(new Paragraph(article.getAmount() + "h")).setFontSize(11);
                }
                Cell price = new Cell().add(new Paragraph(article.getPrice() + ".-")).setFontSize(11);
                Cell discount = new Cell().add(new Paragraph(article.getDiscount() + "%")).setFontSize(11);
                double totald = article.getPrice() * article.getAmount();
                if(article.getDiscount() != 0) {
                    double dis = totald * article.getDiscount() / 100.0;
                    totald -= dis;
                }
                totald =  Math.floor(totald * 100) / 100;
                Cell total = new Cell().add(new Paragraph(totald + ".-")).setFontSize(11);

                if(even) {
                    nr.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.makeLighter(DeviceGray.GRAY)));
                    name.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.makeLighter(DeviceGray.GRAY)));
                    articleNr.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.makeLighter(DeviceGray.GRAY)));
                    amount.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.makeLighter(DeviceGray.GRAY)));
                    price.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.makeLighter(DeviceGray.GRAY)));
                    discount.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.makeLighter(DeviceGray.GRAY)));
                    total.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.makeLighter(DeviceGray.GRAY)));
                }

                table.addCell(nr);
                table.addCell(name);
                table.addCell(articleNr);
                table.addCell(amount);
                table.addCell(price);
                table.addCell(discount);
                table.addCell(total);
                even = !even;
                j = j + 0.01;
            }

            i++;
        }

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
