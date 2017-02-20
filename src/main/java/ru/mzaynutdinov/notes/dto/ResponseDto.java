package ru.mzaynutdinov.notes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Объект для отправки ответа API на запрос пользователю в формате JSON
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {
    private String status;
    private ErrorDto error;
    private NoteDto note;
    private TopDto top;

    public ResponseDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NoteDto getNote() {
        return note;
    }

    public void setNote(NoteDto note) {
        this.note = note;
    }

    public ErrorDto getError() {
        return error;
    }

    public void setError(ErrorDto error) {
        this.error = error;
    }

    public TopDto getTop() {
        return top;
    }

    public void setTop(TopDto top) {
        this.top = top;
    }

    private static class ErrorDto {
        private int code;
        private String name;

        public ErrorDto(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class TopDto {
        private String startDate;
        private String endDate;
        private List<String> list;

        public TopDto(String startDate, String endDate, List<String> list) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.list = list;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }

    /**
     * @return Готовый объект ResponseDto для сообщения об успешной операции
     */
    public static ResponseDto success() {
        return new ResponseDto("success");
    }

    /**
     * @return Готовый объект ResponseDto для сообщения об успешной операции вместе с записью
     */
    public static ResponseDto success(NoteDto note) {
        ResponseDto dto = success();
        dto.setNote(note);
        return dto;
    }

    /**
     * @param code Код ошибки
     * @param name Кодовое название ошибки
     * @return Готовый объект ResponseDto для сообщения об ошибке
     */
    public static ResponseDto error(int code, String name) {
        ResponseDto dto = new ResponseDto("error");
        dto.setError(new ErrorDto(code, name));
        return dto;
    }
}
