# 🏪 Store Management System

A role-based retail management system built in Java, featuring a three-layer architecture, AES-encrypted persistent storage, a loyalty program with credit/debt tracking, a coupon engine, invoice search & reporting, stack-based UI navigation, and real-world business rule enforcement.

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)
![Version](https://img.shields.io/badge/release-v1.3--stable-blue?style=flat-square)
![Architecture](https://img.shields.io/badge/architecture-3--layer-blue?style=flat-square)
![Commits](https://img.shields.io/badge/commits-181-lightgrey?style=flat-square)
![License](https://img.shields.io/badge/license-MIT-green?style=flat-square)

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
- [Screenshots & Demo](#screenshots--demo)
- [Version History](#version-history)
- [Known Limitations & Future Improvements](#known-limitations--future-improvements)
- [Learning Outcomes](#learning-outcomes)
- [License](#license)

---

## Overview

Store Management System is a console-based application developed as a capstone project for an Advanced Programming course. It simulates the full lifecycle of a small retail operation: product cataloguing, customer registration, shopping cart processing, coupon-based discounting, invoice generation and reporting, a loyalty program with credit and debt tracking, and item returns.

The project is built with a deliberate emphasis on **software engineering principles** — layered architecture, object-oriented design patterns, defensive programming, encrypted persistent state, and iterative refactoring driven by real code review — rather than on simply accumulating a feature list. Development spans **181 commits** across 8 tagged milestones, following the [Conventional Commits](https://www.conventionalcommits.org/) convention (`feat:`, `fix:`, `refactor:`, `docs:`).

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
- Random test data generation for products and customers

### Coupon Management *(new)*

- Full coupon CRUD: create, view, edit, activate/deactivate, delete
- Percentage-based discounts with an expiration date and a maximum usage count
- A coupon is only usable while `active`, not expired, and under its usage limit (`isAvailable()`)
- Usage counter increments automatically only when a coupon is actually applied at checkout

### Shopping System

- Product catalogue with live stock and sellability checks
- Cart management with add, remove, and view operations
- **Smart cart merging**: adding the same product twice increments quantity rather than creating a duplicate entry (via `equals`/`hashCode` on `CartItem`)
- Stock validation at the point of adding to cart
- Optional coupon code entry at checkout, applied against the cart total before payment
- Flexible payment selection: cash or credit (loyal customers only, subject to debt limit)
- Checkout flow producing a formatted invoice with itemized coupon discount
- Purchase history per customer

### Loyalty Program

- Membership code login (separate from phone number login)
- Mandatory membership code login for loyal customers — phone login is blocked for them
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

### Invoice Management & Reporting *(new)*

- Admin-facing invoice search: by ID, by customer name (partial match), by phone number, or by membership code
- Tabular invoice report with running totals, cash/credit split
- **Secure Invoice Token** decoder: decrypts an AES-encrypted token (phone, date, final amount) back into a readable summary — a self-contained demonstration of symmetric encryption independent of the store's persistence layer

### Navigation & UX *(new)*

- Stack-based `Navigation` utility renders a breadcrumb trail (e.g. `📍 Admin Panel > Coupons > Add Coupon`) across every menu, replacing flat, context-less menu prints
- Consistent boxed-menu rendering via `InputValidator.printBox()`

### Infrastructure

- Append-only timestamped file logger (`store.log`) covering all business events and errors
- Centralised input validation with retry loops for all user-facing input
- Centralised constants (`Constants.java`) for magic values
- **AES-encrypted, automatic save-on-every-write persistence**

---

## Architecture

The application follows a **three-layer architecture**:

```
┌──────────────────────────────────────────────────────────────┐
│                          UI Layer                             │
│  ConsoleUI · AdminPanel · CustomerPanel                       │
│  ProductManager · CustomerManager · CouponManager             │
│  InvoiceManager · InputValidator · Navigation (breadcrumbs)   │
└────────────────────────────┬───────────────────────────────────┘
                             │ calls
┌────────────────────────────▼───────────────────────────────────┐
│                       Service Layer                           │
│  Store · Logger · RandomDataGenerator · CryptoService          │
└────────────────────────────┬───────────────────────────────────┘
                             │ operates on
┌────────────────────────────▼───────────────────────────────────┐
│                        Model Layer                            │
│  Product · Customer · LoyalCustomer · Coupon                  │
│  Cart · CartItem · Invoice                                    │
│  CartStatus · PaymentMethod · UnitType (enums)                │
└──────────────────────────────────────────────────────────────┘
```

**Data Flow — Checkout with Coupon:**

```
User input (ConsoleUI)
  → shop() in CustomerPanel
    → Cart.addItem()                          [Model]
    → calculateFinalAmountWithCoupon()         [UI  — optional coupon lookup]
      → Store.findCouponByCode()               [Service]
      → Coupon.applyDiscount()                 [Model]
    → Store.checkoutCart()                     [Service]
      → Cart.checkout()                        [Model — closes cart, creates Invoice]
      → Product.reduceStock()                  [Model]
      → LoyalCustomer.addDebt()                [Model, if CREDIT]
      → Coupon.incrementUsage()                [Model, if a coupon was applied]
      → Logger.log()                           [Service]
    → Store.saveToFile()                       [Service — AES-encrypts and persists state]
  ← Invoice printed to console
```

---

## Class Responsibilities

| Class / File | Layer | Responsibility |
| --- | --- | --- |
| `Main` | Entry point | Bootstraps the application; creates `Scanner` and `ConsoleUI` |
| `ConsoleUI` | UI | Main menu loop; handles login and sign-up; routes to Admin or Customer panel |
| `AdminPanel` | UI | Admin menu; delegates to Product/Coupon/Customer/Invoice managers; shows dashboard |
| `ProductManager` | UI | Full product CRUD: add, edit, delete, search, view details, generate samples |
| `CouponManager` | UI | Coupon CRUD: add, view, edit, activate/deactivate, delete |
| `CustomerManager` | UI | Customer listing, loyal customer registration, detail view, sample generation |
| `CustomerPanel` | UI | Shopping flow, cart management, coupon application, payment, returns, account editing, financial status |
| `InvoiceManager` | UI | Invoice search/reporting (by ID, name, phone, membership code); secure invoice token decoding |
| `InputValidator` | UI/Util | Centralised, retry-looped input reading for all types; boxed-menu rendering; pause |
| `Navigation` | UI/Util | Stack-based breadcrumb tracker (`push`/`pop`/`peek`/`getBreadcrumb`) used by every panel |
| `Store` | Service | Core business logic: product/customer/coupon/invoice CRUD, cart creation, checkout orchestration, return processing, encrypted serialisation I/O, code generation |
| `Logger` | Service | Append-only file logger; timestamped entries; no external dependencies |
| `RandomDataGenerator` | Service | Generates randomised `Product` and `Customer` objects using `Store` APIs |
| `CryptoService` | Service | AES encrypt/decrypt for both strings (secure invoice tokens) and raw bytes (store file persistence) |
| `Product` | Model | Immutable identity (code, name), mutable stock/price/discount; Builder-constructed; sellability and expiry logic |
| `Product.Builder` | Model | Fluent builder for `Product`; separates mandatory from optional fields |
| `Customer` | Model | Base customer: name, phone; cash-only, no returns |
| `LoyalCustomer` | Model | Extends `Customer`; adds membership code, credit, debt, join date; can buy on credit (up to limit) and return items |
| `Coupon` | Model | Percentage discount with expiration date, usage cap, and active/inactive status; computes availability and discounted amounts |
| `Cart` | Model | Session-level shopping basket; enforces `OPEN`/`CLOSED` lifecycle; item merging; total calculation |
| `CartItem` | Model | Product-quantity pair; `equals`/`hashCode` keyed on product code for merge behaviour |
| `Invoice` | Model | Immutable transaction record; defensive copy of cart items; coupon discount line; formatted receipt output |
| `CartStatus` | Model | Enum: `OPEN` / `CLOSED` |
| `PaymentMethod` | Model | Enum: `CASH` / `CREDIT` |
| `UnitType` | Model | Enum: `COUNT` / `WEIGHT` / `VOLUME` |
| `Constants` | Util | All magic values: admin code, file paths, date formatters, crypto keys, sample data arrays |

---

## Design Decisions

### Builder Pattern for Product

`Product` has five mandatory fields and eight optional ones. A constructor with thirteen parameters would be unreadable and error-prone. The `Product.Builder` inner class provides a fluent, self-documenting API and makes optional fields explicit through their absence rather than `null` arguments.

### Polymorphism over Conditionals

`Customer` and `LoyalCustomer` each override `canBuyOnCredit()` and `canReturnItem()`. This allows the service and UI layers to call these methods without `instanceof` checks in the core business logic, reducing coupling.
> **Trade-off:** The UI layer still uses pattern-matching `instanceof` (`c instanceof LoyalCustomer lc`) in several places (dashboard totals, invoice search by membership code). A visitor or strategy pattern would eliminate this, but was deferred in favour of simplicity.

### Immutable Invoice

After checkout, an `Invoice` must never change — it is a record of a completed transaction. `Invoice` uses `final` fields, stores a defensive copy of the cart's item list, and returns an unmodifiable view of that list. The cart itself is also closed (`CartStatus.CLOSED`) immediately upon checkout.

### Unmodifiable Collection Views in Store and Cart

`Store.getProducts()`, `Store.getCustomers()`, `Store.getInvoices()`, `Store.getCoupons()`, and `Cart.getItems()` all return `Collections.unmodifiableList(...)`. External callers can observe state but cannot mutate it, preventing accidental corruption from UI code.

### Custom Membership Code Generation

Rather than using a UUID, codes are generated by combining a name-derived hash, the current timestamp, a cryptographic magic constant (`0x9E3779B9` — Knuth's multiplicative hash constant), and random bits. The result is an 8-character uppercase hex string. A uniqueness loop ensures no collision with existing codes.
> **Trade-off:** This approach is deterministic enough to be reproducible in tests if the random seed is fixed, but not cryptographically secure. For a production system, `SecureRandom` + `UUID` would be preferable.

### Coupon as a Self-Contained Value Object

`Coupon` encapsulates its own availability rules (`isExpired()`, `isUsageLimitReached()`, `isAvailable()`) and its own discount math (`calculateDiscount()`, `applyDiscount()`), so the UI and `Store` never need to duplicate that logic — they simply ask the coupon whether it can be used and let it compute the result.

### AES Encryption via a Dedicated CryptoService

`CryptoService` centralises all symmetric-key operations behind two overloads (`String`↔`String` for the invoice token feature, `byte[]`↔`byte[]` for store-file persistence), so callers never touch `Cipher` directly.
> **Trade-off:** Both overloads currently use `Cipher.getInstance("AES")`, which defaults to **AES/ECB/PKCS5Padding**. ECB mode is not semantically secure (identical plaintext blocks encrypt to identical ciphertext blocks), and both AES keys are hardcoded in `Constants.java`. This is acceptable for a university demonstration of encryption concepts, but would need to move to AES/GCM with an externally managed key for any real deployment. See [Known Limitations](#known-limitations--future-improvements).

### Stack-Based Navigation for Breadcrumbs

`Navigation` wraps a `java.util.Stack<String>` with `push`/`pop`/`peek`/`clear` and a `getBreadcrumb()` join, giving every UI panel a consistent, low-effort way to show the user's current location (`📍 Admin Panel > Coupons > Edit Coupon`) without each panel manually tracking or formatting path state.

### Duplicate Return Protection via a Map

Returns are tracked in `Store.returnedQuantities`, a `Map<String, Double>` keyed by `"invoiceId:productCode"`. This allows multiple partial returns of the same item across separate sessions while preventing the cumulative quantity from exceeding what was originally purchased.

### Centralised InputValidator

All user input passes through `InputValidator`. Retry loops are encapsulated inside the validator, keeping UI panels free of `while(true)` input boilerplate. This also means validation rules (phone format, range checks, unit type parsing) are defined once.

### Append-Only Logger

`Logger.log()` opens `store.log` in append mode on every call. This avoids holding a file handle open (no resource leak on crash) at the cost of a file-open syscall per log entry. Acceptable at this scale; a `BufferedWriter` singleton would be used in production.

---

## Persistence Layer

Application state is persisted using **Java Object Serialisation wrapped in AES encryption**.

- The entire `Store` object graph — products, customers, invoices, coupons, and the returned-quantities map — is serialised in memory via `ObjectOutputStream`, then encrypted with `CryptoService.encrypt(byte[])` before being written to `store.dat`. Nothing touches disk in plaintext.
- On startup, `Store.loadFromFile()` reads the encrypted bytes, decrypts them with `CryptoService.decrypt(byte[])`, and deserialises the result. If the file does not exist (first run), a fresh empty `Store` is returned silently.
- The store is saved after every state-changing operation (add product, checkout, return, pay debt, coupon change, etc.) to minimise data loss.

**Exception handling in persistence:**

```
// FileNotFoundException              → normal first-run case; silent fallback to new Store
// IOException | ClassNotFoundException → data corruption, version mismatch, or bad decryption;
//                                        error printed to stderr, fallback to empty Store
```

> **Limitations:**
> - Java serialisation is brittle across class changes. Adding or renaming a field without updating `serialVersionUID` can cause `InvalidClassException` on load.
> - The AES key used to encrypt `store.dat` is a hardcoded constant in `Constants.java` and the cipher runs in ECB mode — this protects against casual inspection of the file but is not a real confidentiality guarantee. A JSON/SQL-based persistence layer with a properly managed key (or no encryption-as-security-theater at all) would be more appropriate for production use.

---

## Error Handling Strategy

The project uses a layered exception strategy:

| Layer | Strategy |
| --- | --- |
| **Model** | Throws `IllegalArgumentException` for invalid constructor/setter arguments; `IllegalStateException` for invalid state transitions (e.g., adding to a closed cart) |
| **Service** | Catches `IOException` and `ClassNotFoundException` in I/O operations; logs and falls back gracefully; throws domain exceptions upward for the UI to handle |
| **UI** | Catches exceptions from the service layer, prints user-friendly messages, and continues the menu loop — the application never crashes on invalid user input |
| **Logger** | Catches its own `IOException` internally and prints to `stderr` to avoid masking other exceptions |

Input validation errors never result in exceptions — the `InputValidator` loops until valid input is received.

---

## Project Structure

```
StoreProject-university/
├── src/
│   └── com/storeapp/
│       ├── Main.java                    # Application entry point
│       ├── model/
│       │   ├── Product.java             # Product entity with inner Builder
│       │   ├── Customer.java            # Base customer entity
│       │   ├── LoyalCustomer.java       # Extended customer with credit/debt
│       │   ├── Coupon.java              # Discount coupon: expiry, usage cap, status
│       │   ├── Cart.java                # Shopping session (OPEN/CLOSED lifecycle)
│       │   ├── CartItem.java            # Product-quantity pair with merge semantics
│       │   ├── Invoice.java             # Immutable transaction record
│       │   ├── CartStatus.java          # Enum: OPEN | CLOSED
│       │   ├── PaymentMethod.java       # Enum: CASH | CREDIT
│       │   └── UnitType.java            # Enum: COUNT | WEIGHT | VOLUME
│       ├── service/
│       │   ├── Store.java               # Core business logic and persistence
│       │   ├── Logger.java              # Append-only file logger
│       │   ├── RandomDataGenerator.java # Test data generator
│       │   └── CryptoService.java       # AES encryption for tokens and store file
│       ├── ui/
│       │   ├── ConsoleUI.java           # Main menu, login, sign-up
│       │   ├── AdminPanel.java          # Admin dashboard and sub-menu routing
│       │   ├── ProductManager.java      # Product CRUD UI
│       │   ├── CouponManager.java       # Coupon CRUD UI
│       │   ├── CustomerManager.java     # Customer management UI
│       │   ├── CustomerPanel.java       # Shopping, coupons, returns, account management
│       │   ├── InvoiceManager.java      # Invoice search/reporting, secure token decoder
│       │   └── navigation/
│       │       └── Navigation.java      # Stack-based breadcrumb tracker
│       └── util/
│           ├── Constants.java           # Centralised constants and config
│           └── InputValidator.java      # Input reading, validation, UI formatting
├── docs/
│   ├── screenshots/                     # 40 PNG screenshots of the running application
│   ├── uml/
│   │   ├── first-umls/                  # Early per-class .drawio diagrams
│   │   └── final-class-diagram.pdf      # Final consolidated class diagram
│   └── presentation_Project.pdf
├── final-presentation.pptx
├── store.dat                            # AES-encrypted serialised store state (generated at runtime)
├── store.log                            # Append-only application log (generated at runtime)
├── .classpath / .project                # Eclipse project files
├── .gitignore
├── LICENSE
└── README.md
```

25 Java source files, ~4,000 lines of code, no external dependencies.

---

## Technologies Used

| Technology | Version | Purpose |
| --- | --- | --- |
| Java | 17+ | Primary language; uses pattern-matching `instanceof` (`obj instanceof Type t`) |
| `javax.crypto` | Standard library | AES encryption for store persistence and secure invoice tokens (`Cipher`, `SecretKeySpec`) |
| Java Serialisation | Standard library | Object graph persistence (`ObjectInputStream` / `ObjectOutputStream`), wrapped in AES |
| Java Collections | Standard library | `ArrayList`, `HashMap`, `Stack`, `Collections.unmodifiableList` |
| `java.time` | Standard library | `LocalDate`, `LocalDateTime`, `DateTimeFormatter` |
| Eclipse IDE | — | Project created with Eclipse (`.classpath`, `.project` included) |

No external libraries or build tools (Maven/Gradle) are used. The project compiles with the standard JDK toolchain only.

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
git clone https://github.com/Paradoxel/StoreProject-university.git
cd StoreProject-university
```

### Compile

The source now spans a nested `ui/navigation` package, so the simplest reliable approach is to let `find` collect every source file:

```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
```

On Windows (PowerShell):

```powershell
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } > sources.txt
javac -d out "@sources.txt"
```

---

## Running the Application

```bash
java -cp out com.storeapp.Main
```

The application will:

1. Attempt to load and decrypt `store.dat` from the current directory (creates a fresh store if absent)
2. Start appending to `store.log`
3. Display the main menu

### Default Credentials

| Role | Login Field | Value |
| --- | --- | --- |
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
Enter your phone number: 0912XXXXXXX
Enter your name: Jane Doe
✅ Account created successfully! Welcome, Jane Doe!
```

### Checking out with a coupon

```
📍 Customer Panel > Checkout > Apply Coupon
Do you have a coupon? (yes/no): yes
Enter coupon code: WELCOME10
✅ Coupon applied! New amount: 5,400,000 Tomans

Payment method (cash/credit): credit
✅ Purchase completed. Thank you!

══════════════════════════════════════════════════════
               🧾 OFFICIAL INVOICE
══════════════════════════════════════════════════════
 Invoice # : INV-1751200000000
 Item                     Qty    Unit     Price        Subtotal
 ------------------------ ------ -------- ------------ --------------
 Milk                     2      COUNT       1,500,000      3,000,000
 Rice                     1.5    WEIGHT      2,000,000      3,000,000
 ------------------------ ------ -------- ------------ --------------
 Coupon Discount:                                            -600,000
 TOTAL AMOUNT : 5,400,000 Tomans
 PAYMENT      : Credit
══════════════════════════════════════════════════════
```

### Admin: invoice search by membership code

```
📍 Admin Panel > Invoices > Search Customer > Membership Code
Enter Membership Code: A1B2C3D4
┌──────────────────────┬───────────────┬─────────────┬──────────────────────┬────────┬──────────────────┬────────────┐
│ Invoice #             │ Customer      │ Phone       │ Date                 │  Items │  Amount (Tomans) │ Payment    │
├──────────────────────┼───────────────┼─────────────┼──────────────────────┼────────┼──────────────────┼────────────┤
│ INV-1751200000000...  │ Jane Doe      │ 0912XXXXXXX │ 2026-06-29 14:30     │      2 │        5,400,000 │ CREDIT     │
└──────────────────────┴───────────────┴─────────────┴──────────────────────┴────────┴──────────────────┴────────────┘

Showing 1 invoice(s)  |  Total: 5,400,000 Tomans  |  Cash: 0  |  Credit: 1
```

> **Note:** All example values above (names, phone numbers, codes) are illustrative, not real data.

---

## Screenshots & Demo

The repository includes **40 real screenshots** of the running console application — covering the main menu, admin dashboard, product/customer/coupon management, the full shopping and checkout flow, returns, and financial status — under [`docs/screenshots/`](docs/screenshots).

Additional reference material:
- [`docs/uml/final-class-diagram.pdf`](docs/uml/final-class-diagram.pdf) — consolidated final class diagram
- [`docs/uml/first-umls/`](docs/uml/first-umls) — early per-class `.drawio` diagrams from the design phase
- [`docs/presentation_Project.pdf`](docs/presentation_Project.pdf) and [`final-presentation.pptx`](final-presentation.pptx) — course presentation materials

---

## Version History

The current release is **`v1.3-stable`**. Development is tracked through 8 tagged milestones spanning the domain model, service layer, admin layer, random data generation, and three post-1.0 stability releases.

See the [Releases page](https://github.com/Paradoxel/StoreProject-university/releases) and [tag list](https://github.com/Paradoxel/StoreProject-university/tags) for the full history.

---

## Known Limitations & Future Improvements

### Known Limitations

- **Serialisation fragility:** Adding or renaming a field in any model class without updating `serialVersionUID` will cause `InvalidClassException` on load, losing all stored data.
- **No unit tests:** All verification is manual. There are no JUnit tests.
- **AES key management:** Both encryption keys (invoice tokens and store-file persistence) are hardcoded constants in `Constants.java`, and `Cipher.getInstance("AES")` defaults to ECB mode rather than an authenticated mode like GCM. Adequate for demonstrating the concept of encryption; not suitable as-is for protecting real data.
- **Invoice ownership check:** Neither `Invoice`, `Customer`, nor `LoyalCustomer` override `equals()`, so `inv.getCustomer().equals(lc)` in the return flow and invoice search compares by object reference. If a customer object is reloaded from file, this comparison may fail unexpectedly.
- **Logger performance:** `Logger` opens a new `FileWriter` on every call. Under load this would be slow; a `BufferedWriter` singleton or a logging framework would scale better.

### Future Improvements

- Replace Java serialisation with a lightweight database (SQLite via JDBC) or JSON persistence (Gson/Jackson)
- Move from AES/ECB to AES/GCM with a properly managed (non-hardcoded) key, or drop the encryption-as-security-theater framing in favor of a real threat model
- Add JUnit 5 unit tests for all model and service classes
- Override `equals()`/`hashCode()` on `Customer`/`Invoice` based on stable identifiers (phone number / invoice ID) to fix reference-equality bugs
- Extract an `AuthService` to centralise login logic currently spread across `ConsoleUI`
- Add product category classification and filtered browsing
- Introduce a REST API layer (Spring Boot) to decouple the UI from business logic
- Exportable reports (CSV, PDF) for invoices and inventory

---

## Learning Outcomes

This project demonstrates the following software engineering concepts in a practical, working codebase:

| Concept | Where Applied |
| --- | --- |
| **Three-layer architecture** | Strict separation of Model, Service, and UI packages |
| **Builder pattern** | `Product.Builder` — separates mandatory from optional construction parameters |
| **Inheritance and polymorphism** | `Customer` → `LoyalCustomer`; `canBuyOnCredit()` / `canReturnItem()` overrides |
| **Encapsulation** | Private fields, unmodifiable collection views, defensive copies in `Invoice` |
| **Immutability** | `Invoice` uses `final` fields; cart items list is defensively copied |
| **Custom `equals` / `hashCode`** | `CartItem` keyed on product code for automatic cart merging |
| **Enum design** | `CartStatus`, `PaymentMethod`, `UnitType` — type-safe state representation |
| **Symmetric-key cryptography** | `CryptoService` — AES encryption for persisted state and a standalone secure-token feature |
| **Java serialisation** | Full object graph persistence with graceful load fallback, wrapped in encryption |
| **Exception handling strategy** | Layered model → service → UI exception propagation with user-friendly recovery |
| **Centralised validation** | `InputValidator` provides a single, reusable, retry-looped input API |
| **Stack data structure applied to UI** | `Navigation` uses `java.util.Stack` to drive breadcrumb navigation |
| **Append-only logging** | Timestamped audit trail for all business events and errors |
| **Defensive programming** | Null checks, range validation, stock checks before every state change |
| **Separation of concerns** | Logger, Constants, RandomDataGenerator, CryptoService each have one clear responsibility |
| **Iterative refactoring** | 181 commits following Conventional Commits, including dedicated `refactor:` passes on UI structure, coupon lookup, and navigation |

---

## License

This project is licensed under the [MIT License](LICENSE).

---

Developed by **Mohammadreza** ([@Paradoxel](https://github.com/Paradoxel)) as part of an Advanced Programming course project.
Feedback, issues, and pull requests are welcome.
