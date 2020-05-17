package ch.zhaw.unicalc.pdfGenerator.Model.Transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ProjectRequest {
    private CustomerRequest customer;
    private CompanyRequest company;
    private BankRequest bankInformation;
}
