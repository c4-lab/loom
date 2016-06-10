<g:applyLayout name="main">
    <div class="login-box">
        <div class="login-box-body">
            <p class="login-box-msg"><g:message code="page.registration.caption.label"/></p>

            <p class="alert-error"><g:message code="${message}"/></p>

            <g:eachError bean="${user}">
                <li class="alert-error"><g:message error="${it}"/></li>
            </g:eachError>

            <g:form controller='user' action="registration" method='POST' autocomplete='off'>
                <input type="hidden" name="original" value="original"/>
                <div class="form-group has-feedback">
                    <input type="text" name='username' value="${user?.username}" class="form-control"
                           placeholder="Username"/>
                </div>

                <div class="form-group has-feedback">
                    <input type="password" name='password' class="form-control" placeholder="Password"/>
                </div>

                <div class="form-group has-feedback">
                    <input type="password" name='confPassword' class="form-control" placeholder="Confirm Password"/>
                </div>


                <div class="row">
                    <div class="col-xs-8">
                        <g:link controller="login" action="auth">Sign in</g:link>
                    </div>

                    <div class="col-xs-4">
                        <input type='submit' id="submit"
                               class="btn btn-primary btn-block btn-flat"
                               value='${message(code: "page.reg.signup.button")}'/>
                    </div>
                </div>
            </g:form>
        </div>
    </div>
</g:applyLayout>