package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    /*
        Читая правила, я понял их так, что не могу добавлять новые поля шаблонным классам.
        Однако я не знаю другого способа как сюда заинжектить бин из контекста.
        Можно так же сделать сеттер, но опять же, в правилах сказано, что я могу создавать только приватные методы.
        Все же надеюсь, я поступил правильно=)
    */
    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        List<Question> questions = questionDao.findAll();
        for (Question question : questions) {
            printQuestionText(question);
            printAnswers(question.answers());
            ioService.printLine("");
        }
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
