package me.ningpp.mmegp.demo.entity;

import me.ningpp.mmegp.annotations.Table;
import me.ningpp.mmegp.annotations.Column;
import me.ningpp.mmegp.annotations.SoftDelete;

@Table
@SoftDelete(column = "deleted")
public class SysAutoUser {
    @Column(id = true, generatedValue = true)
    private Integer id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
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
