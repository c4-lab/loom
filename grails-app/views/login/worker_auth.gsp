<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <div class="login-box">
                <div class="login-box-body">
                    <g:if test="${flash.message}">
                        <p class="alert-error"><g:message code="${flash.message}"/></p>
                    </g:if>
                    <p class="login-box-msg">Sign in with your worker id (Prolific or Mturk) start your session</p>

                    <form action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>
                        <div class="form-group has-feedback">
                            <input type="text" name='workerId' class="form-control" placeholder="Username">
                            <input type="hidden" name="origURI" value="${origURI}">
                        </div>



                        <div class="row">
                            <div class="col-xs-8">
                            </div>

                            <div class="col-xs-4">
                                <button type="submit" class="btn btn-primary btn-block btn-flat">Sign In</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</g:applyLayout>