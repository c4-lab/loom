<g:applyLayout name="main">
    <div class="login-box">
        <div class="login-box-body">
            <p class="login-box-msg"><g:message code="page.auth.caption.label"/></p>

            <p class="alert-error"><g:message code="${flash.message}"/></p>

            %{--<g:form controller='home' action="authenticate" method='POST' autocomplete='off'>--}%
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
</g:applyLayout>