<html>
<head>
    <title>Sign in</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="wrapper">
    <div class="content-wrapper container">
        <div class="login-box">
            <div class="login-box-body">
                <p class="login-box-msg"><g:message code="page.auth.caption.label"/></p>

                <p class="alert-error"><g:message code="${flash.message}"/></p>

                <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>

                    <div class="form-group has-feedback">
                        <input type="text" name='j_username' class="form-control" placeholder="Username"/>
                    </div>

                    <div class="form-group has-feedback">
                        <input type="password" name='j_password' class="form-control" placeholder="Password"/>
                    </div>

                    <div class="row">
                        <div class="col-xs-8">
                            <g:link controller="user" action="registration">New User ?</g:link>
                        </div>

                        <div class="col-xs-4">
                            <input type='submit' id="submit"
                                   class="btn btn-primary btn-block btn-flat"
                                   value='${message(code: "springSecurity.login.button")}'/>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>