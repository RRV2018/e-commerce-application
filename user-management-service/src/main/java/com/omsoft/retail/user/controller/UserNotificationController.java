package com.omsoft.retail.user.controller;

import com.omsoft.retail.user.dto.UserNotificationResponse;
import com.omsoft.retail.user.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Notifications", description = "In-app user notifications")
@RestController
@RequestMapping("/api/user/notifications")
public class UserNotificationController {

    private final UserNotificationService notificationService;

    public UserNotificationController(UserNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get my notifications")
    @GetMapping
    public List<UserNotificationResponse> getNotifications(@RequestHeader("X-User-Id") String userId) {
        return notificationService.getNotifications(userId);
    }

    @Operation(summary = "Get unread count")
    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(@RequestHeader("X-User-Id") String userId) {
        return Map.of("count", notificationService.getUnreadCount(userId));
    }

    @Operation(summary = "Mark notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<UserNotificationResponse> markAsRead(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(userId, id));
    }
}
