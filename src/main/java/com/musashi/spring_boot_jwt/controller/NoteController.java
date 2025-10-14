package com.musashi.spring_boot_jwt.controller;

import com.musashi.spring_boot_jwt.model.Note;
import com.musashi.spring_boot_jwt.model.NoteRequest;
import com.musashi.spring_boot_jwt.model.NoteResponse;
import com.musashi.spring_boot_jwt.service.NoteService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class NoteController {

    private NoteService noteService;

    @Autowired
    public NoteController(NoteService theNoteService){
        this.noteService = theNoteService;
    }

    @GetMapping("/home")
    public String showUserHomePage(Model theModel){
        String ownerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<NoteResponse> notes = noteService.findByOwnerId(ownerId);
        theModel.addAttribute("notes", notes);
        return "home";
    }

    @GetMapping("/addNoteForm")
    public String showAddNoteForm(Model theModel){
        NoteRequest note = new NoteRequest();
        theModel.addAttribute("note", note);
        return "add-note-form";
    }

    @PostMapping("/performAddNote")
    public String addNote(@ModelAttribute("note") NoteRequest note){
        note.setId(null);
        noteService.save(note);
        return "redirect:/user/home";
    }

    @GetMapping("/performDeleteNote")
    public String deleteById(@RequestParam("noteId") String noteId){
//        Note note = noteService.findById(new ObjectId(id));
//        if(note == null){
//            throw new IllegalArgumentException("Note not found!");
//        }
//        String ownerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        if(note.getOwnerId().toHexString().equals(ownerId)) {
//            noteService.deleteById(id);
//        }
        noteService.deleteById(noteId);
        return "redirect:/user/home";
    }
}
