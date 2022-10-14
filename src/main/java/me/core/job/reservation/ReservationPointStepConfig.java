package me.core.job.reservation;


import lombok.extern.slf4j.Slf4j;
import me.core.point.Point;
import me.core.point.PointRepository;
import me.core.point.reservation.Reservation;
import me.core.point.reservation.ReservationRepository;
import me.core.point.wallet.PointWalletRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;
@Configuration
@Slf4j
public class ReservationPointStepConfig {

    @Bean
    @JobScope
    public Step reservationPointStep(StepBuilderFactory stepBuilderFactory,
                                     PlatformTransactionManager platformTransactionManager,
                                     JpaPagingItemReader<Reservation> reservationPointItemReader,
                                     ItemProcessor<Reservation, Pair<Point, Reservation>> reservationPointProcessor,
                                     ItemWriter<Pair<Point, Reservation>> reservationPointItemWriter
                                     ) {

        return stepBuilderFactory.get("reservationPointStep")
                                 .allowStartIfComplete(true)
                                 .transactionManager(platformTransactionManager)
                                 .<Reservation, Pair<Point, Reservation>>chunk(1000)
                                 .reader(reservationPointItemReader)
                                 .processor(reservationPointProcessor)
                                 .writer(reservationPointItemWriter)
                                 .build();

    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Reservation> reservationPointItemReader(EntityManagerFactory entityManagerFactory,
                                                                       @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}") LocalDate today) {
        return new JpaPagingItemReaderBuilder<Reservation>().name("reservationPointItemReader")
                                                      .entityManagerFactory(entityManagerFactory)
                                                      .queryString("select r from Reservation r where r.earnedDate <= :today and r.executed = 0")
                                                      .parameterValues(Map.of("today", today))
                                                      .pageSize(1000)
                                                      .build();

    }

    @Bean
    @StepScope
    public ItemProcessor<Reservation, Pair<Point,Reservation>> reservationPointItemProcessor() {
        return reservation -> {
            reservation.execute();
            Point point = Point.ReservationToPoint(reservation);
            point.plusAmountInWallet();

            return Pair.of(point,reservation);

        };
    }

    @Bean
    @StepScope
    public ItemWriter<Pair<Point, Reservation>> reservationPointItemWriter(PointRepository pointRepository,
                                                                      PointWalletRepository pointWalletRepository,
                                                                      ReservationRepository reservationRepository) {

        return reservationsAndPoints -> {

            for (Pair<Point,Reservation> pair:reservationsAndPoints) {
                Reservation reservation = pair.getSecond();
                if (reservation.isExecuted()) {
                    reservationRepository.save(reservation);
                    pointWalletRepository.save(reservation.getPointWallet());
                    pointRepository.save(pair.getFirst());
                }
            }
        };


    }

}
