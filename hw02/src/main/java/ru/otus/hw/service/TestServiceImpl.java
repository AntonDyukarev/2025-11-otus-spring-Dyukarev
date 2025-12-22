package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.InputtedAnswerValidationResult;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@RequiredArgsConstructor
@Service
@Qualifier
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final InputtedAnswerValidationService validationService;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
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
        Answer answer = new Answer("No answer selected after three attempts", false);
        String userInput = readInputtedAnswer();
        for (int i = 1; i < 3; i++) {
            InputtedAnswerValidationResult validationResult = validationService.validateInput(answers.size(), userInput);
            if (!validationResult.isValid()) {
                ioService.printLine(validationResult.errorMessage());
                ioService.printFormattedLine("Attempt %s of 3", i + 1);
                userInput = readInputtedAnswer();
            } else {
                answer = answers.get(validationResult.answerIndex());
                break;
            }
        }

        return answer;
    }

    private String readInputtedAnswer() {
        return ioService.readStringWithPrompt("Please input the answer number");
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
