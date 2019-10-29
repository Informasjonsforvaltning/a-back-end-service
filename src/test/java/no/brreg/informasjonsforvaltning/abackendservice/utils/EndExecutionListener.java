package no.brreg.informasjonsforvaltning.abackendservice.utils;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;


public class EndExecutionListener implements TestExecutionListener {

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {

        if(System.getProperty("test.type").contains("contract")) {
            ApiTestContainer.stopGracefully();
        }
    }
}
