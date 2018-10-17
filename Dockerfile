FROM java:8
MAINTAINER trendytech

WORKDIR /usr/local/lib/cloudos_query_engine

RUN mkdir bin && mkdir config

COPY target/cloudos_query_engine.jar ./
COPY config/* ./config/
COPY bin/* ./bin/

RUN chmod 755 ./bin/* && /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

CMD ["/usr/local/lib/cloudos_query_engine/bin/cloudos_query_engine", "run"]