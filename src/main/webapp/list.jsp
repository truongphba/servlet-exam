<%@ page import="java.util.ArrayList" %>
<%@ page import="entity.Employee" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% ArrayList<Employee> listEmployees = (ArrayList<Employee>) request.getAttribute("list"); %>
<html>
<head>
    <title>Food Form</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <h2>List Employee</h2>
    <table class="table table-striped">
        <thead>
        <tr>
            <th>FullName</th>
            <th>Birthday</th>
            <th>Address</th>
            <th>Position</th>
            <th>Department</th>
        </tr>
        </thead>
        <tbody>
        <% if (listEmployees != null && listEmployees.size() > 0) {
            for (int i = 0; i < listEmployees.size(); i++) {
        %>
        <tr>
            <th><%= listEmployees.get(i).getFullName() %>
            </th>
            <td><%= listEmployees.get(i).getBirthday() %>
            </td>
            <td><%= listEmployees.get(i).getAddress() %>
            </td>
            <td><%= listEmployees.get(i).getPosition() %>
            </td>
            <td><%= listEmployees.get(i).getDepartment() %>
            </td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>
</div>
</body>
</html>
