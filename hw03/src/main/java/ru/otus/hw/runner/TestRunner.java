package ru.otus.hw.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.TestRunnerService;

@Component
public class TestRunner implements ApplicationRunner {

    private final TestRunnerService testRunnerService;

    @Autowired
    public TestRunner(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        testRunnerService.run();
    }
}
