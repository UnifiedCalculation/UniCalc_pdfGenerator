package ch.zhaw.unicalc.pdfGenerator.Service;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
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
    private String path = "src/main/resources/temp/combined.png";

    @Autowired
    public InvoicePdf(QrCodeGenerator qrCodeGenerator) {
        this.qrCodeGenerator = qrCodeGenerator;
    }

    public void generateInvoice(PdfDocument pdfDocument, OfferRequest offerRequest, double total) {
        PdfPage page = pdfDocument.addNewPage();
        Rectangle rectangle = new Rectangle(0, 0, 100, 250);
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.rectangle(rectangle);
        pdfCanvas.stroke();
        Canvas canvas = new Canvas(pdfCanvas, pdfDocument, rectangle);
        qrCodeGenerator.generateQR(generatePayload(offerRequest, total));
        Image image = null;
        try {
            image = new Image(ImageDataFactory.create(path)).setMaxHeight(30).setPadding(0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (image != null) {
            canvas.add(image);
        }
        canvas.add(new Paragraph("Hello"));
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
