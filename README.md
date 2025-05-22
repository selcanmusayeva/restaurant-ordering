# Restaurant Ordering System

A comprehensive Spring Boot backend for a QR code-based restaurant ordering system that allows customers to scan table QR codes, view menus, place orders, and check order status.

## Features

- QR code-based table identification
- Role-based authentication (KITCHEN_STAFF, MANAGER, WAITER)
- Real-time order status updates
- Menu management with categories
- Order processing with status tracking
- Message queue integration for kitchen orders
- Redis caching for performance optimization

## Technical Stack

- Spring Boot 3.x
- Java 17
- Spring Data JPA
- Spring Security with JWT
- RabbitMQ
- PostgreSQL
- Redis
- Swagger/OpenAPI documentation

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 14 or higher
- RabbitMQ 3.8 or higher
- Redis 6.0 or higher

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd restaurant-ordering
   ```

2. Configure the database:
   - Create a PostgreSQL database named `restaurant_ordering`
   - Update the database credentials in `application.properties`

3. Configure RabbitMQ:
   - Install and start RabbitMQ
   - Update RabbitMQ credentials in `application.properties` if needed

4. Configure Redis:
   - Install and start Redis
   - Update Redis configuration in `application.properties` if needed

5. Build the project:
   ```bash
   mvn clean install
   ```

6. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

Once the application is running, you can access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Security

The application uses JWT-based authentication. To access protected endpoints:
1. Login using the `/api/v1/auth/login` endpoint
2. Include the received JWT token in the Authorization header:
   ```
   Authorization: Bearer <your-token>
   ```

## Project Structure

```
src/main/java/com/ordering/restaurant/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/             # Data Transfer Objects
├── exception/       # Custom exceptions
├── model/           # JPA entities
├── repository/      # JPA repositories
├── security/        # Security configuration
├── service/         # Business logic
└── util/            # Utility classes
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 

## Contribution
Everybody 20%
