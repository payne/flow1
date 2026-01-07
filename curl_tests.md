# CURL Test Commands for Order Flow Application

## Spring Boot Actuator Endpoints

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Application Info
```bash
curl http://localhost:8080/actuator/info
```

### Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

## Flowable REST API Endpoints

### List All Process Definitions
```bash
curl http://localhost:8080/flowable-rest/service/repository/process-definitions
```

### List Running Process Instances
```bash
curl http://localhost:8080/flowable-rest/service/runtime/process-instances
```

### Get Specific Process Definition Details
```bash
# Replace {processDefinitionId} with actual ID from the list command above
curl http://localhost:8080/flowable-rest/service/repository/process-definitions/{processDefinitionId}
```

### Start a Process Instance (Electronics Order)
```bash
curl -X POST http://localhost:8080/flowable-rest/service/runtime/process-instances \
  -H "Content-Type: application/json" \
  -d '{
    "processDefinitionKey": "electronics-order-process",
    "variables": [
      {
        "name": "orderId",
        "value": "ORD-001"
      },
      {
        "name": "totalAmount",
        "value": 1500
      }
    ]
  }'
```

### Start a Process Instance (Clothing Order)
```bash
curl -X POST http://localhost:8080/flowable-rest/service/runtime/process-instances \
  -H "Content-Type: application/json" \
  -d '{
    "processDefinitionKey": "clothing-order-process",
    "variables": [
      {
        "name": "orderId",
        "value": "ORD-002"
      }
    ]
  }'
```

### Start a Process Instance (Food Order - Refrigerated)
```bash
curl -X POST http://localhost:8080/flowable-rest/service/runtime/process-instances \
  -H "Content-Type: application/json" \
  -d '{
    "processDefinitionKey": "food-order-process",
    "variables": [
      {
        "name": "orderId",
        "value": "ORD-003"
      },
      {
        "name": "requiresRefrigeration",
        "value": true
      }
    ]
  }'
```

### List All Tasks
```bash
curl http://localhost:8080/flowable-rest/service/runtime/tasks
```

### Get Task Details
```bash
# Replace {taskId} with actual task ID
curl http://localhost:8080/flowable-rest/service/runtime/tasks/{taskId}
```

### Complete a User Task
```bash
# Replace {taskId} with actual task ID
curl -X POST http://localhost:8080/flowable-rest/service/runtime/tasks/{taskId} \
  -H "Content-Type: application/json" \
  -d '{
    "action": "complete"
  }'
```

### Query Process Instances by Process Definition Key
```bash
curl -X POST http://localhost:8080/flowable-rest/service/query/process-instances \
  -H "Content-Type: application/json" \
  -d '{
    "processDefinitionKey": "electronics-order-process"
  }'
```

### Get Process Instance Variables
```bash
# Replace {processInstanceId} with actual instance ID
curl http://localhost:8080/flowable-rest/service/runtime/process-instances/{processInstanceId}/variables
```

## Notes

- All Flowable REST endpoints are available at `/flowable-rest/service/*`
- For full API documentation, see: https://www.flowable.com/open-source/docs/bpmn/ch15-REST
- Replace placeholder values like `{processDefinitionId}`, `{taskId}`, and `{processInstanceId}` with actual IDs from previous responses
