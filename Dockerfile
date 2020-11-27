FROM gradle:latest AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

FROM openjdk:latest

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/Likes-1.0-all.jar /app/Likes.jar
ENTRYPOINT ["java", "-jar", "/app/Likes.jar"]