package com.mentorhub.session.controller;

import com.mentorhub.common.dto.ApiResponse;
import com.mentorhub.session.dto.BookSessionRequest;
import com.mentorhub.session.entity.MentoringSession;
import com.mentorhub.session.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ApiResponse<MentoringSession> book(@Valid @RequestBody BookSessionRequest request) {
        return ApiResponse.ok(sessionService.book(request));
    }

    @GetMapping
    public ApiResponse<List<MentoringSession>> mySessions() {
        return ApiResponse.ok(sessionService.mySessions());
    }

    @PatchMapping("/{id}/respond")
    public ApiResponse<MentoringSession> respond(
            @PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return ApiResponse.ok(sessionService.respond(id, Boolean.TRUE.equals(body.get("accept"))));
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<MentoringSession> complete(@PathVariable Long id) {
        return ApiResponse.ok(sessionService.complete(id));
    }
}
