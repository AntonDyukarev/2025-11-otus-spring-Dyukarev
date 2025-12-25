package ru.otus.hw.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestConfig;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @Mock
    private QuestionDao questionDao;

    @Mock
    private LocalizedIOService ioService;

    @Mock
    private InputtedAnswerValidationService validationService;

    @Mock
    private TestConfig testConfig;

    private TestService testService;

    private List<Question> questions;

    private Student student;

    @BeforeEach
    public void initTestService() {
        testService = new TestServiceImpl(ioService, questionDao, validationService, testConfig);
    }

    @BeforeEach
    public void initQuestions() {
        questions = List.of(
                generateQuestion(1),
                generateQuestion(2)
        );
    }

    @BeforeEach
    public void initStudent() {
        student = new Student("Student", "Test");
    }

    @Test
    public void successfulPrintsTest() {
        Mockito.when(questionDao.findAll())
                .thenReturn(questions);

        testService.executeTestFor(student);

        InOrder inOrder = Mockito.inOrder(ioService, questionDao);

        checkThatQuestionsHeaderIsPrinted(inOrder);

        inOrder.verify(questionDao)
                .findAll();

        for (Question question : questions) {
            checkThatQuestionTextIsPrinted(inOrder, question);
            checkThatAnswerTextsArePrinted(inOrder, question);
        }
    }

    @Test
    public void questionReadExceptionTest() {
        Mockito.when(questionDao.findAll())
                .thenThrow(QuestionReadException.class);

        Assertions.assertThrows(QuestionReadException.class, () -> testService.executeTestFor(student));
    }

    private void checkThatQuestionsHeaderIsPrinted(InOrder inOrder) {
        inOrder.verify(ioService)
                .printLine("");
        inOrder.verify(ioService)
                .printLineLocalized("TestService.answer.the.questions");
        inOrder.verify(ioService)
                .printLine("");
    }

    private void checkThatQuestionTextIsPrinted(InOrder inOrder, Question question) {
        String questionText = question.text();
        inOrder.verify(ioService).printLine(questionText);
    }

    private void checkThatAnswerTextsArePrinted(InOrder inOrder, Question question) {
        List<Answer> answers = question.answers();

        for (int i = 0; i < answers.size(); i++) {
            int answerNumber = i + 1;
            String answerText = answers.get(i).text();
            inOrder.verify(ioService).printFormattedLine("  %s. %s", answerNumber, answerText);
        }
    }

    private Question generateQuestion(int questionNumber) {
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("Test answer " + questionNumber + "_01",true));
        answers.add(new Answer("Test answer " + questionNumber + "_02",false));
        answers.add(new Answer("Test answer " + questionNumber + "_03",false));

        return new Question("Test question " + questionNumber, answers);
    }
}
