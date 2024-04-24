### Create runtime image fase
FROM docker.io/eclipse-temurin:21-ubi9-minimal as runtime

LABEL name="zaakafhandelcomponent"
LABEL summary="Zaakafhandelcomponent (ZAC) developed for Dimpact"
LABEL description="The zaakafhandelcomponent (ZAC) is an open-source, generic, workflow-based component for managing 'zaken' in the context of zaakgericht werken, a Dutch approach to case management."
LABEL maintainer="Lifely/INFO"
LABEL vendor="Lifely/INFO"
LABEL url="https://github.com/infonl/dimpact-zaakafhandelcomponent"
# Unset labels set by the Temurin Ubi9 base Docker image
LABEL build-date=""
LABEL com.redhat.component=""
LABEL com.redhat.license_terms=""
LABEL io.buildah.version=""
LABEL io.k8s.description=""
LABEL io.k8s.display-name=""
LABEL io.openshift.tags=""
LABEL release=""
LABEL vcs-ref=""
LABEL version=""

# Import certificates into Java truststore
ADD certificates /certificates
RUN keytool -importcert -cacerts -alias SmartDocuments -file /certificates/smartdocuments/smartdocuments_com.cer -storepass changeit -noprompt
RUN keytool -importcert -cacerts -alias QuoVadis_PKIoverheid_Private_Services_CA -file /certificates/kvk/QuoVadis_PKIoverheid_Private_Services_CA_-_G1.crt  -storepass changeit -noprompt
RUN keytool -importcert -cacerts -alias Staat_der_Nederlanden_Private_Root_CA -file /certificates/kvk/Staat_der_Nederlanden_Private_Root_CA_-_G1.crt -storepass changeit -noprompt
RUN keytool -importcert -cacerts -alias Staat_der_Nederlanden_Private_Services_CA -file /certificates/kvk/Staat_der_Nederlanden_Private_Services_CA_-_G1.crt -storepass changeit -noprompt

# Copy zaakafhandelcomponent bootable jar
COPY target/zaakafhandelcomponent.jar /

# Copy build timestamp (used by HealthCheckService.java)
RUN date -Iseconds > /build_timestamp.txt

# Add system property to enable overriding WildFly settings using environment variables
ENV WILDFLY_OVERRIDING_ENV_VARS 1

# Start zaakafhandelcomponent
ENTRYPOINT ["java", "-Xms1024m", "-Xmx1024m", "-jar", "zaakafhandelcomponent.jar"]
EXPOSE 8080 9990

ARG branchName
ARG commitHash
ARG versionNumber
ENV BRANCH_NAME=$branchName COMMIT_HASH=$commitHash VERSION_NUMBER=$versionNumber
