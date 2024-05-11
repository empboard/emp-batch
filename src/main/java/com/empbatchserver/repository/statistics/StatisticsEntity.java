package com.empbatchserver.repository.statistics;

import com.empbatchserver.repository.booking.BookingEntity;
import com.empbatchserver.repository.booking.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @ToString @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "statistics")
public class StatisticsEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statisticsSeq;
    private LocalDateTime statisticsAt;

    private int allCount;
    private int attendedCount;
    private int cancelledCount;

    public static StatisticsEntity of(BookingEntity bookingEntity) {
        return StatisticsEntity.builder()
                .statisticsAt(bookingEntity.getStatisticsAt())
                .allCount(1)
                .attendedCount(bookingEntity.isAttended() ? 1 : 0)
                .cancelledCount(BookingStatus.CANCELLED.equals(bookingEntity.getStatus()) ? 1 : 0)
                .build();
    }

    public void add(final BookingEntity bookingEntity) {
        if (bookingEntity.isAttended()) this.attendedCount++;
        if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) this.cancelledCount++;
        this.allCount++;
    }
}
