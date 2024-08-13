package me.ningpp.mmegp.demo.entity;

import me.ningpp.mmegp.annotations.Generated;
import me.ningpp.mmegp.annotations.GeneratedColumn;
import me.ningpp.mmegp.annotations.SoftDelete;

@Generated
@SoftDelete(column = "deleted")
public class SysAutoUser {
    @GeneratedColumn(id = true, generatedValue = true)
    private Integer id;
    @GeneratedColumn
    private String firstName;
    @GeneratedColumn
    private String lastName;
    @GeneratedColumn
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
