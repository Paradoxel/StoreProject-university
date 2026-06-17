<div align="center">

# 🏪 Store Management System

A role-based retail management system built in Java, featuring a clean three-layer architecture, persistent object storage, a loyalty program, return processing, and real-world business rule enforcement.

[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Version](https://img.shields.io/badge/release-v1.0--stable-success?style=flat-square)](https://github.com/)
[![Architecture](https://img.shields.io/badge/architecture-3--layer-blue?style=flat-square)]()
[![Pattern](https://img.shields.io/badge/pattern-Builder%20%7C%20OOP-purple?style=flat-square)]()
[![License](https://img.shields.io/badge/license-MIT-green?style=flat-square)](LICENSE)

</div>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Class Responsibilities](#class-responsibilities)
- [Design Decisions](#design-decisions)
- [Persistence Layer](#persistence-layer)
- [Error Handling Strategy](#error-handling-strategy)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Usage Examples](#usage-examples)
- [Version History](#version-history)
- [Known Limitations & Future Improvements](#known-limitations--future-improvements)
- [Learning Outcomes](#learning-outcomes)
- [License](#license)

---

## Overview

Store Management System is a console-based application developed as a capstone project for an Advanced Programming course. It simulates the full lifecycle of a small retail operation: product cataloguing, customer registration, shopping cart processing, invoice generation, a loyalty program with credit and debt tracking, and item returns.

The project was designed with a deliberate emphasis on **software engineering principles** — clean layered architecture, object-oriented design patterns, defensive programming, and persistent state — rather than on simply implementing a feature list.

---

## Features

### Administration
- Secure admin login via a fixed access code
- Store dashboard showing total products, customers, invoices, outstanding debt, and total sales
- Product creation using the **Builder pattern** with mandatory and optional fields
- Product editing (price, stock, discount) and deletion with confirmation
- Product search by name or code
- Customer registration (regular and loyal)
- Customer lookup by phone number or membership code
- Invoice history report with tabular formatting
- Random test data generation for products and customers

### Shopping System
- Product catalogue with live stock and sellability checks
- Cart management with add, remove, and view operations
- **Smart cart merging**: adding the same product twice increments quantity rather than creating a duplicate entry (via `equals`/`hashCode` on `CartItem`)
- Stock validation at the point of adding to cart
- Flexible payment selection: cash or credit (loyal customers only, subject to debt limit)
- Checkout flow producing a formatted invoice
- Purchase history per customer

### Loyalty Program
- Membership code login (separate from phone number login)
- Mandatory membership code login for loyal customers — phone login is explicitly blocked for them
- Credit purchases with automatic debt tracking
- Configurable debt ceiling (`1,000,000 Tomans`) enforcing a hard credit limit
- Debt repayment with validation
- Financial status overview (debt, credit, credit eligibility)
- Membership code renewal using a custom hash-based generation algorithm

### Return Processing
- Invoice lookup and ownership validation
- Per-item return quantity validation against original purchase
- **Duplicate return protection**: cumulative returned quantities are tracked per invoice-item pair, preventing over-return across multiple return sessions
- Automatic stock restoration
- Refund credited directly to the customer's loyalty credit balance

### Infrastructure
- Append-only timestamped file logger (`store.log`) covering all business events and errors
- Centralised input validation with retry loops for all user-facing input
- Centralised constants (`Constants.java`) for magic values
- Automatic save-on-every-write persistence

---

## Architecture

The application follows a strict **three-layer architecture**:

```
┌─────────────────────────────────────────────────────┐
│                      UI Layer                        │
│  ConsoleUI · AdminPanel · CustomerPanel              │
│  ProductManager · CustomerManager                    │
│  InputValidator                                      │
└──────────────────────┬──────────────────────────────┘
                       │ calls
┌──────────────────────▼──────────────────────────────┐
│                   Service Layer                      │
│  Store · Logger · RandomDataGenerator               │
└──────────────────────┬──────────────────────────────┘
                       │ operates on
┌──────────────────────▼──────────────────────────────┐
│                    Model Layer                       │
│  Product · Customer · LoyalCustomer                 │
│  Cart · CartItem · Invoice                          │
│  CartStatus · PaymentMethod · UnitType (enums)      │
└─────────────────────────────────────────────────────┘
```

**Data Flow — Checkout:**

```
User input (ConsoleUI)
  → shop() in CustomerPanel
    → Cart.addItem()          [Model]
    → Store.checkoutCart()    [Service]
      → Cart.checkout()       [Model — closes cart, creates Invoice]
      → Product.reduceStock() [Model]
      → LoyalCustomer.addDebt() [Model, if CREDIT]
      → Logger.log()          [Service]
    → Store.saveToFile()      [Service — persists state]
  ← Invoice printed to console
```

---

## Class Responsibilities

| Class / File | Layer | Responsibility |
|---|---|---|
| `Main` | Entry point | Bootstraps the application; creates `Scanner` and `ConsoleUI` |
| `ConsoleUI` | UI | Main menu loop; handles login and sign-up; routes to Admin or Customer panel |
| `AdminPanel` | UI | Admin menu; delegates to `ProductManager` and `CustomerManager`; shows dashboard and reports |
| `ProductManager` | UI | Full product CRUD: add, edit, delete, search, view details, generate samples |
| `CustomerManager` | UI | Customer listing, loyal customer registration, detail view, sample generation |
| `CustomerPanel` | UI | Shopping flow, cart management, payment selection, returns, account editing, financial status |
| `InputValidator` | UI/Util | Centralised, retry-looped input reading for all types; UI box/title printing; pause |
| `Store` | Service | Core business logic: product/customer/invoice CRUD, cart creation, checkout orchestration, return processing, serialisation I/O, code generation |
| `Logger` | Service | Append-only file logger; timestamped entries; no external dependencies |
| `RandomDataGenerator` | Service | Generates randomised `Product` and `Customer` objects using `Store` APIs |
| `Product` | Model | Immutable identity (code, name), mutable stock/price/discount; Builder-constructed; sellability and expiry logic |
| `Product.Builder` | Model | Fluent builder for `Product`; separates mandatory from optional fields |
| `Customer` | Model | Base customer: name, phone; cash-only, no returns |
| `LoyalCustomer` | Model | Extends `Customer`; adds membership code, credit, debt, join date; can buy on credit (up to limit) and return items |
| `Cart` | Model | Session-level shopping basket; enforces `OPEN`/`CLOSED` lifecycle; item merging; total calculation |
| `CartItem` | Model | Product-quantity pair; `equals`/`hashCode` keyed on product code for merge behaviour |
| `Invoice` | Model | Immutable transaction record; defensive copy of cart items; formatted receipt output |
| `CartStatus` | Model | Enum: `OPEN` / `CLOSED` |
| `PaymentMethod` | Model | Enum: `CASH` / `CREDIT` |
| `UnitType` | Model | Enum: `COUNT` / `WEIGHT` / `VOLUME` |
| `Constants` | Util | All magic values: admin code, file paths, date formatters, sample data arrays |

---

## Design Decisions

### Builder Pattern for Product
`Product` has five mandatory fields and eight optional ones. A constructor with thirteen parameters would be unreadable and error-prone. The `Product.Builder` inner class provides a fluent, self-documenting API and makes optional fields explicit through their absence rather than `null` arguments.

### Polymorphism over Conditionals
`Customer` and `LoyalCustomer` each override `canBuyOnCredit()` and `canReturnItem()`. This allows the service and UI layers to call these methods without `instanceof` checks in the business logic, reducing coupling.

> **Trade-off:** The UI layer still uses `instanceof` in several places (e.g., `startPurchase()`, dashboard totals). Introducing a visitor or strategy pattern would eliminate this, but was deferred in favour of simplicity.

### Immutable Invoice
After checkout, an `Invoice` must never change — it is a legal record of a completed transaction. `Invoice` uses `final` fields, stores a defensive copy of the cart's item list, and returns an unmodifiable view of that list. The cart itself is also closed (`CartStatus.CLOSED`) immediately upon checkout.

### Unmodifiable Collection Views in Store and Cart
`Store.getProducts()`, `Store.getCustomers()`, `Store.getInvoices()`, and `Cart.getItems()` all return `Collections.unmodifiableList(...)`. External callers can observe state but cannot mutate it, preventing accidental corruption from UI code.

### Custom Membership Code Generation
Rather than using a UUID, codes are generated by combining a name-derived hash, the current timestamp, a cryptographic magic constant (`0x9E3779B9` — Knuth's multiplicative hash constant), and random bits. The result is an 8-character uppercase hex string. A uniqueness loop ensures no collision with existing codes.

> **Trade-off:** This approach is deterministic enough to be reproducible in tests if the random seed is fixed, but not cryptographically secure. For a production system, `SecureRandom` + `UUID` would be preferable.

### Duplicate Return Protection via a Map
Returns are tracked in `Store.returnedQuantities`, a `Map<String, Double>` keyed by `"invoiceId:productCode"`. This allows multiple partial returns of the same item across separate sessions while preventing the cumulative quantity from exceeding what was originally purchased.

### Centralised InputValidator
All user input passes through `InputValidator`. Retry loops are encapsulated inside the validator, keeping UI panels free of `while(true)` input boilerplate. This also means validation rules (phone format, range checks, unit type parsing) are defined once.

### Append-Only Logger
`Logger.log()` opens `store.log` in append mode on every call. This avoids holding a file handle open (no resource leak on crash) at the cost of a file-open syscall per log entry. Acceptable at this scale; a `BufferedWriter` singleton would be used in production.

---

## Persistence Layer

Application state is persisted using **Java Object Serialisation** (`ObjectOutputStream` / `ObjectInputStream`).

- The entire `Store` object graph — products, customers, invoices, and the returned-quantities map — is written atomically to `store.dat`.
- On startup, `Store.loadFromFile()` reads `store.dat`. If the file does not exist (first run), a fresh empty `Store` is returned silently.
- The store is saved after every state-changing operation (add product, checkout, return, pay debt, etc.) to minimise data loss.

**Exception handling in persistence:**

```java
// FileNotFoundException → normal first-run case; silent fallback to new Store
// IOException | ClassNotFoundException → data corruption or version mismatch;
//   error printed to stderr, fallback to empty Store
```

> **Limitation:** Java serialisation is brittle across class changes. Adding or renaming a field without updating `serialVersionUID` can cause `InvalidClassException` on load. A JSON or SQL-based persistence layer would be more robust.

---

## Error Handling Strategy

The project uses a layered exception strategy:

| Layer | Strategy |
|---|---|
| **Model** | Throws `IllegalArgumentException` for invalid constructor/setter arguments; `IllegalStateException` for invalid state transitions (e.g., adding to a closed cart) |
| **Service** | Catches `IOException` and `ClassNotFoundException` in I/O operations; logs and falls back gracefully; throws domain exceptions upward for the UI to handle |
| **UI** | Catches exceptions from the service layer, prints user-friendly messages, and continues the menu loop — the application never crashes on invalid user input |
| **Logger** | Catches its own `IOException` internally and prints to `stderr` to avoid masking other exceptions |

Input validation errors never result in exceptions — the `InputValidator` loops until valid input is received.

---

## Project Structure

```
StoreProject/
├── src/
│   └── com/storeapp/
│       ├── Main.java                   # Application entry point
│       ├── model/
│       │   ├── Product.java            # Product entity with inner Builder
│       │   ├── Customer.java           # Base customer entity
│       │   ├── LoyalCustomer.java      # Extended customer with credit/debt
│       │   ├── Cart.java               # Shopping session (OPEN/CLOSED lifecycle)
│       │   ├── CartItem.java           # Product-quantity pair with merge semantics
│       │   ├── Invoice.java            # Immutable transaction record
│       │   ├── CartStatus.java         # Enum: OPEN | CLOSED
│       │   ├── PaymentMethod.java      # Enum: CASH | CREDIT
│       │   └── UnitType.java           # Enum: COUNT | WEIGHT | VOLUME
│       ├── service/
│       │   ├── Store.java              # Core business logic and persistence
│       │   ├── Logger.java             # Append-only file logger
│       │   └── RandomDataGenerator.java # Test data generator
│       ├── ui/
│       │   ├── ConsoleUI.java          # Main menu, login, sign-up
│       │   ├── AdminPanel.java         # Admin dashboard and sub-menu routing
│       │   ├── ProductManager.java     # Product CRUD UI
│       │   ├── CustomerManager.java    # Customer management UI
│       │   └── CustomerPanel.java      # Shopping, returns, account management
│       └── util/
│           ├── Constants.java          # Centralised constants and config
│           └── InputValidator.java     # Input reading, validation, UI formatting
├── store.dat                           # Serialised store state (generated at runtime)
├── store.log                           # Append-only application log (generated at runtime)
├── .gitignore
└── README.md
```

---

## Technologies Used

| Technology | Version | Purpose |
|---|---|---|
| Java | 17+ | Primary language; uses records, sealed types available but not used |
| Java Serialisation | Standard library | Object persistence (`ObjectInputStream` / `ObjectOutputStream`) |
| Java Collections | Standard library | `ArrayList`, `HashMap`, `Collections.unmodifiableList` |
| `java.time` | Standard library | `LocalDate`, `LocalDateTime`, `DateTimeFormatter` |
| Eclipse IDE | — | Project created with Eclipse (`.classpath`, `.project` included) |

No external libraries or build tools are required. The project compiles with the standard JDK toolchain.

---

## Installation

### Prerequisites

- Java Development Kit (JDK) 17 or later
- Any terminal / command prompt

Verify your Java version:

```bash
java -version
# Expected: openjdk version "17.x.x" or later
```

### Clone the Repository

```bash
git clone https://github.com/your-username/StoreProject.git
cd StoreProject
```

### Compile

```bash
javac -d out src/com/storeapp/*.java \
             src/com/storeapp/model/*.java \
             src/com/storeapp/service/*.java \
             src/com/storeapp/ui/*.java \
             src/com/storeapp/util/*.java
```

> On Windows, replace `\` with `^` for line continuation, or run as a single command.

---

## Running the Application

```bash
java -cp out com.storeapp.Main
```

The application will:
1. Attempt to load `store.dat` from the current directory (creates a fresh store if absent)
2. Start appending to `store.log`
3. Display the main menu

### Default Credentials

| Role | Login Field | Value |
|---|---|---|
| Administrator | Code | `admin123` |
| Regular Customer | Phone number | (registered during sign-up) |
| Loyal Customer | Membership code | (generated by admin and displayed at registration) |

---

## Usage Examples

### Registering a new customer (Sign Up)

```
┌──────────────────────────────────────────────────┐
│                   MAIN MENU                       │
├──────────────────────────────────────────────────┤
│ 1. Login                                          │
│ 2. Sign Up                                        │
│ 3. Exit                                           │
└──────────────────────────────────────────────────┘
Please choose an option between 1 and 3: 2

┌──────────────────────────────────────────────────┐
│                    SIGN UP                        │
└──────────────────────────────────────────────────┘
Enter your phone number: 09339011332
Enter your name: Mohammadreza
✅ Account created successfully! Welcome, Mohammadreza!
```

### Checking out as a loyal customer

```
Payment method (cash/credit): credit
✅ Purchase completed. Thank you!

══════════════════════════════════════════════════════
               🧾 OFFICIAL INVOICE
══════════════════════════════════════════════════════
 Invoice # : INV-1718600000000
 Date      : 2025-06-17  14:30
 Customer  : Mohammadreza
 Phone     : 09339011332
──────────────────────────────────────────────────────
 Item                     Qty    Unit     Price        Subtotal
 ------------------------ ------ -------- ------------ --------------
 Milk                     2      COUNT       1,500,000      3,000,000
 Rice                     1.5    WEIGHT      2,000,000      3,000,000
 ------------------------ ------ -------- ------------ --------------
 TOTAL AMOUNT : 6,000,000 Tomans
 PAYMENT      : Credit
══════════════════════════════════════════════════════
```

### Returning an item

```
--- Return an Item ---
Enter invoice ID: INV-1718600000000
Items in this invoice:
 - PRD-1 (Milk) x2.0
 - PRD-2 (Rice) x1.5
Product code to return: PRD-1
Quantity to return: 1
✅ Returned 1.0 of PRD-1. Credit: 1,500,000 Tomans.
```

### Admin dashboard

```
┌──────────────────────────────────────────────────┐
│                 STORE DASHBOARD                   │
├──────────────────────────────────────────────────┤
│ Total Products  : 12                              │
│ Total Customers : 8                               │
│ Total Invoices  : 5                               │
│ Total Sales     : 24,500,000 Tomans               │
│ Outstanding Debt: 6,000,000 Tomans                │
└──────────────────────────────────────────────────┘
```

> **Note:** Screenshots of the actual running application can be added to a `docs/screenshots/` directory and embedded here.

---

## Version History

| Tag | Description |
|---|---|
| `model-layer-complete` | Domain model fully implemented: `Product`, `Customer`, `LoyalCustomer`, `Cart`, `CartItem`, `Invoice`, enums |
| `service_layer_complete` | Business logic and persistence complete: `Store`, `Logger`; checkout, return, and code-generation logic |
| `admin-layer-complete` | Admin panel, product CRUD, customer management, dashboard, and invoice reports |
| `random-data-generator-complete` | `RandomDataGenerator` added for testing |
| `v1.0-stable` | Stable release: all features integrated and verified |

---

## Known Limitations & Future Improvements

### Known Limitations

- **Serialisation fragility:** Adding or renaming a field in any model class without updating `serialVersionUID` will cause `InvalidClassException` on load, losing all stored data.
- **No unit tests:** All verification is manual. There are no JUnit tests.
- **Silent name-update bug:** In `CustomerPanel.editCustomerInfo()`, the new name is read but `customer.setName()` is never called — names cannot actually be updated.
- **Invoice ownership check:** `Invoice` does not override `equals()`, so `inv.getCustomer().equals(lc)` in `returnItem()` compares by reference. If a customer object is reloaded from file, this comparison may fail unexpectedly.
- **Mixed-language error messages:** Some `IllegalArgumentException` and `IllegalStateException` messages are in Persian while the UI is in English. A consistent approach (externalised strings or full English) would be more maintainable.
- **Logger performance:** `Logger` opens a new `FileWriter` on every call. Under load this would be slow; a `BufferedWriter` singleton or a logging framework would scale better.

### Future Improvements

- Replace Java serialisation with a lightweight database (SQLite via JDBC) or JSON persistence (Gson/Jackson)
- Add JUnit 5 unit tests for all model and service classes
- Implement a `Discount` strategy object to allow more flexible promotions
- Extract an `AuthService` to centralise login logic currently spread across `ConsoleUI`
- Add product category classification and filtered browsing
- Introduce a REST API layer (Spring Boot) to decouple the UI from business logic
- Exportable reports (CSV, PDF) for invoices and inventory

---

## Learning Outcomes

This project demonstrates the following software engineering concepts in a practical, working codebase:

| Concept | Where Applied |
|---|---|
| **Three-layer architecture** | Strict separation of Model, Service, and UI packages |
| **Builder pattern** | `Product.Builder` — separates mandatory from optional construction parameters |
| **Inheritance and polymorphism** | `Customer` → `LoyalCustomer`; `canBuyOnCredit()` / `canReturnItem()` overrides |
| **Encapsulation** | Private fields, unmodifiable collection views, defensive copies in `Invoice` |
| **Immutability** | `Invoice` uses `final` fields; cart items list is defensively copied |
| **Custom `equals` / `hashCode`** | `CartItem` keyed on product code for automatic cart merging |
| **Enum design** | `CartStatus`, `PaymentMethod`, `UnitType` — type-safe state representation |
| **Java serialisation** | Full object graph persistence with graceful load fallback |
| **Exception handling strategy** | Layered model → service → UI exception propagation with user-friendly recovery |
| **Centralised validation** | `InputValidator` provides a single, reusable, retry-looped input API |
| **Append-only logging** | Timestamped audit trail for all business events and errors |
| **Defensive programming** | Null checks, range validation, stock checks before every state change |
| **Separation of concerns** | Logger, Constants, RandomDataGenerator each have one clear responsibility |

---

## License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">

Developed by **Mohammadreza** as part of an Advanced Programming course project.  
Feedback, issues, and pull requests are welcome.

</div>
