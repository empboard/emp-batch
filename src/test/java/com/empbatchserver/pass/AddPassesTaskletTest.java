package com.empbatchserver.pass;

import com.empbatchserver.job.pass.AddPassesTasklet;
import com.empbatchserver.repository.pass.*;
import com.empbatchserver.repository.user.UserGroupMappingEntity;
import com.empbatchserver.repository.user.UserGroupMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/* Mocking을 사용하여 비즈니스 로직 검증 테스트 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class AddPassesTaskletTest {
    @Mock
    private StepContribution stepContribution;

    @Mock
    private ChunkContext chunkContext;

    @Mock
    private PassRepository passRepository;

    @Mock
    private BulkPassRepository bulkPassRepository;

    @Mock
    private UserGroupMappingRepository userGroupMappingRepository;

    @InjectMocks
    private AddPassesTasklet addPassesTasklet;

    @Test
    public void test_execute() {
        // given
        final String userGroupId = "GROUP";
        final String userId = "A100000";
        final Integer packageSeq = 1;
        final Integer count = 10;
        final LocalDateTime now = LocalDateTime.now();

        final BulkPassEntity bulkPassEntity = BulkPassEntity.builder()
                .packageSeq(packageSeq)
                .userGroupId(userGroupId)
                .status(BulkPassStatus.READY)
                .count(count)
                .startedAt(now)
                .endedAt(now.plusDays(60))
                .build();

        final UserGroupMappingEntity userGroupMappingEntity = UserGroupMappingEntity.builder()
                .userGroupId(userGroupId)
                .userId(userId)
                .build();

        // when
        when(bulkPassRepository.findByStatusAndStartedAtGreaterThan(eq(BulkPassStatus.READY), any())).thenReturn(List.of(bulkPassEntity));
        when(userGroupMappingRepository.findByUserGroupId("GROUP")).thenReturn(List.of(userGroupMappingEntity));
        RepeatStatus repeatStatus = addPassesTasklet.execute(stepContribution, chunkContext);

        // then
        assertEquals(RepeatStatus.FINISHED, repeatStatus);

        // 추가된 PassEntity 값 확인
        ArgumentCaptor<List> passEntitiesCaptor = ArgumentCaptor.forClass(List.class);
        verify(passRepository, times(1)).saveAll(passEntitiesCaptor.capture());
        final List<PassEntity> passEntities = passEntitiesCaptor.getValue();

        assertEquals(1, passEntities.size());

        final PassEntity passEntity = passEntities.get(0);
        assertEquals(packageSeq, passEntity.getPackageSeq());
        assertEquals(userId, passEntity.getUserId());
        assertEquals(PassStatus.READY, passEntity.getStatus());
        assertEquals(count, passEntity.getRemainingCount());
    }
}
