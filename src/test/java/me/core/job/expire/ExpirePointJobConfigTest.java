package me.core.job.expire;

import me.core.batch.BatchTestSupport;
import me.core.point.Point;
import me.core.point.PointRepository;
import me.core.point.wallet.PointWallet;
import me.core.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

class ExpirePointJobConfigTest extends BatchTestSupport {

    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    Job expirePointJob;
    @Test
    void expirePointJob() throws Exception{
        LocalDate earnDate = LocalDate.of(2021, 1, 1);
        LocalDate expireDate = LocalDate.of(2021, 1, 3);
        PointWallet pointWallet = new PointWallet("testUserId", BigInteger.valueOf(6000));
        pointWalletRepository.save(pointWallet);
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));

        //w
        JobParameters today = new JobParametersBuilder().addString("today", "2021-01-04")
                                                        .toJobParameters();
        //t
        JobExecution jobExecution = launchJob(expirePointJob, today);
        assertEquals(jobExecution.getExitStatus(),ExitStatus.COMPLETED);
        List<Point> all = pointRepository.findAll();

        assertEquals(all.stream()
                        .filter(Point::isExpired)
                        .count(), 3);
        PointWallet changedPointWallet = pointWalletRepository.findById(pointWallet.getId())
                                                        .orElseGet(null);
        then(changedPointWallet).isNotNull();
        then(changedPointWallet.getAmount()).isEqualByComparingTo(BigInteger.valueOf(3000));

    }
}