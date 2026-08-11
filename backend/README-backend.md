# Backend README

This is the Spring Boot backend for the job management demo.

## Run
1. Configure `application.yml` with your DB credentials.
2. Build: `mvn clean package`
3. Run: `java -jar target/job-mgt-demo-0.0.1-SNAPSHOT.jar`

APIs:
- POST /api/auth/register
- POST /api/auth/login
- GET /api/jobs
- POST /api/jobs
- POST /api/apply/{jobId}
