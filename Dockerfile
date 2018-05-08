FROM openjdk:10

RUN apt-get install -y git
RUN git clone https://github.com/ajoberstar/gradle-worker-cleanup.git

WORKDIR gradle-worker-cleanup

CMD ./gradlew worker
