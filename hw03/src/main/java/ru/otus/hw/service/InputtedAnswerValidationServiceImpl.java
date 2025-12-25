package ru.otus.hw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.InputtedAnswerValidationResult;
import ru.otus.hw.exceptions.AnswerReadException;

import java.util.Objects;

@Service
@Qualifier
public class InputtedAnswerValidationServiceImpl implements InputtedAnswerValidationService {

    private final LocalizedMessagesService localizedMessagesService;

    @Autowired
    public InputtedAnswerValidationServiceImpl(LocalizedMessagesService localizedMessagesService) {
        this.localizedMessagesService = localizedMessagesService;
    }

    @Override
    public InputtedAnswerValidationResult validateInput(int actualAnswersSize, String userInput) {
        int answerNumber = -1;
        String error = null;

        try {
            answerNumber = convertToAnswerNumber(userInput);
        } catch (RuntimeException e) {
            error = createInputError(actualAnswersSize, e);
        }

        if (Objects.isNull(error)) {
            error = validateAnswerNumber(answerNumber, actualAnswersSize);
        }

        boolean isValid = Objects.isNull(error);
        int answerIndex;
        if (isValid) {
            answerIndex = answerNumber - 1;
        } else {
            answerIndex = -1;
        }
        return new InputtedAnswerValidationResult(isValid, answerIndex, error);
    }

    private String validateAnswerNumber(int answerNumber, int actualAnswersSize) {
        String errorMessage = null;
        if (answerNumber < 1) {
            errorMessage = localizedMessagesService.getMessage("AnswerValidationService.answer.number.less.than");
        } else if (answerNumber > actualAnswersSize) {
            errorMessage = localizedMessagesService.getMessage(
                    "AnswerValidationService.answer.number.greater.than",
                    actualAnswersSize
            );
        }

        if (Objects.isNull(errorMessage)) {
            return null;
        } else {
            return addCorrectNumbersDescription(actualAnswersSize, errorMessage);
        }
    }

    private String createInputError(int actualAnswersSize, RuntimeException e) {
        String errorMessage;
        if (e instanceof NumberFormatException) {
            errorMessage = localizedMessagesService.getMessage("AnswerValidationService.incorrect.input");
        } else if (e instanceof NullPointerException) {
            errorMessage = localizedMessagesService.getMessage("AnswerValidationService.input.cannot.be.null");
        } else {
            throw new AnswerReadException("Can't read answer number", e);
        }
        return addCorrectNumbersDescription(actualAnswersSize, errorMessage);
    }

    private String addCorrectNumbersDescription(int actualAnswersSize, String errorMessage) {
        String correctAnswerAdvice = localizedMessagesService.getMessage(
                "AnswerValidationService.correct.answer.advice",
                actualAnswersSize
        );
        return errorMessage + "\n" + correctAnswerAdvice;
    }

    private int convertToAnswerNumber(String userInput) {
        return Integer.parseInt(userInput.trim());
    }
}
