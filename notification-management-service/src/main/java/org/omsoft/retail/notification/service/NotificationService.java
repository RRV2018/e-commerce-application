package org.omsoft.retail.notification.service;

import org.omsoft.retail.notification.dto.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void send(NotificationRequest request) {
        // For now just log (mock implementation)
        System.out.println("Sending notification");
        System.out.println("To: " + request.getTo());
        System.out.println("Subject: " + request.getSubject());
        System.out.println("Message: " + request.getMessage());
    }
}
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
