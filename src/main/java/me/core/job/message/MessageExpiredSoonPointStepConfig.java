package me.core.job.message;


import me.core.job.listener.InputExpireSoonPointAlarmCriteriaDateStepListener;
import me.core.message.Message;
import me.core.point.ExpiredPointSummary;
import me.core.point.PointRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class MessageExpiredSoonPointStepConfig {


    @JobScope
    @Bean
    public Step messageExpiredSoonPointStep(StepBuilderFactory stepBuilderFactory,
                                            PlatformTransactionManager transactionManager,
                                            RepositoryItemReader<ExpiredPointSummary> messageExpiredSoonPointItemReader,
                                            ItemProcessor<ExpiredPointSummary,Message> messageExpiredSoonPointProcessor,
                                            JpaItemWriter<Message> messageExpiredSoonPointWriter,
                                            InputExpireSoonPointAlarmCriteriaDateStepListener listener
                                            ) {

        return stepBuilderFactory.get("messageExpiredSoonPointStep")
                                 .listener(listener)
                                 .transactionManager(transactionManager)
                                 .allowStartIfComplete(true)
                                 .<ExpiredPointSummary,Message>chunk(1000)
                                 .reader(messageExpiredSoonPointItemReader)
                                 .processor(messageExpiredSoonPointProcessor)
                                 .writer(messageExpiredSoonPointWriter)
                                 .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<ExpiredPointSummary> messageExpiredSoonPointItemReader(PointRepository pointRepository,
                                                                                       @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
                                                                                       LocalDate alarmCriteriaDate) {
        return new RepositoryItemReaderBuilder<ExpiredPointSummary>().repository(pointRepository)
                                                                     .name("messageExpiredSoonPointItemReader")
                                                                     .arguments(alarmCriteriaDate)
                                                                     .methodName("sumByExpiredSoonDate")
                                                                     .pageSize(1000)
                                                                     .sorts(Map.of("pointWallet", Sort.Direction.ASC))
                                                                     .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<ExpiredPointSummary,Message> messageExpiredSoonPointProcessor( @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
                                                                                            LocalDate alarmCriteriaDate) {
        return pointSummary -> Message.expiredSoonPointMessageInstance(pointSummary, alarmCriteriaDate);
    }
    @StepScope
    @Bean
    public JpaItemWriter<Message> messageExpiredSoonPointWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Message> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }


}
