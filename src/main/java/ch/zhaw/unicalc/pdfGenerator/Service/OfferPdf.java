package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ArticleRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.EntryRequest;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class OfferPdf {

    private String path = "src/main/resources/temp/";
    private String[] header = {"Artikel", "Menge", "Einheit", "Preis/Einheit", "Rabatt", "Betrag"};
    private float[] width = {7, 2, 2, 3, 2, 3};
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
            generalPdf.createHeader(doc, offerRequest.getProjectInformation().getCompany());
            generalPdf.createLetterHead(doc, offerRequest.getProjectInformation());

            Table title = new Table(UnitValue.createPercentArray(new float[]{3, 2})).useAllAvailableWidth();
            Table tableHeader = new Table(UnitValue.createPercentArray(width)).useAllAvailableWidth();
            Table table = new Table(UnitValue.createPercentArray(width)).useAllAvailableWidth();

            createTitle(title, offerRequest);
            title.setPaddingBottom(0);
            createHeader(tableHeader);
            double total = createContent(table, offerRequest);
            createTotal(table, total, offerRequest);

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
                .setWidth(8)
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
                .setPaddingLeft(14)
                .setTextAlignment(TextAlignment.LEFT);
        title.addCell(date);
        Cell subTitle = new Cell(1, 1)
                .add(new Paragraph("???"))
                .setFontSize(9)
                .setBold()
                .setWidth(8)
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
            if (i == 2 || i >= 4) {
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
                    .setMargin(0)
                    .setTextAlignment(TextAlignment.LEFT);
            table.addCell(segmentTitleCell);

            for (ArticleRequest article : segment.getArticles()) {
                Cell articleNr = new Cell(1, 6).add(new Paragraph("" + article.getNumber())).setMargin(0).setPadding(0).setFontSize(8).setTextAlignment(TextAlignment.LEFT).setBorder(null).setBold();

                Cell name = new Cell().add(new Paragraph(article.getName())).setMargin(0).setPaddings(0, 0, 2, 0)
                        .setFontSize(9).setTextAlignment(TextAlignment.LEFT).setBorder(null);
                Cell amount = new Cell().add(new Paragraph("" + article.getAmount())).setMargin(0).setTextAlignment(TextAlignment.RIGHT).setPaddings(0, 4, 2, 0)
                        .setFontSize(9).setBorder(null);
                Cell unitType = new Cell().add(new Paragraph(article.getUnit())).setMargin(0).setPaddings(0, 0, 2, 0)
                        .setFontSize(9).setBorder(null);
                Cell price = new Cell().add(new Paragraph(article.getPrice() + ".-")).setMargin(0).setPaddings(0, 0, 2, 0)
                        .setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setBorder(null);
                Cell discount = new Cell().add(new Paragraph(article.getDiscount() + "%")).setMargin(0).setPaddings(0, 0, 2, 0)
                        .setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setBorder(null);
                double totald = article.getPrice() * article.getAmount();
                if (article.getDiscount() != 0) {
                    double dis = totald * article.getDiscount() / 100.0;
                    totald -= dis;
                }
                totald = Math.floor(totald * 100) / 100;
                finalPrice += totald;
                Cell total = new Cell().add(new Paragraph(totald + ".-")).setMargin(0).setPaddings(0, 0, 2, 0)
                        .setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setBorder(null);


                table.addCell(articleNr);
                table.addCell(name);
                table.addCell(amount);
                table.addCell(unitType);
                table.addCell(price);
                table.addCell(discount);
                table.addCell(total);
            }
            Cell space = new Cell(1, 6)
                    .add(new Paragraph(""))
                    .setFontSize(11)
                    .setBorder(null);
            table.addCell(space);
        }
        return finalPrice;

    }

    private void createTotal(Table table, double total, OfferRequest offerRequest) {

        Cell comment = new Cell(1, 3)
                .add(new Paragraph("Zahlungsziel: (Platzhalter bspw. 30 Tage netto)"))
                .setFontSize(7)
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setPaddingTop(0)
                .setTextAlignment(TextAlignment.LEFT);
        table.addCell(comment);
        Cell totalString = new Cell(1, 1)
                .add(new Paragraph("Total"))
                .setFontSize(9)
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setPaddingRight(0)
                .setPaddingBottom(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalString);
        Cell totalDouble = new Cell(1, 2)
                .add(new Paragraph(Math.floor(total * 100) / 100 + " CHF"))
                .setFontSize(9)
                .setBorderBottom(null)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setPaddingRight(0)
                .setPaddingBottom(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalDouble);
        Cell emptySpace = new Cell(1, 3)
                .add(new Paragraph(""))
                .setBorder(null)
                .setFontSize(9);
        table.addCell(emptySpace);
        Cell totalDiscountString = new Cell(1, 1)
                .add(new Paragraph("Rabatt"))
                .setFontSize(9)
                .setBorder(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalDiscountString);
        Cell totalDiscount = new Cell(1, 1)
                .add(new Paragraph("-" + offerRequest.getDiscount() + "%"))
                .setFontSize(9)
                .setPadding(0)
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalDiscount);
        Double toSubtract = Math.floor(total * offerRequest.getDiscount()) / 100;
        Cell totalDisountComputed = new Cell(1, 1)
                .add(new Paragraph("-" + toSubtract + " CHF"))
                .setFontSize(9)
                .setBorder(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(totalDisountComputed);
        table.addCell(emptySpace);
        Cell nettoString = new Cell(1, 1)
                .add(new Paragraph("Netto"))
                .setFontSize(9)
                .setBorderLeft(null)
                .setBorderBottom(null)
                .setBorderRight(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(nettoString);
        Double netto = total - toSubtract;
        Cell nettoDouble = new Cell(1, 2)
                .add(new Paragraph(netto + " CHF"))
                .setFontSize(9)
                .setBorderLeft(null)
                .setBorderBottom(null)
                .setBorderRight(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(nettoDouble);
        table.addCell(emptySpace);

        Cell mwstString = new Cell(1, 1)
                .add(new Paragraph("MWST"))
                .setFontSize(9)
                .setBorder(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(mwstString);

        Cell mwstDisount = new Cell(1, 1)
                .add(new Paragraph("7.7%"))
                .setFontSize(9)
                .setBorder(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(mwstDisount);
        Cell mwstNumber = new Cell(1, 1)
                .add(new Paragraph(Math.floor((netto + 0.077) * 100) / 100  + " CHF"))
                .setFontSize(9)
                .setBorder(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(mwstNumber);
        table.addCell(emptySpace);
        Cell finalAmountString = new Cell(1, 1)
                .add(new Paragraph("Gesamtbetrag"))
                .setFontSize(9)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(finalAmountString);
        Cell finalAmountNumber = new Cell(1, 2)
                .add(new Paragraph(Math.floor((netto + 1.077) * 100) / 100  + " CHF"))
                .setFontSize(9)
                .setBorderLeft(null)
                .setBorderRight(null)
                .setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(finalAmountNumber);
    }

}
