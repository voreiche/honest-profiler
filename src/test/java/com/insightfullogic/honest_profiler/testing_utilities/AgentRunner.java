/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.testing_utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;

public class AgentRunner {

    private static final Logger logger = LoggerFactory.getLogger(AgentRunner.class);

    public static void run(final String className, final Consumer<AgentRunner> handler) throws IOException {
        run(className, null, handler);
    }

    public static void run(final String className, final String args, final Consumer<AgentRunner> handler) throws IOException {
        AgentRunner runner = new AgentRunner(className, args);
        runner.start();
        try {
            handler.accept(runner);
        } finally {
            runner.stop();
        }
    }

    private final String className;
    private final String args;

    private Process process;
    private int processId;

    private AgentRunner(final String className, final String args) {
        this.className = className;
        this.args = args;
    }

    private void start() throws IOException {
        startProcess();
        readProcessId();
    }

    private void startProcess() throws IOException {
        String java = System.getProperty("java.home") + "/bin/java";
        String agentArg = "-agentpath:build/liblagent.so" + (args != null ? "=" + args : "");
        // Eg: java -agentpath:build/liblagent.so -cp target/classes/ InfiniteExample
        process = new ProcessBuilder()
                .command(java, agentArg, "-cp", "target/classes/", className)
                .redirectError(new File("/tmp/error.log"))
                .start();
    }

    private void readProcessId() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            System.out.println(line);
            processId = parseInt(line);
        }
    }

    private void stop() {
        process.destroy();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public int getProcessId() {
        return processId;
    }

    public void startProfiler() throws IOException {
        process.getOutputStream().write('S');
        process.getOutputStream().write('\n');
        process.getOutputStream().flush();
    }

    public void stopProfiler() throws IOException {
        process.getOutputStream().write('s');
        process.getOutputStream().write('\n');
        process.getOutputStream().flush();
    }
}
