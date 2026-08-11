# Docker run instructions added

## Docker (one-line)
After adding auth / customizing .env (or using the supplied .env.example), run:

  docker-compose up --build

This will start three containers:
- db (MySQL) -> port 3306
- backend (Spring Boot) -> port 8080
- frontend (nginx serving built Vue files) -> port 5173 (mapped to container 80)

Front-end will be available at http://localhost:5173 and will communicate with backend at http://localhost:8080
