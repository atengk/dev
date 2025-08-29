package local.ateng.java.mybatisjdk8.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * 自定义 MyBatis-Plus 内置拦截器示例，实现了 InnerInterceptor 接口的全部方法。
 * <p>
 * 该拦截器可以在 SQL 执行的各个阶段获取 SQL 信息，包括查询、更新、插入、删除等操作。
 * 适用于 SQL 日志打印、性能监控、动态 SQL 修改等场景。
 * </p>
 *
 * @author 孔余
 * @since 2025-08-22
 */
public class MyCustomInterceptor implements InnerInterceptor {

    /**
     * 查询执行前是否继续执行 beforeQuery。
     *
     * @param executor      执行器
     * @param ms            映射语句对象
     * @param parameter     方法入参
     * @param rowBounds     分页参数
     * @param resultHandler 查询结果处理器
     * @param boundSql      SQL 绑定对象
     * @return true 继续执行 beforeQuery，false 跳过
     * @throws SQLException SQL 异常
     */
    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                               ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 可在这里根据需求动态决定是否执行查询
        return true;
    }

    /**
     * 查询执行前回调方法，可获取 BoundSql。
     *
     * @param executor      执行器
     * @param ms            映射语句对象
     * @param parameter     方法入参
     * @param rowBounds     分页参数
     * @param resultHandler 查询结果处理器
     * @param boundSql      SQL 绑定对象
     * @throws SQLException SQL 异常
     */
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                            ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        String executableSql = getExecutableSql(ms.getConfiguration(), boundSql);
        System.out.println("[查询 SQL] " + executableSql);
    }

    /**
     * 更新执行前是否继续执行 beforeUpdate。
     *
     * @param executor  执行器
     * @param ms        映射语句对象
     * @param parameter 方法入参
     * @return true 继续执行 beforeUpdate，false 跳过
     * @throws SQLException SQL 异常
     */
    @Override
    public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        return true;
    }

    /**
     * 更新执行前回调方法，可获取 BoundSql 或 MappedStatement 信息。
     *
     * @param executor  执行器
     * @param ms        映射语句对象
     * @param parameter 方法入参
     * @throws SQLException SQL 异常
     */
    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        BoundSql boundSql = ms.getSqlSource().getBoundSql(parameter);
        String executableSql = getExecutableSql(ms.getConfiguration(), boundSql);
        System.out.println("[更新/插入/删除 SQL] " + executableSql + "，类型: " + ms.getSqlCommandType());
    }

    /**
     * SQL 预处理阶段回调，可获取 StatementHandler 和 Connection。
     * <p>
     * 该方法可用于统一获取所有类型 SQL，或对 SQL 进行修改。
     * </p>
     *
     * @param sh                 StatementHandler
     * @param connection         数据库连接
     * @param transactionTimeout 事务超时时间
     */
    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
    }

    public static String getExecutableSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql();

        if (parameterMappings != null && !parameterMappings.isEmpty() && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = configuration.newMetaObject(parameterObject);

            for (ParameterMapping parameterMapping : parameterMappings) {
                if (parameterMapping.getMode() == ParameterMode.OUT) {
                    continue;
                }

                String propertyName = parameterMapping.getProperty();
                Object value;
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    // 动态 SQL 中的参数（例如 foreach）
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (metaObject.hasGetter(propertyName)) {
                    value = metaObject.getValue(propertyName);
                } else {
                    value = parameterObject;
                }

                // 使用 TypeHandler 来获取展示值（避免枚举、JSON 等丢失）
                String valueStr = getParameterValue(value, parameterMapping, typeHandlerRegistry);

                // 注意替换第一个 ?，防止正则错误
                sql = sql.replaceFirst("\\?", valueStr.replace("$", "\\$"));
            }
        }
        return sql;
    }

    /**
     * 获取参数值的字符串表示，优先使用 TypeHandler
     */
    private static String getParameterValue(Object value, ParameterMapping parameterMapping,
                                            TypeHandlerRegistry typeHandlerRegistry) {
        if (value == null) {
            return "NULL";
        }

        Class<?> javaType = parameterMapping.getJavaType();
        TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(javaType);

        // 如果有自定义 TypeHandler，调用它的逻辑
        if (typeHandler != null && !(value instanceof String)) {
            // 简化展示，不实际调用 setParameter（否则要构造 PreparedStatement）
            return "'" + value.toString() + "'";
        }

        if (value instanceof String) {
            return "'" + value + "'";
        }
        if (value instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            return "'" + formatter.format((Date) value) + "'";
        }
        if (value instanceof Enum<?>) {
            // 枚举：打印枚举名，或者可根据需求打印枚举的 code
            return "'" + ((Enum<?>) value).name() + "'";
        }

        return value.toString();
    }

    /**
     * 获取 BoundSql 前的回调，可用于最后阶段修改 SQL。
     *
     * @param sh StatementHandler
     */
    @Override
    public void beforeGetBoundSql(StatementHandler sh) {
    }

    /**
     * 设置自定义配置属性，可通过配置文件注入参数。
     *
     * @param properties 属性集合
     */
    @Override
    public void setProperties(Properties properties) {
        // 可读取配置参数，例如日志开关、SQL 修改规则等
    }

}