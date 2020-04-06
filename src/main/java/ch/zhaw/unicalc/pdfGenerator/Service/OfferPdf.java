package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ArticleRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.EntryRequest;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class OfferPdf {

    private String path = "target/temp/";
    private String[] header = {"Artikel", "Menge", "Einheit", "Preis/Einheit", "Rabatt", "Betrag"};
    private float[] width = {7, 2, 2, 3, 2, 2};
    private GeneralPdf generalPdf;


    @Autowired
    public OfferPdf(GeneralPdf generalPdf) {
        this.generalPdf = generalPdf;
    }

    public byte[] generatePDF(OfferRequest offerRequest) {
        // --------------- Create PDF -----------------------------
        try {

            String sourcePath = path + offerRequest.getTitle() + ".pdf";
            File file = new File(sourcePath);
            file.getParentFile().mkdirs();

            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(sourcePath));

            Document doc = new Document(pdfDocument);
            generalPdf.createHeader(doc, offerRequest.getBusiness());

            Table title = new Table(UnitValue.createPercentArray(new float[]{3, 2})).useAllAvailableWidth();
            Table tableHeader = new Table(UnitValue.createPercentArray(width)).useAllAvailableWidth();
            Table table = new Table(UnitValue.createPercentArray(width)).useAllAvailableWidth();

            createTitle(title, offerRequest);
            title.setPaddingBottom(0);
            createHeader(tableHeader);
            double total = createContent(table, offerRequest);
            createTotal(table, total);

            doc.add(title);
            doc.add(tableHeader);
            doc.add(table);
            doc.close();


            InputStream inputStream = new FileInputStream(sourcePath);
            byte[] bytes = this.generalPdf.convertPdfToByte(inputStream);
            inputStream.close();

            file.delete();
            return bytes;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void createTitle(Table title, OfferRequest offerRequest) {
        Cell titleName = new Cell(1, 1)
                .add(new Paragraph("Angebot " + offerRequest.getTitle()))
                .setFontSize(10)
                .setBold()
                .setWidth(3)
                .setBorder(null)
                .setPaddingBottom(0)
                .setMarginBottom(0)
                .setTextAlignment(TextAlignment.LEFT);
        title.addCell(titleName);
        Cell date = new Cell(1, 1)
                .add(new Paragraph(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))))
                .setFontSize(10)
                .setWidth(2)
                .setBorder(null)
                .setTextAlignment(TextAlignment.LEFT);
        title.addCell(date);
        Cell subTitle = new Cell(1,1)
                .add(new Paragraph("???"))
                .setFontSize(9)
                .setBold()
                .setWidth(3)
                .setBorder(null)
                .setPaddingTop(0)
                .setMarginTop(0)
                .setTextAlignment(TextAlignment.LEFT);
        title.addCell(subTitle);
        title.setPaddingBottom(0);
        title.setMarginBottom(8);
    }


    /**
     * Creates Header for the Table
     *
     * @param table
     */
    private void createHeader(Table table) {
        for (int i = 1; i < 7; i++) {
            Cell cell = new Cell(1, 1)
                    .add(new Paragraph(header[i - 1]))
                    .setWidth(width[i - 1])
                    .setFontSize(10)
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderRight(Border.NO_BORDER)
                    .setBorderLeft(Border.NO_BORDER)
                    .setBold();
            if(i >= 4) {
                cell.setTextAlignment(TextAlignment.RIGHT);
            }
            table.addCell(cell);
        }
    }

    /**
     * Creates the content from the JSON
     *
     * @param table
     */
    private Double createContent(Table table, OfferRequest offerRequest) {
        double finalPrice = 0.0;
        for (EntryRequest segment : offerRequest.getEntries()) {
            Cell segmentTitleCell = new Cell(1, 6)
                    .add(new Paragraph(segment.getTitle()))
                    .setFontSize(9)
                    .setBold()
                    .setBorder(null)
                    .setPaddings(1, 0, 0, 0)
                    .setMargins(1,0,0,0)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(segmentTitleCell);

            for (ArticleRequest article : segment.getArticles()) {
                Cell articleNr = new Cell(1, 6).add(new Paragraph("" + article.getNumber())).setPadding(0).setMargin(0).setFontSize(9).setBorder(null).setBold();

                Cell name = new Cell().add(new Paragraph(article.getName())).setBorder(null).setPaddings(0, 0, 2, 0)
                        .setMargins(0,0,2,0).setFontSize(9);
                Cell amount;
                if (!article.getUnit().equals("Stunden")) {
                    amount = new Cell().add(new Paragraph("" + article.getAmount())).setBorder(null).setPaddings(0, 0, 2, 0)
                            .setMargins(0,0,2,0).setFontSize(9);
                } else {
                    amount = new Cell().add(new Paragraph(article.getAmount() + "h")).setBorder(null).setPaddings(0, 0, 2, 0)
                            .setMargins(0,0,2,0).setFontSize(9);
                }
                Cell unitType = new Cell().add(new Paragraph(article.getUnit())).setBorder(null).setPaddings(0, 0, 2, 0)
                        .setMargins(0,0,2,0).setFontSize(9);
                Cell price = new Cell().add(new Paragraph(article.getPrice() + ".-")).setPaddings(0, 0, 2, 0)
                        .setMargins(0,0,2,0).setFontSize(9).setBorder(null).setTextAlignment(TextAlignment.RIGHT);
                Cell discount = new Cell().add(new Paragraph(article.getDiscount() + "%")).setPaddings(0, 0, 2, 0)
                        .setMargins(0,0,2,0).setFontSize(9).setBorder(null).setTextAlignment(TextAlignment.RIGHT);
                double totald = article.getPrice() * article.getAmount();
                if (article.getDiscount() != 0) {
                    double dis = totald * article.getDiscount() / 100.0;
                    totald -= dis;
                }
                totald = Math.floor(totald * 100) / 100;
                finalPrice += totald;
                Cell total = new Cell().add(new Paragraph(totald + ".-")).setPaddings(0, 0, 2, 0)
                        .setMargins(0,0,2,0).setFontSize(9).setBorder(null).setTextAlignment(TextAlignment.RIGHT);


                table.addCell(articleNr);
                table.addCell(name);
                table.addCell(amount);
                table.addCell(unitType);
                table.addCell(price);
                table.addCell(discount);
                table.addCell(total);
            }
            Cell space = new Cell(1,6)
                    .add(new Paragraph(""))
                    .setFontSize(11)
                    .setBorder(null);
            table.addCell(space);
        }
        return finalPrice;

    }

    private void createTotal(Table table, double total) {

            Cell cell2 = new Cell(1, 6)
                    .add(new Paragraph("Total"))
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell2);
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
                    .add(new Paragraph(Math.floor(total * 100) / 100 + ".-"))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell6);
            Cell cell8 = new Cell(1, 6)
                    .add(new Paragraph("Zusatzrabatt bei Abholung"))
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(cell8);
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

    }

}
