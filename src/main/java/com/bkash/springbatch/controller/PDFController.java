package com.bkash.springbatch.controller;

import com.bkash.springbatch.config.JobCompletionNotificationListener;
import com.bkash.springbatch.model.Report;
import com.bkash.springbatch.service.PdfService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/testapp")
@Controller
public class PDFController {

    JobLauncher jobLauncher;

    PdfService pdfService;

    @Autowired
    Job job;

    @Autowired
    MultiResourceItemReader multiResourceItemReader;

    @Autowired
    public PDFController(JobLauncher jobLauncher, PdfService pdfService) {
        this.jobLauncher = jobLauncher;
        this.pdfService = pdfService;
    }

    @RequestMapping("/generatePdf")
    public @ResponseBody Report savePDF() throws Exception {
        multiResourceItemReader.setResources(getResources());

        JobParameters params2 = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(job, params2);

        return new Report();
    }

    private Resource[] getResources(){
        Resource[] resources = {new ClassPathResource("Report_EFT_800000000101006545_1_20190728122021.csv"),
                new ClassPathResource("Report_EFT_800000000101006545_1_20190728122022.csv")};
        return resources;
    }




}