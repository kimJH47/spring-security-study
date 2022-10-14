package me.core.job.message;

import me.core.batch.BatchTestSupport;
import me.core.message.Message;
import me.core.message.MessageRepository;
import me.core.point.Point;
import me.core.point.PointRepository;
import me.core.point.reservation.Reservation;
import me.core.point.wallet.PointWallet;
import me.core.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;



class MessageExpiredPointJobConfigTest extends BatchTestSupport {

    @Autowired
    Job messageExpiredPointJob;
    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    MessageRepository messageRepository;
    @Test
    public void 포인트완료메시지_발송테스트() throws Exception{
        //given
        LocalDate earnDate = LocalDate.of(2021, 1, 1);
        LocalDate expireDate = LocalDate.of(2021, 1, 3);

        PointWallet pointWallet1 = new PointWallet("testUserId1", BigInteger.ZERO);
        PointWallet pointWallet2 = new PointWallet("testUserId2", BigInteger.valueOf(6000));
        pointWalletRepository.save(pointWallet1);
        pointWalletRepository.save(pointWallet2);

        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate,false,true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(2000), earnDate, expireDate,false,true));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(3000), earnDate, expireDate,false,true));

        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(4000), earnDate, expireDate.plusDays(1)));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(5000), earnDate, expireDate.plusDays(1)));
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(10000), earnDate, expireDate,false,true));

        JobParameters today = new JobParametersBuilder()
                .addString("today", "2021-01-04")
                .toJobParameters();
        //when
        JobExecution jobExecution = launchJob(messageExpiredPointJob, today);
        //then
        then(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        List<Message> messages = messageRepository.findAll();
        then(messages.size()).isEqualTo(2);

    }

}