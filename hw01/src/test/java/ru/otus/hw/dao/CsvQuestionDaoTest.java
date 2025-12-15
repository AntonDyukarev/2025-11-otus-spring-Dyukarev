package ru.otus.hw.dao;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    @Mock
    private TestFileNameProvider fileNameProvider;

    private QuestionDao questionDao;

    @BeforeEach
    public void initMocks() {
        questionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    public void questionsFileNotFoundTest() {
        Mockito.when(fileNameProvider.getTestFileName())
                .thenReturn("non-existent_file.csv");

        Assertions.assertThrows(QuestionReadException.class, questionDao::findAll);
    }

    @Test
    public void successfulReceiptAllQuestionsTest() {
        Mockito.when(fileNameProvider.getTestFileName())
                .thenReturn("questions.csv");

        List<Question> questions = questionDao.findAll();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(questions.size()).as("Questions size").isEqualTo(1);

        Question question = questions.get(0);
        softly.assertThat(question.text()).as("Question text").isEqualTo("Test question text");

        List<Answer> answers = question.answers();
        softly.assertThat(answers.size()).as("Answers size").isEqualTo(2);

        Answer wrongAnswer = answers.get(0);
        softly.assertThat(wrongAnswer.text()).as("Wrong answer text").isEqualTo("Wrong answer");
        softly.assertThat(wrongAnswer.isCorrect()).as("Wrong answer correctness").isEqualTo(false);

        Answer correctAnswer = answers.get(1);
        softly.assertThat(correctAnswer.text()).as("Correct answer text").isEqualTo("Correct answer");
        softly.assertThat(correctAnswer.isCorrect()).as("Correct answer correctness").isEqualTo(true);

        softly.assertAll();
    }
}
