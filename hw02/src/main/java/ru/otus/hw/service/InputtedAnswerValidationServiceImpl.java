package ru.otus.hw.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.InputtedAnswerValidationResult;
import ru.otus.hw.exceptions.AnswerReadException;

import java.util.Objects;

@Service
@Qualifier
public class InputtedAnswerValidationServiceImpl implements InputtedAnswerValidationService {

    private static final String CORRECT_NUMBERS_DESCRIPTION_TEMPLATE =
            "For the correct answer, enter a number from 1 to ";

    private static final String CANT_READ_ANSWER_NUMBER = "Can't read answer number";

    private static final String INPUT_CANNOT_BE_NULL = "Input cannot be null!";

    private static final String INCORRECT_INPUT = "Incorrect input!";

    private static final String ANSWER_CANNOT_BE_GREATER_THAN_ONE = "Answer number cannot be greater than %s!";

    private static final String ANSWER_CANNOT_BE_LESS_THAN_ONE = "Answer number cannot be less than 1!";

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
            errorMessage = ANSWER_CANNOT_BE_LESS_THAN_ONE;
        } else if (answerNumber > actualAnswersSize) {
            errorMessage = String.format(ANSWER_CANNOT_BE_GREATER_THAN_ONE, actualAnswersSize);
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
            errorMessage = INCORRECT_INPUT;
        } else if (e instanceof NullPointerException) {
            errorMessage = INPUT_CANNOT_BE_NULL;
        } else {
            throw new AnswerReadException(CANT_READ_ANSWER_NUMBER, e);
        }
        return addCorrectNumbersDescription(actualAnswersSize, errorMessage);
    }

    private String addCorrectNumbersDescription(int actualAnswersSize, String errorMessage) {
        return errorMessage + "\n" + CORRECT_NUMBERS_DESCRIPTION_TEMPLATE + actualAnswersSize;
    }

    private int convertToAnswerNumber(String userInput) {
        return Integer.parseInt(userInput.trim());
    }
}
