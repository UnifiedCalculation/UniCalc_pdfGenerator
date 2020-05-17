package ch.zhaw.unicalc.pdfGenerator;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PDFControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setUp() {

    }

    @Test
    public void calculateTotal() {

    }
}
