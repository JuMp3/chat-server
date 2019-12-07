FROM openjdk:8-alpine

MAINTAINER Giampiero Poggi '<jmusic@hotmail.it>'

ARG APP_NAME=chat-server

COPY ./${APP_NAME}.jar /usr/${APP_NAME}/

RUN chgrp -R 0 /usr/${APP_NAME}/ && \
    chmod -R g=rwx /usr/${APP_NAME}/

EXPOSE 10000

USER 1001

WORKDIR /usr/${APP_NAME}/

CMD ["java", "-jar", "chat-server.jar"]