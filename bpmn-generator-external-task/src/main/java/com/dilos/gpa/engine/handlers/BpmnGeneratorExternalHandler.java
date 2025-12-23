package com.dilos.gpa.engine.handlers;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ExternalTaskSubscription("bpmn-generator-worker")
public class BpmnGeneratorExternalHandler implements ExternalTaskHandler {

    public void execute(ExternalTask task, ExternalTaskService service) {

        String pedidoId = task.getVariable("pedidoId");
        Integer valor = task.getVariable("valor");

        try {
            System.out.println("Processando pedidoId=" + pedidoId + " valor=" + valor);

            Map<String, Object> vars = Map.of(
                    "statusProcessamento", "OK"
            );

            service.complete(task, vars);

        } catch (Exception ex) {
            Integer retries = task.getRetries();
            if (retries == null) {
                retries = 3;
            }

            service.handleFailure(
                    task,
                    ex.getMessage(),
                    ex.toString(),
                    retries - 1,
                    30000
            );
        }
    }
}
