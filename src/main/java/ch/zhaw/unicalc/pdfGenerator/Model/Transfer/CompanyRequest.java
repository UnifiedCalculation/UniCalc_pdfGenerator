package ch.zhaw.unicalc.pdfGenerator.Model.Transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CompanyRequest {

    private String name;
    private String url;
    private String logo;
    private String mail;
    private String address;
    private String phone;
    private String zip;
    private String city;
    private String contactPerson;
}
