package me.ningpp.mmegp.demo.entity;

import me.ningpp.mmegp.annotations.Generated;
import me.ningpp.mmegp.annotations.GeneratedColumn;
import org.apache.ibatis.type.JdbcType;

@Generated(table = "sys_auto_user")
public class SysAutoUser {
    @GeneratedColumn(name = "id", jdbcType = JdbcType.INTEGER, id = true, generatedValue = true)
    private Integer id;
    @GeneratedColumn(name = "first_name", jdbcType = JdbcType.VARCHAR)
    private String firstName;
    @GeneratedColumn(name = "last_name", jdbcType = JdbcType.VARCHAR)
    private String lastName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
