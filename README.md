# 🎂 Sistema de Pastelería - Microservicios

Proyecto semestral de arquitectura de microservicios para la gestión integral de una pastelería: usuarios, productos, categorías, inventario, pedidos, pagos, notificaciones y reportes.

## Integrantes

- Solange Valladares

## Microservicios implementados

| Microservicio | Puerto | Descripción |
|---|---|---|
| ms-auth | 8089 | Autenticación y registro de usuarios |
| ms-usuarios | 8081 | Gestión de usuarios |
| ms-categorias | 8082 | Gestión de categorías de productos |
| ms-productos | 8083 | Gestión de productos |
| ms-pedidos | 8084 | Gestión de pedidos |
| ms-inventario | 9085 | Gestión de inventario e ingredientes |
| ms-pagos | 8086 | Gestión de pagos |
| ms-notificaciones | 8087 | Envío de notificaciones |
| ms-reportes | 8088 | Generación de reportes |
| ms-eureka-server | 8761 | Service Discovery (Eureka) |
| ms-gateway | 8080 | API Gateway (Spring Cloud Gateway) |

## Rutas principales del Gateway

Todas las peticiones pasan por el Gateway en `http://localhost:8080`:

- `/api/auth/**` → ms-auth
- `/api/usuarios/**` → ms-usuarios
- `/api/categorias/**` → ms-categorias
- `/api/productos/**` → ms-productos
- `/api/pedidos/**` → ms-pedidos
- `/api/inventario/**` → ms-inventario
- `/api/pagos/**` → ms-pagos
- `/api/notificaciones/**` → ms-notificaciones
- `/api/reportes/**` → ms-reportes

## Documentación Swagger

Cada microservicio expone su documentación en: