package me.core.job.reservation;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationPointJobConfig {


    @Bean
    public Job reservationPointJob(JobBuilderFactory jobBuilderFactory, Step reservationPointStep) {
        return jobBuilderFactory.get("reservationPointJob")
                                .start(reservationPointStep)
                                .build();
    }
}
