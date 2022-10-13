package me.core.job.expire;

import me.core.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpirePointJobConfig {
    @Bean
    public Job expirePointJob(JobBuilderFactory jobBuilderFactory,
                              Step expirePointStep,
                              TodayJobParameterValidator validator) {

        return jobBuilderFactory.get("expirePointJob")
                                .validator(validator)
                                .incrementer(new RunIdIncrementer())
                                .start(expirePointStep)
                                .build();
    }
}
