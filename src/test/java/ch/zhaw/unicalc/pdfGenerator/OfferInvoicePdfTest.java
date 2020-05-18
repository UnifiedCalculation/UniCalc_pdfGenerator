package ch.zhaw.unicalc.pdfGenerator;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ArticleRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.CompanyRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.CustomerRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.EntryRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.ProjectRequest;
import ch.zhaw.unicalc.pdfGenerator.Service.GeneralPdf;
import ch.zhaw.unicalc.pdfGenerator.Service.InvoicePdf;
import ch.zhaw.unicalc.pdfGenerator.Service.OfferInvoicePdf;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OfferInvoicePdfTest {

    @Mock
    private GeneralPdf generalPdfMock;

    @Mock
    private InvoicePdf invoicePdf;

    @Mock
    private Table table;

    private OfferRequest offerRequestWithout;
    private OfferRequest offerRequest;

    private OfferInvoicePdf offerInvoicePdf;

    @Before
    public void setUp() {
        ArticleRequest articleRequestWithout1 = ArticleRequest.builder()
                .price(10.40)
                .amount(3)
                .discount(0.0)
                .name("")
                .description("")
                .unit("")
                .build();
        ArticleRequest articleRequestWithout2 = ArticleRequest.builder()
                .price(42.75)
                .amount(4)
                .discount(0.0)
                .name("")
                .description("")
                .unit("")
                .build();
        ArticleRequest articleRequestWithout3 = ArticleRequest.builder()
                .price(5.45)
                .amount(35)
                .discount(0.0)
                .name("")
                .description("")
                .unit("")
                .build();
        EntryRequest entryRequestWithout = EntryRequest.builder()
                .articles(new HashSet<>())
                .title("")
                .build();
        entryRequestWithout.getArticles().add(articleRequestWithout1);
        entryRequestWithout.getArticles().add(articleRequestWithout2);
        entryRequestWithout.getArticles().add(articleRequestWithout3);

        ArticleRequest articleRequest1 = ArticleRequest.builder()
                .price(10.40)
                .amount(3)
                .discount(5.0)
                .name("")
                .description("")
                .unit("")
                .build();
        ArticleRequest articleRequest2 = ArticleRequest.builder()
                .price(42.75)
                .amount(4)
                .discount(50.0)
                .name("")
                .description("")
                .unit("")
                .build();
        ArticleRequest articleRequest3 = ArticleRequest.builder()
                .price(5.45)
                .amount(35)
                .discount(10.5)
                .name("")
                .description("")
                .unit("")
                .build();
        EntryRequest entryRequest = EntryRequest.builder()
                .articles(new HashSet<>())
                .title("")
                .build();
        entryRequest.getArticles().add(articleRequest1);
        entryRequest.getArticles().add(articleRequest2);
        entryRequest.getArticles().add(articleRequest3);

        CompanyRequest companyRequest = CompanyRequest.builder()
                .logo("https://community.upc.ch/t5/image/serverpage/image-id/943i3E665BDBC8E5F07E?v=1.0")
                .address("ABCStrasse 123")
                .build();

        CustomerRequest customerRequest = CustomerRequest.builder()
                .name("Customer")
                .build();

        ProjectRequest projectRequest = ProjectRequest.builder()
                .company(companyRequest)
                .customer(customerRequest)
                .build();

        this.offerRequestWithout = OfferRequest.builder()
                .discount(0.0)
                .entries(new HashSet<>())
                .projectInformation(projectRequest)
                .build();
        this.offerRequestWithout.getEntries().add(entryRequestWithout);

        this.offerRequest = OfferRequest.builder()
                .discount(0.0)
                .entries(new HashSet<>())
                .projectInformation(projectRequest)
                .build();
        this.offerRequest.getEntries().add(entryRequest);
        this.offerInvoicePdf = new OfferInvoicePdf(generalPdfMock, invoicePdf);
    }

    @Test
    public void calculateTotalSimple() {
        when(this.table.addCell(any(Cell.class))).thenReturn(this.table);
        Double total = this.offerInvoicePdf.createContent(table, this.offerRequestWithout);
        assertEquals(392.95, total);
    }

    @Test
    public void calculateTotalWithDiscount() {
        when(this.table.addCell(any(Cell.class))).thenReturn(this.table);
        Double total = this.offerInvoicePdf.createContent(table, this.offerRequest);
        assertEquals(285.86, total);
    }

}
