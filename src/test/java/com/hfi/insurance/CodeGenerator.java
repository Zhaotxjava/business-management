package com.hfi.insurance;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;


public class CodeGenerator {

    private static void run() {
        // 1.设置全局配置global
        GlobalConfig config = new GlobalConfig();
        // 获取项目根路径
        String projectPath = System.getProperty("user.dir");
        // 设置生成路径
        config.setOutputDir(projectPath + "/src/main/java");
        // config.setOutputDir(“e:\codeGen”); 设置到磁盘中
        // 设置活动记录ActiveRecord
        config.setActiveRecord(false);
        // 设置作者
        config.setAuthor("ChenZX");
        // 设置文件第二次是二次覆
        config.setFileOverride(true);
        // 设置缓存cache 二级缓存
        config.setEnableCache(false);
        // 实体属性 Swagger2 注解
        config.setOpen(true);
        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        config.setMapperName("%sMapper");
        config.setXmlName("%sMapper");
        // XML ResultMap
        config.setBaseResultMap(true);
        // XML columList
        config.setBaseColumnList(true);
//        if (!serviceNameStartWithI) {
//            // 设置service接口前是否加I（service接口命名）
//            config.setServiceName("%sService");
//            config.setServiceImplName("%sServiceImpl");
//        }
        String dbUrl = "jdbc:mysql://192.30.255.22:3306/db_insurance_info?useUnicode\\=true&characterEncoding\\=utf-8&allowMultiQueries\\=true";
        String username = "test";
        String password = "test@22";
        String driverName = "com.mysql.jdbc.Driver";

        // 2.设置数据库连接池DataSource
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                .setUrl(dbUrl)
                .setUsername(username)
                .setPassword(password)
                .setDriverName(driverName);
        // 3.设置策略Strategy
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setCapitalMode(true);
        // 结合了Lombok插件，所以设置为true，如果没有集成Lombok，可以设置为false
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        // .setSuperMapperClass(“cn.saytime.mapper.BaseMapper”)
        strategyConfig.setInclude("yb_flow_info");//修改替换成你需要的表名，多个表名传数组
        // 4.设置包配置package
        PackageConfig packageConfig = new PackageConfig();
        //pc.setModuleName(scanner(“模块名”));
        // 设置包名
        packageConfig.setParent("com.hfi.insurance");
        //packageConfig.setController("controller");
        // 设置对象实例类
        packageConfig.setEntity("model");
        // dao接口包名
        //packageConfig.setMapper("mapper");
        // service接口包名
        //packageConfig.setService("service");
        // service实现包名
        //packageConfig.setServiceImpl("service.impl");
        packageConfig.setXml("mapper.xml");

        // 代码生成器–4.应用自动生成
        AutoGenerator mpg = new AutoGenerator();
        mpg.setGlobalConfig(config)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(packageConfig).execute();
    }

    public static void main(String[] args) {
        run();
    }
}

