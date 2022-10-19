package me.core.job.message;

import me.core.batch.BatchTestSupport;
import me.core.message.Message;
import me.core.message.MessageRepository;
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

class MessageExpiredSoonPointJobConfigTest extends BatchTestSupport {

    @Autowired
    Job messageExpiredSoonPointJob;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    MessageRepository messageRepository;

    @Test
    public void 포인트만료예정_메시지_배치_테스트() throws Exception{
        //given
        LocalDate earnDate = LocalDate.of(2021, 1, 1);
        LocalDate expireDate = LocalDate.of(2021, 1, 20);

        PointWallet pointWallet1 = new PointWallet("testUserId1", BigInteger.ZERO);
        PointWallet pointWallet2 = new PointWallet("testUserId2", BigInteger.valueOf(6000));
        pointWalletRepository.save(pointWallet1);
        pointWalletRepository.save(pointWallet2);

        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(1000), earnDate, expireDate,false,false));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(2000), earnDate, expireDate,false,false));
        pointRepository.save(new Point(pointWallet1, BigInteger.valueOf(3000), earnDate, expireDate,false,false));

        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(4000), earnDate, expireDate.plusDays(10))); //만료예정포인트 x
        pointRepository.save(new Point(pointWallet2, BigInteger.valueOf(10000), earnDate, expireDate,false,false));
        JobParameters today = new JobParametersBuilder().addString("today", "2021-01-17")
                                                        .toJobParameters();

        //when
        JobExecution jobExecution = launchJob(messageExpiredSoonPointJob, today);
        //then
        then(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        List<Message> messageList = messageRepository.findAll();
        then(messageList.get(0).getTitle()).isEqualTo("6000 포인트 만료예정");
        then(messageList.get(1).getTitle()).isEqualTo("10000 포인트 만료예정");



    }

}