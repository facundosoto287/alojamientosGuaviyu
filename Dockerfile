# ---------- Etapa 1: compilar el .war con Maven y Java 21 ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Primero solo el pom, para que Docker guarde las dependencias en caché
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Etapa 2: ejecutar en Tomcat 11 con Java 21 ----------
FROM tomcat:11.0-jdk21-temurin

ENV TZ=America/Montevideo

# Detrás de Dokploy (Traefik) la app recibe HTTP, pero el usuario entra por HTTPS.
# Esta válvula hace que Tomcat lea X-Forwarded-Proto, así request.isSecure() y
# request.getScheme() devuelven "https" (cookies seguras y links de correo correctos).
RUN sed -i 's#</Host>#  <Valve className="org.apache.catalina.valves.RemoteIpValve" protocolHeader="X-Forwarded-Proto" />\n      </Host>#' conf/server.xml

# Sacamos las apps de ejemplo y publicamos la nuestra en la raíz del sitio (/)
RUN rm -rf webapps/* webapps.dist
COPY --from=build /app/target/*.war webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
