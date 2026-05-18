# MyMessenger-Server 🚀

The robust backend infrastructure for a client-server messenger ecosystem, built on top of Java, Spring Boot, and PostgreSQL. The platform delivers secure, low-latency, real-time message brokering via WebSockets (STOMP protocol) alongside high-performance stateless HTTP authentication utilizing decoupled JWT layers (Access & Refresh tokens).

## 🛠 Tech Stack Overview
* **Java 21**
* **Spring Boot 3.x**
    * Spring Security (Custom JWT Middleware)
    * Spring Data JPA
    * Spring WebSocket (STOMP & SockJS Message Brokers)
    * Spring Web
* **PostgreSQL**
* **Lombok**
* **Maven**
* **Docker & Docker Compose** (Containerization & Local Orchestration)

---

## 🔒 Security Architecture & Dual-Token Scheme
To eliminate stateful server overhead and mitigate risks, the system implements a highly secure decoupled token lifecycle:
1. **Access Token (JWS):** A short-lived token utilized to authorize incoming HTTP REST endpoints and validate runtime WebSocket connection upgrades. Digitally signed via a dedicated cryptographic secret.
2. **Refresh Token (JWE):** A long-lived, fully encrypted token designed strictly for requesting new Access Tokens upon expiration. Leverages advanced payload encryption wrappers for maximum security.

---

## 📦 Containerization & Deployment via Docker

The repository features automated environment provisioning configurations. To initialize the database cluster and target server instance in isolated network environments, execute the steps below:

### 1. Provision the Environment File (`.env`)
Create a hidden file named exactly `.env` within the root project directory (directly adjacent to `docker-compose.yml`) to store infrastructure credentials securely:

```env
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password
ACCESS_JWT_TOKEN=your_very_long_random_access_secret_key_minimum_32_characters
REFRESH_JWT_TOKEN=your_very_long_random_refresh_secret_key_minimum_32_characters
```
### 2. Orchestrate Container Lifecycles
Execute the following commands in your host terminal to flush stale persistent storage nodes, compile binaries, and bring up the container architecture:

```
docker compose down -v
docker compose up --build
```