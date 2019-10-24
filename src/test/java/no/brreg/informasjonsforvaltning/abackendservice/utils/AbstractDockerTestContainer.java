package no.brreg.informasjonsforvaltning.abackendservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.io.IOException;

import static no.brreg.informasjonsforvaltning.abackendservice.utils.AuthMockKt.startMockAuth;
import static no.brreg.informasjonsforvaltning.abackendservice.utils.TestDataKt.*;

public abstract class AbstractDockerTestContainer {

    private static File testComposeFile = TestUtilsKt.createTmpComposeFile();
    private final static Logger logger = LoggerFactory.getLogger(AbstractDockerTestContainer.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer apiLog = new Slf4jLogConsumer(logger).withPrefix("api-container");
    private static GenericContainer mongoContainer;
    public static GenericContainer TEST_API;
    static {

        startMockAuth();
        Testcontainers.exposeHostPorts(LOCAL_SERVER_PORT);


        Network apiNetwork = Network.newNetwork();

        mongoContainer = new GenericContainer("mongo:latest")
                .withEnv(getMONGO_ENV_VALUES())
                .withLogConsumer(mongoLog)
                .withExposedPorts(MONGO_PORT)
                .withNetwork(apiNetwork)
                .withNetworkAliases("mongodb")
                .waitingFor(Wait.forListeningPort());

        TEST_API = new GenericContainer("brreg/a-backend-service:latest")
                .withExposedPorts(API_PORT)
                .withLogConsumer(apiLog)
                .dependsOn(mongoContainer)
                .withEnv(getAPI_ENV_VALUES())
                .waitingFor(Wait.forHttp("/version").forStatusCode(200))
                .withNetwork(apiNetwork);

            mongoContainer.start();
            TEST_API.start();
        try {
            Container.ExecResult result =TEST_API.execInContainer("wget", "-O", "-", "http://host.testcontainers.internal:5000/ping");
            if (!result.getStderr().contains("200")){
                logger.debug("Ping to AuthMock server failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Legg inn testdata i mongodb
    }

}
