# Architecture Summary

This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules, such as patient and appointment management. The application interacts with two separate databases: MySQL is used for storing relational data including patients, doctors, appointments, and admin accounts, while MongoDB is utilized for managing prescriptions as document-based data. All controllers route requests through a common service layer, which handles business logic and coordinates with the appropriate repositories. MySQL interactions use JPA entities, while MongoDB interactions use document models.

# Numbered flow of data and control

1. The user interacts with the application through the web interface (Thymeleaf templates for dashboards) or via REST API endpoints.
2. The incoming HTTP request is routed to the corresponding MVC or REST controller based on the URL path.
3. The controller extracts necessary data (such as path variables or request bodies) and performs initial validation or authorization checks.
4. The controller calls the appropriate method in the service layer to process the business logic.
5. The service layer interacts with the data repositories, using either JpaRepository for MySQL or MongoRepository for MongoDB.
6. The repository performs the database operation (CRUD) on the respective MySQL or MongoDB instance.
7. Data is returned from the database through the repository and service back to the controller, which then renders a Thymeleaf view or returns a JSON response to the user.
