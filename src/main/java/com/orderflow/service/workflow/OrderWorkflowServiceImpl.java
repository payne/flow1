package com.orderflow.service.workflow;

import com.orderflow.domain.ItemCategory;
import com.orderflow.domain.Order;
import com.orderflow.repository.OrderRepository;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for Flowable workflow integration.
 * Determines which BPMN process to trigger based on item category.
 */
@Service
@Transactional
public class OrderWorkflowServiceImpl implements OrderWorkflowService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public String startOrderProcess(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Determine category from order items (using first item's category)
        ItemCategory category = order.getPrimaryCategory();
        if (category == null) {
            throw new RuntimeException("Order has no items: " + orderId);
        }

        // Get process key from category enum
        String processKey = category.getProcessKey();

        // Prepare process variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId);
        variables.put("orderNumber", order.getOrderNumber());
        variables.put("category", category.name());
        variables.put("totalAmount", order.getTotalAmount().doubleValue());
        variables.put("customerId", order.getCustomer().getId());

        // Start the appropriate BPMN process
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(processKey, variables);

        // Store process instance ID in order
        order.setProcessInstanceId(processInstance.getId());
        orderRepository.save(order);

        return processInstance.getId();
    }

    @Override
    public ProcessInstance getProcessInstanceForOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getProcessInstanceId() == null) {
            return null;
        }

        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(order.getProcessInstanceId())
                .singleResult();
    }

    @Override
    public List<Task> getActiveTasksForOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getProcessInstanceId() == null) {
            return List.of();
        }

        return taskService.createTaskQuery()
                .processInstanceId(order.getProcessInstanceId())
                .active()
                .list();
    }

    @Override
    public Task getTask(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    @Override
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }

    @Override
    public List<Task> getPendingApprovalTasks() {
        return taskService.createTaskQuery()
                .taskCandidateGroup("qa-team")
                .or()
                .taskCandidateGroup("food-safety-team")
                .endOr()
                .active()
                .list();
    }
}
