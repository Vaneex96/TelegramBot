FROM maven:3.8-openjdk-17-slim

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/rest-service
ADD ./rest-service/pom.xml $HOME/rest-service
ADD ./rest-service/src $HOME/rest-service/src
RUN mvn -f $HOME/rest-service/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

CMD java -jar $HOME/rest-service/target/rest-service-0.0.1-SNAPSHOT.jar