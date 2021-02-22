package com.swapServer.config.mybaits;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.transaction.annotation.TransactionManagementConfigurer;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
///**
// *@Data : 2020/12/25
// *@Author : zhangsw
// *@Descripe : TODO
// *@Version : 0.1
// */
//@Configuration
//@EnableTransactionManagement
//@MapperScan({ "com.swapServer.mapper" })
//// 开启注解事务支持
//public class MybatisConfig implements TransactionManagementConfigurer {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Bean(name = "sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//
//        org.apache.ibatis.session.Configuration configuration=new org.apache.ibatis.session.Configuration();
//        configuration.setUseGeneratedKeys(true);//使用jdbc的getGeneratedKeys获取数据库自增主键值
//        configuration.setUseColumnLabel(true);//使用列别名替换列名 select user as User
//        configuration.setMapUnderscoreToCamelCase(true);//-自动使用驼峰命名属性映射字段   userId    user_id
//        bean.setConfiguration(configuration);
//        bean.setFailFast(true);
//
//
//        Properties properties = new Properties();
//        properties.setProperty("helperDialect", "postgresql");
//        properties.setProperty("reasonable", "true");
//        properties.setProperty("supportMethodsArguments", "true");
//        properties.setProperty("params", "count=countSql");
//        return bean.getObject();
//    }
//
//    @Bean
//    public SqlSessionTemplate sqlSessionTemplate(
//            SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//
//    // 开启注解事务
//    @Bean
//    @Override
//    public PlatformTransactionManager annotationDrivenTransactionManager() {
//        return new DataSourceTransactionManager(dataSource);
//    }
//}
