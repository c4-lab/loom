<g:applyLayout name="main">
    <div class="login-box">
        <div class="login-box-body">
            <p class="login-box-msg"><g:message code="page.auth.caption.label"/></p>

            <p class="alert-error"><g:message code="${flash.message}"/></p>

            <g:form controller='home' action="authenticate" method='POST' autocomplete='off'>
                <div class="form-group has-feedback">
                    <input type="text" name='j_username' class="form-control" placeholder="Username">
                </div>

                <div class="row">
                    <div class="col-xs-8">
                    </div>

                    <div class="col-xs-4">
                        <input type='submit' id="submit"
                               class="btn btn-primary btn-block btn-flat"
                               value='${message(code: "springSecurity.login.button")}'/>
                    </div>
                </div>
            </g:form>
        </div>
    </div>
</g:applyLayout>