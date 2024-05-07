package com.empbatchserver.repository.notification;

import com.empbatchserver.repository.BaseEntity;
import com.empbatchserver.repository.booking.BookingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class NotificationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationSeq;
    private String uuid;
    private NotificationEvent event;
    private String text;
    private boolean sent;
    private LocalDateTime sentAt;

    public static NotificationEntity of(BookingEntity bookingEntity, NotificationEvent notificationEvent, String text) {
        return NotificationEntity.builder()
                .uuid(bookingEntity.getUserEntity().getUuid())
                .event(notificationEvent)
                .text(text)
                .build();
    }

    public void updateSent(boolean sent) {
        this.sent = sent;
    }

    public void updateSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
