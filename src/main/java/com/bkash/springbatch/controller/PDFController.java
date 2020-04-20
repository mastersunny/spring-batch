package com.bkash.springbatch.controller;

import com.bkash.springbatch.model.CompanyFundingRecordEntity;
import com.bkash.springbatch.service.PdfService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/testapp")
@Controller
public class PDFController {

    JobLauncher jobLauncher;

    PdfService pdfService;

    @Autowired
    Job job;

    @Autowired
    FlatFileItemReader reader;

    @Autowired
    MultiResourceItemReader multiResourceItemReader;

    @Autowired
    public PDFController(JobLauncher jobLauncher, PdfService pdfService) {
        this.jobLauncher = jobLauncher;
        this.pdfService = pdfService;
    }

    @GetMapping("/generatePdf")
    public @ResponseBody
    CompanyFundingRecordEntity savePDF() throws Exception {
        multiResourceItemReader.setResources(getResources());

        JobParameters params2 = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(job, params2);

        return new CompanyFundingRecordEntity();
    }

    private Resource[] getResources(){
        Resource[] resources = {new ClassPathResource("company_funding_records.csv")};
        return resources;
    }




}