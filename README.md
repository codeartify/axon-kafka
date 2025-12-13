# Event-Sourced Microservices with Axon's ES Domain-Driven Design Domain Model and Kafka as Event Bus
## Prerequisites

- Java 21
- Docker & Docker Compose
- Maven

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

#### Register a Customer

Use `requests/customer.http`:

```http
POST http://localhost:8082/customers/register
Content-Type: application/json

{
  "name": "Oliver Zihler"
}
```

#### Get the Customer ID

Connect to the PostgreSQL database and retrieve the aggregate ID from the aggregate_identifier column of the domain_event_entry for CustomerRegisteredEvent:

```bash
psql -h localhost -p 5433 -U test -d customers
```

Password: `test`

```sql
select * from domain_event_entry;
```
```sql
global_index |           event_identifier           | meta_data | payload | payload_revision |                      payload_type                      |        time_stamp        |         aggregate_identifier         | sequence_number |       type        
--------------+--------------------------------------+-----------+---------+------------------+--------------------------------------------------------+--------------------------+--------------------------------------+-----------------+-------------------
            1 | a7dcdda5-e31c-4d1c-aaa1-226143031a32 |     16472 |   16473 |                  | com.codeartify.customerservice.CustomerRegisteredEvent | 2025-12-13T13:15:46.996Z | a37632b5-b6f8-401b-997a-457f28f7de3d |               0 | CustomerAggregate

```


Copy the `aggregate_identifier` value (`a37632b5-b6f8-401b-997a-457f28f7de3d`).

#### Place an Order

Use `requests/orders.http` with the customer ID:

```http
POST http://localhost:8081/orders?customerId=<PASTE-CUSTOMER-ID>&amount=12.5
```

The order event will be published to Kafka and consumed by the Customer Service.
There will be a OrderPlacedEvent (from Order Service) and an OrderAddedEvent (from Customer Service)

```sql
customers=# select * from domain_event_entry;
```
```sql
 global_index |           event_identifier           | meta_data | payload | payload_revision |                      payload_type                      |        time_stamp        |         aggregate_identifier         | sequence_number |       type        
--------------+--------------------------------------+-----------+---------+------------------+--------------------------------------------------------+--------------------------+--------------------------------------+-----------------+-------------------
            1 | aa77946d-4cd8-4fe4-a70c-88c015fc3afe |     16472 |   16473 |                  | com.codeartify.customerservice.CustomerRegisteredEvent | 2025-12-13T13:53:58.583Z | 8f02b67e-7d56-4991-96d6-b145928efff6 |               0 | CustomerAggregate
            2 | c9f67462-ec22-4106-a888-7e19f7af769a |     16474 |   16475 |                  | com.codeartify.customerservice.OrderPlacedEvent        | 2025-12-13T13:54:49.082Z | c9f67462-ec22-4106-a888-7e19f7af769a |               0 | 
            3 | a24ea1cc-befe-478b-89dc-fa5205f4469c |     16476 |   16477 |                  | com.codeartify.customerservice.OrderAddedEvent         | 2025-12-13T13:54:49.300Z | 8f02b67e-7d56-4991-96d6-b145928efff6 |               1 | CustomerAggregate
(3 rows)

```
## Architecture

- **Order Service**: Creates orders and publishes `OrderPlacedEvent` to Kafka
- **Customer Service**: Consumes order events from Kafka and updates customer records
- **Kafka**: Event bus for inter-service communication
- **PostgreSQL**: Event store and projection databases
