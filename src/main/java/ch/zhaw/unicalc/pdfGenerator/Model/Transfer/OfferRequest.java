package ch.zhaw.unicalc.pdfGenerator.Model.Transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class OfferRequest {
    private String title;
    private Integer discount;
    private Set<EntryRequest> entries;
    private ProjectRequest projectInformation;
}
