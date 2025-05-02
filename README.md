# Payment Transaction Manager

Sistema de gestión de pagos con integración de Stripe que permite procesar pagos con tarjetas de crédito y mantener un registro sincronizado de las transacciones.

## Características Principales

- ✅ Integración con Stripe para procesamiento de pagos
- ✅ Sincronización automática de estados de pago
- ✅ Gestión de historial de estados de pago
- ✅ Soporte para múltiples tipos de tarjetas de prueba
- ✅ API RESTful para gestión de pagos
- ✅ Base de datos local para seguimiento de transacciones
- ✅ Despliegue con Docker

## Requisitos Previos

- Java 17 o superior
- Maven
- Cuenta de Stripe (modo test o producción)
- Base de datos PostgreSQL
- Docker y Docker Compose (opcional, para despliegue)

## Configuración

1. Clonar el repositorio
2. Configurar las variables de entorno en el archivo `.env`:
   ```
   STRIPE_API_KEY=sk_test_...
   STRIPE_PUBLISHABLE_KEY=pk_test_...
   ```

3. Configurar la base de datos en `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/payment_db
       username: payment_user
       password: payment_pass
   ```

## Despliegue con Docker

El proyecto incluye configuración para despliegue con Docker:

1. Construir la imagen:
```bash
docker build -t payment-transaction-manager .
```

2. Iniciar los servicios con Docker Compose:
```bash
docker-compose up -d
```

Esto iniciará:
- La aplicación Spring Boot en el puerto 8080
- Una base de datos PostgreSQL en el puerto 5432

## Estructura del Proyecto

```
src/main/java/com/payment/
├── card/                 # Gestión de tarjetas y confirmaciones
├── transaction/          # Gestión de transacciones
│   ├── controller/       # Controladores REST
│   ├── service/          # Servicios de negocio
│   ├── repository/       # Repositorios de datos
│   ├── model/           # Entidades de dominio
│   └── dto/             # Objetos de transferencia de datos
└── exception/           # Manejo de excepciones
```

## Endpoints Principales

### Gestión de Pagos
- `POST /api/v1/payments/create` - Crear un nuevo pago
- `GET /api/v1/payments/{paymentIntentId}` - Obtener estado de un pago
- `PUT /api/v1/payments/update/{paymentIntentId}` - Actualizar monto de pago
- `POST /api/v1/payments/cancel/{paymentIntentId}` - Cancelar un pago
- `PATCH /api/v1/payments/capture/{paymentIntentId}` - Capturar un pago autorizado

### Confirmación de Pagos con Tarjetas
- `POST /api/v1/payments/card/confirm/{paymentIntentId}` - Confirmar pago con tarjeta
- `POST /api/v1/payments/card/test/visa/confirm/{paymentIntentId}` - Confirmar con tarjeta Visa de prueba
- `POST /api/v1/payments/card/test/mastercard/confirm/{paymentIntentId}` - Confirmar con tarjeta Mastercard de prueba
- `GET /api/v1/payments/card/test/list` - Listar tarjetas de prueba disponibles

## Sincronización de Estados

El sistema implementa dos mecanismos de sincronización:

1. **Sincronización Manual**: A través de los endpoints de confirmación de pago
2. **Sincronización Automática**: Mediante el `PaymentStatusPollingService` que verifica el estado de los pagos pendientes cada 30 segundos

## Tarjetas de Prueba

El sistema incluye soporte para las siguientes tarjetas de prueba de Stripe:

- Visa (4242 4242 4242 4242)
- Visa Debit (4000 0000 0000 0002)
- Mastercard (5555 5555 5555 4444)
- Mastercard 2-series (2223 0031 2200 3222)
- American Express (3782 8224 6310 005)
- Discover (6011 1111 1111 1117)
- Diners Club (3056 9309 0259 04)
- JCB (3566 0020 2036 0505)
- UnionPay (6200 0000 0000 0005)

## Ejemplo de Uso

1. Crear un nuevo pago:
```bash
curl -X POST http://localhost:8080/api/v1/payments/create \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000, "currency": "usd"}'
```

2. Confirmar el pago con una tarjeta de prueba:
```bash
curl -X POST http://localhost:8080/api/v1/payments/card/test/visa/confirm/{paymentIntentId}
```

## Seguridad

- Las credenciales de Stripe se manejan a través de variables de entorno
- Validación de estados de pago antes de operaciones críticas
- Manejo centralizado de excepciones
- Registro de historial de cambios de estado

## Documentación Adicional

- [Documentación de Stripe](https://stripe.com/docs)
- [Guía de Integración de Stripe](https://stripe.com/docs/payments)
- [Documentación de Spring Boot](https://spring.io/projects/spring-boot)
- [Documentación de PostgreSQL](https://www.postgresql.org/docs/)

## Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.
