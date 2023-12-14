<%@ page import="edu.msu.mi.loom.UserSession; edu.msu.mi.loom.User" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <p>

                                The session has been cancelled by the administrator due to a server error or the loss of participants. You will still be compensated for your participation.


                        </p>
%{--                        <p>--}%
%{--                            If you wish to keep waiting, click <a href="/loom/session/s/${session.id}?workerId=${user.username}">here</a>.--}%
%{--                        </p>--}%
                        <p>
                            Please enter the following confirmation code into the HIT to get credit for your time. Hope to see you again!
                        </p>
                        <h1 class="text-center">
                            ${session.fullCode}
                        </h1>

                    </div>
                    <div class="col-md-1"></div>
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
</g:applyLayout>