package ch.zhaw.unicalc.pdfGenerator.Controller;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Service.PDFService;
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

    private PDFService pdfService;

    @Autowired
    public PDFController(PDFService pdfService) {
        this.pdfService = pdfService;
    }

    @RequestMapping(value = "/toPDF", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generatePDF(@RequestBody OfferRequest offerRequest) {

        byte[] pdf = pdfService.generatePDF(offerRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = offerRequest.getTitle() + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        return response;
    }

}
