package com.bkash.springbatch.controller;

import com.bkash.springbatch.model.Report;
import com.bkash.springbatch.service.PdfService;
import com.bkash.springbatch.utils.NumberToSpelling;
import com.itextpdf.text.DocumentException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.text.DecimalFormat;
import java.util.List;

@RequestMapping("/testapp")
@Controller
public class PDFController {

    JobLauncher jobLauncher;

    Job job;

    PdfService pdfService;

    @Autowired
    public PDFController(JobLauncher jobLauncher, Job job, PdfService pdfService) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.pdfService = pdfService;
    }

    @RequestMapping("/generatePdf")
    public @ResponseBody Report savePDF() throws IOException, DocumentException, Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(job, params);

        return new Report();
    }



}