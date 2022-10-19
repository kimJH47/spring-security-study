package me.core.job.message;


import antlr.debug.MessageAdapter;
import me.core.job.listener.InputExpiredPointAlarmCriteriaDateStepListener;
import me.core.message.Message;
import me.core.message.MessageRepository;
import me.core.point.ExpiredPointSummary;
import me.core.point.Point;
import me.core.point.PointRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class MessageExpirePointStepConfig {

    @Bean
    @JobScope
    public Step messageExpirePointStep(StepBuilderFactory stepBuilderFactory,
                                       PlatformTransactionManager platformTransactionManager,
                                       RepositoryItemReader<ExpiredPointSummary> messageExpirePointItemReader,
                                       ItemProcessor<ExpiredPointSummary, Message> messageExpirePointItemProcessor,
                                       JpaItemWriter<Message> messageExpirePointStepItemWriter,
                                       InputExpiredPointAlarmCriteriaDateStepListener listener) {

        return stepBuilderFactory.get("messageExpirePointStep")
                                 .allowStartIfComplete(true)
                                 .transactionManager(platformTransactionManager)
                                 .listener(listener)
                                 .<ExpiredPointSummary, Message>chunk(1000)
                                 .reader(messageExpirePointItemReader)
                                 .processor(messageExpirePointItemProcessor)
                                 .writer(messageExpirePointStepItemWriter)
                                 .build();

    }

    @Bean
    @StepScope
    public RepositoryItemReader<ExpiredPointSummary> messageExpirePointItemReader(PointRepository pointRepository,
                                                                                  @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
                                                                                  LocalDate alarmCriteriaDate
    ) {

        return new RepositoryItemReaderBuilder<ExpiredPointSummary>()
                .name("messageExpirePointItemReader")
                .repository(pointRepository)
                .methodName("sumByExpiredDate")
                .pageSize(1000)
                .arguments(alarmCriteriaDate)
                .sorts(Map.of("pointWallet", Sort.Direction.ASC))
                .build();

    }

    @Bean
    @StepScope
    public ItemProcessor<ExpiredPointSummary, Message> messageExpirePointItemProcessor( @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
                                                                                            LocalDate alarmCriteriaDate) {
        return pointSummary -> Message.expiredPointMessageInstance(pointSummary,alarmCriteriaDate);
    }

    @Bean
    @StepScope
    public JpaItemWriter<Message> messageExpirePointStepItemWriter(EntityManagerFactory entityManagerFactory) {

        JpaItemWriter<Message> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

}
