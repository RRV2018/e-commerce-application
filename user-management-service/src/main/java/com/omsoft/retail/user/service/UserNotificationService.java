package com.omsoft.retail.user.service;

import com.omsoft.retail.user.dto.UserNotificationResponse;
import com.omsoft.retail.user.entiry.UserNotification;
import com.omsoft.retail.user.repository.UserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserNotificationRepository repository;

    @Transactional(readOnly = true)
    public List<UserNotificationResponse> getNotifications(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return repository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public UserNotificationResponse markAsRead(String userId, Long notificationId) {
        UserNotification n = repository.findById(notificationId)
                .filter(notification -> notification.getUserId().equals(userId))
                .orElseThrow();
        n.setRead(true);
        return toResponse(repository.save(n));
    }

    public UserNotification create(String userId, String title, String message) {
        UserNotification n = new UserNotification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setMessage(message);
        return repository.save(n);
    }

    private UserNotificationResponse toResponse(UserNotification n) {
        return new UserNotificationResponse(
                n.getId(),
                n.getTitle(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}
