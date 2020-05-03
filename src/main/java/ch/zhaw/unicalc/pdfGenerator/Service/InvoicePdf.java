package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;


/**
 * Generates for a separate page the Invoice with the Swiss-QR-Code
 */
@Service
public class InvoicePdf {
    private QrCodeGenerator qrCodeGenerator;
    private final String qrImagePath = "src/main/resources/temp/combined.png";
    private final String scissorImagePath = "src/main/resources/pictures/Scherensymbol.png";
    private final static double heightUnit = 842/297;
    private final static double widthUnit = 594/210;

    @Autowired
    public InvoicePdf(QrCodeGenerator qrCodeGenerator) {
        this.qrCodeGenerator = qrCodeGenerator;
    }

    public void generateInvoice(PdfDocument pdfDocument, OfferRequest offerRequest, double total) {
        PdfPage page = pdfDocument.addNewPage();
        Rectangle rectangle = new Rectangle(0, 0, 594, (float) (105*heightUnit));
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.rectangle(rectangle).setFillColor(ColorConstants.CYAN).fill();
        Canvas canvas = new Canvas(pdfCanvas, pdfDocument, rectangle);
        qrCodeGenerator.generateQR(generatePayload(offerRequest, total));
        Image qrImage = null;
        Image scissorsImage = null;
        try {
            qrImage = new Image(ImageDataFactory.create(qrImagePath)).setMaxWidth((float) (56 *widthUnit)).setPadding(0);
            scissorsImage = new Image(ImageDataFactory.create(scissorImagePath)).setMaxWidth((float)widthUnit*10).setPadding(0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (qrImage != null) {
            canvas.add(qrImage.setFixedPosition(178, (float)heightUnit*35));
        }
        if(scissorsImage != null) {
            canvas.add(scissorsImage.setFixedPosition((float)widthUnit*57, 0));
        }
        canvas.setFontColor(ColorConstants.BLACK);
        canvas.add(new Paragraph("Empfangsschein").setFontSize(11).setBold().setPaddings((float) heightUnit*6,(float)widthUnit*5, (float)widthUnit*3,(float)widthUnit*5).setMargin(0));
        canvas.add(new Paragraph("Konto / Zahlbar an").setFontSize(7).setBold().setPaddings(0, 0, 0, (float)widthUnit*5).setMargin(0));
        canvas.add(new Paragraph(offerRequest.getProjectInformation().getCompany().getAccount() +"\n"+
                offerRequest.getProjectInformation().getCompany().getName()+"\n"+
                offerRequest.getProjectInformation().getCompany().getZip() +" "+ offerRequest.getProjectInformation().getCompany().getCity()).setFontSize(8).setPaddings(0, 0, 0, (float)widthUnit*5).setMargin(0));
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
}
