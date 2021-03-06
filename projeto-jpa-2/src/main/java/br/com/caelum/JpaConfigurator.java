package br.com.caelum;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
public class JpaConfigurator {
	
	// datasource que trabalha com pool de conexões - JpaConfigurator
	@Bean(destroyMethod = "close") // garante que as conexões sejam fechadas ao parar o tomcat
	public DataSource getDatSource() throws PropertyVetoException {
	    ComboPooledDataSource dataSource = new ComboPooledDataSource();
	    dataSource.setDriverClass("org.postgresql.Driver");
	    dataSource.setUser("postgres");
	    dataSource.setPassword("postgres");
	    dataSource.setJdbcUrl("jdbc:postgresql://localhost/projeto_jpa_2");

	    dataSource.setMinPoolSize(3); // número de conexões iniciais
	    dataSource.setMaxPoolSize(5);  // número máximo de conexões
	    dataSource.setNumHelperThreads(15); //criar threads, trabalhar de forma assíncrona
	    dataSource.setIdleConnectionTestPeriod(1); // a cada segundo é testado conexões ociosas

	    return dataSource;
	    
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();

		entityManagerFactory.setPackagesToScan("br.com.caelum");
		entityManagerFactory.setDataSource(dataSource);

		entityManagerFactory
				.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		Properties props = new Properties();

		props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
		props.setProperty("hibernate.show_sql", "true");
		props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		props.setProperty("hibernate.cache.use_second_level_cache", "true");
		props.setProperty("hibernate.cache.use_query_cache", "true");
		props.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
		props.setProperty("hibernate.generate_statistics", "true");

		entityManagerFactory.setJpaProperties(props);
		return entityManagerFactory;
	}

	@Bean
	public JpaTransactionManager getTransactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}
	
	@Bean
	public Statistics statistics(EntityManagerFactory emf) {
	    SessionFactory factory = emf.unwrap(SessionFactory.class);
	    return factory.getStatistics();
	}


}
