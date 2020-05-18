package ch.zhaw.unicalc.pdfGenerator;

import ch.zhaw.unicalc.pdfGenerator.Model.Transfer.OfferRequest;
import ch.zhaw.unicalc.pdfGenerator.Service.OfferInvoicePdf;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PDFControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OfferInvoicePdf offerInvoicePdf;

    @Mock
    private OfferRequest offerRequest;

    @BeforeAll
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testConnectionOffer_ok() throws Exception {
        when(offerInvoicePdf.generatePDF(true, offerRequest)).thenReturn(new byte[]{});
        this.mockMvc.perform(post("/toPdf/offer")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(convertJSON("/controller/offer.json")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testConnectionInvoice_ok() throws Exception {
        when(offerInvoicePdf.generatePDF(false, offerRequest)).thenReturn(new byte[]{});
        this.mockMvc.perform(post("/toPdf/invoice")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(convertJSON("/controller/invoice.json")))
                .andExpect(status().is2xxSuccessful());
    }

    private String convertJSON(String json) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(json), "UTF-8");
    }
}
