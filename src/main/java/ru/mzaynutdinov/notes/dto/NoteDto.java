package ru.mzaynutdinov.notes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import ru.mzaynutdinov.notes.entity.Note;

import java.text.SimpleDateFormat;

/**
 * Обёртка сущности <code>Note</code> для отправки в формате JSON пользователю
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteDto {
    private static final SimpleDateFormat humanDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private Long id;
    private String title;
    private String text;
    private String createdDate;
    private String lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "NoteDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                '}';
    }

    public static NoteDto from(Note n) {
        NoteDto dto = new NoteDto();

        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setText(n.getText());
        dto.setCreatedDate(humanDateFormat.format(n.getCreatedDate()));
        dto.setLastModifiedDate(humanDateFormat.format(n.getLastModifiedDate()));

        return dto;
    }
}
