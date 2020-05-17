package ch.zhaw.unicalc.pdfGenerator.Model.Transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BankRequest {
    private String paymentReference;
    private String additionalInformation;
    private String invoiceInformation;
    private String[] alternativeProcedure;
}
