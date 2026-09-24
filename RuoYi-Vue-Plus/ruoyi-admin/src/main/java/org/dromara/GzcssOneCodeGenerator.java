package com.gzcss;

import cn.hutool.core.text.CharSequenceUtil;
import com.gzcss.starter.codegenerate.CodeGenerator;
import com.gzcss.starter.codegenerate.constant.NamingStyle;
import com.gzcss.starter.codegenerate.vo.DatabaseVo;
import com.gzcss.starter.codegenerate.vo.GenerateVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GzcssOneCodeGenerator {

    /**
     * 生成代码绝对路径 = {ProjectPath} + src/main/java + {BusinessPackage} + {EntityPackage}
     */
    public static void main(String[] args) {
        String bussiPackage = "com.gzcss.modules";
        String module = "metro";
        String[] tables = new String[]{"metro_spare_part_purchase"
        };
        for (String tableName : tables) {
            String entityName = CharSequenceUtil.upperFirst(CharSequenceUtil.toCamelCase(tableName));
            GenerateVo vo = new GenerateVo();
            vo.setTableName(tableName);// 表名称
            vo.setEntityName(entityName);// 类名称首字母大写
            vo.setPrimaryKey("id");// 表主键
            // vo.setFtlDescription(""); //描述，为空自动读取表注释
            vo.setBussiPackage(bussiPackage);// 业务包路径
            vo.setEntityPackage(module);// 包名称/微服务名称
            vo.setAuthor("autogen");
            // 给字段加上@TableLogic
            // vo.setTableLogic("delFlag");
            // 表名大小写
            // DEFAULT: 默认，
            // UPPER_CASE：强制大写 @TableName("\"TABLE_NAME\"")，
            // LOWER_CASE：强制小写 @TableName("\"table_name\"")
            vo.setTableNameStyle(NamingStyle.LOWER_CASE);
            // 字段名大小写，同上@TableField(value = xx)
            vo.setColumnNameStyle(NamingStyle.LOWER_CASE);
            setConfig(vo);
            CodeGenerator.generate(vo);
        }
    }

    public static void setConfig(GenerateVo vo) {
        DatabaseVo databaseVo = new DatabaseVo();
        databaseVo.setUrl("jdbc:mysql://localhost:3306/sz-jdyw?characterEncoding=UTF-8&useUnicode=true&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true");
        databaseVo.setDriverClass("com.mysql.cj.jdbc.Driver");
        databaseVo.setUsername("root");
        databaseVo.setPassword("123456");
        vo.setDatabaseVo(databaseVo);
        vo.setProjectPath("D:\\AAA_WorkProject\\jidian\\sz-jdyw-dev-master\\microservices\\base"); // 微服务项目路径
    }

}
