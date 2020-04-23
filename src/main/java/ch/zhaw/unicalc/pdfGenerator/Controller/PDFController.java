package ch.zhaw.unicalc.pdfGenerator.Controller;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Service.OfferInvoicePdf;
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

    private OfferInvoicePdf offerInvoicePdf;
    private static final String pdfGenerator = "/toPdf";

    @Autowired
    public PDFController(OfferInvoicePdf offerInvoicePdf) {
        this.offerInvoicePdf = offerInvoicePdf;
    }


    /**
     * Generates a PDF for the Invoice and returns it.
     *
     * @param offerRequest Invoice is constructed the same way as offer
     * @return The Invoice PDF in byte[]
     */
    @RequestMapping(value = pdfGenerator + "/invoice", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generateInvoice(@RequestBody OfferRequest offerRequest) {
        byte[] pdf = offerInvoicePdf.generatePDF(false, offerRequest);

        HttpHeaders headers = createPdfHeader(offerRequest.getTitle());
        ResponseEntity<byte[]> response = new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        return response;
    }


    /**
     * Generates a PDF for the Offer and returns it as a byte-Array;
     *
     * @param offerRequest The Offer-JSON
     * @return The Offer PDF in byte[]
     */
    @RequestMapping(value = pdfGenerator + "/offer", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generateOffer(@RequestBody OfferRequest offerRequest) {

        byte[] pdf = offerInvoicePdf.generatePDF(true, offerRequest);

        HttpHeaders headers = createPdfHeader(offerRequest.getTitle());
        ResponseEntity<byte[]> response = new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        return response;
    }

    private HttpHeaders createPdfHeader(String title) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = title + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return headers;
    }

}
