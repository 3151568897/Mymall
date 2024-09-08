//package com.example.mymall.ware.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
//import io.seata.rm.datasource.DataSourceProxy;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
///**
// * 由于Seata需要对数据源进行代理，以实现分布式事务的管理，我们需要对数据源进行配置
// */
//@Configuration
//public class DataSourceConfig {
//
//    @Value("${mybatis-plus.mapper-locations}")
//    private String mapperLocations;
//
//    // 定义 DruidDataSource
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DruidDataSource druidDataSource(){
//        return new DruidDataSource();
//    }
//
//    // 定义 DataSourceProxy，并将其作为主数据源
//    @Bean("dataSource")
//    @Primary
//    public DataSourceProxy dataSourceProxy(DruidDataSource druidDataSource) {
//        return new DataSourceProxy(druidDataSource);
//    }
//
//    // 配置 SqlSessionFactory 使用 DataSourceProxy
//    @Bean
//    public SqlSessionFactory sqlSessionFactoryBean(DataSourceProxy dataSourceProxy) throws Exception {
//        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSourceProxy);
//        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
//                .getResources(mapperLocations));
//        sqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());
//        sqlSessionFactoryBean.setTypeAliasesPackage("com.example.mymall.order.entity"); // 根据实际情况调整
//        return sqlSessionFactoryBean.getObject();
//    }
//
//    // 配置事务管理器使用 DataSourceProxy
//    @Bean
//    public PlatformTransactionManager transactionManager(DataSourceProxy dataSourceProxy) {
//        return new DataSourceTransactionManager(dataSourceProxy);
//    }
//}
//
//
