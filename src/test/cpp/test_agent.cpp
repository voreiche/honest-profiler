#include <UnitTest++.h>

#include <thread>
#include <vector>
#include <iostream>
#include "fixtures.h"
#include "../../main/cpp/agent.cpp"

TEST(ParseSetsDefaultOptions) {
    ConfigurationOptions options;
    parseArguments((char*) NULL, options);
    CHECK_EQUAL(options.samplingIntervalMin, DEFAULT_SAMPLING_INTERVAL);
    CHECK_EQUAL(options.samplingIntervalMax, DEFAULT_SAMPLING_INTERVAL);
    CHECK(!options.logFilePath);
}

TEST(ParsesSamplingInterval) {
    ConfigurationOptions options;
    parseArguments((char *) "interval=10", options);
    CHECK_EQUAL(10, options.samplingIntervalMin);
    CHECK_EQUAL(10, options.samplingIntervalMax);

    parseArguments((char *) "intervalMin=12,intervalMax=17", options);
    CHECK_EQUAL(12, options.samplingIntervalMin);
    CHECK_EQUAL(17, options.samplingIntervalMax);
}

TEST(ParsesLogPath) {
    ConfigurationOptions options;
    char* string = (char *) "logPath=/home/richard/log.hpl";
    parseArguments(string, options);
    CHECK(options.logFilePath > (string + strlen(string)));
    CHECK_EQUAL("/home/richard/log.hpl", options.logFilePath);
}

TEST(ParsesMultipleArguments) {
    ConfigurationOptions options;
    char* string = (char *) "interval=10,logPath=/home/richard/log.hpl";
    parseArguments(string, options);
    CHECK_EQUAL(10, options.samplingIntervalMin);
    CHECK_EQUAL(10, options.samplingIntervalMax);
    CHECK_EQUAL("/home/richard/log.hpl", options.logFilePath);

    string = (char *) "logPath=/home/richard/log.hpl,interval=10";
    parseArguments(string, options);
    CHECK_EQUAL(10, options.samplingIntervalMin);
    CHECK_EQUAL(10, options.samplingIntervalMax);
    CHECK_EQUAL("/home/richard/log.hpl", options.logFilePath);
}
