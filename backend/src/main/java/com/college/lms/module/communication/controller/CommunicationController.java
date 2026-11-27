package com.college.lms.module.communication.controller;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.communication.dto.*;
import com.college.lms.module.communication.service.CommunicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/communication")
public class CommunicationController {

    private final CommunicationService communicationService;

    public CommunicationController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @GetMapping("/courses/{courseId}/threads")
    public ResponseEntity<List<DiscussionThreadDto>> getThreads(@PathVariable Long courseId) {
        return ResponseEntity.ok(communicationService.getCourseThreads(courseId));
    }

    @GetMapping("/threads/{threadId}")
    public ResponseEntity<DiscussionThreadDto> getThreadDetails(@PathVariable Long threadId) {
        return ResponseEntity.ok(communicationService.getThreadDetails(threadId));
    }

    @PostMapping("/courses/{courseId}/threads")
    public ResponseEntity<DiscussionThreadDto> createThread(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        String title = body.getOrDefault("title", "New Discussion");
        String content = body.getOrDefault("content", "");
        return ResponseEntity.ok(communicationService.createThread(courseId, title, content, principal));
    }

    @PostMapping("/threads/{threadId}/replies")
    public ResponseEntity<DiscussionPostDto> addReply(
            @PathVariable Long threadId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal) {
        String content = body.getOrDefault("content", "");
        return ResponseEntity.ok(communicationService.addReply(threadId, content, principal));
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> getNotifications(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(communicationService.getUserNotifications(principal));
    }

    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        communicationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/courses/{courseId}/live-classes")
    public ResponseEntity<List<LiveClassDto>> getLiveClasses(@PathVariable Long courseId) {
        return ResponseEntity.ok(communicationService.getLiveClasses(courseId));
    }
}
