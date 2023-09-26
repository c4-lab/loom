<%@ page import="com.amazonaws.mturk.requester.HITStatus; edu.msu.mi.loom.MturkHIT; edu.msu.mi.loom.ConstraintProvider; edu.msu.mi.loom.Session; edu.msu.mi.loom.ConstraintTest; edu.msu.mi.loom.Story; edu.msu.mi.loom.CrowdService; edu.msu.mi.loom.TrainingSet; edu.msu.mi.loom.ExpType" %>

<g:applyLayout name="main">
    <div class="wrapper">
        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper container">
            <!-- Content Header (Page header) -->
            <section class="content-header">

                <ol class="breadcrumb">
                    <li><a href="javascript:void(0);"><i class="fa fa-dashboard"></i> Home</a></li>
                    <li class="active">Admin board</li>
                </ol>

            </section>

            <!-- Main content -->
            <section class="content">
                <g:if test="${flash.error}">
                    <div class="alert alert-error alert-dismissible" role="alert">
                        ${flash.error}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        This is a dismissible alert!
                    </div>
                </g:if>
                <g:if test="${flash.message}">
                    <div class="alert alert-info alert-dismissible" role="alert">
                        ${flash.message}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        This is a dismissible alert!
                    </div>

                    <div class="alert alert-success">${flash.message}</div>
                </g:if>
                <div class="row">
                    <div class="col-sm-3">

                        <!-- Profile Image -->
                        <div class="box box-primary">
                            <div class="box-body box-profile">
                                <asset:image src="avatar.jpg" class="profile-user-img img-responsive img-circle"
                                             alt="User profile picture"/>
                                <h3 class="profile-username text-center">Admin</h3>

                                <p class="text-muted text-center">Super user</p>

                                <ul class="list-group list-group-unbordered">
                                    <li class="list-group-item">
                                        <b>Experiments</b> <a class="pull-right">${experiments.size()}</a>
                                    </li>
                                    <li class="list-group-item">
                                        <b>Sessions</b> <a class="pull-right">${sessions.size()}</a>
                                    </li>
                                    <li class="list-group-item">
                                        <b>Trainings</b> <a class="pull-right">${trainings.size()}</a>
                                    </li>
                                </ul>

                                <a href="javascript:void(0);" id="create-experiment-button"
                                   class="btn btn-primary btn-block"><b>Create an experiment</b></a>


                                <a href="javascript:void(0);" id="create-trainingset"
                                   class="btn btn-primary btn-block"><b>Create a training set</b></a>

                                <a href="javascript:void(0);" id="create-reading"
                                   class="btn btn-primary btn-block"><b>Create a reading test</b></a>

                                <a href="javascript:void(0);" id="create-survey"
                                   class="btn btn-primary btn-block"><b>Create a survey</b></a>

                                <a href="javascript:void(0);" id="create-simulation"
                                   class="btn btn-primary btn-block"><b>Create a simulation</b></a>

                                <a href="javascript:void(0);" id="create-stories"
                                   class="btn btn-primary btn-block"><b>Create a story set</b></a>

                                <a href="javascript:void(0);" id="create-users"
                                   class="btn btn-primary btn-block"><b>Create users</b></a>

                                <a href="javascript:void(0);" id="create-credentials"
                                   class="btn btn-primary btn-block"><b>Create credentials</b></a>

                                <g:link controller="admin" action="exportCSV" id="export-csv"
                                        class="btn btn-primary btn-block"><b>Export CSV</b></g:link>
                            </div><!-- /.box-body -->
                        </div><!-- /.box -->

                    </div><!-- /.col -->

                    <div class="col-sm-9">
                        <div class="nav-tabs-custom">
                            <ul class="nav nav-tabs">
                                <li class="active"><a href="#sessions" data-toggle="tab">Sessions</a></li>
                                %{--                               <li><a href="#session-hit" data-toggle="tab">Sessions-HIT</a></li>--}%
                                <li><a href="#experiments" data-toggle="tab">Experiments</a></li>
                                <li><a href="#trainings" data-toggle="tab">Trainings</a></li>
                                %{--                                <li><a href="#training-hit" data-toggle="tab">Trainings-HIT</a></li>--}%
                                <li><a href="#stories" data-toggle="tab">Stories</a></li>
                                <li><a href="#users" data-toggle="tab">Users</a></li>
                                <li><a href="#credentials" data-toggle="tab">Credentials</a></li>

                            </ul>

                            <div class="tab-content">
                                <div class="active tab-pane" id="sessions">
                                    <g:each in="${sessions}" var="loomSession">
                                        ${loomSession.name}
                                        <g:render template="session_detail" model="[loomSession: loomSession]"/>
                                    </g:each>
                                </div>


                                <div class="tab-pane" id="experiments">
                                    <table class="table table-bordered grid" border="1">
                                        <th style="text-align:center">Title</th>
                                        <th style="text-align:center">Created</th>
                                        <th style="text-align:center">Story</th>
                                        <th style="text-align:center">Network</th>
                                        <th style="text-align:center">Available sessions</th>
                                        <th style="text-align:center">Total sessions</th>
                                        <th style="text-align:center">Action</th>
                                        <tbody>
                                        <g:each in="${experiments}" var="experiment">
                                            <tr>
                                                <td>
                                                    %{--                                             <th>Title</th>--}%
                                                    <g:link controller="admin" action="view"
                                                            params="[experiment: experiment.id]">${experiment.name}</g:link>
                                                </td>
                                                <td>
                                                    %{--                                            <th>Created</th>--}%
                                                    <g:formatDate
                                                            format="yyyy/MM/dd HH:mm"
                                                            date="${experiment.created}"/></td>
                                                <td>
                                                    %{--                                            <th>Story</th>--}%

                                                    <g:link controller="admin" action="view"
                                                            params="[story: experiment.defaultSessionParams.story]">${experiment.defaultSessionParams.story?.name}</g:link>

                                                </td>

                                                <td>
                                                    %{--                                            <th>Network</th>--}%
                                                    ${experiment.defaultSessionParams?.networkTemplate?.class?.simpleName}
                                                </td>

                                                <td>
                                                    ${Session.countByExpAndStateInList(experiment,
                                                            [Session.State.PENDING, Session.State.WAITING, Session.State.ACTIVE])}
                                                    %{--                                            <th>Available sessions</th>--}%
                                                </td>
                                                <td>
                                                    %{--                                            <th>Total sessions</th>--}%
                                                    ${Session.countByExp(experiment)}
                                                </td>
                                                <td>
                                                    %{--                                            <th>Action</th> --}%
                                                    <button type="button"
                                                            class="btn btn-primary create-session-button">Create session
                                                        <span
                                                                class="expid hidden">${experiment.id}</span>
                                                    </button>

                                                </td>
                                            </tr>
                                        </g:each>
                                        </tbody>

                                    </table>
                                </div>


                                <div class="tab-pane" id="trainings">

                                    <table class="table table-bordered grid" border="1">
                                        <th style="text-align:center">TrainingSet</th>
                                        <th style="text-align:center">Interface</th>
                                        <th style="text-align:center">Simulation</th>
                                        <th style="text-align:center">Reading</th>
                                        <th style="text-align:center">Surveys</th>
                                        <th style="text-align:center">State</th>
                                        <th style="text-align:center">HITs (Avail/Tot)</th>
                                        <th style="text-align:center">Action</th>
                                        <tbody>
                                        <g:each in="${trainings}" var="trainingSet">
                                            <tr>
                                                <td>
                                                    %{--                                             <th>Title</th>--}%
                                                    <g:link controller="admin" action="view"
                                                            params="[trainingId: trainingSet.id]">${trainingSet.name}</g:link>
                                                </td>
                                                <td>
                                                    ${trainingSet.uiflag ? "Paragraph" : "List"}
                                                </td>
                                                <td>
                                                    %{--                                            <th>Story</th>--}%
                                                    <g:if test="${trainingSet.simulations}">
                                                        <g:each in="${trainingSet.simulations}" var="simulation">
                                                            <g:link controller="admin" action="view"
                                                                    params="[simulation: simulation.id]">
                                                                ${simulation.story.name}</g:link>
                                                        </g:each>
                                                    </g:if>
                                                    <g:else>
                                                        --
                                                    </g:else>
                                                </td>
                                                <td>
                                                    <g:if test="${trainingSet.readings}">
                                                        <g:each in="${trainingSet.readings}" var="reading">
                                                            <g:link controller="admin" action="view"
                                                                    params="[reading: reading.id]">
                                                                ${reading.name}</g:link>
                                                        </g:each>
                                                    </g:if>
                                                    <g:else>
                                                        --
                                                    </g:else>
                                                </td>
                                                <td>
                                                    <g:if test="${trainingSet.surveys}">
                                                        <g:each in="${trainingSet.surveys}" var="survey">
                                                            <g:link controller="admin" action="view"
                                                                    params="[survey: survey.id]">
                                                                ${survey.name}</g:link>
                                                        </g:each>
                                                    </g:if>
                                                    <g:else>
                                                        --
                                                    </g:else>
                                                </td>
                                                <td>
                                                    ${trainingSet.state}
                                                </td>
                                                <td>
                                                    ${trainingSet.countByHitStatus("Assignable")}/${trainingSet.countByHitStatus(null)}
                                                </td>
                                                <td>
                                                    <button type="button"
                                                            class="btn btn-primary show-training-launch-modal">
                                                        <g:if test="${trainingSet.state == TrainingSet.State.PENDING}">
                                                            Launch
                                                            <span class="mode hidden">launch</span>
                                                        </g:if>
                                                        <g:else>
                                                            Cancel
                                                            <span class="mode hidden">cancel</span>
                                                        </g:else>
                                                        <span class="trainingId hidden">${trainingSet.id}</span>

                                                    </button>

                                                </td>
                                            </tr>
                                        </g:each>
                                        </tbody>

                                    </table>
                                </div>

                                <div class="tab-pane" id="stories">
                                    <g:each in="${stories}" var="story">
                                        <div class="post">
                                            <div class="user-block">
                                                <span class='username'>
                                                    <g:link controller="admin" action="view"
                                                            params="[training: story.id]">${story.name}</g:link>

                                                </span>
                                                <span class='description'>
                                                    ${story.toString()}
                                                </span>

                                            </div>


                                            <ul class="list-inline">
                                            </ul>
                                        </div>
                                    </g:each>
                                </div>

                                <div class="tab-pane" id="users">
                                    <g:each in="${users}" var="user">
                                        <div class="post">
                                            <div class="user-block">
                                                %{--                                                <g:if test="${user.username}!="admin">--}%
                                                <span class='username'>
                                                    Username: ${user.username}

                                                </span>
                                                <span class='description'>
                                                    Created Date: ${user.dateCreated}
                                                </span>

                                            </div>


                                            <ul class="list-inline">
                                            </ul>
                                        </div>
                                    </g:each>
                                </div>

                                <div class="tab-pane" id="credentials">
                                    <g:each in="${credentials}" var="cred">
                                        <div class="post">
                                            <div class="user-block">
                                                %{--                                                <g:if test="${user.username}!="admin">--}%
                                                <span class='username'>
                                                    ${cred.formattedName}
                                                    <g:link controller="admin" action="deleteCredential"
                                                            class='pull-right btn-box-tool'
                                                            params="[credentialId: cred.id]">
                                                        <i class='fa fa-times'></i>
                                                    </g:link>
                                                </span>

                                                <span class='description'>
                                                    Service: ${cred.serviceType}
                                                </span>

                                                <span class='description'>
                                                    Access Key: ${cred.access_key}
                                                </span>

                                            </div>


                                            <ul class="list-inline">
                                            </ul>
                                        </div>
                                    </g:each>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

            </section>
        </div>

        <div class="control-sidebar-bg"></div>
    </div>
</g:applyLayout>

<div id="constraint-source-inputs" style="display: none">
    <!-- This code is just template code for filling in the constraint table below -->
    <table>
        <tr>
            <td>
                <g:select name="constraint"
                          from="${ConstraintProvider.list().findAll() { it.class != Story.class }}"
                          optionKey="id" style="color: black"
                          optionValue="constraintTitle" noSelection="${['': 'Select One...']}"/>
            </td>
            <td>
                <g:select name="operator"
                          from="${ConstraintTest.Operator}" style="color: black"
                          noSelection="${['': 'Select One...']}"/>
            </td>
            <td>
                <input type="text" name="parameters" value="" placeholder="Enter a value">
            </td>
            <td>
                <button type="button" class="btn btn-primary remove-constraint">Remove</button>
            </td>

        </tr>
    </table>
</div>

<g:render template="experiment_modal"/>
<g:render template="reading_modal"/>
<g:render template="simulation_modal"/>
<g:render template="survey_modal"/>
<g:render template="training_set_modal"/>
<g:render template="story_modal"/>
<g:render template="create_session_modal" model="[stories: stories]"/>
<g:render template="launch_training_modal"/>
<g:render template="create_users_modal"/>
<g:render template="create_credentials_modal"/>

<script type="application/javascript">
    var intervalId

    $(document).ready(function () {
        var hash = location.hash.replace(/^#/, '');  // ^ means starting, meaning only match the first hash
        if (hash) {
            $('.nav-tabs a[href="#' + hash + '"]').tab('show');
        }

        // Change hash for page-reload
        $('.nav-tabs a').on('shown.bs.tab', function (e) {
            window.location.hash = e.target.hash;
        });

        let sessionCount = $('.post.session-info.panel.panel-info').length;
        if (sessionCount > 0) {
            intervalId = setInterval(function () {
                updateSessionInfo()
            }, 1000)
        }

        $('button.show-session-cancel').on('click', function (e) {
            clearInterval(intervalId)
            const id = $(".session-id", this.parentNode).text()
            window.location.href = "/loom/admin/cancelSession?sessionId=" + id
        });

        $('button.show-session-clone').on('click', function (e) {
            clearInterval(intervalId)
            const id = $(".session-id", this.parentNode).text()
            window.location.href = "/loom/admin/cloneSession?sessionId=" + id
        });


    });

    function credentialsShowMTurkOptions(event) {
        if ($(event.target).val() === "${CrowdService.MTURK.toString()}") {
            $("#credentials-model-sandbox-options").show()
        } else {
            $("#credentials-model-sandbox-options").hide()
        }
    }


    function updateSessionInfo() {
        $.ajax({
            type: "GET",
            url: "/loom/admin/getDynamicSessionInfo",
            dataType: "json",
            success: function (result) {
                console.log(result)
                for (let [sessionid, info] of Object.entries(result['waiting'])) {
                    let domElt = $("#session-info-" + sessionid)
                    for (let [key, value] of Object.entries(info)) {
                        $(".session-wait-" + key, domElt).text(value)
                    }
                    $(".session-waiting-block", domElt).removeClass("hidden")
                    $("button.show-session-cancel", domElt).prop("disabled", false)
                    domElt.removeClass("panel-info")
                    domElt.addClass("panel-warning")
                    domElt.addClass("panel-success")


                }
                for (let [sessionid, info] of Object.entries(result['active'])) {
                    let domElt = $("#session-info-" + sessionid)
                    for (let [key, value] of Object.entries(info)) {
                        $(".session-active-" + key, domElt).text(value)
                    }
                    $(".session-active-block", domElt).show()
                    $("button.show-session-cancel", domElt).prop("disabled", false)
                    domElt.removeClass("panel-warning")
                    domElt.addClass("panel-success")
                }
            }
        });

    }


</script>


