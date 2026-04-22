package com.example.demo.odata;

import com.example.demo.repository.UserRepository;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Configuration
public class ODataConfig {

    private final UserRepository userRepository;

    public ODataConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public ServletRegistrationBean<HttpServlet> odataServlet() {
        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) {
                OData odata = OData.newInstance();
                ServiceMetadata metadata = odata.createServiceMetadata(new UserEdmProvider(), new ArrayList<>());
                ODataHttpHandler handler = odata.createHandler(metadata);
                handler.register(new UserEntityCollectionProcessor(userRepository));
                handler.register(new UserEntityProcessor(userRepository));
                handler.process(req, resp);
            }
        };

        ServletRegistrationBean<HttpServlet> bean = new ServletRegistrationBean<>(servlet, "/odata/*");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
