<%@ page import="edu.msu.mi.loom.UserSession; edu.msu.mi.loom.User" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <g:hiddenField id="sessionId" name="sessionId" value="${session.id}"/>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>

            <section class="content-header my-padding">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <div class="box box-success box-solid  m-a-2">
                            <div class="box-header with-border">
                                <h3 class="box-title">Waiting room</h3>

                                <div class="box-tools pull-right"></div>
                            </div>

                            <div class="box-body">
                                <loom:progressBar userCount="${edu.msu.mi.loom.UserSession.countBySessionAndState(session,"WAITING")}"
                                                  userMaxCount="${session.exp.userCount}"/>
                                <a href="javascript:void(0);" id="stop-waiting" class="btn btn-block btn-success">Stop waiting</a>

                            </div>
                        </div>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>
        </div>
    </div>

    <script type="text/javascript">



        jQuery(document).ready(function () {
            var session = ${session.id};
            window.onbeforeunload = logout;

            setInterval(function () {
                jQuery.ajax({
                    url: "/loom/session/checkExperimentReadyState",
                    type: 'POST',
                    data: {
                        session: session
                    }
                }).success(function (data) {
                    if (data.experiment_ready) {
                        shouldLogout = false;
                        window.location = "/loom/session/s/" + session+"?workerId=${username}";
                    } else {
                        updateProgressBar(data.count, ${session.exp.userCount})
                    }
                }).error(function () {
                    window.location = "/loom/"

                });
            }, 3000);


        });
    </script>
</g:applyLayout>