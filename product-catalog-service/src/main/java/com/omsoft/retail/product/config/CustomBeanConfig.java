package com.omsoft.retail.product.config;

import com.omsoft.retail.product.entity.Product;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;

@Configuration
public class CustomBeanConfig {
    @Bean
    @StepScope
    public FlatFileItemReader<Product> reader(
            @Value("#{jobParameters['filePath']}") String path) {
    return new FlatFileItemReaderBuilder<Product>()
                .name("productReader")
                .resource(new FileSystemResource(path))
                .linesToSkip(1) // skip header row
                .delimited()
                .delimiter(",")
                .names(
                        "name",
                        "version",
                        "description",
                        "price",
                        "ram",
                        "storage",
                        "size",
                        "color",
                        "stock",
                        "category"
                )
                .fieldSetMapper(productFieldSetMapper())
                .build();
    }

    @Bean
    public FieldSetMapper<Product> productFieldSetMapper() {
        return fieldSet -> {
            Product p = new Product();
            p.setName(fieldSet.readString("name"));
            p.setVersion(fieldSet.readString("version"));
            p.setDescription(fieldSet.readString("description"));
            p.setPrice(fieldSet.readBigDecimal("price"));
            p.setStock(fieldSet.readInt("stock"));

            // CSV → Entity mapping
            p.setRamSize(fieldSet.readInt("ram"));
            p.setHardDiskSize(fieldSet.readInt("storage"));
            p.setScreenSize(fieldSet.readFloat("size"));
            p.setColor(fieldSet.readString("color"));

            // category will be handled in processor (lookup from DB)
            return p;
        };
    }
    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager tx,
                     FlatFileItemReader<Product> reader) {

        return new StepBuilder("csv-step", jobRepository)
                .<Product, Product>chunk(20, tx)
                .reader(reader)
                .processor(processor())
                .writer(writer(null))
                .build();
    }

    @Bean
    public JpaItemWriter<Product> writer(EntityManagerFactory emf) {
        JpaItemWriter<Product> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public ItemProcessor<Product, Product> processor() {
        return product -> {
            if (product.getPrice().compareTo(BigDecimal.ZERO)  <= 0) return null; // skip invalid
            return product;
        };
    }

}
