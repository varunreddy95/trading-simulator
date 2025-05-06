# 💹 Paper Trading Simulator Backend (Spring Boot)

This is the backend for a **realistic paper trading simulator**, built using **Java 17, Spring Boot 3, PostgreSQL**, and integrates live market data via the **Alpha Vantage API**. Designed to mimic the functionality of a real trading platform, this backend supports **user registration/login, trade execution, portfolio tracking, P&L computation**, chart data, and more.

---

## 📌 Features

✅ **User Authentication & Security**
- Email & password sign-up (with confirm password)
- Login via email or username
- Password encryption using `BCryptPasswordEncoder`

✅ **Trading Engine**
- Simulated BUY and SELL trades with validations
- Auto-updated holdings (positions) per stock
- Position-level **Profit & Loss (P&L)** calculation
- Portfolio summary with account balance + market value

✅ **Stock Data**
- Paginated stock listings
- Search by symbol or name
- Live symbol search via Alpha Vantage (`SYMBOL_SEARCH`)
- Stock data seeded using CSV import

✅ **Analytics**
- Trade insights: grouped by stock symbol
- Chart data: last _n_ days’ closing prices

✅ **Robust Testing**
- Unit tests using JUnit + Mockito
- REST API tests using MockMvc + @WebMvcTest
- 90%+ test coverage across layers

---

## ⚙️ Tech Stack

| Layer          | Technology                    |
|----------------|-------------------------------|
| Backend        | Java 17, Spring Boot 3        |
| Security       | Spring Security + BCrypt      |
| Database       | PostgreSQL                    |
| API Client     | RestTemplate + Alpha Vantage  |
| Testing        | JUnit 5, Mockito, MockMvc     |
| Build Tool     | Maven                         |
| Dev Tools      | Swagger/OpenAPI, Lombok       |

---

## 🔧 Setup Instructions

### 1. Clone the repo

```bash
git clone https://github.com/your-username/trading-simulator.git
cd trading-simulator/apps/app-backend
```

### 2. Configure environment

Create `application.properties` or `application.yml` under `src/main/resources`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trading_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update

# Alpha Vantage config
spring.alphavantage.api-key=YOUR_API_KEY
spring.alphavantage.base-url=www.alphavantage.co
```

### 3. Import stock data (optional for dev)

Place a CSV file with stock data in `src/main/resources/data/stocks.csv`, and enable the loader in your config:

```properties
app.seed-stocks=true
```

Then run the application, and stocks will be seeded at startup.

### 4. Run the backend

```bash
./mvnw spring-boot:run
```

### 5. Swagger API Docs

Visit:  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 📊 API Coverage

| Endpoint | Description |
|----------|-------------|
| `POST /api/auth/register` | Register a new user |
| `POST /api/auth/login` | Login via username/email |
| `GET /api/stocks` | List all stocks with pagination |
| `GET /api/stocks/search?query=...` | Search by symbol or name |
| `GET /api/chart/{symbol}/history?days=...` | Get historical chart data |
| `POST /api/trades` | Place a BUY/SELL trade |
| `GET /api/positions/user/{userId}` | Get holdings for a user |
| `GET /api/positions/user/{userId}/pl` | Holdings + P&L per stock |
| `GET /api/portfolio/user/{userId}` | Net portfolio value + P&L |
| `GET /api/trade-insights/user/{userId}` | Trade insights grouped by symbol |

---

## ✅ Tests

Run tests via:

```bash
./mvnw test
```

Includes:
- Service layer unit tests
- Controller integration tests with MockMvc
- Edge cases, validation errors, auth failures, etc.

---

## 🛠️ In Progress / Planned

| Feature | Status |
|---------|--------|
| Forgot password (email + reset token) | 🟡 Coming up |
| Google Sign-In with username fallback | 🟡 Coming up |
| Frontend integration (Next.js) | 🔜 Final Phase |
| Deployment (Render / Railway / Fly.io) | 🔜 Phase 5 |
| Leaderboard by portfolio returns | ⏳ Stretch goal |

---

## 👤 Author

**Sai Varun Reddy Kamatham**  
[LinkedIn](https://linkedin.com/in/sai-varun-reddy-kamatham/) • [GitHub](https://github.com/varunreddy95)  
📍 Fullstack Java + React Developer | Former Data Scientist | MBA in International Business | Bachelor of Technology in Computer Science

---
