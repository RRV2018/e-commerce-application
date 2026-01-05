package org.omsoft.retail.notification.service;

import org.omsoft.retail.notification.dto.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> send(
            @RequestBody NotificationRequest request) {

        service.send(request);
        return ResponseEntity.ok("Notification sent successfully");
    }
}
