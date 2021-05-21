<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="entity.Employee" %>
<%@ page import="java.util.ArrayList" %>
<%
    HashMap<String, ArrayList<String>> errors = (HashMap<String, ArrayList<String>>) request.getAttribute("errors");
    if (errors == null) {
        errors = new HashMap<>();
    }
%>
<html>
<head>
    <title>Food Form</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <style>
        .error-msg {
            color: red;
        }
    </style>
</head>
<body>
   <div class="container">
       <h2>Create Employee</h2>
       <form method="post" action="/create">
           <div class="form-group">
               <label>Full Name:</label>
               <input type="text" name="fullName" class="form-control">
               <span class="error-msg">
                    <%
                        if (errors.containsKey("fullName")) {
                            ArrayList<String> fullNameErrors = errors.get("fullName");
                            for (String msg : fullNameErrors) {
                    %>
                    <li><%=msg%></li>
                    <%
                            }
                        }%>
                </span>
           </div>
           <div class="form-group">
               <label>Birthday:</label>
               <input type="date" name="birthday" class="form-control">
               <span class="error-msg">
                    <%
                        if (errors.containsKey("birthday")) {
                            ArrayList<String> fullNameErrors = errors.get("birthday");
                            for (String msg : fullNameErrors) {
                    %>
                    <li><%=msg%></li>
                    <%
                            }
                        }%>
                </span>
           </div>
           <div class="form-group">
               <label>Address:</label>
               <input type="text" name="address" class="form-control">
               <span class="error-msg">
                    <%
                        if (errors.containsKey("address")) {
                            ArrayList<String> fullNameErrors = errors.get("address");
                            for (String msg : fullNameErrors) {
                    %>
                    <li><%=msg%></li>
                    <%
                            }
                        }%>
                </span>
           </div>
           <div class="form-group">
               <label>Position:</label>
               <input type="text" name="position" class="form-control">
               <span class="error-msg">
                    <%
                        if (errors.containsKey("position")) {
                            ArrayList<String> fullNameErrors = errors.get("position");
                            for (String msg : fullNameErrors) {
                    %>
                    <li><%=msg%></li>
                    <%
                            }
                        }%>
                </span>
           </div>
           <div class="form-group">
               <label>Department:</label>
               <input type="text" name="department" class="form-control">
               <span class="error-msg">
                    <%
                        if (errors.containsKey("department")) {
                            ArrayList<String> fullNameErrors = errors.get("department");
                            for (String msg : fullNameErrors) {
                    %>
                    <li><%=msg%></li>
                    <%
                            }
                        }%>
                </span>
           </div>
           <div class="form-group">
                <button class="btn btn-success">Submit</button>
                <button type="button" class="btn btn-primary" id="reset">Reset</button>
           </div>
       </form>
   </div>
<script>
    $('#reset').click(function (){
        $('input').val('')
    })
</script>
</body>
</html>
