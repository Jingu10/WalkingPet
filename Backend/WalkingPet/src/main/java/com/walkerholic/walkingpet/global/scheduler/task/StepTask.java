package com.walkerholic.walkingpet.global.scheduler.task;

import com.walkerholic.walkingpet.global.redis.service.StepService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@RequiredArgsConstructor
//@EnableBatchProcessing
@EnableScheduling
public class StepTask {
    private final StepService stepService;

    @Bean
    public Tasklet dailyStepTasklet() {
        return (stepContribution, chunkContext) -> {
            stepService.updateUserStepInfo();
            return null;
        };
    }

    @Bean
    public Step dailyStepStep(JobRepository jobRepository, Tasklet dailyStepTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("dailyStepStep", jobRepository)
                .tasklet(dailyStepTasklet, transactionManager) // or .chunk(chunkSize, transactionManager)
                .build();
    }

    @Bean
    public Job dailyStepJob(JobRepository jobRepository, Step dailyStepStep) {
        return new JobBuilder("dailyStepJob", jobRepository)
                .start(dailyStepStep)
//                .next(helloStep2)
                .build();
    }

//    @Scheduled(cron = "0 0 0 * * ?")
//    public void runDailyStepJob() throws Exception {
//        dailyStepJob(jobRepository(), dailyStepStep()).execute(null);
//    }

//    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
//    public void runDailyStepJob() throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("JobID", String.valueOf(System.currentTimeMillis()))
//                .toJobParameters();
//        jobLauncher.run(dailyStepJob(), jobParameters);
//    }
}
