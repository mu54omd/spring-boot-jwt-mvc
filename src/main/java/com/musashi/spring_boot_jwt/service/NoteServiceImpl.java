package com.musashi.spring_boot_jwt.service;

import com.musashi.spring_boot_jwt.model.Note;
import com.musashi.spring_boot_jwt.model.NoteRequest;
import com.musashi.spring_boot_jwt.model.NoteResponse;
import com.musashi.spring_boot_jwt.repository.NoteRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService{

    private NoteRepository noteRepository;

    @Autowired
    public NoteServiceImpl(NoteRepository theNoteRepository){
        this.noteRepository = theNoteRepository;
    }
    @Override
    public NoteResponse save(NoteRequest body) {
        String ownerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        ObjectId noteId = null;
        if(body.getId() == null){
            noteId = ObjectId.get();
        }else{
            noteId = new ObjectId(body.getId());
        }
        Note theNote = new Note(
                noteId,
                new ObjectId(ownerId),
                body.getTitle(),
                body.getContent(),
                body.getColor(),
                Instant.now()
        );
        Note note = noteRepository.save(theNote);
        return new NoteResponse(
                note.getId().toHexString(),
                note.getTitle(),
                note.getContent(),
                note.getColor(),
                note.getCreatedAt()
        );
    }

    @Override
    public Note findById(ObjectId id) {
        Optional<Note> note  = noteRepository.findById(id);
        return note.orElse(null);
    }

    @Override
    public List<NoteResponse> findByOwnerId(String ownerId) {
        return noteRepository
                .findByOwnerId(new ObjectId(ownerId))
                .stream()
                .map(note ->
                        new NoteResponse(
                            note.getId().toHexString(),
                            note.getTitle(),
                            note.getContent(),
                            note.getColor(),
                            note.getCreatedAt())
                ).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        noteRepository.deleteById(new ObjectId(id));
    }
}
