package me.core.job.message;


import me.core.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageExpiredSoonPointJobConfig {

    @Bean
    public Job messageExpiredSoonPointJob(JobBuilderFactory jobBuilderFactory,
                                          Step messageExpiredSoonPointStep,
                                          TodayJobParameterValidator validator) {
        return jobBuilderFactory.get("messageExpiredSoonPointJob")
                                .incrementer(new RunIdIncrementer())
                                .validator(validator)
                                .start(messageExpiredSoonPointStep)
                                .build();
    }
}
