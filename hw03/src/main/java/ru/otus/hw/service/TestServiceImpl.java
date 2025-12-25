package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.InputtedAnswerValidationResult;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    private final InputtedAnswerValidationService validationService;

    private final TestConfig testConfig;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            printQuestionText(question);
            List<Answer> answers = question.answers();
            printAnswers(answers);
            Answer answer = getAnswerByUserInput(answers);
            var isAnswerValid = answer.isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private Answer getAnswerByUserInput(List<Answer> answers) {
        int attemptsCount = testConfig.getAttemptsInputAnswerCount();
        Answer answer = createNoSelectedAnswer(attemptsCount);

        InputtedAnswerValidationResult validationResult = null;
        for (int i = 0; i < attemptsCount; i++) {
            if (Objects.nonNull(validationResult)) {
                ioService.printLine(validationResult.errorMessage());
                ioService.printFormattedLineLocalized(
                        "TestService.answer.attempt.counter",
                        i + 1,
                        attemptsCount
                );
            }

            String userInput = readInputtedAnswer();
            validationResult = validationService.validateInput(answers.size(), userInput);
            if (validationResult.isValid()) {
                answer = answers.get(validationResult.answerIndex());
                break;
            }
        }

        return answer;
    }

    private Answer createNoSelectedAnswer(int attemptsCount) {
        String noAnswerSelected =  ioService.getMessage(
                "TestService.answer.not.selected",
                attemptsCount
        );
        return new Answer(noAnswerSelected, false);
    }

    private String readInputtedAnswer() {
        return ioService.readStringWithPromptLocalized("TestService.answer.number.request");
    }

    private void printQuestionText(Question question) {
        String questionText = question.text();
        ioService.printLine(questionText);
    }

    private void printAnswers(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            int answerNumber = i + 1;
            String answerText = answers.get(i).text();
            ioService.printFormattedLine("  %s. %s", answerNumber, answerText);
        }
    }
}
