package br.com.designpattern.proxy.controller;

import br.com.designpattern.proxy.config.ConfigurationProxy;
import br.com.designpattern.proxy.service.ConfigurationDummyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/delegates")
public class DelegateController {

    private final ConfigurationProxy proxy;
    private final ApplicationContext applicationContext;
    private final ConfigurationDummyService service;

    public DelegateController(ConfigurationProxy proxy, ApplicationContext applicationContext, ConfigurationDummyService service) {
        this.proxy = proxy;
        this.applicationContext = applicationContext;
        this.service = service;
    }

    @GetMapping("/update-delegate")
    public ResponseEntity<?> updateDelegate() {
        return ResponseEntity.ok(proxy.updateDelegate());
    }

    @GetMapping("/current-delegate")
    public ResponseEntity<?> getDelegate() {
        return ResponseEntity.ok(proxy.currentDelegateId());
    }

    @GetMapping("/amount")
    public ResponseEntity<?> getAmount() {
        service.logDelegateId();

        Map<String, ?> beansConfigurationDummyService = applicationContext.getBeansOfType(ConfigurationDummyService.class);

        return ResponseEntity.ok(Map.of(
                "proxyDelegateId", proxy.currentDelegateId(),
                "beansConfigurationDummyService", beansConfigurationDummyService
        ));
    }

    @GetMapping("/simulate")
    public ResponseEntity<?> callService() {
        return ResponseEntity.ok(service.simulateBehaviour());
    }
}
