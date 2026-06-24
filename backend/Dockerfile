# ========== 阶段1：编译构建 ==========
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ========== 阶段2：运行 ==========
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="tlias-student-management"
WORKDIR /app

COPY --from=builder /build/target/tlias-backend-1.0.0.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
