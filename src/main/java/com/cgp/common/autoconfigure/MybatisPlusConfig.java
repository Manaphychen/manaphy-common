package com.cgp.common.autoconfigure;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Date;

/**
 * Mybatis-plus设置分页插件
 *
 * @author Manaphy
 */
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Configuration(proxyBeanMethods = false)
@MapperScan("com.**.mapper")
public class MybatisPlusConfig {

    /**
     * Mybatis-Plus内置插件
     *
     * @return {@link MybatisPlusInterceptor}
     * @since 3.4.0
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页分页条数限制
        paginationInnerInterceptor.setMaxLimit(-1L);
//        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // sql性能规范插件
//        interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());

        return interceptor;
    }

    /**
     * 处理自动填充创建时间和修改时间
     *
     * @return {@link MetaObjectHandler}
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            /**
             * 插入时的填充策略
             *
             * @param metaObject 元对象
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                //推荐使用的填充--填充日期
                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
                //通用填充方法
                this.setFieldValByName("updateTime", new Date(), metaObject);
                //自动填充字符串
                this.strictInsertFill(metaObject, "operator", String.class, "Manaphy");
            }

            /**
             * 更新时的填充策略
             *
             * @param metaObject 元对象
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                //自动填充时间
                this.setFieldValByName("updateTime", new Date(), metaObject);
                //自动填充字符串
                this.strictInsertFill(metaObject, "operator", String.class, "Chen");
            }
        };
    }

}
