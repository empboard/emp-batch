package com.empbatchserver.job.pass;

import com.empbatchserver.repository.booking.BookingEntity;
import com.empbatchserver.repository.booking.BookingRepository;
import com.empbatchserver.repository.booking.BookingStatus;
import com.empbatchserver.repository.pass.PassEntity;
import com.empbatchserver.repository.pass.PassRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Future;

public class UsePassesJobConfig {
    private final int CHUNK_SIZE = 5;
    private final JobRepository jobRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;
    private final PassRepository passRepository;
    private final BookingRepository bookingRepository;

    public UsePassesJobConfig(JobRepository jobRepository, EntityManagerFactory entityManagerFactory, PlatformTransactionManager transactionManager, PassRepository passRepository, BookingRepository bookingRepository) {
        this.jobRepository = jobRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.transactionManager = transactionManager;
        this.passRepository = passRepository;
        this.bookingRepository = bookingRepository;
    }

    @Bean
    public Job usePassesJob() {
        return new JobBuilder("usePassesJob", jobRepository)
                .start(usePassesStep())
                .build();
    }

    @Bean
    public Step usePassesStep() {
        return new StepBuilder("usePassesStep", jobRepository)
                .<BookingEntity, Future<BookingEntity>>chunk(CHUNK_SIZE, transactionManager)
                .reader(usePassesItemReader())
                .processor(usePassesAsyncItemProcessor())
                .writer(usePassesAsyncItemWriter())
                .build();
    }

    @Bean
    public JpaCursorItemReader<BookingEntity> usePassesItemReader() {
        return new JpaCursorItemReaderBuilder<BookingEntity>()
                .name("usePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT b from BookingEntity b JOIN FETCH b.passEntity WHERE b.status = :status AND b.usedPass = false AND b.endedAt < :endedAt")
                .parameterValues(Map.of("status", BookingStatus.COMPLETED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public AsyncItemProcessor<BookingEntity, BookingEntity> usePassesAsyncItemProcessor() {
        AsyncItemProcessor<BookingEntity, BookingEntity> asyncItemProcessor =
                new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(usePassesItemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return asyncItemProcessor;
    }

    @Bean
    public ItemProcessor<BookingEntity, BookingEntity> usePassesItemProcessor() {
        return bookingEntity -> {
            PassEntity passEntity = bookingEntity.getPassEntity();
            passEntity.updateRemainingCount(passEntity.getRemainingCount() - 1);
            bookingEntity.updatePassEntity(passEntity);
            bookingEntity.updateUsedPass(true);

            return bookingEntity;
        };
    }

    @Bean
    public AsyncItemWriter<BookingEntity> usePassesAsyncItemWriter() {
        AsyncItemWriter<BookingEntity> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(usePassesItemWriter());

        return asyncItemWriter;
    }

    @Bean
    public ItemWriter<BookingEntity> usePassesItemWriter() {
        return bookingEntities -> {
            for (BookingEntity bookingEntity : bookingEntities) {
                int updatedCount = passRepository.updateRemainingCount(
                        bookingEntity.getPassSeq(),
                        bookingEntity.getPassEntity().getRemainingCount()
                );

                if (updatedCount > 0) {
                    bookingRepository.updateUsedPass(bookingEntity.getPassSeq(), bookingEntity.isUsedPass());
                }
            }
        };
    }
}
