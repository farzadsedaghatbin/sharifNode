package com.isc.npsd.sharif.node;

import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import com.isc.npsd.sharif.node.adapter.SharedObjectsContainer;
import com.isc.npsd.sharif.node.entities.NodeProperties;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;



@Configuration
@ComponentScan({"com.isc.npsd.sharif.node.services"})
@EnableJpaRepositories({"com.isc.npsd.sharif.node.repositories"})
@EnableTransactionManagement()
public class AppConfig {
    private static ApplicationContext context = new AnnotationConfigApplicationContext("com.isc.npsd.sharif.node");

    public AppConfig() {
    }

    public static ApplicationContext getContext() {
        return context;
    }

    @Bean
    static DataSource createDataSource() {
        AtomikosNonXADataSourceBean atomikosNonXADataSourceBean = new AtomikosNonXADataSourceBean();
        atomikosNonXADataSourceBean.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        atomikosNonXADataSourceBean.setUser((String) SharedObjectsContainer.properties.get(NodeProperties.SHARIF_DB_USER.name()));
        atomikosNonXADataSourceBean.setPassword((String) SharedObjectsContainer.properties.get(NodeProperties.SHARIF_DB_PASS.name()));
        atomikosNonXADataSourceBean.setUrl((String) SharedObjectsContainer.properties.get(NodeProperties.SHARIF_DB_URL.name()));
        atomikosNonXADataSourceBean.setBorrowConnectionTimeout(Integer.parseInt((String) SharedObjectsContainer.properties.get(NodeProperties.SHARIF_DB_CONNECTION_TIMEOUT.name())));
        atomikosNonXADataSourceBean.setPoolSize(Integer.parseInt((String) SharedObjectsContainer.properties.get(NodeProperties.SHARIF_DB_POOL_SIZE.name())));
        atomikosNonXADataSourceBean.setDefaultIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
        atomikosNonXADataSourceBean.setUniqueResourceName((String) SharedObjectsContainer.properties.get(NodeProperties.SHARIF_DB_RESOURCE.name()));
        return atomikosNonXADataSourceBean;
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.Oracle10gDialect");
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public HibernatePersistenceProvider persistenceProvider() {
        return new HibernatePersistenceProvider();
    }

    @Bean(
            name = {"entityManagerFactory"}
    )
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(this.jpaVendorAdapter());
        factory.setPersistenceProvider(this.persistenceProvider());
        factory.setPackagesToScan(new String[]{"com.isc.npsd.sharif.node.entities"});
        factory.setDataSource(createDataSource());
        HashMap persistenceMap = new HashMap();
        persistenceMap.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
        persistenceMap.put("javax.persistence.validation.mode", "none");
        persistenceMap.put("hibernate.hbm2ddl.auto", "update");
        persistenceMap.put("javax.persistence.transactionType", "JTA");
        persistenceMap.put("hibernate.transaction.jta.platform", "com.isc.npsd.sharif.node.JTAPlatform");
        persistenceMap.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        persistenceMap.put("hibernate.jdbc.batch_size", SharedObjectsContainer.properties.get(NodeProperties.SHARIF_BATCH_SIZE.name()));
        persistenceMap.put("hibernate.show_sql", SharedObjectsContainer.properties.get(NodeProperties.SHARIF_HIBERNATE_SHOW_SQL.name()));
        persistenceMap.put("hibernate.format_sql", SharedObjectsContainer.properties.get(NodeProperties.SHARIF_HIBERNATE_SHOW_SQL.name()));
        persistenceMap.put("org.hibernate.flushMode", "COMMIT");
        Properties properties = new Properties();
        properties.putAll(persistenceMap);
        factory.setJpaProperties(properties);
        return factory;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean(name = "transactionManager")
    @DependsOn({"userTransaction", "atomikosTransactionManager", "userTransactionService"})
    public JtaTransactionManager transactionManager() {
        return new JtaTransactionManager(atomikosUserTransaction(), atomikosTransactionManager());
    }

    @Bean(name = "userTransaction")
    @DependsOn({"userTransactionService"})
    public UserTransaction atomikosUserTransaction() {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        JTAPlatform.userTransaction = userTransactionImp;
        return userTransactionImp;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @DependsOn({"userTransactionService"})
    public TransactionManager atomikosTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        JTAPlatform.transactionManager = userTransactionManager;
        return userTransactionManager;
    }

    @Bean(initMethod = "init", destroyMethod = "shutdownForce")
    public UserTransactionServiceImp userTransactionService() {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("com.atomikos.icatch.service", "com.atomikos.icatch.standalone.UserTransactionServiceFactory");
        properties.put("com.atomikos.icatch.serializable_logging", "false");
        properties.put("com.atomikos.icatch.registered", "true");
        properties.put("com.atomikos.icatch.max_timeout", (String) SharedObjectsContainer.properties.get(NodeProperties.SHARIF_TRANSACTION_TIMEOUT.name()));
        properties.put("com.atomikos.icatch.default_jta_timeout", (String) SharedObjectsContainer.properties.get(NodeProperties.SHARIF_TRANSACTION_TIMEOUT.name()));
        try {
            properties.put("com.atomikos.icatch.tm_unique_name", InetAddress.getLocalHost().getHostName() + LocalDateTime.now().getNano());
        } catch (UnknownHostException e) {
            properties.put("com.atomikos.icatch.tm_unique_name", String.valueOf(LocalDateTime.now().getNano()));
        }
        Properties props = new Properties();
        props.putAll(properties);
        UserTransactionServiceImp userTransactionServiceImp = new UserTransactionServiceImp(props);
        return userTransactionServiceImp;
    }
}
