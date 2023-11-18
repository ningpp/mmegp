package me.ningpp.mmegp.demo.entity;

import me.ningpp.mmegp.annotations.Generated;
import me.ningpp.mmegp.annotations.GeneratedColumn;
import me.ningpp.mmegp.annotations.SoftDelete;
import org.apache.ibatis.type.JdbcType;

@Generated(table = "sys_auto_user")
@SoftDelete(column = "deleted")
public class SysAutoUser {
    @GeneratedColumn(name = "id", jdbcType = JdbcType.INTEGER, id = true, generatedValue = true)
    private Integer id;
    @GeneratedColumn(name = "first_name", jdbcType = JdbcType.VARCHAR)
    private String firstName;
    @GeneratedColumn(name = "last_name", jdbcType = JdbcType.VARCHAR)
    private String lastName;
    @GeneratedColumn(name = "deleted", jdbcType = JdbcType.TINYINT)
    private Byte deleted;

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

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }
}
