package com.tujuhsembilan.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.example.controller.dto.NoteDto;
import com.tujuhsembilan.example.model.Note;
import com.tujuhsembilan.example.repository.NoteRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NoteService {
    private final NoteRepo noteRepo;
    private final JwtDecoder jwtDecoder;

    public List<Note> findAll(String token) {
        return noteRepo.findByCreatedBy(jwtDecoder.decode(token).getSubject());
    }

    public Note saveNote(NoteDto request, String token) {
        return noteRepo.save(Note.builder()
                .content(request.getContent())
                .createdBy(jwtDecoder.decode(token).getSubject())
                .build());
    }

}
