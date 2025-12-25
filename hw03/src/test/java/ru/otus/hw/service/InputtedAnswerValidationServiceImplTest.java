package ru.otus.hw.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.otus.hw.config.LocaleConfig;
import ru.otus.hw.domain.InputtedAnswerValidationResult;

import java.util.Locale;

@ExtendWith(MockitoExtension.class)
public class InputtedAnswerValidationServiceImplTest {

    @Mock
    private LocaleConfig localeConfig;

    private InputtedAnswerValidationService validationService;

    @BeforeEach
    public void initValidationService() {
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasename("messages");
        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);

        LocalizedMessagesService localizedMessagesService = new LocalizedMessagesServiceImpl(localeConfig, ms);
        validationService = new InputtedAnswerValidationServiceImpl(localizedMessagesService);
    }

    @Test
    public void nullInputValidationTest() {
        setUpEnLocaleMock();
        InputtedAnswerValidationResult validationResult = validationService.validateInput(
                3,
                null
        );
        boolean actualValid = validationResult.isValid();
        int actualAnswerIndex = validationResult.answerIndex();
        String actualErrorMessage = validationResult.errorMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualValid).as("Valid flag").isFalse();
        softly.assertThat(actualAnswerIndex).as("Answer index").isEqualTo(-1);
        softly.assertThat(actualErrorMessage).as("Error message")
                .isEqualTo("Input cannot be null!\nFor the correct answer, enter a number from 1 to 3");
        softly.assertAll();
    }

    @Test
    public void emptyInputValidationTest() {
        setUpEnLocaleMock();

        InputtedAnswerValidationResult validationResult = validationService.validateInput(
                3,
                ""
        );
        boolean actualValid = validationResult.isValid();
        int actualAnswerIndex = validationResult.answerIndex();
        String actualErrorMessage = validationResult.errorMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualValid).as("Valid flag").isFalse();
        softly.assertThat(actualAnswerIndex).as("Answer index").isEqualTo(-1);
        softly.assertThat(actualErrorMessage).as("Error message")
                .isEqualTo("Incorrect input!\nFor the correct answer, enter a number from 1 to 3");
        softly.assertAll();
    }

    @Test
    public void invalidInputValidationTest() {
        setUpEnLocaleMock();

        InputtedAnswerValidationResult validationResult = validationService.validateInput(
                3,
                "asd123"
        );
        boolean actualValid = validationResult.isValid();
        int actualAnswerIndex = validationResult.answerIndex();
        String actualErrorMessage = validationResult.errorMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualValid).as("Valid flag").isFalse();
        softly.assertThat(actualAnswerIndex).as("Answer index").isEqualTo(-1);
        softly.assertThat(actualErrorMessage).as("Error message")
                .isEqualTo("Incorrect input!\nFor the correct answer, enter a number from 1 to 3");
        softly.assertAll();
    }

    @Test
    public void correctInputValidationTest() {
        InputtedAnswerValidationResult validationResult = validationService.validateInput(
                3,
                "2"
        );
        boolean actualValid = validationResult.isValid();
        int actualAnswerIndex = validationResult.answerIndex();
        String actualErrorMessage = validationResult.errorMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualValid).as("Valid flag").isTrue();
        softly.assertThat(actualAnswerIndex).as("Answer index").isEqualTo(1);
        softly.assertThat(actualErrorMessage).as("Error message").isNull();
        softly.assertAll();
    }

    @Test
    public void correctInputWithSpaceValidationTest() {
        InputtedAnswerValidationResult validationResult = validationService.validateInput(
                3,
                "   2   "
        );
        boolean actualValid = validationResult.isValid();
        int actualAnswerIndex = validationResult.answerIndex();
        String actualErrorMessage = validationResult.errorMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualValid).as("Valid flag").isTrue();
        softly.assertThat(actualAnswerIndex).as("Answer index").isEqualTo(1);
        softly.assertThat(actualErrorMessage).as("Error message").isNull();
        softly.assertAll();
    }

    @Test
    public void lowerBoundInputValidationTest() {
        setUpEnLocaleMock();

        InputtedAnswerValidationResult zeroValidationResult = validationService.validateInput(
                3,
                "0"
        );
        boolean actualZeroValid = zeroValidationResult.isValid();
        int actualZeroAnswerIndex = zeroValidationResult.answerIndex();
        String actualZeroErrorMessage = zeroValidationResult.errorMessage();

        InputtedAnswerValidationResult oneValidationResult = validationService.validateInput(
                3,
                "1"
        );
        boolean actualOneValid = oneValidationResult.isValid();
        int actualOneAnswerIndex = oneValidationResult.answerIndex();
        String actualOneErrorMessage = oneValidationResult.errorMessage();
        
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualZeroValid).as("Valid flag").isFalse();
        softly.assertThat(actualZeroAnswerIndex).as("Answer index").isEqualTo(-1);
        softly.assertThat(actualZeroErrorMessage).as("Error message")
                .isEqualTo("Answer number cannot be less than 1!\n" +
                        "For the correct answer, enter a number from 1 to 3");
        
        softly.assertThat(actualOneValid).as("Valid flag").isTrue();
        softly.assertThat(actualOneAnswerIndex).as("Answer index").isEqualTo(0);
        softly.assertThat(actualOneErrorMessage).as("Error message").isNull();
        softly.assertAll();
    }

    @Test
    public void upperBoundInputValidationTest() {
        setUpEnLocaleMock();

        InputtedAnswerValidationResult fourValidationResult = validationService.validateInput(
                3,
                "4"
        );
        boolean actualFourValid = fourValidationResult.isValid();
        int actualFourAnswerIndex = fourValidationResult.answerIndex();
        String actualFourErrorMessage = fourValidationResult.errorMessage();

        InputtedAnswerValidationResult oneValidationResult = validationService.validateInput(
                3,
                "3"
        );
        boolean actualOneValid = oneValidationResult.isValid();
        int actualOneAnswerIndex = oneValidationResult.answerIndex();
        String actualOneErrorMessage = oneValidationResult.errorMessage();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(actualFourValid).as("Valid flag").isFalse();
        softly.assertThat(actualFourAnswerIndex).as("Answer index").isEqualTo(-1);
        softly.assertThat(actualFourErrorMessage).as("Error message")
                .isEqualTo("Answer number cannot be greater than 3!\n" +
                        "For the correct answer, enter a number from 1 to 3");

        softly.assertThat(actualOneValid).as("Valid flag").isTrue();
        softly.assertThat(actualOneAnswerIndex).as("Answer index").isEqualTo(2);
        softly.assertThat(actualOneErrorMessage).as("Error message").isNull();
        softly.assertAll();
    }

    private void setUpEnLocaleMock() {
        Mockito.when(localeConfig.getLocale()).thenReturn(new Locale("en", "US"));
    }

}
