● Internet Banking Microservices — Project Analysis

  Architecture Overview

  A Java 21 / Spring Boot 3.2.4 microservices project simulating an internet banking system, built as a tutorial series from javatodev.com. Uses Spring Cloud 2023.0.0 with Gradle.

  ---
  Services (7 total)

  ┌─────────────────────────┬──────┬──────────────────────────────────────────────────────┐
  │         Service         │ Port │                       Purpose                        │
  ├─────────────────────────┼──────┼──────────────────────────────────────────────────────┤
  │ Service Registry        │ 8081 │ Netflix Eureka — service discovery                   │
  ├─────────────────────────┼──────┼──────────────────────────────────────────────────────┤
  │ Config Server           │ 8090 │ Spring Cloud Config — centralized config from Git    │
  ├─────────────────────────┼──────┼──────────────────────────────────────────────────────┤
  │ API Gateway             │ 8082 │ Spring Cloud Gateway — routing + OAuth2/JWT security │
  ├─────────────────────────┼──────┼──────────────────────────────────────────────────────┤
  │ Core Banking Service    │ 8092 │ Accounts, users, transactions (core engine)          │
  ├─────────────────────────┼──────┼──────────────────────────────────────────────────────┤
  │ User Service            │ 8083 │ User management + Keycloak integration               │
  ├─────────────────────────┼──────┼──────────────────────────────────────────────────────┤
  │ Fund Transfer Service   │ 8084 │ Inter-account transfers                              │
  ├─────────────────────────┼──────┼──────────────────────────────────────────────────────┤
  │ Utility Payment Service │ 8085 │ Bill payments (electricity, water, etc.)             │
  └─────────────────────────┴──────┴──────────────────────────────────────────────────────┘

  ---
  Key Technical Decisions

  Security: OAuth2 + JWT via Keycloak 23.0.7. Only user registration and actuator endpoints are public; everything else requires a valid JWT. The API Gateway handles validation via JWK Set URI.

  Inter-service Communication: Spring Cloud OpenFeign — Fund Transfer and Utility Payment services call Core Banking Service for account lookups and transaction processing.

  Databases:
  - MySQL 8.4 — Core banking data (users, accounts, transactions, utility accounts)
  - PostgreSQL 15 — Keycloak auth data
  - Flyway for migrations

  Observability: Zipkin for distributed tracing + Micrometer + Actuator endpoints.

  ---
  Data Model (Core Banking)

  banking_core_user → banking_core_account (1:N)
  banking_core_account → banking_core_transaction (1:N)
  banking_core_utility_account (standalone, for utility providers)

  ---
  API Endpoints (via Gateway at :8082)

  ┌───────────┬─────────────────────────────────────────┬─────────────────┐
  │  Method   │                  Path                   │     Service     │
  ├───────────┼─────────────────────────────────────────┼─────────────────┤
  │ POST      │ /user/api/v1/bank-users/register        │ User (public)   │
  ├───────────┼─────────────────────────────────────────┼─────────────────┤
  │ GET/PATCH │ /user/api/v1/bank-users/**              │ User            │
  ├───────────┼─────────────────────────────────────────┼─────────────────┤
  │ POST/GET  │ /fund-transfer/api/v1/transfer          │ Fund Transfer   │
  ├───────────┼─────────────────────────────────────────┼─────────────────┤
  │ POST/GET  │ /utility-payment/api/v1/utility-payment │ Utility Payment │
  └───────────┴─────────────────────────────────────────┴─────────────────┘

  Auth token: POST http://localhost:8080/realms/javatodev-internet-banking/protocol/openid-connect/token

  ---
  Startup Order (Docker Compose)

  Zipkin + DBs → Keycloak → Config Server + Registry → All App Services

  A Postman collection is included at /postman_collection/ for testing all APIs.

  ---
  Summary: A clean, well-structured educational project demonstrating enterprise microservices patterns — service discovery, centralized config, API gateway, OAuth2 security, Feign-based service communication, and distributed
  tracing. Good reference for learning Spring Cloud + Keycloak integration.

✻ Sautéed for 2m 19s

❯ more detail explantion of the business logic.                                                                                                                                                                                     

● Explore(Deep dive into business logic)
  ⎿  Done (71 tool uses · 61.3k tokens · 1m 43s)
  (ctrl+o to expand)

● Business Logic — Detailed Explanation

  ---
  1. User Registration & Onboarding

  This is a two-system registration flow involving both Keycloak (auth) and Core Banking (account data).

  Step-by-step (UserService.createUser):
  1. Check Keycloak — if the email already exists → throw UserAlreadyRegisteredException (USER-SERVICE-1001)
  2. Call Core Banking Service via Feign using the user's identificationNumber (national ID / passport)
  3. If no user found in core banking → throw InvalidBankingUserException (USER-SERVICE-1003)
  4. Cross-validate: email in request must match email in core banking → throw InvalidEmailException (USER-SERVICE-1002)
  5. Create the user in Keycloak with credentials
  6. Save the user locally with status PENDING

  Approval flow (UserService.updateUser):
  - When admin sets status to APPROVED → Keycloak user is enabled and email is verified
  - Other statuses (DISABLED, BLACKLIST) just update the local record

  User statuses: PENDING → APPROVED → DISABLED / BLACKLIST

  ▎ The core banking user record (name, email, accounts) must be pre-seeded in the DB (via Flyway migrations or admin tools). User Service doesn't create core banking records — it validates against them.

  ---
  2. Fund Transfer Flow

  Two services cooperate: Fund Transfer Service (orchestrator) and Core Banking Service (executor).

  Fund Transfer Service (FundTransferService.fundTransfer):
  1. Save a FundTransferEntity with status PENDING to its own DB
  2. Call Core Banking Service: POST /api/v1/transaction/fund-transfer
  3. On success: update entity with transactionReference and set status → SUCCESS

  Core Banking Service (TransactionService.fundTransfer):
  1. Look up source account (fromAccount) — throw EntityNotFoundException if missing
  2. Look up destination account (toAccount) — throw EntityNotFoundException if missing
  3. Check availableBalance >= amount — throw InsufficientFundsException if not
  4. Call internalFundTransfer(source, destination, amount):
    - Deduct amount from source availableBalance and actualBalance
    - Add amount to destination availableBalance and actualBalance
    - Create debit TransactionEntity on source account (negative amount)
    - Create credit TransactionEntity on destination account (positive amount)
  5. Return transactionId (UUID)

  ▎ The entire core banking operation is @Transactional — debit and credit are atomic. If either fails, both roll back.

  Two balances are maintained:
  - actualBalance   → real settled balance
  - availableBalance → balance minus reserved/pending amounts
  Both are updated simultaneously in this project.

  ---
  3. Utility Payment Flow

  Same orchestrator pattern as fund transfer.

  Utility Payment Service (UtilityPaymentService.utilPayment):
  1. Save a UtilityPaymentEntity with status PROCESSING to its own DB
  2. Call Core Banking Service: POST /api/v1/transaction/util-payment
  3. On success: update entity with transactionId and set status → SUCCESS

  Core Banking Service (TransactionService.utilPayment):
  1. Look up the payer's bank account
  2. Look up the utility provider account (by providerId)
  3. Validate availableBalance >= amount
  4. Deduct amount from payer's both balances
  5. Create a UTILITY_PAYMENT TransactionEntity on payer's account
  6. (Placeholder) call third-party utility provider API — not yet implemented
  7. Return transactionId

  ---
  4. Account & Transaction Data Model

  UserEntity (1) ──────────── (N) BankAccountEntity
                                        │
                                        │ (1:N)
                                        ▼
                                 TransactionEntity
                           (type: FUND_TRANSFER | UTILITY_PAYMENT)

  UtilityAccountEntity  ──  standalone, holds provider info

  Account Types: SAVINGS_ACCOUNT, FIXED_DEPOSIT, LOAN_ACCOUNT
  Account Statuses: PENDING, ACTIVE, DORMANT, BLOCKED
  Transaction Types: FUND_TRANSFER, UTILITY_PAYMENT

  ---
  5. Error Handling Strategy

  Each service defines its own error codes and a GlobalExceptionHandler:

  ┌───────────────────────────┬──────────────────────────┬──────────────┐
  │           Code            │         Meaning          │   Service    │
  ├───────────────────────────┼──────────────────────────┼──────────────┤
  │ BANKING-CORE-SERVICE-1000 │ Entity not found         │ Core Banking │
  ├───────────────────────────┼──────────────────────────┼──────────────┤
  │ BANKING-CORE-SERVICE-1001 │ Insufficient funds       │ Core Banking │
  ├───────────────────────────┼──────────────────────────┼──────────────┤
  │ USER-SERVICE-1000         │ Entity not found         │ User Service │
  ├───────────────────────────┼──────────────────────────┼──────────────┤
  │ USER-SERVICE-1001         │ Email already registered │ User Service │
  ├───────────────────────────┼──────────────────────────┼──────────────┤
  │ USER-SERVICE-1002         │ Email mismatch           │ User Service │
  ├───────────────────────────┼──────────────────────────┼──────────────┤
  │ USER-SERVICE-1003         │ No user found for NIC    │ User Service │
  └───────────────────────────┴──────────────────────────┴──────────────┘

  All exceptions extend SimpleBankingGlobalException and are caught by GlobalExceptionHandler, returning a standardized { code, message } response.

  ---
  6. Audit Trail

  Fund Transfer, Utility Payment, and User Service entities all extend AuditAware, which automatically tracks:
  - createdDate, createdBy
  - modifiedDate, modifiedBy
  - version (optimistic locking)

  ---
  Key Design Observations

  - No saga/compensation logic — if Core Banking succeeds but the orchestrator crashes before saving SUCCESS, the local record stays PENDING forever. This is a known simplification for a tutorial project.
  - No event-driven messaging (no Kafka/RabbitMQ) — all communication is synchronous via Feign.
  - Core banking is the single source of truth for balances and transactions; the other services are thin orchestrators that keep their own audit records.
  - User pre-seeding required — core banking user + account data must exist before anyone can register through the User Service.