package org.omsoft.retail.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.omsoft.retail.notification.dto.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@Slf4j
public class NotificationService {

    public void send(NotificationRequest request) {
        // For now just log (mock implementation)
        log.info("Sending notification");
        log.info("To: " + request.getTo());
        log.info("Subject: " + request.getSubject());
        log.info("Message: " + request.getMessage());
    }
}
