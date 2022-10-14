package me.core.job.message;

import me.core.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageExpiredPointJobConfig {

    @Bean
    public Job messageExpiredPointJob(JobBuilderFactory jobBuilderFactory,
                                      Step messageExpirePointStep,
                                      TodayJobParameterValidator validator) {

        return jobBuilderFactory.get("messageExpiredPointJob")
                                .validator(validator)
                                .incrementer(new RunIdIncrementer())
                                .start(messageExpirePointStep)
                                .build();
    }
}
