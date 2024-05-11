package com.empbatchserver.repository.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@AllArgsConstructor
public class AggregatedStatistics {
    private LocalDateTime statisticsAt;
    private long allCount;
    private long attendedCount;
    private long cancelledCount;

    public void merge(final AggregatedStatistics that) {
        this.allCount += that.getAllCount();
        this.attendedCount += that.getAttendedCount();
        this.cancelledCount += that.getCancelledCount();
    }
}
