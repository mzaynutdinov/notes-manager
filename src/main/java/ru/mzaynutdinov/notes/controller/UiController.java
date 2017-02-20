package ru.mzaynutdinov.notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mzaynutdinov.notes.logic.NotesLogic;

import java.util.Map;

@Controller
public class UiController {
    @Autowired
    NotesLogic notesLogic;

    /**
     * Показывает главную страницу
     */
    @GetMapping("/")
    public String mainPage(Map<String, Object> model) {
        model.put("notes", notesLogic.getNotesAsDto(null));

        return "index";
    }
}
