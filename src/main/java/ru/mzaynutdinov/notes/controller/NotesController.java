package ru.mzaynutdinov.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.mzaynutdinov.notes.logic.NotesLogic;
import ru.mzaynutdinov.notes.dto.NoteDto;
import ru.mzaynutdinov.notes.dto.ResponseDto;
import ru.mzaynutdinov.notes.entity.Note;
import ru.mzaynutdinov.notes.utils.ApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NotesController {
    @Autowired
    NotesLogic notesLogic;

    /**
     * Возвращает список записей с (или без) фильтрацией по строке из заголовка и/или текста
     *
     * @param filter Фильтр записей. Если не указан, то возвращаются <b>все</b> записи
     * @return Cписок записей
     */
    @GetMapping
    @ResponseBody
    public List<NoteDto> getAllNotes(@RequestParam(name = "filter", required = false) String filter) {
        return notesLogic.getNotesAsDto(filter);
    }

    /**
     * Возвращает одну запись из БД по её идентификатору
     * <p>
     * Возможные ошибки:
     * <ul>
     * <li><code>1000 NOTE_NOT_FOUND</code> - запись с таким <code>id</code> не найдена в БД</li>
     * </ul>
     *
     * @param id Идентификатор запрашиваемой записи
     * @return Запись с идентифактором <code>id</code>
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseDto getNote(@PathVariable("id") Long id) {
        Note note = notesLogic.getNoteById(id);
        if (note == null) throw new ApiException(ApiException.Type.NOTE_NOT_FOUND);

        return ResponseDto.success(NoteDto.from(note));
    }

    /**
     * Добавляет новую запись в базу данных
     * <p>
     * Возможные ошибки:
     * <ul>
     * <li><code>1000 NOTE_NOT_FOUND</code> - запись с таким <code>id</code> не найдена в БД</li>
     * </ul>
     *
     * @param dto Данные новой записи. Обязательны для заполнения поля <code>title</code> и <code>text</code>
     * @return Информацию об успешной/неуспешной операции и саму заметку с <code>id</code>
     */
    @PostMapping
    @ResponseBody
    public ResponseDto addNewNote(@RequestBody NoteDto dto) {
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setText(dto.getText());
        note.setCreatedDate(new Date());
        note.setLastModifiedDate(note.getCreatedDate());
        notesLogic.save(note);

        return ResponseDto.success(NoteDto.from(note));
    }

    /**
     * Обновляет информацию о записи в базе данных.
     * <p>
     * Возможные ошибки:
     * <ul>
     * <li><code>1000 NOTE_NOT_FOUND</code> - запись с таким <code>id</code> не найдена в БД</li>
     * <li><code>1001 DIFFERENT_NOTES_IDS</code> - значения поля <code>id</code> объекта <code>dto</code> не совпадает
     * со значением одноимённого параметра пути запроса (в случае, если поле <code>id</code> заполнено)</li>
     * </ul>
     *
     * @param id  Идентификатор редактируемой записи
     * @param dto Новые данные записи
     * @return Информацию об успешной/неуспешной операции и саму заметку
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseDto editNote(@PathVariable("id") Long id, @RequestBody NoteDto dto) {
        Note note = notesLogic.getNoteById(id);
        if (note == null) throw new ApiException(ApiException.Type.NOTE_NOT_FOUND);
        if (dto.getId() != null && !dto.getId().equals(id))
            throw new ApiException(ApiException.Type.DIFFERENT_NOTES_IDS);

        note.setTitle(dto.getTitle());
        note.setText(dto.getText());
        note.setLastModifiedDate(new Date());
        notesLogic.save(note);

        return ResponseDto.success(NoteDto.from(note));
    }

    /**
     * Удаляет запись из базы данных
     *
     * @param id Идентификатор удаляемой записи
     * @return Информацию об успешной/неуспешной операции
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseDto deleteNote(@PathVariable("id") Long id) {
        Note note = notesLogic.getNoteById(id);
        if (note == null) throw new ApiException(ApiException.Type.NOTE_NOT_FOUND);
        notesLogic.delete(note);

        return ResponseDto.success();
    }

    /**
     * Возвращает топ-5 слов по заметкам в указанном интервале времени <b>создания</b>
     *
     * @param startDateStr Начало временного интервала. Если не указано, то поиск ведётся без ограничения даты начала
     * @param endDateStr   Конец временного интервала. Если не указано, то поиск ведётся по сегодняшний день
     * @return Информацию о топ-5 заметках с указанием временного интервала
     */
    @GetMapping("/top5")
    @ResponseBody
    public ResponseDto getTop5Words(@RequestParam(name = "start", required = false) String startDateStr, @RequestParam(name = "end", required = false) String endDateStr) {
        Date startDate = new Date(0);
        Date endDate = new Date();

        SimpleDateFormat sdfFull = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        SimpleDateFormat sdfShort = new SimpleDateFormat("dd.MM.yyyy");

        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            try {
                startDate = sdfFull.parse(startDateStr);
            } catch (ParseException e) {
                try {
                    startDate = sdfShort.parse(startDateStr);
                } catch (ParseException e1) {
                    throw new ApiException(ApiException.Type.INCORRECT_START_DATE_FORMAT);
                }
            }
        }

        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                endDate = sdfFull.parse(endDateStr);
            } catch (ParseException e) {
                try {
                    endDate = sdfShort.parse(endDateStr);
                } catch (ParseException e1) {
                    throw new ApiException(ApiException.Type.INCORRECT_END_DATE_FORMAT);
                }
            }
        }

        List<String> words = notesLogic.getTop5Words(startDate, endDate);

        ResponseDto dto = ResponseDto.success();
        dto.setTop(new ResponseDto.TopDto(
                sdfFull.format(startDate),
                sdfFull.format(endDate),
                words
        ));

        return dto;
    }
}
