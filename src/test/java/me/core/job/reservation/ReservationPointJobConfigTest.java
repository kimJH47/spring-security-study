package me.core.job.reservation;

import me.core.batch.BatchTestSupport;
import me.core.point.Point;
import me.core.point.PointRepository;
import me.core.point.reservation.Reservation;
import me.core.point.reservation.ReservationRepository;
import me.core.point.wallet.PointWallet;
import me.core.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import static org.assertj.core.api.BDDAssertions.then;

class ReservationPointJobConfigTest extends BatchTestSupport {
    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    Job reservationPointJob;
    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void reservationPointJobTest() throws Exception{
        //g
        LocalDate earnDate = LocalDate.of(2021, 1, 1);
        PointWallet pointWallet = new PointWallet("testUserId", BigInteger.valueOf(6000));
        pointWalletRepository.save(pointWallet);
        reservationRepository.save(new Reservation(pointWallet, BigInteger.valueOf(1000), earnDate, 10));
        reservationRepository.save(new Reservation(pointWallet, BigInteger.valueOf(2000), earnDate, 9));
        reservationRepository.save(new Reservation(pointWallet, BigInteger.valueOf(3000), earnDate, 5));
        JobParameters today = new JobParametersBuilder()
                .addString("today", "2021-01-11")
                .toJobParameters();
        //w
        JobExecution jobExecution = launchJob(reservationPointJob, today);

        //t
        then(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        PointWallet changeWallet = pointWalletRepository.findById(pointWallet.getId())
                                                        .orElseGet(null);

        then(changeWallet.getAmount()).isEqualByComparingTo(BigInteger.valueOf(12000));
        then(changeWallet).isNotNull();
        then(pointRepository.findByPointWallet(changeWallet)
                            .stream()
                            .filter(point -> !point.isExpired())
                            .map(Point::getAmount)
                            .count()).isEqualByComparingTo(3L);



    }

}