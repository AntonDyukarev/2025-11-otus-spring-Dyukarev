package ru.otus.hw.domain;

public record InputtedAnswerValidationResult(boolean isValid, int answerIndex, String errorMessage) {
}
