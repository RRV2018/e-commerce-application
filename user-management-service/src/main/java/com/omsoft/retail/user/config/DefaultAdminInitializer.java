package com.omsoft.retail.user.config;

import com.omsoft.retail.user.entiry.User;
import com.omsoft.retail.user.entiry.type.Role;
import com.omsoft.retail.user.repository.UserRepository;
import com.omsoft.retail.user.util.EncryptDecryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs after the application is ready. Ensures a default Admin user exists.
 * Creates it only if missing. Configure via app.default-admin.* or use defaults.
 */
@Component
@Slf4j
public class DefaultAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptDecryptUtil encryptDecryptUtil;

    @Value("${app.default-admin.enabled:true}")
    private boolean enabled;

    @Value("${app.default-admin.email:admin@gmail.com}")
    private String defaultAdminEmail;

    @Value("${app.default-admin.name:Admin}")
    private String defaultAdminName;

    @Value("${app.default-admin.password:admin123}")
    private String defaultAdminPassword;

    public DefaultAdminInitializer(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   EncryptDecryptUtil encryptDecryptUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptDecryptUtil = encryptDecryptUtil;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void ensureDefaultAdminExists() {
        if (!enabled) {
            log.debug("Default admin creation is disabled (app.default-admin.enabled=false)");
            return;
        }
        if (userRepository.findByEmail(defaultAdminEmail).isPresent()) {
            log.debug("Default admin user already exists (email={})", defaultAdminEmail);
            return;
        }
        User admin = User.builder()
                .username(defaultAdminName)
                .email(defaultAdminEmail)
                .password(passwordEncoder.encode(defaultAdminPassword))
                .decryptablePassword(encryptDecryptUtil.encrypt(defaultAdminPassword))
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
        log.info("Default admin user created: email={}", defaultAdminEmail);
    }
}
