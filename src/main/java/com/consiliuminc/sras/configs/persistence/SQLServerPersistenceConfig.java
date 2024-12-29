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
@PropertySource({"classpath:persistence.properties"})
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.consiliuminc.sras.repository.sqlserver",
        entityManagerFactoryRef = "sqlServerEntityManagerFactory",
        transactionManagerRef = "sqlServerTransactionManager"
)
public class SQLServerPersistenceConfig {

    @Value("${spring.jpa.hibernate.dialect}")
    private String hibernate_dialect;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String hibernate_hbm2ddl_auto;

    @Value("${spring.jpa.show-sql}")
    private boolean hibernate_show_sql;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        HikariDataSource ds =(HikariDataSource) DataSourceBuilder.create().build();
        ds.setConnectionTestQuery("SELECT getdate()");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean sqlServerEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[]{"com.consiliuminc.sras.entities.sqlserver"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        properties.put("hibernate.dialect", hibernate_dialect);
        properties.put("hibernate.show_sql", hibernate_show_sql);
        em.setJpaPropertyMap(properties);


        return em;
    }


    @Bean
    public PlatformTransactionManager sqlServerTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                sqlServerEntityManagerFactory().getObject());

        return transactionManager;
    }

}
