<div align="center">

# 🏪 Store Management System

A role-based retail management system built in Java with a layered architecture, persistent storage, and real-world business rules.

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square)
![Version](https://img.shields.io/badge/version-v1.0--stable-success?style=flat-square)
![Architecture](https://img.shields.io/badge/architecture-3--layer-blue?style=flat-square)
![License](https://img.shields.io/badge/license-MIT-green?style=flat-square)

</div>

---

## Overview

Store Management System is a console-based application developed as part of an Advanced Programming course project.

The system supports multiple user roles, product and customer management, shopping workflows, invoice generation, loyalty programs, credit purchases, and return processing. It was designed with a strong focus on clean architecture, maintainability, and business rule enforcement rather than simply implementing features.

---

## Architecture

The project follows a classic three-layer architecture:

```text
UI Layer
    │
    ▼
Service Layer
    │
    ▼
Model Layer
```

### Design Concepts

- Builder Pattern for product creation
- Polymorphism through customer inheritance
- Dependency Injection between layers
- Immutable transaction records
- Defensive copying for data safety
- Separation of concerns

---

## Features

### Administration

- Product creation, editing, and deletion
- Product search and lookup
- Customer registration and management
- Loyal customer registration
- Customer search by phone number or membership code
- Invoice reporting
- Random test data generation

### Shopping System

- Product browsing
- Smart cart management
- Stock validation
- Flexible payment options
- Checkout processing
- Invoice generation
- Purchase history

### Loyalty Program

- Membership code login
- Credit purchases
- Debt tracking
- Debt repayment
- Financial status overview
- Membership code renewal

### Return Processing

- Invoice validation
- Quantity verification
- Duplicate return protection
- Automatic credit handling

---

## Technical Highlights

### Smart Cart Behavior

Cart items automatically merge based on product identity through custom `equals()` and `hashCode()` implementations. This prevents duplicate entries and simplifies cart management.

### Custom Membership Code Generation

Loyal customer membership codes are generated using a custom algorithm that combines timestamp information with hashing techniques to ensure uniqueness.

### Immutable Invoice Model

Invoices are treated as completed transactions and remain immutable after creation through defensive copies and unmodifiable collections.

### Persistent Storage

Application data is stored using Java serialization and automatically restored when the application starts.

### Input Validation

Validation logic is centralized to ensure consistent handling of user input across the entire application.

---

## Project Structure

```text
com.storeapp
│
├── model
│   ├── Product
│   ├── Customer
│   ├── LoyalCustomer
│   ├── Cart
│   ├── CartItem
│   └── Invoice
│
├── service
│   └── Store
│
├── ui
│   ├── ConsoleUI
│   ├── AdminPanel
│   └── CustomerPanel
│
└── util
```

---

## Getting Started

### Requirements

```text
Java 17+
```

### Compile

```bash
javac -d out src/com/storeapp/**/*.java
```

### Run

```bash
java -cp out com.storeapp.Main
```

### Default Administrator Code

```text
admin123
```

---

## Development Workflow

The project was developed incrementally using feature branches and milestone-based releases.

### Branches

```text
master
├── feature/customer-panel
└── feature/shop-flow
```

### Milestones

| Tag | Description |
|------|-------------|
| model-layer-complete | Domain model implementation |
| service_layer_complete | Business logic and persistence |
| admin-layer-complete | Administrative functionality |
| random-data-generator-complete | Test data generators |
| v1.0-stable | Stable release |

---

## Future Improvements

- Unit testing with JUnit
- Database integration
- Exportable reports
- Logging support
- REST API layer
- Dependency injection framework

---

## License

This project is licensed under the MIT License.

---

## Acknowledgments

Thank you for taking the time to explore this project.

This application represents my effort to apply object-oriented design, software architecture principles, and real-world business logic in a practical Java project.

Feedback, suggestions, and contributions are always welcome.

Your sinerecly ,  
**Mohammadreza**
