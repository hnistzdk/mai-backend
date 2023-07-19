package com.zdk.mai.common.datasource.inteceptors;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @Description mybatisplus SQL执行打印
 * @Author zdk
 * @Date 2023/3/10 16:22
 */
@Intercepts({
        //type指定代理的是那个对象，method指定代理Executor中的那个方法
        //args指定Executor中的query方法都有哪些参数对象
        //由于Executor中有两个query，因此需要两个@Signature
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),//需要代理的对象和方法
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})//需要代理的对象和方法
})
@SuppressWarnings({"all"})
public class MybatisPlusSqlInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlusSqlInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        long start = System.currentTimeMillis();
        Object returnValue = invocation.proceed();
        long time = System.currentTimeMillis() - start;
        showSql(configuration, boundSql, time, sqlId);
        return returnValue;
    }

    private static void showSql(Configuration configuration, BoundSql boundSql, long time, String sqlId) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //替换空格、换行、tab缩进等
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        if ((propertyName.equals("content") || propertyName.equals("html")
                                || propertyName.equals("markdown") || propertyName.equals("images")) ||
                                propertyName.equals("et.content") || propertyName.equals("et.html")
                                || propertyName.equals("et.markdown") || propertyName.equals("et.images")) {
                            sql = sql.replaceFirst("\\?", "内容过长不显示");
                        } else {
                            Object obj = metaObject.getValue(propertyName);
                            sql = sql.replaceFirst("\\?", getParameterValue(obj));
                        }
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        if ((propertyName.equals("content") || propertyName.equals("html")
                                || propertyName.equals("markdown") || propertyName.equals("images")) ||
                                (propertyName.equals("et.content") || propertyName.equals("et.html")
                                || propertyName.equals("et.markdown") || propertyName.equals("et.images"))) {
                            sql = sql.replaceFirst("\\?", "内容过长不显示");
                        } else {
                            Object obj = boundSql.getAdditionalParameter(propertyName);
                            sql = sql.replaceFirst("\\?", getParameterValue(obj));
                        }
                    }
                }
            }
        }
        logs(time, sql, sqlId);
    }

    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value.replace("$", "\\$");
    }

    private static void logs(long time, String sql, String sqlId) {
        StringBuilder timeAndId = new StringBuilder()
                .append("Execute SQL Time：").append(time)
                .append(" ms - ID：").append(sqlId);
        log.info(timeAndId.toString());
        StringBuilder execSql = new StringBuilder().append("Execute SQL：")
                .append(sql).append(StringPool.NEWLINE);
        log.info(execSql.toString());
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties0) {
    }
}
