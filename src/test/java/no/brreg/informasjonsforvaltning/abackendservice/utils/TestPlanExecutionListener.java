package no.brreg.informasjonsforvaltning.abackendservice.utils;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;


public class TestPlanExecutionListener implements TestExecutionListener {

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {

        if (System.getProperty("test.type").contains("contract")) {
            ApiTestContainer.stopGracefully();
        }
    }
}
