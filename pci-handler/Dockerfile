FROM java:openjdk-8

ADD config.json /etc/config.json

COPY target/pci-handler-1.0-SNAPSHOT.jar application.jar

ADD entrypoint.sh .

RUN chmod +x entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["./entrypoint.sh"]
