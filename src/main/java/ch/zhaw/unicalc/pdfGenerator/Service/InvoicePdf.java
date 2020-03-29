package ch.zhaw.unicalc.pdfGenerator.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoicePdf {

    private GeneralPdf generalPdf;

    @Autowired
    public InvoicePdf(GeneralPdf generalPdf) {
        this.generalPdf = generalPdf;
    }
}
