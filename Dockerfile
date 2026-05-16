# syntax=docker/dockerfile:1.6

# ---------- build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- runtime stage ----------
FROM tomcat:11.0-jdk17-temurin

# Postgres JDBC driver must live on Tomcat's common classpath so the
# container-managed JNDI Resource can load it (WEB-INF/lib is not enough).
COPY --from=build \
    /root/.m2/repository/org/postgresql/postgresql/42.7.2/postgresql-42.7.2.jar \
    /usr/local/tomcat/lib/postgresql-42.7.2.jar

# Deploy at the ROOT context (so the app is served at http://host:8080/).
COPY --from=build /build/target/FinTrack.war /usr/local/tomcat/webapps/ROOT.war

# Per-host context descriptor — takes precedence over META-INF/context.xml
# inside the WAR. Points the DataSource at the "db" compose service.
COPY docker/context.xml /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml

EXPOSE 8080
CMD ["catalina.sh", "run"]
