package no.brreg.informasjonsforvaltning.abackendservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.IOException;

import static no.brreg.informasjonsforvaltning.abackendservice.utils.ContractMockKt.startMockServer;
import static no.brreg.informasjonsforvaltning.abackendservice.utils.TestDataKt.*;

public abstract class ApiTestContainer {

    private final static Logger logger = LoggerFactory.getLogger(ApiTestContainer.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private static Slf4jLogConsumer apiLog = new Slf4jLogConsumer(logger).withPrefix("api-container");
    public static GenericContainer mongoContainer;
    public static GenericContainer TEST_API;
    static {

        startMockServer();

        Testcontainers.exposeHostPorts(LOCAL_SERVER_PORT);
        Network apiNetwork = Network.newNetwork();

        mongoContainer = new GenericContainer("mongo:latest")
                .withEnv(getMONGO_ENV_VALUES())
                .withLogConsumer(mongoLog)
                .withExposedPorts(MONGO_PORT)
                .withNetwork(apiNetwork)
                .withNetworkAliases("mongodb")
                .waitingFor(Wait.forListeningPort());
        mongoContainer.start();
        TestUtilsKt.populateDB();

        TEST_API = new GenericContainer("brreg/a-backend-service:latest")
                .withExposedPorts(API_PORT)
                .withLogConsumer(apiLog)
                .dependsOn(mongoContainer)
                .withEnv(getAPI_ENV_VALUES())
                .waitingFor(Wait.forHttp("/version").forStatusCode(200))
                .withNetwork(apiNetwork);


            TEST_API.start();


        try {
            Container.ExecResult result =TEST_API.execInContainer("wget", "-O", "-", "http://host.testcontainers.internal:5000/auth/realms/fdk/protocol/openid-connect/certs");
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
