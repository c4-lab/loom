<%--
  Created by IntelliJ IDEA.
  User: josh
  Date: 5/8/16
  Time: 7:36 PM
--%>

<%@ page import="java.text.DecimalFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Training Complete</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="wrapper">
    <div class="content-wrapper container">
        <section class="content">
            <div class="row">
                <div class="col-md-2"></div>

                <div class="col-md-8">
                    <table class="table table-condensed table-centered">
                        <thead>
                        <tr>
                            <th>Round</th> <th>Score</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${scores}" var="score" status="idx">
                            <tr>
                                <td>${idx}</td><td>${score}</td>
                            </tr>
                        </g:each>
                        <tr>
                            <td><b>Average:</b></td>
                            <td>${new DecimalFormat("####0.00").format(scores.sum() / scores.size())}</td>
                        </tr>

                        </tbody>
                    </table>

                    <div class="center-block">
                        <g:form controller="training" action="trainingComplete">
                            <g:hiddenField name="trainingSetId" value="${trainingId}"/>
                            <g:submitButton name="continue" class="btn btn-success" value="Continue"/>
                        </g:form>
                    </div>
                </div>

                <div class="col-md-2"></div>
            </div>
        </section>
    </div>
</div>

</body>
</html>