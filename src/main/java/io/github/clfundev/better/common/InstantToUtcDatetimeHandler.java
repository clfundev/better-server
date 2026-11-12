package io.github.clfundev.better.common;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 将Instant存储为DATETIME类型UTC时间的处理器
 */
@MappedTypes(Instant.class)
@MappedJdbcTypes(JdbcType.TIMESTAMP)
public class InstantToUtcDatetimeHandler extends BaseTypeHandler<Instant> {
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Instant parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, LocalDateTime.ofInstant(parameter, UTC_ZONE));
    }

    @Override
    public Instant getNullableResult(ResultSet rs, String columnName) throws SQLException {
        LocalDateTime localDateTime = rs.getObject(columnName, LocalDateTime.class);
        return localDateTime == null ? null : localDateTime.atZone(UTC_ZONE).toInstant();
    }

    @Override
    public Instant getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        LocalDateTime localDateTime = rs.getObject(columnIndex, LocalDateTime.class);
        return localDateTime == null ? null : localDateTime.atZone(UTC_ZONE).toInstant();
    }

    @Override
    public Instant getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        LocalDateTime localDateTime = cs.getObject(columnIndex, LocalDateTime.class);
        return localDateTime == null ? null : localDateTime.atZone(UTC_ZONE).toInstant();
    }
}