package com.consiliuminc.sras.configs.persistence;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@PropertySource({"classpath:persistence.properties"})
@EnableJpaRepositories(
        basePackages = "com.consiliuminc.sras.repository.postgres",
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef = "postgresTransactionManager"
)

public class PostgresPersistenceConfig {


    @Value("${spring.second-jpa.hibernate.dialect}")
    private String hibernate_dialect;

    @Value("${spring.second-jpa.hibernate.ddl-auto}")
    private String hibernate_hbm2ddl_auto;

    @Value("${spring.second-jpa.show-sql}")
    private boolean hibernate_show_sql;


    @Bean
    @ConfigurationProperties(prefix = "spring.second-datasource")
    public DataSource postgresDataSource() {

        HikariDataSource ds =(HikariDataSource) DataSourceBuilder.create().build();
        ds.setConnectionTestQuery("SELECT getdate()");
        return ds;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(postgresDataSource());
        em.setPackagesToScan(new String[]{"com.consiliuminc.sras.entities.postgres"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        properties.put("hibernate.dialect", hibernate_dialect);
        properties.put("hibernate.show_sql", hibernate_show_sql);
        properties.put("hibernate.connection.defaultNChar", false);


        //properties.put("hibernate.temp.use_jdbc_metadata_defaults", false);
        em.setJpaPropertyMap(properties);

        return em;
    }


    @Bean
    public PlatformTransactionManager postgresTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                postgresEntityManagerFactory().getObject());
        return transactionManager;
    }


//    @Bean(name = "postgresEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(@Qualifier("postgresDataSource") DataSource dataSource) {
//        LocalContainerEntityManagerFactoryBean em
//                = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan(new String[]{"com.consiliuminc.sras.entities.postgres"});
//
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//        em.setJpaProperties(additionalProperties());
//
//        return em;
//    }
//
//
//    @Bean(name = "postgresTransactionManager")
//    public PlatformTransactionManager postgresTransactionManager(@Qualifier("postgresEntityManagerFactory") EntityManagerFactory emf) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(emf);
//
//        return transactionManager;
//    }
//
//
//    private Properties additionalProperties() {
//        Properties properties = new Properties();
//        properties.put("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
//        properties.put("hibernate.dialect", hibernate_dialect);
//        properties.put("hibernate.show_sql", hibernate_show_sql);
//        properties.put("hibernate.temp.use_jdbc_metadata_defaults", false);
//        return properties;
//    }
}
