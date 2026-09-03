# ESTÁGIO 1: Build da Aplicação
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app

# Otimização de Cache: Copia apenas o pom.xml primeiro para baixar as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e força a compilação do executável ignorando os testes
COPY src ./src
RUN mvn clean package -DskipTests

# ESTÁGIO 2: Imagem de Produção
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Extrai o artefato .jar gerado no Estágio 1 e descarta o resto
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot vai usar no ecossistema
EXPOSE 8080

# Disparo da máquina virtual Java
ENTRYPOINT ["java", "-jar", "app.jar"]