package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TestServiceTest {

    @Mock
    QuestionDao questionDao;

    @Mock
    IOService ioService;

    private TestService testService;

    private List<Question> questions;

    @BeforeEach
    public void initTestService() {
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @BeforeEach
    public void initQuestions() {
        questions = List.of(
                generateQuestion(1),
                generateQuestion(2)
        );
    }

    @Test
    public void test() {
        Mockito.when(questionDao.findAll())
                .thenReturn(questions);

        testService.executeTest();

        InOrder inOrder = Mockito.inOrder(ioService, questionDao);

        checkThatQuestionsHeaderIsPrinted(inOrder);

        inOrder.verify(questionDao)
                .findAll();

        for (Question question : questions) {
            checkThatQuestionTextIsPrinted(inOrder, question);
            checkThatAnswerTextsArePrinted(inOrder, question);
            inOrder.verify(ioService)
                    .printLine("");
        }
    }

    private void checkThatQuestionsHeaderIsPrinted(InOrder inOrder) {
        inOrder.verify(ioService)
                .printLine("");
        inOrder.verify(ioService)
                .printFormattedLine("Please answer the questions below%n");
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
