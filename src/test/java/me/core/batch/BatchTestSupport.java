package me.core.batch;

import me.core.PointManagementApplication;
import me.core.message.MessageRepository;
import me.core.point.PointRepository;
import me.core.point.reservation.ReservationRepository;
import me.core.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = PointManagementApplication.class)
@ActiveProfiles("test")
public abstract class BatchTestSupport {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PointWalletRepository pointWalletRepository;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    protected JobExecution launchJob(Job job, JobParameters jobParameters)throws Exception {
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJob(job);
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJobRepository(jobRepository);
        return jobLauncherTestUtils.launchJob(jobParameters);
    }
    @AfterEach
    protected void deleteAll() {
        messageRepository.deleteAll();
        pointRepository.deleteAll();
        reservationRepository.deleteAll();
        pointWalletRepository.deleteAll();
    }
}
