package me.ningpp.mmegp.demo.entity;

import me.ningpp.mmegp.annotations.Column;
import me.ningpp.mmegp.annotations.Table;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.apache.ibatis.type.JdbcType.VARCHAR;

@Table(table = "sys_company")
public class SysCompanySimple {
    @Column(name = "id", jdbcType = VARCHAR, id = true)
    private String id;

    @Column(name = "name", jdbcType = VARCHAR)
    private String name;

    @Column(name = "start_date", jdbcType = JdbcType.DATE)
    private LocalDate startDate;

    @Column(name = "market_cap", jdbcType = JdbcType.DECIMAL)
    private BigDecimal marketCap;

    @Column(name = "unified_code", jdbcType = VARCHAR)
    private String unifiedCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public String getUnifiedCode() {
        return unifiedCode;
    }

    public void setUnifiedCode(String unifiedCode) {
        this.unifiedCode = unifiedCode;
    }
}
