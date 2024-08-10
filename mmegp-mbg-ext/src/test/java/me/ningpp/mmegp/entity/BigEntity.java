/*
 *    Copyright 2021-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package me.ningpp.mmegp.entity;

import me.ningpp.mmegp.annotations.Generated;
import me.ningpp.mmegp.annotations.GeneratedColumn;
import me.ningpp.mmegp.mybatis.type.uuid.UUIDStringTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.Date;
import java.util.UUID;

import static org.apache.ibatis.type.JdbcType.VARCHAR;

@Generated(table = "big_entity")
public class BigEntity {
    @GeneratedColumn(name = "id", id = true)
    private String id;
    @GeneratedColumn(name = "f_boolean")
    private boolean fboolean;
    @GeneratedColumn(name = "f_byte")
    private byte fbyte;
    @GeneratedColumn(name = "f_bytes", jdbcType = JdbcType.BINARY)
    private byte[] fbytes;
    @GeneratedColumn(name = "field_bytes", jdbcType = JdbcType.BLOB, blob = true)
    private Byte[] fieldBytes;
    @GeneratedColumn(name = "f_char")
    private char fchar;
    @GeneratedColumn(name = "f_character")
    private Character fcharacter;
    @GeneratedColumn(name = "f_short")
    private short fshort;
    @GeneratedColumn(name = "field_short")
    private Short fieldShort;
    @GeneratedColumn(name = "f_int")
    private int fint;
    @GeneratedColumn(name = "field_integer")
    private Integer fieldInteger;
    @GeneratedColumn(name = "f_int2", jdbcType = JdbcType.TINYINT)
    private int fint2;
    @GeneratedColumn(name = "field_integer2", jdbcType = JdbcType.TINYINT)
    private Integer fieldInteger2;
    @GeneratedColumn(name = "f_long")
    private long flong;
    @GeneratedColumn(name = "field_long")
    private Long fieldLong;
    @GeneratedColumn(name = "f_float")
    private float ffloat;
    @GeneratedColumn(name = "field_float")
    private Float fieldFloat;
    @GeneratedColumn(name = "f_double")
    private double fdouble;
    @GeneratedColumn(name = "field_double")
    private Double fieldDouble;
    @GeneratedColumn(name = "f_bigint")
    private BigInteger fbigint;
    @GeneratedColumn(name = "f_bigdecimal")
    private BigDecimal fbigdecimal;
    @GeneratedColumn(name = "f_date")
    private Date fdate;
    @GeneratedColumn(name = "f_sqldate")
    private java.sql.Date fsqldate;
    @GeneratedColumn(name = "f_timestamp")
    private Timestamp ftimestamp;
    @GeneratedColumn(name = "f_year")
    private Year fyear;
    @GeneratedColumn(name = "f_yearmonth", jdbcType = VARCHAR)
    private YearMonth fyearmonth;
    @GeneratedColumn(name = "f_localdate")
    private LocalDate flocaldate;
    @GeneratedColumn(name = "f_localtime")
    private LocalTime flocaltime;
    @GeneratedColumn(name = "f_localdatetime")
    private LocalDateTime flocaldatetime;
    @GeneratedColumn(name = "f_uuid", jdbcType = VARCHAR, typeHandler = UUIDStringTypeHandler.class)
    private UUID fuuid;
    @GeneratedColumn(name = "f_content", jdbcType = JdbcType.VARCHAR, blob = true)
    private String fcontent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFboolean() {
        return fboolean;
    }

    public void setFboolean(boolean fboolean) {
        this.fboolean = fboolean;
    }

    public byte getFbyte() {
        return fbyte;
    }

    public void setFbyte(byte fbyte) {
        this.fbyte = fbyte;
    }

    public byte[] getFbytes() {
        return fbytes;
    }

    public void setFbytes(byte[] fbytes) {
        this.fbytes = fbytes;
    }

    public Byte[] getFieldBytes() {
        return fieldBytes;
    }

    public void setFieldBytes(Byte[] fieldBytes) {
        this.fieldBytes = fieldBytes;
    }

    public char getFchar() {
        return fchar;
    }

    public void setFchar(char fchar) {
        this.fchar = fchar;
    }

    public Character getFcharacter() {
        return fcharacter;
    }

    public void setFcharacter(Character fcharacter) {
        this.fcharacter = fcharacter;
    }

    public short getFshort() {
        return fshort;
    }

    public void setFshort(short fshort) {
        this.fshort = fshort;
    }

    public Short getFieldShort() {
        return fieldShort;
    }

    public void setFieldShort(Short fieldShort) {
        this.fieldShort = fieldShort;
    }

    public int getFint() {
        return fint;
    }

    public void setFint(int fint) {
        this.fint = fint;
    }

    public Integer getFieldInteger() {
        return fieldInteger;
    }

    public void setFieldInteger(Integer fieldInteger) {
        this.fieldInteger = fieldInteger;
    }

    public int getFint2() {
        return fint2;
    }

    public void setFint2(int fint2) {
        this.fint2 = fint2;
    }

    public Integer getFieldInteger2() {
        return fieldInteger2;
    }

    public void setFieldInteger2(Integer fieldInteger2) {
        this.fieldInteger2 = fieldInteger2;
    }

    public long getFlong() {
        return flong;
    }

    public void setFlong(long flong) {
        this.flong = flong;
    }

    public Long getFieldLong() {
        return fieldLong;
    }

    public void setFieldLong(Long fieldLong) {
        this.fieldLong = fieldLong;
    }

    public float getFfloat() {
        return ffloat;
    }

    public void setFfloat(float ffloat) {
        this.ffloat = ffloat;
    }

    public Float getFieldFloat() {
        return fieldFloat;
    }

    public void setFieldFloat(Float fieldFloat) {
        this.fieldFloat = fieldFloat;
    }

    public double getFdouble() {
        return fdouble;
    }

    public void setFdouble(double fdouble) {
        this.fdouble = fdouble;
    }

    public Double getFieldDouble() {
        return fieldDouble;
    }

    public void setFieldDouble(Double fieldDouble) {
        this.fieldDouble = fieldDouble;
    }

    public BigInteger getFbigint() {
        return fbigint;
    }

    public void setFbigint(BigInteger fbigint) {
        this.fbigint = fbigint;
    }

    public BigDecimal getFbigdecimal() {
        return fbigdecimal;
    }

    public void setFbigdecimal(BigDecimal fbigdecimal) {
        this.fbigdecimal = fbigdecimal;
    }

    public Date getFdate() {
        return fdate;
    }

    public void setFdate(Date fdate) {
        this.fdate = fdate;
    }

    public java.sql.Date getFsqldate() {
        return fsqldate;
    }

    public void setFsqldate(java.sql.Date fsqldate) {
        this.fsqldate = fsqldate;
    }

    public Timestamp getFtimestamp() {
        return ftimestamp;
    }

    public void setFtimestamp(Timestamp ftimestamp) {
        this.ftimestamp = ftimestamp;
    }

    public Year getFyear() {
        return fyear;
    }

    public void setFyear(Year fyear) {
        this.fyear = fyear;
    }

    public YearMonth getFyearmonth() {
        return fyearmonth;
    }

    public void setFyearmonth(YearMonth fyearmonth) {
        this.fyearmonth = fyearmonth;
    }

    public LocalDate getFlocaldate() {
        return flocaldate;
    }

    public void setFlocaldate(LocalDate flocaldate) {
        this.flocaldate = flocaldate;
    }

    public LocalTime getFlocaltime() {
        return flocaltime;
    }

    public void setFlocaltime(LocalTime flocaltime) {
        this.flocaltime = flocaltime;
    }

    public LocalDateTime getFlocaldatetime() {
        return flocaldatetime;
    }

    public void setFlocaldatetime(LocalDateTime flocaldatetime) {
        this.flocaldatetime = flocaldatetime;
    }

    public UUID getFuuid() {
        return fuuid;
    }

    public void setFuuid(UUID fuuid) {
        this.fuuid = fuuid;
    }

    public String getFcontent() {
        return fcontent;
    }

    public void setFcontent(String fcontent) {
        this.fcontent = fcontent;
    }
}
