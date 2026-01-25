package com.omsoft.retail.product.config;

import com.omsoft.retail.product.entity.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ProductFileJobConfig {

    @Bean
    public Job productUploadJob(JobRepository jobRepository, Step productStep) {
        return new JobBuilder("productUploadJob", jobRepository)
                .start(productStep)
                .build();
    }

    @Bean
    public Step productStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ItemReader<Product> reader,
                            ItemProcessor<Product, Product> processor,
                            ItemWriter<Product> writer) {

        return new StepBuilder("productStep", jobRepository)
                .<Product, Product>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
