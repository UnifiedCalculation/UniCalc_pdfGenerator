package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;


/**
 * Generates for a separate page the Invoice with the Swiss-QR-Code
 */
@Service
public class InvoicePdf {
    private QrCodeGenerator qrCodeGenerator;
    private final String qrImagePath = "src/main/resources/temp/combined.png";
    private final String scissorImagePath = "src/main/resources/pictures/Scherensymbol.png";
    private final static double heightUnit = 842 / 297;
    private final static double widthUnit = 594 / 210;

    @Autowired
    public InvoicePdf(QrCodeGenerator qrCodeGenerator) {
        this.qrCodeGenerator = qrCodeGenerator;
    }

    public void generateInvoice(PdfDocument pdfDocument, OfferRequest offerRequest, double total) {
        PdfPage page = pdfDocument.addNewPage();
        Rectangle rectangle = new Rectangle(0, 0, 594, (float) heightUnit * 110);
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.rectangle(rectangle).setFillColor(ColorConstants.WHITE).fill();
        Canvas canvas = new Canvas(pdfCanvas, pdfDocument, rectangle);
        qrCodeGenerator.generateQR(generatePayload(offerRequest, total));
        Image qrImage = null;
        Image scissorImage = null;
        try {
            qrImage = new Image(ImageDataFactory.create(qrImagePath)).setMaxWidth((float) (66 * widthUnit)).setPadding(0);
            scissorImage = new Image(ImageDataFactory.create(scissorImagePath)).setMaxWidth(594).setPadding(0).setMargins(0, 0, 0, 0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (qrImage != null) {
            canvas.add(qrImage.setFixedPosition((float) widthUnit * 81, (float) heightUnit * 37));
        }
        if (scissorImage != null) {
            canvas.add(scissorImage.setFixedPosition(0, (float) heightUnit * 105));
        }

        canvas.setFontColor(ColorConstants.BLACK);
        createLeftSideOfPayment(canvas, offerRequest, total);
        createRightSideOfPayment(canvas, offerRequest, total);

        String sourcePath = "src/main/resources/temp/combined.png";
        File file = new File(sourcePath);
        file.getParentFile().mkdirs();
        file.delete();

        canvas.close();

    }

    private String generatePayload(OfferRequest offerRequest, double total) {
        String payload = "SPC\r\n" +
                "0200\r\n" +
                "1\r\n" +
                offerRequest.getProjectInformation().getCompany().getAccount() + "\r\n" +
                "S\r\n" +
                offerRequest.getProjectInformation().getCompany().getName() + "\r\n" +
                offerRequest.getProjectInformation().getCompany().getAddress().split(" ")[0] + "\r\n" +
                offerRequest.getProjectInformation().getCompany().getAddress().split(" ")[1] + "\r\n" +
                offerRequest.getProjectInformation().getCompany().getZip() + "\r\n" +
                offerRequest.getProjectInformation().getCompany().getCity() + "\r\n" +
                offerRequest.getProjectInformation().getCompany().getLand() + "\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n" +
                total + "\r\n" +
                "CHF\r\n" +
                "S\r\n" +
                offerRequest.getProjectInformation().getCustomer().getName() + "\r\n" +
                offerRequest.getProjectInformation().getCustomer().getAddress().split(" ")[0] + "\r\n" +
                offerRequest.getProjectInformation().getCustomer().getAddress().split(" ")[1] + "\r\n" +
                offerRequest.getProjectInformation().getCustomer().getZip() + "\r\n" +
                offerRequest.getProjectInformation().getCustomer().getCity() + "\r\n" +
                offerRequest.getProjectInformation().getCustomer().getLand() + "\r\n" +
                "QRR\r\n" +
                offerRequest.getProjectInformation().getBankInformation().getPaymentReference() + "\r\n" +
                offerRequest.getProjectInformation().getBankInformation().getAdditionalInformation() + "\r\n" +
                "EPD\r\n" +
                offerRequest.getProjectInformation().getBankInformation().getInvoiceInformation();
        for (int i = 0; i < offerRequest.getProjectInformation().getBankInformation().getAlternativeProcedure().length; i++) {
            payload += "\r\n" + offerRequest.getProjectInformation().getBankInformation().getAlternativeProcedure()[i];
        }

        return payload;
    }

    private void createLeftSideOfPayment(Canvas canvas, OfferRequest offerRequest, double total) {
        canvas.add(new Paragraph("Empfangsschein").setBold().setFixedPosition((float) widthUnit * 5, (float) heightUnit * 100, (float) widthUnit * 52).setFontSize(11).setPadding(0).setMargin(0));
        canvas.add(new Paragraph("Konto / Zahlbar an").setBold().setFixedPosition((float) widthUnit * 5, (float) heightUnit * 93, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));
        canvas.add(new Paragraph(offerRequest.getProjectInformation().getCompany().getAccount() + "\n" +
                offerRequest.getProjectInformation().getCompany().getName() + "\n" +
                offerRequest.getProjectInformation().getCompany().getZip() + " " + offerRequest.getProjectInformation().getCompany().getCity()).setFixedPosition((float) widthUnit * 5, (float) heightUnit * 78, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));

        canvas.add(new Paragraph("Referenz").setBold().setFixedPosition((float) widthUnit * 5, (float) heightUnit * 70, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));
        canvas.add(new Paragraph(offerRequest.getProjectInformation().getBankInformation().getPaymentReference()).setFixedPosition((float) widthUnit * 5, (float) heightUnit * 65, (float) widthUnit * 60).setFontSize(7).setPadding(0).setMargin(0));

        canvas.add(new Paragraph("Zahlbar durch").setBold().setFixedPosition((float) widthUnit * 5, (float) heightUnit * 58, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));
        canvas.add(new Paragraph(offerRequest.getProjectInformation().getCustomer().getCompanyName() + "\n" +
                offerRequest.getProjectInformation().getCustomer().getZip() + " " +
                offerRequest.getProjectInformation().getCustomer().getCity()).setFontSize(7).setFixedPosition((float) widthUnit * 5, (float) heightUnit * 48, (float) widthUnit * 52).setPadding(0).setMargin(0));

        canvas.add(new Paragraph("Währung\t\t Betrag").setFontSize(7).setBold().setFixedPosition((float) widthUnit * 5, (float) heightUnit * 30, (float) widthUnit * 52).setPadding(0).setMargin(0));
        canvas.add(new Paragraph(String.format("CHF\t\t\t %.2f", total)).setFixedPosition((float) widthUnit * 5, (float) heightUnit * 25, (float) widthUnit * 52).setFontSize(8).setPaddingTop((float) heightUnit * 15).setMargin(0));

        canvas.add(new Paragraph("Annahmestelle").setBold().setFixedPosition((float) widthUnit * 5, (float) heightUnit * 13, (float) widthUnit * 64).setTextAlignment(TextAlignment.RIGHT).setFontSize(7).setPadding(0).setMargin(0));

        canvas.add(new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n").setFontSize(9).setFixedPosition((float) widthUnit * 62, 0, 30).setBorderRight(new DashedBorder(1)).setMargin(0).setPadding(0));


    }

    private void createRightSideOfPayment(Canvas canvas, OfferRequest offerRequest, double total) {
        /* Unter QRCode  */
        canvas.add(new Paragraph("Zahlteil").setBold().setFontSize(11).setPadding(0).setMargin(0).setFixedPosition((float) widthUnit * 85, (float) heightUnit * 100, (float) widthUnit * 56));

        canvas.add(new Paragraph("Währung\tBetrag").setBold().setFontSize(7).setPadding(0).setMargin(0).setFixedPosition((float) widthUnit * 85, (float) heightUnit * 30, (float) widthUnit * 52));
        canvas.add(new Paragraph(String.format("CHF\t\t %.2f", total)).setFixedPosition((float) widthUnit * 85, (float) heightUnit * 25, (float) widthUnit * 52).setFontSize(8).setPaddingTop((float) heightUnit * 15).setMargin(0));

        if (offerRequest.getProjectInformation().getBankInformation().getAlternativeProcedure().length > 0) {
            int i = 13;
            for (String procedure : offerRequest.getProjectInformation().getBankInformation().getAlternativeProcedure()) {
                canvas.add(new Paragraph(procedure).setFontSize(7).setPadding(0).setMargin(0).setFixedPosition((float) widthUnit * 85, (float) heightUnit * i, (float) widthUnit * 70));
                i -= 4;
            }
        }

        /* Bereich Rechts Angaben */
        canvas.add(new Paragraph("Konto / Zahlbar an").setBold().setFixedPosition((float) widthUnit * 158, (float) heightUnit * 102, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));
        canvas.add(new Paragraph(offerRequest.getProjectInformation().getCompany().getAccount() + "\n" +
                offerRequest.getProjectInformation().getCompany().getName() + "\n" +
                offerRequest.getProjectInformation().getCompany().getAddress() + "\n" +
                offerRequest.getProjectInformation().getCompany().getZip() + " " + offerRequest.getProjectInformation().getCompany().getCity()).setFixedPosition((float) widthUnit * 158, (float) heightUnit * 82, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));

        canvas.add(new Paragraph("Referenz").setBold().setFixedPosition((float) widthUnit * 158, (float) heightUnit * 73, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));
        canvas.add(new Paragraph(offerRequest.getProjectInformation().getBankInformation().getPaymentReference()).setFixedPosition((float) widthUnit * 158, (float) heightUnit * 68, (float) widthUnit * 60).setFontSize(7).setPadding(0).setMargin(0));

        canvas.add(new Paragraph("Zusätzliche Informationen").setBold().setFixedPosition((float) widthUnit * 158, (float) heightUnit * 61, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));
        canvas.add(new Paragraph(offerRequest.getProjectInformation().getBankInformation().getAdditionalInformation()).setFixedPosition((float) widthUnit * 158, (float) heightUnit * 56, (float) widthUnit * 60).setFontSize(7).setPadding(0).setMargin(0));

        canvas.add(new Paragraph("Zahlbar durch").setBold().setFixedPosition((float) widthUnit * 158, (float) heightUnit * 45, (float) widthUnit * 52).setFontSize(7).setPadding(0).setMargin(0));
        canvas.add(new Paragraph(offerRequest.getProjectInformation().getCustomer().getCompanyName() + "\n" +
                offerRequest.getProjectInformation().getCustomer().getAddress() + "\n" +
                offerRequest.getProjectInformation().getCustomer().getZip() + " " +
                offerRequest.getProjectInformation().getCustomer().getCity()).setFontSize(7).setFixedPosition((float) widthUnit * 158, (float) heightUnit * 30, (float) widthUnit * 52).setPadding(0).setMargin(0));

    }
}
