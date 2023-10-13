FROM openjdk:11
ARG JAR_FILE=build/libs/snsApp.jar
COPY ${JAR_FILE} ./snsApp.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java","-jar","./snsApp.jar"]