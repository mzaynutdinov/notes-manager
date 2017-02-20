package ru.mzaynutdinov.notes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mzaynutdinov.notes.entity.Note;

import java.util.Date;
import java.util.List;

public interface NotesRepository extends JpaRepository<Note, Long> {
    /**
     * Ищет заметки, которые содержат <code>text</code> либо в заголовке, либо в тексте
     *
     * @param text Искомый текст
     * @return Заметки, подходящие условию поиска
     */
    @Query(
            "         from Note n" +
                    " where " +
                    "       lower(n.title) like lower(concat('%', :text, '%')) or" +
                    "       lower(n.text) like lower(concat('%', :text, '%'))"
    )
    List<Note> findByTitleOrTextContaining(@Param("text") String text);

    /**
     * Ищет заметки, созданные между <code>startDate</code> (включительно) и
     * <code>endDate</code> (включительно)
     *
     * @param startDate Начало интервала
     * @param endDate   Конец интервала
     * @return Заметки, подходящие условию поиска
     */
    List<Note> findByCreatedDateBetween(Date startDate, Date endDate);
}
