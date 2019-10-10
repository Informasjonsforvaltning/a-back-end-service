package no.template; /*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/

import org.jacoco.agent.rt.internal_035b120.core.data.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;


/**
 * This example reads execution data files given as program arguments and dumps
 * their content.
 */
public final class JacocoWriter {

    public  JacocoWriter(){
        try {
            dump(System.getProperty("user.dir")+"/target/jacoco-client.exec");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dump(final String file) throws IOException {
        System.out.printf("exec file: %s%n", file);
        System.out.println("CLASS ID         HITS/PROBES   CLASS NAME");

        final FileInputStream in = new FileInputStream(file);
        final ExecutionDataReader reader = new ExecutionDataReader(in);
        reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
            public void visitSessionInfo(final SessionInfo info) {
                System.out.printf("Session \"%s\": %s - %s%n", info.getId(), new Date(
                                info.getStartTimeStamp()),
                        new Date(info.getDumpTimeStamp()));
            }
        });
        reader.setExecutionDataVisitor(new IExecutionDataVisitor() {
            public void visitClassExecution(final ExecutionData data) {
                System.out.printf("%016x  %3d of %3d   %s%n",
                        Long.valueOf(data.getId()),
                        Integer.valueOf(getHitCount(data.getProbes())),
                        Integer.valueOf(data.getProbes().length),
                        data.getName());
            }
        });
        reader.read();
        in.close();
        System.out.println("Done");
    }

    private int getHitCount(final boolean[] data) {
        int count = 0;
        for (final boolean hit : data) {
            if (hit) {
                count++;
            }
        }
        return count;
    }
}
