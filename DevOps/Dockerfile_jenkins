# Dockerfile 

FROM rtfpessoa/ubuntu-jdk8:latest

LABEL Author="Marco Guassone"

#ARG BASE="/opt"

#WORKDIR ${BASE}

RUN apt-get update
RUN apt-get --yes install sudo

#installo zip
RUN sudo apt-get --yes install zip

#installo maven
RUN sudo apt-get --yes install maven

#installo node
RUN sudo apt-get --yes install nodejs

#intallo ansible
RUN sudo apt-get --yes install software-properties-common
RUN sudo apt-add-repository --yes --update ppa:ansible/ansible
RUN sudo apt-get update
RUN sudo apt-get --yes install ansible

#intallo flyway
RUN sudo wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/6.1.1/flyway-commandline-6.1.1-linux-x64.tar.gz |sudo tar xvz && sudo ln -s `pwd`/flyway-6.1.1/flyway /usr/local/bin 

#COPY ./sql/* /sql/

#COPY ./DevOps/* /DevOps/
