# TechDesk Project

Welcome to the TechDesk project! This repository contains a fully functional ticketing system built to streamline support operations. The project consists of two main components: a robust backend service running inside a Docker container and a Java Swing desktop client provided as a JAR file. In addition, comprehensive API documentation, code documentation, and testing have been completed.

---

## Table of Contents

- [Overview](#overview)
  - [Backend Service](#backend-service)
  - [Database Design](#database-design)
  - [Java Swing Client](#java-swing-client)
  - [Documentation and Testing](#documentation-and-testing)
- [How to Run the Project](#how-to-run-the-project)
  - [Prerequisites](#prerequisites)
  - [Running the Backend Service](#running-the-backend-service)
  - [Running the Java Swing Client](#running-the-java-swing-client)
  - [Viewing the Code Documentation](#viewing-the-code-documentation)
- [Additional Information](#additional-information)
- [Acknowledgments](#acknowledgments)

---

## Overview

The TechDesk project was developed to efficiently manage support tickets and related operations. Below are the key aspects of the work:

### Backend Service

- **Functionality:**  
  Handles RESTful API calls for managing users, tickets, comments, and audit logs.

- **Database Integration:**  
  Interacts with an Oracle database that stores critical tables such as `APP_USERS`, `TICKETS`, `COMMENTS`, and `TICKET_AUDIT_LOGS`.

- **Technology:**  
  Packaged as a Docker container for ease of deployment.

### Database Design

- **Schema Details:**  
  - Uses `RAW(16)` fields to store UUIDs, ensuring data integrity and uniqueness.
  - Enforces relationships through primary keys, foreign keys, and unique constraints.
- **Security:**  
  Implements role-based access control to differentiate access levels among administrators, support staff, and employees.

### Java Swing Client

- **User Interface:**  
  Provides an intuitive graphical interface for interacting with the backend services.
- **Delivery:**  
  Delivered as a JAR file for easy execution on any system with Java installed.

### Documentation and Testing

- **API Documentation:**  
  Full Swagger API documentation is available to detail the available endpoints.  
  Access it here: [Swagger API Documentation](http://localhost:8080/swagger-ui/index.html#/)
- **Code Documentation:**  
  Comprehensive Javadoc has been generated.  
  View it at: [Code Documentation](http://localhost:63342/TechDesk/target/reports/apidocs/com/techdesk/services/Impl/package-summary.html)  
  *Note:* The port (`63342`) is provided by IntelliJ.

---

## How to Run the Project

Follow these steps to set up and run the TechDesk project in your environment.

### Prerequisites

- **Docker:**  
  Ensure Docker is installed on your system. Download it from [Docker's official site](https://www.docker.com/get-started).

- **Java:**  
  Make sure Java JDK 8 or higher is installed. Download it from [Oracle's Java downloads](https://www.oracle.com/java/technologies/javase-downloads.html).

### Running the Backend Service

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/Elmehdi-Erraji/TechDesk.git
   cd techdesk
   ```

2. **Build the Docker Image:**

   Build the backend image using the provided Dockerfile:

   ```bash
   docker build -t techdesk-backend .
   ```

3. **Run the Docker Container:**

   Start the container by executing:

   ```bash
   docker run -d -p 8080:8080 --name techdesk-backend techdesk-backend
   ```

   This command runs the container in detached mode and maps port `8080` on your host machine to port `8080` in the container.

4. **Verify the Container is Running:**

   Use the following command to ensure the container is active:

   ```bash
   docker ps
   ```

   The backend service should now be accessible at [http://localhost:8080](http://localhost:8080).

### Running the Java Swing Client

1. **Obtain the Swing Client JAR:**

   Ensure the provided JAR file (e.g., `techdesk-swing.jar`) is in your working directory.

2. **Launch the Swing Client:**

   Open a terminal and run:

   ```bash
   java -jar techdesk-swing.jar
   ```

   This launches the Swing client, which will connect to the backend service at `http://localhost:8080`. Make sure the Docker container is running before starting the client.

### Viewing the Code Documentation

If you need to review the generated code documentation:

1. **Open Your Browser:**

   Navigate to the Javadoc index. For example, if you're using IntelliJ, you can access it at:

   [http://localhost:63342/TechDesk/target/reports/apidocs/com/techdesk/services/Impl/package-summary.html](http://localhost:63342/TechDesk/target/reports/apidocs/com/techdesk/services/Impl/package-summary.html)

   *Note:* The port (`63342`) is provided by IntelliJ.

---

## Additional Information

- **Logging and Auditing:**  
  The backend logs all key operations, ensuring that ticket updates, and comments are efficiently tracked.

- **User Management:**  
  Role-based access control is implemented, ensuring secure and distinct access levels for administrators, support staff, and employees.

- **Testing:**  
  Comprehensive tests have been performed to validate the functionality and reliability of the application.

- **API Documentation:**  
  For a detailed overview of the API endpoints and usage, please refer to the Swagger documentation at:  
  [Swagger API Documentation](http://localhost:8080/swagger-ui/index.html#/)

---

## Acknowledgments

Thank you for reviewing and using the TechDesk project. Your feedback is highly appreciated. If you encounter any issues or have suggestions for improvements, please feel free to open an issue or contact me directly.

Enjoy working with TechDesk!