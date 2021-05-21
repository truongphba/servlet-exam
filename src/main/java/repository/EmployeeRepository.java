package repository;

import entity.Employee;
import helper.GenericModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    private static List<Employee> list = new ArrayList<>();
    private GenericModel<Employee> genericModel = new GenericModel<>(Employee.class);

    public boolean save(Employee employee){
        genericModel.save(employee);
        return true;
    }
    public List<Employee> findAll(){
        List<Employee> list = genericModel.findAll();
        return list;
    }
}
