package ch.zhaw.unicalc.pdfGenerator.Model.Transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@Builder
public class SegmentRequest {
    private String title;
    private Set<ArticleRequest> articles;
}
