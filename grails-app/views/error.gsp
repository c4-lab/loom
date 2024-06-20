<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
    <meta name="layout" content="main">
</head>

<body>
<div class="wrapper">
    <div class="content-wrapper container">
        <section class="content">
            <div class="row">
                <div class="col-md-12">

                    <h1>Sorry!  Something bad happened</h1>

                    <p>
                        Unfortunately, we encountered an internal error.  Please email <a
                            href="mailto:sunyqs@gmail.com">sunyqs@gmail.com</a> with the following information.

                    </p>
                    <g:if test="${message}">
                        <p>
                            <h2>Message:</h2>
                           ${message}
                        </p>
                    </g:if>

                    <p>

                        <g:if test="${exception}">
                            <h2>Full Stack Trace:</h2>
                            <g:renderException exception="${exception}"/>
                        </g:if>

                    </p>

                </div>
            </div>
        </section>
    </div>
</div>
</body>
</html>
