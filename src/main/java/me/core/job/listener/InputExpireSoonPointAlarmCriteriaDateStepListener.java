package me.core.job.listener;

import org.springframework.batch.core.*;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Component
public class InputExpireSoonPointAlarmCriteriaDateStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameter todayParameter = stepExecution.getJobParameters()
                                                   .getParameters()
                                                   .get("today");
        LocalDate today = LocalDate.parse((String) todayParameter.getValue());
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        executionContext.put("alarmCriteriaDate", today.plusDays(7).format(DateTimeFormatter.ISO_DATE));

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
