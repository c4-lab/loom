<%--
  Created by IntelliJ IDEA.
  User: josh
  Date: 5/16/16
  Time: 9:16 AM
--%>

<%@ page import="java.text.SimpleDateFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Loom Trainings</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="wrapper">
    <div class="content-wrapper container">
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <h1>Your Trainings</h1>
                    <table class="table">
                        <thead>
                        <tr>
                            <th>Training</th>
                            <th>Date completed</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${model.entrySet()}" var="ts">

                        <tr>
                            <td>${ts.key.name}</td>
                            <td>${ts.value?ts.value.getDateTimeString():"--"}</td>
                            <td>
                                <g:if test="${ts.value == null}">
                                    <g:createLink controller="training" action="training" id="${ts.key.id}">Take training</g:createLink>
                                </g:if></td>

                        </tr>
                        </g:each>

                        </tbody>
                    </table>

                </div>
            </div>

        </section>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        shouldLogout = true;
        logout();
    });
</script>


</body>
</html>