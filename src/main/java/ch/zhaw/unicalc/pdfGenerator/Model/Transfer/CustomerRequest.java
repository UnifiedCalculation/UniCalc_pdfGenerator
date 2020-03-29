package ch.zhaw.unicalc.pdfGenerator.Model.Transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CustomerRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
}
