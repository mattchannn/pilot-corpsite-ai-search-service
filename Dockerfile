FROM amazoncorretto:21-alpine-full
MAINTAINER Mat Chan <klchan.358@gmail.com>
WORKDIR /app
ADD target/ai-search-service*.jar /app/ai-search-service.jar
ADD entry.sh .
RUN ls -l
RUN chmod +x /app/entry.sh
EXPOSE 8080

ENTRYPOINT ["/app/entry.sh"]