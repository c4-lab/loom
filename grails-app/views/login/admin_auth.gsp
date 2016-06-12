<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <div class="login-box">
                <div class="login-box-body">
                    <p class="login-box-msg">Sign in to start your session</p>

                    <form action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>
                        <div class="form-group has-feedback">
                            <input type="text" name='j_username' class="form-control" placeholder="Username">
                        </div>

                        <div class="form-group has-feedback">
                            <input type="password" name="j_password" class="form-control" placeholder="Password">
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