package no.brreg.informasjonsforvaltning.abackendservice.integration;

import no.brreg.informasjonsforvaltning.abackendservice.TestUtilsKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

import static no.brreg.informasjonsforvaltning.abackendservice.TestDataKt.*;


public abstract class AbstractDockerTestContainer {

    private static File testComposeFile = TestUtilsKt.createTmpComposeFile();
    private final static Logger logger = LoggerFactory.getLogger(ServiceEndpointApiServiceTest.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer apiLog = new Slf4jLogConsumer(logger).withPrefix("api-container");
    public static DockerComposeContainer TEST_API;
    static {
        if (testComposeFile != null && testComposeFile.exists()) {
            TEST_API = new DockerComposeContainer<>(testComposeFile)
                    .withExposedService(MONGO_SERVICE_NAME, MONGO_PORT, Wait.forListeningPort())
                    .withExposedService(API_SERVICE_NAME, API_PORT, Wait.forHttp("/version").forStatusCode(200))
                    .withTailChildContainers(true)
                    .withPull(false)
                    .withLocalCompose(true)
                    .withLogConsumer(MONGO_SERVICE_NAME, mongoLog)
                    .withLogConsumer(API_SERVICE_NAME, apiLog);

            TEST_API.start();

        } else {
            logger.debug("Unable to start containers, missing test-compose.yml");
        }
    }

}
