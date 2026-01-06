# Order Management System with Flowable BPMN

A Spring Boot application that uses Flowable BPMN to manage orders for an e-commerce website. Items are categorized into three types (Electronics, Clothing, Food), each processed by a distinct BPMN workflow with category-specific approval and shipping rules.

## Architecture

### Technology Stack
- **Backend**: Spring Boot 3.2.1, Java 17
- **Workflow Engine**: Flowable 7.0.1
- **Database**: PostgreSQL 16 with dual schemas
- **Frontend**: Thymeleaf, Bootstrap 5.3.2
- **Build Tool**: Maven
- **Database Migration**: Flyway

### Database Design
PostgreSQL instance with **two separate schemas**:
1. **`app_schema`** - Application data (customers, orders, items, inventory)
2. **`flowable_schema`** - Flowable BPMN engine tables (auto-created)

### BPMN Workflows

Three category-specific order processing workflows:

#### 1. Electronics Order Process (`electronics-order-process.bpmn`)
- **Validation**: Check warranty info, inventory availability
- **Approval**: QA approval required for orders > $1000
- **Fulfillment**: Include warranty card, firmware check
- **Shipping**: Signature required, insurance, FedEx delivery

#### 2. Clothing Order Process (`clothing-order-process.bpmn`)
- **Validation**: Check size/color availability
- **Approval**: None (fastest workflow)
- **Fulfillment**: Quality check, include return label
- **Shipping**: Standard USPS, easy returns

#### 3. Food Order Process (`food-order-process.bpmn`)
- **Validation**: Check expiration dates, refrigeration needs
- **Approval**: Food safety approval for refrigerated items
- **Fulfillment**: Temperature monitoring, expiration labeling
- **Shipping**: Refrigerated express for perishables, standard for non-perishables

## Project Structure

```
flow1/
├── pom.xml                                   # Maven dependencies
├── docker/
│   ├── docker-compose.yml                    # PostgreSQL + pgAdmin
│   └── init-db.sql                           # Schema creation
├── src/main/
│   ├── java/com/orderflow/
│   │   ├── OrderFlowApplication.java         # Main class
│   │   ├── config/
│   │   │   ├── DataSourceConfig.java         # Dual datasource setup
│   │   │   └── FlowableConfig.java           # Flowable configuration
│   │   ├── domain/                           # 7 entities + 2 enums
│   │   ├── repository/                       # 7 Spring Data repositories
│   │   ├── service/                          # Business logic layer
│   │   │   └── workflow/                     # Flowable integration
│   │   │       ├── delegates/                # 6 BPMN service task delegates
│   │   │       ├── OrderWorkflowService.java
│   │   │       └── OrderWorkflowServiceImpl.java
│   │   ├── controller/                       # MVC controllers (to be created)
│   │   └── dto/                              # Data transfer objects
│   └── resources/
│       ├── application.yml                   # Dual datasource config
│       ├── db/migration/                     # Flyway migrations
│       │   ├── V1__create_application_schema.sql
│       │   └── V2__insert_sample_data.sql
│       ├── processes/                        # BPMN 2.0 process definitions
│       │   ├── electronics-order-process.bpmn
│       │   ├── clothing-order-process.bpmn
│       │   └── food-order-process.bpmn
│       ├── static/                           # CSS, JS, images
│       └── templates/                        # Thymeleaf templates (to be created)
```

## Session Logging

This project automatically documents all Claude Code development sessions.

**View Current Session:**
```bash
cat SESSION_LOG.md
```

**All Session Logs:** `docs/sessions/`

Each session log includes:
- Requirements and planning
- Files created/modified
- Design decisions and rationale
- Issues encountered and solutions
- Next steps

See [`docs/SESSION_LOGGING_SETUP.md`](docs/SESSION_LOGGING_SETUP.md) for details.

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for PostgreSQL)

### Step 1: Start PostgreSQL Database

```bash
cd docker
docker-compose up -d
```

This starts:
- PostgreSQL on port 5432
- pgAdmin on port 5050 (http://localhost:5050)
  - Email: admin@orderflow.com
  - Password: admin

### Step 2: Verify Database Schemas

Connect to PostgreSQL and verify both schemas exist:

```bash
docker exec -it orderflow-db psql -U orderflow_user -d orderflow
```

```sql
-- List all schemas
\dn

-- Should show:
-- app_schema
-- flowable_schema
```

### Step 3: Build the Application

```bash
mvn clean install
```

### Step 4: Run the Application

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**

### Step 5: Verify Startup

Check the logs for:
- Flowable engine initialization
- Database schema creation in `flowable_schema`
- Flyway migrations applied to `app_schema`
- BPMN processes deployed (3 processes)

## Database Schema

### Application Tables (`app_schema`)

- **customer** - Customer information
- **item** - Product catalog (category: ELECTRONICS, CLOTHING, FOOD)
- **inventory** - Stock management with reservation
- **order** - Orders with `process_instance_id` linking to Flowable
- **order_item** - Line items
- **approval** - Approval tracking (QA for electronics, safety for food)
- **shipment** - Shipping and tracking

### Sample Data

The `V2__insert_sample_data.sql` migration creates:
- 5 customers
- 15 items (5 electronics, 5 clothing, 5 food)
- Inventory records for all items

## How It Works

### Order Processing Flow

1. **Create Order** → `OrderService.createOrder(OrderDTO)`
2. **Determine Category** → First item's category determines workflow
3. **Start BPMN Process** → `OrderWorkflowService.startOrderProcess(orderId)`
   - Electronics → `electronics-order-process`
   - Clothing → `clothing-order-process`
   - Food → `food-order-process`
4. **Execute Workflow**:
   - Validation → `ValidateOrderDelegate` (checks inventory, reserves stock)
   - Approval (if needed) → User task for QA/Safety team
   - Payment → `ProcessPaymentDelegate`
   - Fulfillment → `FulfillmentDelegate` (finalize inventory)
   - Shipping → Category-specific shipping delegate
5. **Complete** → Order status updated to SHIPPED

### Process Selection

The `ItemCategory` enum maps categories to BPMN process keys:

```java
public enum ItemCategory {
    ELECTRONICS("electronics-order-process"),
    CLOTHING("clothing-order-process"),
    FOOD("food-order-process");
}
```

## API Endpoints (To Be Implemented)

### Customer Endpoints
- `GET /orders/new` - Order form
- `POST /orders` - Submit order
- `GET /orders/{id}` - Order status

### Admin Endpoints
- `GET /admin` - Dashboard
- `GET /admin/orders` - List all orders
- `GET /admin/orders/{id}` - Order details
- `POST /admin/orders/tasks/{taskId}` - Complete approval task
- `GET /admin/inventory` - Inventory management

## Development Status

### ✅ Completed

1. Project structure and Maven configuration
2. Dual datasource configuration (app_schema + flowable_schema)
3. Database schema with Flyway migrations
4. Domain model (7 entities, 2 enums)
5. Repository layer (7 JPA repositories)
6. Service layer (Order, Customer, Inventory services)
7. Flowable integration (OrderWorkflowService)
8. BPMN processes (3 workflows)
9. Service task delegates (6 delegates)
10. Sample data migration

### 🚧 To Be Implemented

1. **Controllers**:
   - CustomerOrderController
   - AdminOrderController
   - AdminInventoryController
   - AdminDashboardController

2. **Thymeleaf Templates**:
   - Layout template with Bootstrap
   - Order form (customer)
   - Order status page
   - Admin dashboard
   - Orders list (admin)
   - Order detail with workflow visualization
   - Inventory management

3. **Additional Features**:
   - Exception handling
   - Validation
   - Security (Spring Security)
   - REST API endpoints
   - Order tracking
   - Email notifications
   - Reporting

## Testing

### Test Order Creation (Once UI is ready)

Create an order with:
- **Electronics item (>$1000)** → Triggers QA approval task
- **Food item (refrigerated)** → Triggers food safety approval task
- **Clothing item** → No approval, fastest processing

### Monitor Workflow

Use Flowable UI (if enabled) or check database:

```sql
-- View process instances
SELECT * FROM flowable_schema.act_ru_execution;

-- View active tasks
SELECT * FROM flowable_schema.act_ru_task;

-- View process variables
SELECT * FROM flowable_schema.act_ru_variable;
```

## Configuration

### Database Credentials

Update `src/main/resources/application.yml` or use environment variables:

```yaml
spring:
  datasource:
    app:
      jdbc-url: jdbc:postgresql://localhost:5432/orderflow?currentSchema=app_schema
      username: orderflow_user
      password: orderflow_pass
```

### Flowable Configuration

Flowable auto-creates tables in `flowable_schema`:
- Process definitions
- Process instances
- Tasks
- Variables
- History

## Troubleshooting

### Schema Not Found

Ensure both schemas exist:
```sql
CREATE SCHEMA IF NOT EXISTS app_schema;
CREATE SCHEMA IF NOT EXISTS flowable_schema;
```

### Flyway Migration Fails

Drop and recreate `app_schema`:
```sql
DROP SCHEMA app_schema CASCADE;
CREATE SCHEMA app_schema;
```

### Process Not Starting

Check:
1. BPMN file is in `src/main/resources/processes/`
2. Process key matches `ItemCategory` enum
3. Process has `isExecutable="true"`

## Next Steps

1. **Create Controllers** - Implement MVC controllers for customer and admin
2. **Build UI** - Create Thymeleaf templates with Bootstrap
3. **Add Security** - Implement Spring Security for admin pages
4. **Testing** - Write unit and integration tests
5. **Monitoring** - Add Flowable Admin UI for process monitoring

## Contributing

To extend the application:
- Add new item categories → Create new enum value + BPMN process
- Modify workflows → Edit BPMN files in Flowable Modeler
- Add approval steps → Create user tasks in BPMN + approval table
- Custom shipping logic → Create new shipping delegate

## License

This project is created for educational purposes.
