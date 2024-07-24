package com.tujuhsembilan.example.controller;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tujuhsembilan.example.controller.dto.NoteDto;
import com.tujuhsembilan.example.repository.NoteRepo;
import com.tujuhsembilan.example.service.NoteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

  private final NoteRepo repo;

  private final ModelMapper mdlMap;

  private final NoteService noteService;

  @GetMapping
  public Object getNotes(HttpServletRequest request) {
    return ResponseEntity
        .ok(noteService.findAll(request.getHeader("Authorization").substring(7))
            .stream()
            .map(o -> mdlMap.map(o, NoteDto.class))
            .collect(Collectors.toSet()));
  }

  @PostMapping
  public Object saveNote(HttpServletRequest request, @RequestBody NoteDto body) {
    return ResponseEntity.status(HttpStatus.CREATED).body(noteService.saveNote(body,
        request.getHeader("Authorization").substring(7)));
  }

  @GetMapping("/admin")
  @PreAuthorize("hasAuthority('ADMIN')")
  public Object getNotesAdmin() {
    return ResponseEntity
        .ok(repo.findAll()
            .stream()
            .map(o -> mdlMap.map(o, NoteDto.class))
            .collect(Collectors.toSet()));
  }

}
