# Payment Transaction Manager with Stripe Integration

A Spring Boot application for processing payments using Stripe's API. This project provides a robust solution for handling payment transactions, including validation, error handling, and test card processing.

## Features

- Integration with Stripe Payment Intents API
- Test card processing capabilities
- Spring Batch for transaction processing
- Swagger UI for API documentation
- MySQL database integration
- Docker support for database deployment

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Docker and Docker Compose
- MySQL 8.0 (via Docker)
- Stripe API key (test mode)

## Getting Started

### 1. Clone the Repository
```bash
git clone [repository-url]
cd payment-transaction-manager
```

### 2. Configure Environment Variables
Create a `.env` file in the project root with your Stripe API key:
```
STRIPE_API_KEY=your_stripe_test_api_key
```

### 3. Start MySQL Database
```bash
docker-compose up -d
```

### 4. Build and Run the Application
```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Access the Swagger UI at `http://localhost:8080/swagger-ui.html` to view and test the API endpoints.

## Database Configuration

The application uses MySQL with the following default configuration:
- Database: payment_db
- Username: payment_user
- Password: payment_pass
- Port: 3306

## Test Cards

The application supports various test cards for different scenarios:
- Visa: 4242 4242 4242 4242
- Mastercard: 5555 5555 5555 4444
- American Express: 3782 822463 10005
- Discover: 6011 1111 1111 1117

## Project Structure

```
src/main/java/com/payment/
├── card/
│   ├── controller/    # Payment card controllers
│   ├── dto/          # Data Transfer Objects
│   └── service/      # Business logic
├── transaction/      # Transaction processing
└── PaymentTransactionManagerApplication.java
```

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   - Ensure MySQL container is running: `docker ps`
   - Check database credentials in `application.yml`
   - Verify port 3306 is not in use

2. **Stripe API Issues**
   - Verify your Stripe API key is correct
   - Ensure you're using test mode keys
   - Check Stripe API documentation for error codes

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## References

* [Stripe Payment Intents API Documentation](https://docs.stripe.com/api/payment_intents/object)
* [Stripe Payment Intents Guide](https://docs.stripe.com/payments/payment-intents)
* [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
* [MySQL Documentation](https://dev.mysql.com/doc/)
