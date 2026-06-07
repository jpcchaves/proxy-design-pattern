package br.com.designpattern.proxy.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Getter
@Service("configurationDummyService")
public class ConfigurationDummyService {

    private final String serviceId;

    public ConfigurationDummyService() {
        String serviceId = UUID.randomUUID().toString();
        this.serviceId = serviceId;

        log.info("[ConfigurationDummyService] Iniciando configuration dummy service com o ID: {}", serviceId);
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
