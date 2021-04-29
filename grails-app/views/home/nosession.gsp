<%@ page import="edu.msu.mi.loom.UserSession; edu.msu.mi.loom.User" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">

                        <p>
                            There are no sessions for which you are qualified running right now.  Please contact the administrator (jeintron@syr.edu) if you have questions about how to participate.
                        </p>


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