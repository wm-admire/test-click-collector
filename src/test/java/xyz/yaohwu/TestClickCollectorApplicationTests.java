package xyz.yaohwu;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.yaohwu.web.TestClickCollectorApplication;

@SpringBootTest(classes = TestClickCollectorApplication.class)
class TestClickCollectorApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertTrue(true);
    }

}
