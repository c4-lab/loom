<%@ page import="java.text.DecimalFormat" %>
<html>
<head>
    <title>Game Over</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="wrapper">
    <div class="content-wrapper container">
        <section class="content">

            <div class="row">
                <div class="col-md-2"></div>

                <div class="col-md-8">
                    <p>
                        Thanks for playing the Story Loom! Hope you had fun and that you come back and try it again.
                        <g:if test="${isTurker}">
                            Please enter your confirmation code into the HIT on the Mturk site to receive credit!!!
                        </g:if>

                    </p>


                    <h1 class="text-center">
                        ${completionCode}
                    </h1>

                </div>

                <div class="col-md-2"></div>
            </div>


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
                            <td>${new java.text.DecimalFormat("####0.00").format(scores.sum() / scores.size())}</td>
                        </tr>

                        </tbody>
                    </table>

                    <p>
                        Please contact the requester, Qiusi Sun, via email at <a
                            href="mailto:sunyqs@gmail.com">sunyqs@gmail.com</a> if you have any questions or concerns.
                    </p>

                </div>

                <div class="col-md-2"></div>
            </div>

        </section>
    </div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        shouldLogout = true;
        logout();

    });
</script>

</body>
</html>
