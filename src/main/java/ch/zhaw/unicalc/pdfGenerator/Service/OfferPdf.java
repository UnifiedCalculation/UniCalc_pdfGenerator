package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ArticleRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.SegmentRequest;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class OfferPdf {

    private String path = "target/temp/";
    private String[] header = {"Artikel", "Menge", "Einheit", "Preis/Einheit", "Rabatt", "Betrag"};
    private float[] width = {5, 2, 2, 3, 2, 3};


    @Autowired
    public OfferPdf() {
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
            Map totalMap = createContent(table, offerRequest);
            createTotal(table, totalMap);


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
     * Creates Header for the Table
     *
     * @param table
     */
    private void createHeader(Table table) {
        for (int i = 1; i < 8; i++) {
            Cell cell = new Cell(1, 1)
                    .add(new Paragraph(header[i - 1]))
                    .setWidth(width[i - 1])
                    .setFontSize(12)
                    .setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY))
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell);
        }
    }

    /**
     * Creates the content from the JSON
     *
     * @param table
     */
    private Map<Integer, Double> createContent(Table table, OfferRequest offerRequest) {
        int i = 1;
        double finalPrice = 0.0;
        for (SegmentRequest segment : offerRequest.getSegments()) {
            Cell cell1 = new Cell(1, 1)
                    .add(new Paragraph(Integer.toString((int) i)))
                    .setFontSize(12)
                    .setBackgroundColor(DeviceGray.GRAY)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell1);
            Cell cell2 = new Cell(1, 6)
                    .add(new Paragraph(segment.getTitle()))
                    .setFontSize(12)
                    .setBackgroundColor(DeviceGray.GRAY)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell2);
            boolean even = false;
            double j = 0.1;

            for (ArticleRequest article : segment.getArticles()) {
                Cell nr = new Cell().add(new Paragraph(j + i + "")).setFontSize(11);
                Cell name = new Cell().add(new Paragraph(article.getName())).setFontSize(11);
                Cell articleNr = new Cell().add(new Paragraph("")).setFontSize(11);
                Cell amount;
                if (article.getUnit().equals("m")) {
                    amount = new Cell().add(new Paragraph("" + article.getAmount())).setFontSize(11);
                } else {
                    amount = new Cell().add(new Paragraph(article.getAmount() + "h")).setFontSize(11);
                }
                Cell price = new Cell().add(new Paragraph(article.getPrice() + ".-")).setFontSize(11);
                Cell discount = new Cell().add(new Paragraph(article.getDiscount() + "%")).setFontSize(11);
                double totald = article.getPrice() * article.getAmount();
                if (article.getDiscount() != 0) {
                    double dis = totald * article.getDiscount() / 100.0;
                    totald -= dis;
                }
                totald = Math.floor(totald * 100) / 100;
                finalPrice += totald;
                Cell total = new Cell().add(new Paragraph(totald + ".-")).setFontSize(11);

                if (even) {
                    nr.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY));
                    name.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY));
                    articleNr.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY));
                    amount.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY));
                    price.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY));
                    discount.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY));
                    total.setBackgroundColor(DeviceGray.makeLighter(DeviceGray.GRAY));
                }

                table.addCell(nr);
                table.addCell(name);
                table.addCell(articleNr);
                table.addCell(amount);
                table.addCell(price);
                table.addCell(discount);
                table.addCell(total);
                even = !even;
                j = j + 0.1;
            }

            i++;
        }
        Map<Integer, Double> map = new HashMap<>();
        map.put(i, finalPrice);
        return map;

    }

    private void createTotal(Table table, Map<Integer, Double> totalMap) {
        totalMap.forEach((k, v) -> {
            Cell cell1 = new Cell(1, 1)
                    .add(new Paragraph(k + ""))
                    .setFontSize(12)
                    .setBackgroundColor(DeviceGray.GRAY)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell1);
            Cell cell2 = new Cell(1, 6)
                    .add(new Paragraph("Total"))
                    .setFontSize(12)
                    .setBackgroundColor(DeviceGray.GRAY)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell2);
            Cell cell3 = new Cell(1, 1)
                    .add(new Paragraph(0.1 + k + ""))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell3);
            Cell cell4 = new Cell(1, 4)
                    .add(new Paragraph("Total Brutto"))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell4);
            Cell cell5 = new Cell(1, 1)
                    .add(new Paragraph("??%"))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell5);
            Cell cell6 = new Cell(1, 1)
                    .add(new Paragraph(Math.floor(v * 100) / 100 + ".-"))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell6);
            Cell cell7 = new Cell(1, 1)
                    .add(new Paragraph(0.2 + k + ""))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell7);
            Cell cell8 = new Cell(1, 6)
                    .add(new Paragraph("Zusatzrabatt bei Abholung"))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell8);
            Cell cell9 = new Cell(1, 1)
                    .add(new Paragraph(0.3 + k + ""))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell9);
            Cell cell10 = new Cell(1, 4)
                    .add(new Paragraph("MwST."))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell10);
            Cell cell11 = new Cell(1, 1)
                    .add(new Paragraph("7.7%"))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell11);

            Cell cell12 = new Cell(1, 1)
                    .add(new Paragraph("??"))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell12);
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

}
