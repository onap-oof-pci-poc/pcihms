FROM java:openjdk-8

ADD config.json /etc/config.json

ADD policy.crt /policy.crt

RUN keytool -import -file /policy.crt -alias policycert -keystore /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts -storepass changeit -noprompt

COPY target/pci-handler-1.0-SNAPSHOT.jar application.jar

ADD entrypoint.sh .

RUN chmod +x entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["./entrypoint.sh"]
