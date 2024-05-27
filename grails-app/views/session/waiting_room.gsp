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
                                <!--TODO: Need to fix the logic for counting the number of users in the waiting room -->
                                <loom:progressBar userCount="${0}"
                                                  userMaxCount="${session.sessionParameters.safeGetMaxNode()}"/>
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

            $("#stop-waiting").click(function() {
                shouldLogout = false;
                window.location="/loom/session/stopWaiting?session="+$("#sessionId").val();
            });

            startWaitingTimer(function() {
                jQuery.ajax({
                    url: "/loom/session/checkExperimentReadyState",
                    type: 'GET',
                    data: {
                        session: session
                    }
                }).success(function (data) {
                    if (data.experiment_ready) {
                        shouldLogout = false;
                        window.location = "/loom/session/s/" + session+"?workerId=${username}";
                    } else {
                        document.title = "Waiting...";
                        updateProgressBar(data.count, ${session.sessionParameters.safeGetMaxNode()})
                    }
                }).error(function () {
                    window.location = "/loom/"

                });
            })



        });
    </script>
</g:applyLayout>