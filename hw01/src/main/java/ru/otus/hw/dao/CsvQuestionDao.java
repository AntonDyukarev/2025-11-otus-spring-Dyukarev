package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        String questionsFileName = fileNameProvider.getTestFileName();

        try (InputStream inputStream = getClass().getResourceAsStream("/" + questionsFileName);) {
            if (Objects.isNull(inputStream)) {
                String errorMessage = String.format("Could not find file with name %s", questionsFileName);
                throw new QuestionReadException(errorMessage);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            return new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                    .withSkipLines(1)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .build()
                    .parse()
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
