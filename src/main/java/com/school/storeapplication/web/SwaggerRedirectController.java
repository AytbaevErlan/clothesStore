package com.school.storeapplication.web;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    private final String swaggerUiPath;

    public SwaggerRedirectController(
            @Value("${springdoc.swagger-ui.path:/swagger-ui/index.html}") String swaggerUiPath) {
        this.swaggerUiPath = normalize(swaggerUiPath);
    }

    @GetMapping("/swagger-ui.html")
    public String redirect() {
        return "redirect:" + swaggerUiPath;
    }

    private static String normalize(String path) {
        if (path == null || path.isEmpty()) {
            return "/swagger-ui/index.html";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}