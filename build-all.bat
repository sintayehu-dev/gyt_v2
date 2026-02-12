@echo off
echo Building all microservices...

set services=config-server discovery-server api-gateway auth-service user-service movie-service schedule-service ticket-service

for %%s in (%services%) do (
    echo Building %%s...
    cd %%s
    call mvn clean package -DskipTests
    if errorlevel 1 (
        echo X %%s build failed
        exit /b 1
    )
    echo + %%s built successfully
    cd ..
)

echo.
echo All services built successfully!
echo You can now run: docker-compose up -d
