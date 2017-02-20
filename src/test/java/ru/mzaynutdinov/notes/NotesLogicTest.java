package ru.mzaynutdinov.notes;

import org.junit.Before;
import org.junit.Test;
import ru.mzaynutdinov.notes.dao.NotesRepository;
import ru.mzaynutdinov.notes.entity.Note;
import ru.mzaynutdinov.notes.logic.NotesLogic;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class NotesLogicTest {
    private NotesRepository dao;
    private NotesLogic logic;

    @Before
    public void init() {
        dao = mock(NotesRepository.class);
        logic = new NotesLogic();
        logic.setNotesRepository(dao);
    }

    /**
     * Проверка на то, что без указания фильтра метод getNotesAsDto использует только findAll()
     */
    @Test
    public void getNotesAsDto_noFilterTest() {
        logic.getNotesAsDto(null);
        logic.getNotesAsDto("");
        logic.getNotesAsDto(" ");
        verify(dao, times(3)).findAll();
        verify(dao, never()).findByTitleOrTextContaining(anyString());
    }

    /**
     * Проверка на то, что при указании фильтра метод getNotesAsDto использует только findByTitleOrTextContaining()
     */
    @Test
    public void getNotesAsDto_withFilterTest() {
        String uuid = UUID.randomUUID().toString();
        logic.getNotesAsDto(uuid);
        verify(dao, never()).findAll();
        verify(dao, times(1)).findByTitleOrTextContaining(uuid);
    }

    /**
     * Проверка метода, возвращающего 5 самых встречаемых слов в записях
     */
    @Test
    public void getTop5Notes_moreThanFiveWords() {
        Date d = new Date();
        List<Note> notes = new ArrayList<>(25);
        for (int i = 0; i < 10; i++) {
            notes.add(new Note("aaa bbb", "ccc ddd eee fff"));
            notes.add(new Note("gg hh ii", "jj kk ll mm nn oo pp"));
        }

        notes.add(new Note("aaa bbb ccc ii", "ee bbb ffff"));

        when(dao.findByCreatedDateBetween(d, d)).thenReturn(notes);

        List<String> words = logic.getTop5Words(d, d);
        assertEquals(
                "Полученный список топ-5 отличается от ожидаемого",
                Arrays.asList("bbb", "aaa", "ccc", "ii", "ddd"),
                words
        );
    }
}
