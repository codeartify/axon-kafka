# Axon Kafka Demo

Event-driven microservices demo using Axon Framework with Kafka and PostgreSQL.

## Prerequisites

- Java 21
- Docker & Docker Compose
- Maven
- IntelliJ IDEA (recommended for HTTP requests)

## Running the Application

### 1. Start Infrastructure

```bash
docker compose up
```

This starts:
- PostgreSQL (customers) on port 5433
- PostgreSQL (orders) on port 5434
- Kafka on port 9092

### 2. Start Services

Start the Order Service:
```bash
cd order-service
mvn spring-boot:run
```

Start the Customer Service (in a new terminal):
```bash
cd customer-service
mvn spring-boot:run
```

The services run on:
- Order Service: http://localhost:8081
- Customer Service: http://localhost:8082

### 3. Test the Application

#### Step 1: Register a Customer

Use `requests/customer.http`:

```http
POST http://localhost:8082/customers/register
Content-Type: application/json

{
  "name": "Oliver Zihler"
}
```

The response will include the customer ID, which is automatically stored in the HTTP client environment (`{{customerId}}`).

Response:
```json
{
  "id": "e6686671-7de9-45fb-8a02-b449db20dc6e",
  "name": "Oliver Zihler",
  "orders": []
}
```

#### Step 2: Place an Order

Use `requests/orders.http` - the `{{customerId}}` is automatically used from the previous request:

```http
POST http://localhost:8081/orders?customerId={{customerId}}&amount=12.5
```

The order event will be:
1. Published to Kafka by Order Service (`OrderPlacedEvent`)
2. Consumed by Customer Service
3. Processed as a command to add the order to the customer (`AddOrderCommand`)
4. Stored as an event (`OrderAddedEvent`)

#### Step 3: Verify the Customer Data

Use the GET endpoint in `requests/orders.http`:

```http
GET http://localhost:8082/customers/{{customerId}}
```

Response:
```json
{
  "id": "e6686671-7de9-45fb-8a02-b449db20dc6e",
  "name": "Oliver Zihler",
  "orders": [
    {
      "id": "4673006b-e505-493e-b28f-243dae359180",
      "amount": 12.5
    }
  ]
}
```

## Architecture

### Services

- **Order Service**: Creates orders and publishes `OrderPlacedEvent` to Kafka
- **Customer Service**:
  - Manages customer aggregates using Event Sourcing
  - Consumes order events from Kafka
  - Maintains SQL projections in PostgreSQL
  - Provides REST API with subscription queries for eventual consistency

### Infrastructure

- **Kafka**: Event bus for inter-service communication
- **PostgreSQL**:
  - Event store (domain_event_entry table)
  - SQL projections (customers and orders tables)

### Key Patterns

- **Event Sourcing**: Customer aggregate state is reconstructed from events
- **CQRS**: Separate write model (aggregates) and read model (projections)
- **Subscription Queries**: REST endpoints wait for projections to be updated before returning
- **Event-Driven Architecture**: Services communicate via Kafka events
