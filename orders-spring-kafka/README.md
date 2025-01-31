# Order Management System (Microservices)

[![GPL License](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

### Client Requirements

Design and implement a **lightweight, high-performance order management system** that seamlessly processes **200,000+ orders** per day while ensuring a **minimum of 99% uptime**. 
The core workflow involves **receiving an order from an external system**, performing **internal processing** (e.g., validation, enrichment, calculations), and then **sending the completed order to a third-party service**.

## 1. Architecture Definition

### 1.1 Overview

The proposed solution is a **lightweight order management system** designed to handle **200,000+ orders per day**. It must ensure high availability (99%+ uptime), fast processing, and easy scalability.

1. **Order Flow**
    - Orders are **ingested** from an external system via direct message publication.
    - The service processes each order (e.g., validation, calculations) and **stores** essential data in a relational database.
    - A **finalized** order event is published to a **third-party system**.

2. **Core Components**
    - **Application Layer**: A Spring Boot–based service that handles incoming requests, applies business rules, and manages orders.
    - **Data Layer**: A PostgreSQL database for robust, ACID-compliant storage of orders.
    - **Messaging Layer**: An Apache Kafka cluster used to ingest and distribute order-related events.

## 2. Technology Choices

### 2.1 Build and Dependency Management: **Gradle**

- **Why Gradle?**
    - **Faster builds** compared to traditional tools like Maven (especially with incremental compilation).

### 2.2 Data Access: **Spring Data JDBC**

- **Why Spring Data JDBC?**
    - **Lightweight** alternative to JPA for simpler data models.
    - Reduces the “magic” of heavy ORM layers, offering **better performance** and **straightforward SQL** operations.
    - Faster startup times and lower memory footprint, beneficial for **high-throughput** services.

### 2.3 Messaging and Event Streaming: **Apache Kafka**

- **Why Kafka?**
    - **High throughput** and **low latency**—Kafka can comfortably handle 200k+ daily events (and much more).
    - **Scalability**: Horizontal scaling via topic partitions and consumer groups.
    - **Reliability**: Replication and built-in persistence allow for fault tolerance and replayable events.

### 2.4 Relational Database: **PostgreSQL**

- **Why PostgreSQL?**
    - **Robust** open-source relational database with excellent **ACID** transaction support.
    - **Performance** and **scalability** features (indexes, partitioning, JSON support).
    - Trusted by large-scale production systems, matches well with Spring Data JDBC’s relational approach.

### 2.5 Database Migrations: **Flyway**

- **Why Flyway?**
    - **Automated schema migrations** ensure a consistent DB state across different environments (dev, test, prod).
    - Simplifies deployment pipelines by versioning each schema change, reducing human error and manual DB scripting.

### 2.6 Load Testing: **JMeter**

- **Why JMeter?**
    - **Comprehensive load and performance testing** framework for APIs, web services, and Kafka (with plugins).
    - Allows realistic simulation of high traffic (200k+ orders/day) and **identification of bottlenecks**.
    - **Open-source** and well-integrated with CI/CD pipelines.

### 2.7 Containerization: **Docker**

- **Why Docker?**
    - **Consistent runtime environment** across local, staging, and production.
    - Easy to **scale** by running multiple container instances, especially in orchestration platforms like Kubernetes.
    - Simplifies distribution and deployment of the microservice and its dependencies (Kafka, PostgreSQL, etc.).

## 3. Rationale and Benefits

1. **High Throughput and Real-Time Processing**
    - Kafka ensures orders can be **ingested** and **distributed** quickly, enabling near-real-time updates.
    - Spring Data JDBC’s lean data access layer avoids ORM overhead, improving response times.

2. **Scalability and Fault Tolerance**
    - Containerization (Docker) allows **horizontal scaling**: you can spin up more instances to handle spikes in traffic.
    - Kafka’s **replication** and PostgreSQL’s **reliability** provide resilience against node failures.

3. **Maintainability and Simplicity**
    - Gradle offers clear build scripts and faster builds for rapid iteration.
    - Flyway eliminates manual DB updates, reducing deployment risks and versioning errors.

4. **Performance Validation**
    - JMeter provides a robust mechanism to **stress-test** the system, ensuring it meets the **200k+ orders/day** benchmark.
    - Insights from load tests help with **capacity planning** and **tuning**.

## 4. How to Test

### 4.1 Start the Services

Use Docker Compose to build and run the containers:
```bash
docker-compose up --build
```

### 4.2 Open a New Console

In a second terminal window, exec into the running Kafka container:
```bash
docker exec -it kafka bash
```

Then paste the JSON payload to create a new order:
```bash
kafka-console-producer --broker-list localhost:9092 --topic orders <<EOF
{
  "action": "RECEIVE_ORDER",
  "order": {
    "number": "20250129-123456",
    "items": [
      {
        "description": "Red Sneakers",
        "quantity": 2,
        "product": {
          "sku": "SKU123456789",
          "barcode": "987654321012",
          "name": "Red Sneakers",
          "description": "High-quality running shoes",
          "thumbnailUrl": "https://cdn.example.com/images/sneakers.jpg",
          "price": 25.12
        }
      }
    ]
  }
}
EOF
```

### 4.3 Observe the Logs

```bash
c.j.o.kafka.listeners.OrderListener: Received: KafkaMessage[action=RECEIVE_ORDER, order=Order(...)]
c.j.o.k.producers.OrderProcessingProducer: Sending: KafkaMessage[action=PROCESS_ORDER, order=Order(...)]
c.j.o.k.listeners.OrderProcessingListener: Received: KafkaMessage[action=PROCESS_ORDER, order=Order(...)]
c.j.o.k.listeners.OrderProcessingListener: Processed: Order[...]
c.j.o.k.producers.OrderPickupProducer: Sending: KafkaMessage[action=PICKUP_ORDER, order=Order(...)]
```

### 4.4 Search for orders via the Rest API

```bash
curl --location 'http://localhost:8080/orders?page=0&size=10'
```

```json
{
  "content": [
    {
      "id": "209aa68e-4fb0-4627-8c14-69ebef059821",
      "status": "PICKUP",
      "number": "20250129-123456",
      "subtotalAmount": 50.24,
      "totalCharges": 0,
      "totalDiscounts": 0,
      "totalAmount": 50.24,
      "items": [
        {
          "id": "c1a944f1-92de-49a4-a55f-630168cd23f7",
          "index": 0,
          "description": "Red Sneakers",
          "quantity": 2,
          "totalAmount": 50.24,
          "product": {
            "id": "6625fb82-5f73-412e-92ee-6332797e5554",
            "sku": "SNK123456789",
            "barcode": "987654321012",
            "name": "Red Sneakers",
            "description": "High-quality running shoes",
            "thumbnailUrl": "https://cdn.example.com/images/sneakers.jpg",
            "price": 25.12
          }
        }
      ]
    }
  ]
}
```

### Conclusion

By combining **Spring Boot** with **Spring Data JDBC**, **Kafka**, **PostgreSQL**, **Flyway**, **JMeter**, and **Docker**, this architecture delivers a **high-performance, scalable, and fault-tolerant** solution. It meets the **client’s requirement** to process **200,000+ daily orders** with **99% uptime**, while also providing **flexibility** for future growth and integrations.

## License

This project is licensed under the **GNU General Public License v3.0** - see the [LICENSE](LICENSE) file for details.

Copyright (C) 2025 [Jerez Tech](https://jereztech.com)
