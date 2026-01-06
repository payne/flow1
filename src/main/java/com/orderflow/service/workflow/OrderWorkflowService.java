package com.orderflow.service.workflow;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Flowable workflow operations.
 */
public interface OrderWorkflowService {

    String startOrderProcess(Long orderId);

    ProcessInstance getProcessInstanceForOrder(Long orderId);

    List<Task> getActiveTasksForOrder(Long orderId);

    void completeTask(String taskId, Map<String, Object> variables);

    List<Task> getPendingApprovalTasks();
}
