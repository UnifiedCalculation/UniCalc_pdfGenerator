package ch.zhaw.unicalc.pdfGenerator.Service;

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

    public void generateInvoice(PdfDocument pdfDocument) {
        PdfPage page = pdfDocument.addNewPage();
        Rectangle rectangle = new Rectangle(36, 500, 100, 250);
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.rectangle(rectangle);
        pdfCanvas.stroke();
        Canvas canvas = new Canvas(pdfCanvas, pdfDocument, rectangle);
        qrCodeGenerator.generateQR("");
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
}
