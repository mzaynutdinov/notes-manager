package ru.mzaynutdinov.notes.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mzaynutdinov.notes.dao.NotesRepository;
import ru.mzaynutdinov.notes.dto.NoteDto;
import ru.mzaynutdinov.notes.entity.Note;
import ru.mzaynutdinov.notes.utils.ApiException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotesLogic {
    @Autowired
    NotesRepository notesRepository;

    /**
     * Возвращает список записей с (или без) фильтрацией по строке из заголовка и/или текста,
     * обёрнутыми в <code>NoteDto</code>
     *
     * @param filter Фильтр записей. Если не указан, то возвращаются <b>все</b> записи
     * @return Cписок записей
     */
    public List<NoteDto> getNotesAsDto(String filter) {
        List<Note> notes;

        if (filter == null || filter.trim().isEmpty()) {
            notes = notesRepository.findAll();
        } else {
            notes = notesRepository.findByTitleOrTextContaining(filter);
        }

        return notes.stream().
                map(NoteDto::from).
                collect(Collectors.toList());
    }

    /**
     * Получает из БД запись по её идентификатору
     *
     * @param id Идентификатор записи
     * @return Искомая записи
     */
    public Note getNoteById(Long id) {
        return notesRepository.findOne(id);
    }

    /**
     * Сохраняет запись в БД
     *
     * @param note Сохраняемая запись
     */
    public void save(Note note) {
        notesRepository.save(note);
    }

    /**
     * Удаляет запись из базы данных
     *
     * @param note Удаляемая запись
     */
    public void delete(Note note) {
        notesRepository.delete(note);
    }

    /**
     * Возвращает топ-5 слов по заметкам в указанном интервале времени <b>создания</b>
     *
     * @param startDate Начало интервала
     * @param endDate   Конец интервала
     * @return Массив из наиболее встречающихся слов в заметках в рамках указанного временного периода
     */
    public List<String> getTop5Words(Date startDate, Date endDate) {
        List<Note> notes = notesRepository.findByCreatedDateBetween(startDate, endDate);
        if (notes == null || notes.isEmpty()) throw new ApiException(ApiException.Type.NOTES_NOT_FOUND);

        List<String> words = notes.stream().
                map(n -> Arrays.asList(
                        n.getTitle().split("\\s+"),
                        n.getText().split("\\s+")
                )).
                flatMap(Collection::stream).
                map(Arrays::asList).
                flatMap(Collection::stream).
                filter(s -> !s.trim().isEmpty()).
                map(s -> s.replaceAll("[.,;:]+", "")).
                collect(Collectors.toList());

        Map<String, Integer> freqs = new HashMap<>();
        for (String s : words) {
            if (!freqs.containsKey(s)) freqs.put(s, 0);
            freqs.put(s, freqs.get(s) + 1);
        }

        return freqs.entrySet().stream().
                sorted((e1, e2) -> {
                    if (e1.getValue().equals(e2.getValue())) {
                        return e1.getKey().compareTo(e2.getKey());
                    } else return e2.getValue().compareTo(e1.getValue());
                }).
                limit(5).
                map(Map.Entry::getKey).
                collect(Collectors.toList());
    }

    public void setNotesRepository(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }
}
