# Dockerfile 

FROM rtfpessoa/ubuntu-jdk8:latest

LABEL Author="Marco Guassone"

ARG BASE="/app"

WORKDIR ${BASE}

RUN apt-get update
RUN apt-get --yes install sudo

#installo maven
RUN sudo apt-get --yes install maven

#installo node
RUN sudo apt-get --yes install nodejs

COPY /target/*.jar ./
COPY /pom.xml ./
COPY /src/main/resources/* ./

CMD [ "java", "-version" ]