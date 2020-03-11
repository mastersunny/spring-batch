package com.bkash.springbatch.config;

import com.bkash.springbatch.dao.ReportRepository;
import com.bkash.springbatch.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
   private PdfService pdfService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        /**
         * As of now empty but can add some before job conditions
         */
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
            try{
                pdfService.generatePdf();
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }
}