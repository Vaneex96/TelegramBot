FROM maven:3.8-openjdk-17-slim

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

#RUN mkdir -p $HOME/common-rabbitmq
#ADD ./common-rabbitmq $HOME/common-rabbitmq
#RUN mvn -f $HOME/common-rabbitmq/pom.xml -s $MVN_SETTINGS clean package
#RUN mvn org.apache.maven.plugins:maven-install:2.5.2:install-file -Dfile=$HOME/common-rabbitmq/target/common-rabbitmq-0.0.1.jar --debug
#
#RUN mkdir -p $HOME/common-utils
#ADD ./common-utils $HOME/common-utils
#RUN mvn -f $HOME/common-utils/pom.xml -s $MVN_SETTINGS clean package
#RUN mvn org.apache.maven.plugins:maven-install:2.5.2:install-file -Dfile=$HOME/common-utils/target/common-utils-0.0.1.jar --debug

RUN mkdir -p $HOME/mail-service
ADD ./mail-service/pom.xml $HOME/mail-service
ADD ./mail-service/src $HOME/mail-service/src
RUN mvn -f $HOME/mail-service/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

CMD java -jar $HOME/mail-service/target/mail-service-0.0.1-SNAPSHOT.jar