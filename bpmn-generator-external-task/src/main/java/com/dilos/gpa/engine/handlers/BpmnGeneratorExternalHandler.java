package com.dilos.gpa.engine.handlers;

import java.util.Map;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription("bpmn-generator-worker")
public class BpmnGeneratorExternalHandler implements ExternalTaskHandler {

    public void execute(ExternalTask task, ExternalTaskService service) {
        try {
            System.out.println("External Task BPMN Generator - Started Processing");

            Map<String, Object> vars = Map.of(
                    "statusProcess", "OK"
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
