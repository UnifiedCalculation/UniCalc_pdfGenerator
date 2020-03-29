package ch.zhaw.unicalc.pdfGenerator.Controller;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.InvoiceRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Service.OfferPdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PDFController {

    private OfferPdf offerPdf;
    private static final String pdfGenerator = "/toPdf";

    @Autowired
    public PDFController(OfferPdf offerPdf) {
        this.offerPdf = offerPdf;
    }


    /**
     * Generates a PDF for the Invoice and returns it.
     *
     * @param invoiceRequest The Invoice-JSON
     * @return The Invoice PDF in byte[]
     */
    @RequestMapping(value = pdfGenerator + "/invoice", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generateInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        return null;
    }


    /**
     * Generates a PDF for the Offer and returns it as a byte-Array;
     *
     * @param offerRequest The Offer-JSON
     * @return The Offer PDF in byte[]
     */
    @RequestMapping(value = pdfGenerator + "/offer", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generateOffer(@RequestBody OfferRequest offerRequest) {

        byte[] pdf = offerPdf.generatePDF(offerRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = offerRequest.getTitle() + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        return response;
    }

}
