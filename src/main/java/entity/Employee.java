package entity;

import myanotation.Column;
import myanotation.Entity;
import myanotation.Id;
import myanotation.Validate;

import java.util.ArrayList;
import java.util.HashMap;

@Entity(tableName = "employees")
public class Employee {
    @Id(autoIncreament = true)
    @Column(columnName = "id", columnType = "int")
    @Validate(required = true, requiredMessage = "Id is required")
    private int id;

    @Column(columnName = "fullName", columnType = "varchar(255)")
    @Validate(required = true, requiredMessage = "FullName is required")
    private String fullName;

    @Column(columnName = "birthday", columnType = "varchar(255)")
    @Validate(required = true, requiredMessage = "Birthday is required")
    private String birthday;

    @Column(columnName = "address", columnType = "varchar(255)")
    @Validate(required = true, requiredMessage = "Address is required")
    private String address;

    @Column(columnName = "position", columnType = "varchar(255)")
    @Validate(required = true, requiredMessage = "Position is required")
    private String position;

    @Column(columnName = "department", columnType = "varchar(255)")
    @Validate(required = true, requiredMessage = "Department is required")
    private String department;

    private HashMap<String, ArrayList<String>> errors;

    public Employee() {
    }
    public Employee(String fullName, String birthday, String address, String position, String department) {
        this.fullName = fullName;
        this.birthday = birthday;
        this.address = address;
        this.position = position;
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", address='" + address + '\'' +
                ", position='" + position + '\'' +
                ", department='" + department + '\'' +
                '}';
    }

    public HashMap<String, ArrayList<String>> getErrors() {
        return new HashMap<>();
    }

}
