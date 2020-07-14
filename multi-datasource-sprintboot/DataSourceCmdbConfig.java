package csg.ios.app;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;


/**
 * @author huangwang
 */
//@MapperScan(basePackages  = "csg.ios.app.dao.cmdb" , sqlSessionFactoryRef = "cmdbSqlSessionFactory")
public class DataSourceCmdbConfig {
    private Logger logger = LoggerFactory.getLogger(DataSourceCmdbConfig.class);
    // 精确到 cluster 目录，以便跟其他数据源隔离
    private final String MAPPER_LOCATION = "classpath:mappers.cmdb/*.xml";
    private final String DOMAIN_PACKAGE = "csg.ios.app.dao.cmdb";

    @Value("${spring.datasource.cmdb.url}")
    private String dbUrl;

    @Value("${spring.datasource.cmdb.username}")
    private String username;

    @Value("${spring.datasource.cmdb.password}")
    private String password;

    @Value("${spring.datasource.cmdb.driver-class-name}")
    private String driverClassName;



    @Bean(name="cmdb")   //声明其为Bean实例
    public DataSource cmdbDataSource() {
        PooledDataSource datasource = new PooledDataSource();

        datasource.setUrl(this.dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriver(driverClassName);
        return datasource;
    }

/*    @Bean(name = "cmdbTransactionManager")
    public DataSourceTransactionManager cmdbTransactionManager() {
        return new DataSourceTransactionManager(new PooledDataSource());
    }

    @Bean(name = "cmdbSqlSessionFactory")
    public SqlSessionFactory cmdbSqlSessionFactory(@Qualifier("cmdbDataSource") DataSource cmdbDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(cmdbDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        sessionFactory.setTypeAliasesPackage(DOMAIN_PACKAGE);
        //mybatis 数据库字段与实体类属性驼峰映射配置
        sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sessionFactory.getObject();
    }*/



}
