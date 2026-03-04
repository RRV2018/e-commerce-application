package org.omsoft.retail.notification.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omsoft.retail.notification.dto.NotificationRequest;
import org.omsoft.retail.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        notificationController = new NotificationController(notificationService);
    }

    @Test
    void send_callsServiceAndReturnsOk() {
        NotificationRequest request = new NotificationRequest();
        request.setTo("user@example.com");
        request.setSubject("Test");
        request.setMessage("Hello");

        ResponseEntity<String> response = notificationController.send(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("success"));
        verify(notificationService).send(any(NotificationRequest.class));
    }
}
