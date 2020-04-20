package com.bkash.springbatch.config;

import com.bkash.springbatch.dao.CompanyFundingRecordRepository;
import com.bkash.springbatch.model.CompanyFundingRecordEntity;
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
    CompanyFundingRecordRepository companyFundingRecordRepository;

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
                .<CompanyFundingRecordEntity, CompanyFundingRecordEntity> chunk(3)
                .reader(multiResourceItemReader)
//                .processor(processor())
                .writer(writer())
                .build();
        return step;
    }

    @Bean
    public MultiResourceItemReader multiResourceItemReader(FlatFileItemReader flatFileItemReader){
        Resource[] resources = {};
        MultiResourceItemReader<CompanyFundingRecordEntity> multiResourceItemReader = new MultiResourceItemReaderBuilder<CompanyFundingRecordEntity>()
                .delegate(flatFileItemReader)
                .resources(resources)
                .name("reportReader")
                .build();
        return multiResourceItemReader;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CompanyFundingRecordEntity> reader() {
        FlatFileItemReader<CompanyFundingRecordEntity> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<CompanyFundingRecordEntity>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] {"permalink", "company", "numEmps", "category","city","state",
                                "fundedDate","raisedAmt","raisedCurrency","round"});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper() {
                    {
                        setTargetType(CompanyFundingRecordEntity.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    public JpaItemWriter writer() {
        JpaItemWriter writer = new JpaItemWriter();
        writer.setEntityManagerFactory(emf);
        return writer;
    }
}
