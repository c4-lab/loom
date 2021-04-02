<%@ page import="edu.msu.mi.loom.UserSession; edu.msu.mi.loom.User" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <p>
%{--                            <g:if test="${time > 60}">--}%
%{--                                The session has been cancelled by the administrator. You have been waiting for at least an hour!  Sorry not enough people showed up.  In addition to the--}%
%{--                                payment for the HIT, you will recieve the maximum allowable bonus for waiting, which is $1.80.--}%

%{--                            </g:if>--}%
%{--                            <g:else>--}%
                                You already joined this session, please return this HIT if you accidently accept it, thank you!


%{--                            </g:else>--}%
                        </p>

                        <h1 class="text-center">
                            ${session.waitingCode}
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