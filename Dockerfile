FROM 535324349245.dkr.ecr.ap-southeast-1.amazonaws.com/base:jdk-11.0.17

RUN mkdir /data

COPY bb-server/target/bb-server-2.0.0.jar /data/
COPY start.sh /data/

WORKDIR /data

EXPOSE 7010

CMD ["/bin/sh",  "start.sh"]
