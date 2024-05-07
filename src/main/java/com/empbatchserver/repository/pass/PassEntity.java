package com.empbatchserver.repository.pass;

import com.empbatchserver.repository.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "pass")
public class PassEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer passSeq;

    @Column(nullable = false)
    private Integer packageSeq;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    private PassStatus status;

    @Column(nullable = false)
    private Integer remainingCount;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime expiredAt;

    public static PassEntity of(BulkPassEntity bulkPassEntity, String userId) {
        return PassEntity.builder()
                .packageSeq(bulkPassEntity.getPackageSeq())
                .userId(userId)
                .status(PassStatus.READY)
                .remainingCount(bulkPassEntity.getCount())
                .startedAt(bulkPassEntity.getStartedAt())
                .endedAt(bulkPassEntity.getEndedAt())
                .build();
    }

    public void updateExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public void updateStatus(PassStatus status) {
        this.status = status;
    }
}
