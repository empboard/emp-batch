package com.empbatchserver.job.pass;

import com.empbatchserver.repository.pass.PassEntity;
import com.empbatchserver.repository.pass.PassStatus;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class ExpirePassesJobConfig {
    private final int CHUNK_SIZE = 5;

    // v4: JobBuilderFactory와 StepBuilderFactory는 @EnableBatchProcessing에서 제공
    // v5: 위 Factory들이 Deprecated, JobBuilder와 StepBuilder 사용, PlatformTransactionManager 주입 받아 직접 JobRepository 구성
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    public ExpirePassesJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Bean
    public Job expirePassesJob() {
        return new JobBuilder("expirePassesJob", jobRepository)
                .start(expirePassesStep())
                .build();
    }

    @Bean
    public Step expirePassesStep() {
        return new StepBuilder("expirePassesStep", jobRepository)
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE, transactionManager)
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassesItemWriter())
                .build();
    }

    /*
     * JpaCursorItemReader: JpaPagingItemReader만 지원하다가 Spring 4.3에서 추가
     * 페이징 기법보다 높은 성능, 데이터 변경에 무관한 무결성 조회 가능
     */
    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p from PassEntity p where p.status = :status AND p.endedAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return passEntity -> {
            passEntity.updateStatus(PassStatus.EXPIRED);
            passEntity.updateExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }

    // JpaItemWriter: JPA 영속성 관리를 위해 EntityManager를 필수로 설정해야 한다.
    @Bean
    public JpaItemWriter<PassEntity> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
