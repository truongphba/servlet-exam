package controller;

import entity.Employee;
import helper.GenericValidateClass;
import service.EmployeeService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateEmployeeController extends HttpServlet {

    private EmployeeService employeeService = new EmployeeService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String fullName = req.getParameter("fullName");
        String address = req.getParameter("address");
        String position = req.getParameter("position");
        String department = req.getParameter("department");
        String birthday = req.getParameter("birthday");
        Employee employee = new Employee(fullName, birthday, address, position, department);

        GenericValidateClass<Employee> foodGenericValidateClass = new GenericValidateClass<>(Employee.class);
        System.out.println("Validate");
        System.out.println(foodGenericValidateClass.validate(employee));
        System.out.println("End Validate");
        // validate
        if(!foodGenericValidateClass.validate(employee)){
            System.out.println("Have Error");
            HashMap<String, ArrayList<String>> errors = foodGenericValidateClass.getErrors();

            req.setAttribute("errors", errors);
            req.setAttribute("food", employee);
            req.getRequestDispatcher("/form.jsp").forward(req, resp);
            return;
        }
        System.out.println(foodGenericValidateClass.getErrors());
        System.out.println("Create Food");
        employeeService.create(employee);
        resp.sendRedirect("/list");
    }
}
