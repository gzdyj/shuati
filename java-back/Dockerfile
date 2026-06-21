FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

RUN mkdir -p /root/.m2 && echo '<settings><mirrors><mirror><id>aliyunmaven</id><mirrorOf>central</mirrorOf><url>https://maven.aliyun.com/repository/public</url></mirror><mirror><id>huaweicloud</id><mirrorOf>central</mirrorOf><url>https://repo.huaweicloud.com/repository/maven/</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/quiz-master-1.0.0.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
