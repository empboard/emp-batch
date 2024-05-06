package com.empbatchserver.repository.pass;

import com.empbatchserver.repository.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @ToString @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "bulk_pass")
public class BulkPassEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bulkPassSeq;

    private Integer packageSeq;

    private String userGroupId;

    @Enumerated(EnumType.STRING)
    private BulkPassStatus status;
    private Integer count;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public void updateStatus(BulkPassStatus bulkPassStatus) {
        this.status = bulkPassStatus;
    }
}
