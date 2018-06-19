FROM maven:3.5.2-jdk-8-alpine
LABEL Name=avro-generator Version=1.0.0

# Container configuration
RUN mkdir /avro-generator
COPY . /avro-generator
WORKDIR /avro-generator
RUN cd /avro-generator
#VOLUME /avro-generator
RUN mvn clean install
