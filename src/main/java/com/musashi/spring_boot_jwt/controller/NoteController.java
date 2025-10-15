package com.musashi.spring_boot_jwt.controller;

import com.musashi.spring_boot_jwt.model.Note;
import com.musashi.spring_boot_jwt.model.NoteRequest;
import com.musashi.spring_boot_jwt.model.NoteResponse;
import com.musashi.spring_boot_jwt.model.User;
import com.musashi.spring_boot_jwt.service.NoteService;
import com.musashi.spring_boot_jwt.service.UserService;
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
    private UserService userService;

    @Autowired
    public NoteController(NoteService theNoteService, UserService theUserService){
        this.noteService = theNoteService;
        this.userService = theUserService;
    }

    @GetMapping("/home")
    public String showUserHomePage(Model theModel){
        String ownerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userService.findById(new ObjectId(ownerId));
        List<NoteResponse> notes = noteService.findByOwnerId(ownerId);
        theModel.addAttribute("notes", notes);
        theModel.addAttribute("user", user);
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
        if(note.getId().isEmpty()) {
            note.setId(null);
        }
        noteService.save(note);
        return "redirect:/user/home";
    }

    @GetMapping("/performDeleteNote")
    public String deleteById(@RequestParam("noteId") String noteId){
        noteService.deleteById(noteId);
        return "redirect:/user/home";
    }

    @GetMapping("/showFormForEdit")
    public String editNoteById(@RequestParam("noteId") String noteId, Model theModel){
        Note note = noteService.findById(new ObjectId(noteId));
        theModel.addAttribute("note",note);
        return "add-note-form";
    }
}
