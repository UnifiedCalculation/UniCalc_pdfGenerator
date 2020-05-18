package ch.zhaw.unicalc.pdfGenerator;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;

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

    @BeforeAll
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testConnectionOffer_ok() throws Exception {
        this.mockMvc.perform(post("/toPdf/offer")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(convertJSON("/controller/offer.json")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testConnectionInvoice_ok() throws Exception {
        this.mockMvc.perform(post("/toPdf/invoice")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(convertJSON("/controller/invoice.json")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test(expected = NestedServletException.class)
    public void invalidLogoFileTest() throws Exception {
        this.mockMvc.perform(post("/toPdf/offer")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(convertJSON("/controller/invoiceInvalidLogo.json")));
    }


    private String convertJSON(String json) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream(json), "UTF-8");
    }
}
