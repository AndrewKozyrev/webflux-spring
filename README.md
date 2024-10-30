# **Book Management API with Spring WebFlux**

This project is a reactive REST API for managing books, built with Spring WebFlux. The API supports CRUD operations and
demonstrates essential WebFlux features like reactive programming, custom error handling, and pagination with
validation.

## Table of Contents

* [Features](#features)
* [Tech Stack](#tech-stack)
* [Setup Instructions](#setup-instructions)
* [Usage](#usage)
* [Endpoints](#endpoints)
* [Error Handling](#error-handling)
* [Testing](#testing)
* [Future Improvements](#future-improvements)

## Features

* Reactive programming: Built with Spring WebFlux to leverage non-blocking, reactive streams.
* CRUD operations: Create, Read, Update, and Delete books.
* Pagination: Retrieve paginated lists of books with server-side validation.
* Custom Error Handling: Detailed error responses with custom exception handling using `@RestControllerAdvice`.
* Validation: Request validation for DTOs and query parameters.
* Fallback and Retry Mechanisms: Configurable error handling using WebFlux
  operators (`onErrorResume`, `doOnError`, `retry`) in data fetching.

## Tech Stack

* Java 17
* Spring Boot and Spring WebFlux
* Reactor Core for reactive programming
* H2 Database (or in-memory storage) for data persistence
* JUnit 5 and WebTestClient for testing

## Setup Instructions

### Prerequisites

* Java 17 installed
* Maven or Gradle for dependency management

### Running the Application

1. Clone the repository:

```bash
git clone https://github.com/AndrewKozyrev/webflux-spring
cd webflux-spring
```

2. Install dependencies:

```bash
./mvnw clean install
```

3. Run the application:

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

## Usage

### Creating a Book

Use the following template to create a book:

```json
{
  "title": "Sample Book",
  "author": "Author Name",
  "publishedYear": 2020
}
```

### Pagination

The `GET /books` endpoint supports pagination. Use the `page` and `size` query parameters to specify the page number and
page size.

### Example Request

```http request
GET /books?page=0&size=10
```

## Endpoints

| **Method** | **Endpoint**  | **Description**                 |
|------------|:--------------|:--------------------------------|
| POST       | `/books`      | Create a new book               |
| GET	       | `/books`      | Retrieve all books (paginated)  |
| GET	       | `/books/{id}` | 	Retrieve a specific book by ID |
| PUT	       | `/books/{id}` | 	Update an existing book        |
| DELETE     | `/books/{id}` | 	Delete a book                  |

## Error Handling

Custom error handling is implemented to provide meaningful error messages for the client. Error responses include a
timestamp, status code, and error details.

Common errors:

* 400 Bad Request: For validation errors (e.g., invalid page size).
* 404 Not Found: When a book is not found.
* 500 Internal Server Error: For unexpected server errors.

Example of a 404 error response:

```json
{
  "timestamp": "2023-10-30T15:23:45",
  "status": "NOT_FOUND",
  "error": "Book Not Found.",
  "message": "Book with id = [id] not found"
}
```
## Testing

The project includes unit and integration tests to verify API behavior.

Run tests using:

```bash
./mvnw test
```

### Test Highlights

* Validation Tests: Ensure DTO validation is working as expected.
* Custom Error Handling: Verifies `HandlerMethodValidationException` and `WebExchangeBindException` handling.
* Fallback Testing: Tests the custom error handling and fallback mechanisms.

## Future Improvements

* Add JWT-based authentication and authorization.
* Integrate with an actual database (e.g., MongoDB) for persistent storage.
* Enhance test coverage for edge cases and error conditions.
* Add Docker configuration for containerized deployment.