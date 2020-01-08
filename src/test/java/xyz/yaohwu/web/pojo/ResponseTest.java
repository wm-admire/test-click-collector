package xyz.yaohwu.web.pojo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResponseTest {

    @Test
    void status404() {
        Response response = Response.error(404, "not found", "resource not found");
        Assertions.assertEquals(404, response.getStatus());
    }

    @Test
    void status400() {
        Response response = Response.error(400, "bad request", "bad request");

        Assertions.assertEquals(400, response.getStatus());
    }
}