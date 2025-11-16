package org.example.customerservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigTestRestController {

    @Autowired
    private CustomerConfigParams customerConfigParams;

    @GetMapping("/config-test")
    public CustomerConfigParams getConfigParams() {
        return customerConfigParams;
    }
}
