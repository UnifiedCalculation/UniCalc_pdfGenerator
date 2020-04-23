package ch.zhaw.unicalc.pdfGenerator.Model.Transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CustomerRequest {
    private String name;
    private String companyName;
    private String department;
    private String address;
    private String land;
    private String zip;
    private String city;
}
