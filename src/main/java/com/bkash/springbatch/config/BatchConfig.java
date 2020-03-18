package com.bkash.springbatch.config;

import com.bkash.springbatch.dao.ReportRepository;
import com.bkash.springbatch.model.Report;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    EntityManagerFactory emf;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    JobCompletionNotificationListener listener;
//
//    @Bean
//    public PersonItemProcessor processor() {
//        return new PersonItemProcessor();
//    }

    @Bean
    public Job readCSVFileJob(Step step){
        Job job =  jobBuilderFactory.get("processCpsFileJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build();
        return job;
    }

    @Bean
    public Step step(MultiResourceItemReader multiResourceItemReader){
        Step step =  stepBuilderFactory.get("processCpsFileStep")
                .<Report, Report> chunk(3)
                .reader(multiResourceItemReader)
//                .processor(processor())
                .writer(writer())
                .build();
        return step;
    }

    @Bean
    public MultiResourceItemReader multiResourceItemReader(FlatFileItemReader flatFileItemReader){
        Resource[] resources = {};
        MultiResourceItemReader<Report> multiResourceItemReader = new MultiResourceItemReaderBuilder<Report>()
                .delegate(flatFileItemReader)
                .resources(resources)
                .name("reportReader")
                .build();
        return multiResourceItemReader;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Report> reader(@Value("#{jobParameters['columnNames']}") String columnNames) {
        FlatFileItemReader<Report> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<Report>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
//                        setNames(new String[] {"RefundAmount", "BankName", "AccountNo", "AccountName"});
                        setNames(columnNames.split(","));
                        setIncludedFields(new int[]{9,11,15,16});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper() {
                    {
                        setTargetType(Report.class);
                    }
                });
            }
        });
        return reader;
    }

    private Resource[] getResources(){
        Resource[] resources = {new ClassPathResource("Report_EFT_800000000101006545_1_20190728122021.csv"),
                new ClassPathResource("Report_EFT_800000000101006545_1_20190728122022.csv")};
        return resources;
    }



    @Bean
    public JpaItemWriter writer() {
        JpaItemWriter writer = new JpaItemWriter();
        writer.setEntityManagerFactory(emf);
        return writer;
    }
}
