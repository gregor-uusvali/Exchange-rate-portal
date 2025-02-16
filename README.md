# Exchange-rate-portal

A web application for tracking and managing currency exchange rates. Built with Angular on the frontend and Spring Boot on the backend, this application fetches daily exchange rates from the European Central Bank.
[Link to demo](http://5.189.135.103:4200/)

## Features

- View current exchange rates from the European Central Bank
- Historical exchange rate data visualization (chart/table)
- Currency calculator with real-time conversion
- Automatic daily rate updates using Quartz scheduling
- 90-day historical data population on first run
- H2 database for data persistence

## Development Setup

### Frontend
```bash
cd frontend
npm install
npm start
```
Navigate to `http://localhost:4200/`

### Backend
```bash
cd backend
gradle bootRun
```
The API will be available at `http://localhost:8080`

## Docker

The project includes Docker support for both frontend and backend services.

### Using Docker Compose
```bash
docker-compose up --build
```

This will start both services:
- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`

### Environment Variables

Backend:
- `SPRING_DATASOURCE_URL`: H2 database URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## Project Structure
```
.
├── frontend/             # Angular frontend
│   ├── src/              # Source files
│   ├── package.json      # Frontend dependencies
│   └── Dockerfile        # Frontend Docker configuration
├── backend/              # Spring Boot backend
│   ├── src/              # Source files
│   ├── build.gradle      # Backend dependencies
│   └── Dockerfile        # Backend Docker configuration
└── docker-compose.yml    # Docker Compose configuration
```

## Docker Configuration

The project uses three Docker-related files:

1. `docker-compose.yml`: Orchestrates both services
2. `frontend/Dockerfile`: Node.js-based image for Angular frontend
3. `backend/Dockerfile`: OpenJDK 17-based image for Spring Boot backend

## Author

[Gregor Uusväli](https://github.com/gregor-uusvali/)