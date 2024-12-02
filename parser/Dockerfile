FROM chrome

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/parser
RUN mkdir -p $HOME/parser/selenium
ADD ./parser/pom.xml $HOME/parser
ADD ./parser/src $HOME/parser/src
ADD ./parser/selenium $HOME/parser/selenium
RUN mvn -f $HOME/parser/pom.xml -s $MVN_SETTINGS clean package -DskipTests --debug

CMD java -jar $HOME/parser/target/parser-0.0.1-SNAPSHOT.jar