package br.com.designpattern.proxy.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Getter
@Setter
@Service("configurationDummyService")
public class ConfigurationDummyService {

    private String serviceId;

    public ConfigurationDummyService() {
        this.serviceId = UUID.randomUUID().toString();
    }

    public void logDelegateId() {
        log.info("[ConfigurationDummyService] Delegate atual no contexto do spring: {}", this.getServiceId());
    }

    public String simulateBehaviour() {
        var message = "Simulando comportamento do serviço com ID: " + this.getServiceId();
        log.info(message);
        return message;
    }
}
