# 🍽️ Menu Analyzer (Spring Boot Backend)

[![Java 17](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.11-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-blue)](https://www.postgresql.org/)

Menu Analyzer is a powerful backend service that allows users to upload images of restaurant menus, extract the dish names using Optical Character Recognition (OCR), and enrich each dish with detailed nutritional information, dietary classifications, and recipes via the Spoonacular Food API.

This project demonstrates strong capabilities in **Spring Boot architecture**, **External API Integration**, **Database Management**, and **RESTful service design**.

---

## ✨ Features

- **Document Processing**: Upload images of restaurant menus (`multipart/form-data`) and extract food items automatically using OCR technology.
- **Data Enrichment**: Integrates with the Spoonacular Food Nutrition API to map simple string names to complex nutritional data objects (Calories, Protein, Fat, Carbs, Vitamin profiles).
- **Relational Persistence**: Uses PostgreSQL for robust, persistent storage of Menus and their associated Dishes via Spring Data JPA.
- **Direct Search**: Includes standalone endpoints to directly search the Spoonacular API for ad-hoc nutritional queries without uploading a menu.
- **Architectural Best Practices**: Built using clean code principles, separating concerns across Controllers, Services, Mappers, Repositories, and Entities.

---

## 🏗️ Architecture & Tech Stack

- **Framework:** Spring Boot 3.x, Spring Web, Spring Data JPA
- **Language:** Java 17
- **Database:** PostgreSQL (Production/Dev), H2 (In-Memory for optional testing)
- **3rd Party Integration:** Spoonacular Food API
- **Tooling:** Maven, Lombok

---

## 🚀 Getting Started

### Prerequisites
- JDK 17
- Maven
- PostgreSQL running locally (or adjust `application.properties` to use H2)
- A Spoonacular API Key (Get a free one at [Spoonacular](https://spoonacular.com/food-api/console))

### 1. Configure the Environment
Clone the repository and locate `src/main/resources/application.properties.example`. Rename it or copy its contents to a new file named `application.properties`.

Add your specific configuration:
```properties
# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/menu_analyzer
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.jpa.hibernate.ddl-auto=update

# Spoonacular API Configuration
spoonacular.api.key=YOUR_ACTUAL_SPOONACULAR_KEY
spoonacular.api.baseUrl=https://api.spoonacular.com
```

### 2. Run the Application
You can run the application directly using the Maven Wrapper from your terminal:

```bash
# On Windows
.\mvnw.cmd spring-boot:run

# On Mac/Linux
./mvnw spring-boot:run
```
The server will start on `http://localhost:8080`.

---

## 📖 API Endpoint Reference

### Menu Processing Flow
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/menus/scan` | Uploads a menu image (`file`) and returns parsed dishes. |
| `GET` | `/menus/{menuId}` | Retrieves a previously scanned menu and its dishes. |
| `GET` | `/menus/{menuId}/dishes` | Retrieves all standard dishes parsed for a single menu. |
| `GET` | `/menus/{menuId}/dishes/{dishId}` | Retrieves a single specific dish. |
| `POST`| `/menus/{menuId}/dishes/{dishId}/enrich`| **[External API call]** Forces Spoonacular to pull real nutrition/recipe data for the dish and saves it to the DB. |

### Direct Search Flow
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/foods/search?query={food_name}` | Direct lookup to Spoonacular for comprehensive data about a specific food item. |

---

## 🛠️ Future Improvements / Roadmap
- [ ] Implement Resilience4j Circuit Breakers to handle Spoonacular API downtime gracefully.
- [ ] Add Redis or Spring Cache to prevent redundant calls to the Spoonacular API for commonly searched foods.
- [ ] Implement an asynchronous Message Queue (RabbitMQ/Kafka) for the OCR scanning process to prevent HTTP timeouts.
- [ ] Deploy utilizing Docker and AWS (EC2/RDS).

---
*Created by [Loveesh Singh]*
