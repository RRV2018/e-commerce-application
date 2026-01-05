package org.omsoft.retail.notification.service;

import org.omsoft.retail.notification.dto.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
