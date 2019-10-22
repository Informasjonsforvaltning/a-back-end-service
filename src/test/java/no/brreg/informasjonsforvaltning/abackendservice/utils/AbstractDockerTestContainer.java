package no.brreg.informasjonsforvaltning.abackendservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;


public abstract class AbstractDockerTestContainer {

    private static File testComposeFile = TestUtilsKt.createTmpComposeFile();
    private final static Logger logger = LoggerFactory.getLogger(AbstractDockerTestContainer.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer apiLog = new Slf4jLogConsumer(logger).withPrefix("api-container");
    public static DockerComposeContainer TEST_API;
    static {
        if (testComposeFile != null && testComposeFile.exists()) {
            TEST_API = new DockerComposeContainer<>(testComposeFile)
                    .withExposedService(TestDataKt.MONGO_SERVICE_NAME, TestDataKt.MONGO_PORT, Wait.forListeningPort())
                    .withExposedService(TestDataKt.API_SERVICE_NAME, TestDataKt.API_PORT, Wait.forHttp("/version").forStatusCode(200))
                    .withTailChildContainers(true)
                    .withPull(false)
                    .withLocalCompose(true)
                    .withLogConsumer(TestDataKt.MONGO_SERVICE_NAME, mongoLog)
                    .withLogConsumer(TestDataKt.API_SERVICE_NAME, apiLog);

            TEST_API.start();

            //Legg inn testdata i mongodb

        } else {
            logger.debug("Unable to start containers, missing test-compose.yml");
        }
    }

}
