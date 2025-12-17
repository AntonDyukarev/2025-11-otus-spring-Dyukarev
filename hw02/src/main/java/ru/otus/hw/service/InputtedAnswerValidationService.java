package ru.otus.hw.service;

import ru.otus.hw.domain.InputtedAnswerValidationResult;

public interface InputtedAnswerValidationService {
    InputtedAnswerValidationResult validateInput(int actualAnswersSize, String userInput);
}
