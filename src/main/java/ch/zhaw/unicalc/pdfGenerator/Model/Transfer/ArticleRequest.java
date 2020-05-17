package ch.zhaw.unicalc.pdfGenerator.Model.Transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ArticleRequest {

    private String name;
    private Integer number;
    private String unit;
    private Integer amount;
    private Double discount;
    private Double price;
    private String description;
}
