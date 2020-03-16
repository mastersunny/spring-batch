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
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    JpaItemWriter writer;

    @Autowired
    JobCompletionNotificationListener listener;



    @Autowired
    public PDFController(JobLauncher jobLauncher, PdfService pdfService) {
        this.jobLauncher = jobLauncher;
        this.pdfService = pdfService;
    }

    @RequestMapping("/generatePdf")
    public @ResponseBody Report savePDF() throws Exception {


        MultiResourceItemReader<Report> reportMultiResourceItemReader = new MultiResourceItemReaderBuilder<Report>()
                .delegate(reader())
                .resources(getResources())
                .name("reportReader")
                .build();

       Step step =  stepBuilderFactory.get("processCpsFileStep")
                .<Report, Report> chunk(3)
                .reader(reportMultiResourceItemReader)
//                .processor(processor())
                .writer(writer)
                .build();

       Job job =  jobBuilderFactory.get("processCpsFileJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build();


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


    public FlatFileItemReader<Report> reader()
    {
        //Create reader instance
        FlatFileItemReader<Report> reader = new FlatFileItemReader<>();

        //Set input file location
//        reader.setResource(new ClassPathResource("Report_EFT_800000000101006545_1_20190728122021.csv"));

        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(1);

        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper<Report>() {
            {
                //3 columns in each row
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] {"RefundAmount", "BankName", "AccountNo", "AccountName"});
//                        setIncludedFields(new int[]{9,11,15,16});
                    }
                });
                //Set values in Employee class
//                setFieldSetMapper(new BeanWrapperFieldSetMapper() {
//                    {
//                        setTargetType(Report.class);
//                    }
//                });
            }
        });
        return reader;
    }



}