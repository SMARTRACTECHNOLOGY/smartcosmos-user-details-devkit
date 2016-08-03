FROM smartcosmos/service
MAINTAINER SMART COSMOS Platform Core Team

ADD target/smartcosmos-*.jar  /opt/smartcosmos/smartcosmos-user-details-devkit.jar

CMD ["/opt/smartcosmos/smartcosmos-user-details-devkit.jar"]
