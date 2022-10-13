package me.core.job.reservation;


import me.core.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationPointJobConfig {


    @Bean
    public Job reservationPointJob(JobBuilderFactory jobBuilderFactory,
                                   Step reservationPointStep,
                                   TodayJobParameterValidator validator) {

        return jobBuilderFactory.get("reservationPointJob")
                                .start(reservationPointStep)
                                .validator(validator)
                                .incrementer(new RunIdIncrementer())
                                .build();
    }
}
