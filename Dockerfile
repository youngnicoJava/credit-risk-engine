FROM eclipse-temurin:25-jre
WORKDIR /work
RUN useradd --system --uid 10001 --create-home appuser
COPY --chown=10001:10001 target/quarkus-app/lib/ /work/lib/
COPY --chown=10001:10001 target/quarkus-app/*.jar /work/
COPY --chown=10001:10001 target/quarkus-app/app/ /work/app/
COPY --chown=10001:10001 target/quarkus-app/quarkus/ /work/quarkus/
USER 10001:10001
EXPOSE 8082
ENV PORT=8082
ENTRYPOINT ["java","-jar","/work/quarkus-run.jar"]
