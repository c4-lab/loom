<%@ page import="edu.msu.mi.loom.ConstraintProvider; edu.msu.mi.loom.Session; edu.msu.mi.loom.ConstraintTest; edu.msu.mi.loom.Story; edu.msu.mi.loom.CrowdService; edu.msu.mi.loom.TrainingSet; edu.msu.mi.loom.ExpType" %>

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
                    <div class="alert alert-error">${flash.error}</div>
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

                                %{--                                    <asset:javascript src="loom.js"/>--}%
                                %{--                                    <g:set var="session" value="${sessions}" />--}%
                                %{--                                    <g:javascript> var sessionss = ${sessions} </g:javascript>--}%
                                    <g:each in="${sessions}" var="session" id="each-session">
                                        <div class="post session">
                                            <div class="row session-row">
                                                <div class="user-block ">
                                                    <span class='username'>
                                                        <g:link controller="admin" action="view"
                                                                params="[session: session.id]">${session.name}</g:link>

                                                    </span>
                                                    <span class='description'>Created - <g:formatDate
                                                            format="yyyy/MM/dd HH:mm"
                                                            date="${session.dateCreated}"/></span>
                                                    <span class='description'>
                                                        Experiment: ${session.exp.name}(${session.exp.id}),
                                                        Thread: ${sessionState[session.id] ? "Up" : "Down"},
                                                        %{--                                                        Status: <b>${session.state ?: "INACTIVE"}</b>,--}%

                                                    </span>
                                                    <span class='description connected'>Connected users: <b>${sessionState[session.id][6]}</b>
                                                    </span>
                                                    <span class='description session-span'>Status: ${session.state ?: "INACTIVE"}</span>
                                                    <span class='description current-round'>Current round: ${sessionState[session.id][4]}</span>

                                                    <span class='description payment-status'
                                                          hidden>Payment status: ${sessionState[session.id][5]}</span>
                                                    <span class='description count'>User sessions: <b>${sessionState[session.id][1]}</b>
                                                    </span>
                                                    <span class='description set-timer'></span>
                                                    <span class='description'>
                                                        <b>URL: ${request.getScheme()}://${request.getServerName()}:${request.getServerPort()}${request.contextPath}/session/s/${session.id}?workerId=?</b></br>
                                                    </span>

                                                </div>

                                            </div>

                                            <div class="row">
                                                <div class="user-block">

                                                    <g:link controller="admin" action="deleteExperiment"
                                                            class='btn btn-primary'
                                                            params="[sessionId: session.id, type: ExpType.SESSION]">
                                                        Delete
                                                    </g:link>
                                                    <button class='btn btn-primary session-action'
                                                            data-text-swap="Start Listening"
                                                            data-text-original="Cancel">Cancel</button>
                                                    <button class='btn btn-primary start-session'>Launch</button>
                                                    <button class='btn btn-primary check-payble'><i
                                                            class="check-payable-i"></i>Check Payable</button>
                                                    <button class='btn btn-primary pay-session'><i class="pay-i"></i>Pay
                                                    </button>
                                                    <span class="sessionId" style="display:none">${session.id}</span>
                                                    <span class="startPending"
                                                          style="display:none">${sessionState[session.id][2]}</span>
                                                    <span class="startActive"
                                                          style="display:none">${sessionState[session.id][3]}</span>

                                                </div>
                                            </div>
                                        </div>
                                    </g:each>
                                </div>

                                %{--                                <div class="tab-pane" id="session-hit">--}%

                                %{--                                    <g:each in="${sessions}" var="session">--}%
                                %{--                                        <div class="post">--}%
                                %{--                                            <div class="row">--}%
                                %{--                                                <div class="user-block ">--}%
                                %{--                                                    <span>--}%
                                %{--                                                        <g:link controller="admin" action="view"--}%
                                %{--                                                                params="[session: session.id]">${session.name}</g:link>--}%

                                %{--                                                    </span>--}%

                                %{--                                                    <g:each in="${session.HITTypeId}" var="hit">--}%
                                %{--                                                        <span class='description hits'>https://workersandbox.mturk.com/mturk/preview?groupId=${hit}</span>--}%
                                %{--                                                    </g:each>--}%
                                %{--                                                </div>--}%

                                %{--                                            </div>--}%

                                %{--                                        </div>--}%
                                %{--                                    </g:each>--}%
                                %{--                                </div>--}%

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
                                                            date="${experiment.dateCreated}"/></td>
                                                <td>
                                                    %{--                                            <th>Story</th>--}%
                                                    <g:each in="${experiment.stories}" var="story">
                                                        <g:link controller="admin" action="view"
                                                                params="[story: story]">${story.title}</g:link>
                                                    </g:each>
                                                </td>

                                                <td>
                                                    %{--                                            <th>Network</th>--}%
                                                    ${experiment.network_type}
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
                                                    <button type="button" id="create-session-button"
                                                            class="btn btn-primary">Create session
                                                        <span
                                                                class="expid hidden">${experiment.id}</span>
                                                    </button>

                                                </td>
                                            </tr>
                                        </g:each>
                                        </tbody>

                                    </table>
                                </div>
                                <div class="tab-pane" id="session">
                                    <table class="table table-bordered grid" border="1">
                                        <th style="text-align:center">Experiment</th>
                                        <th style="text-align:center">Created</th>
                                        <th style="text-align:center">Story</th>
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
                                                            date="${experiment.dateCreated}"/></td>
                                                <td>
                                                    %{--                                            <th>Story</th>--}%
                                                    <g:each in="${experiment.stories}" var="story">
                                                        <g:link controller="admin" action="view"
                                                                params="[story: story]">${story.title}</g:link>
                                                    </g:each>
                                                </td>
                                                <td>
                                                    %{--                                            <th>Constraints</th>--}%
                                                    <g:each in="${experiment.constraintTests}" var="constraint"
                                                            status="i">
                                                        ${constraint.buildMturkString()}
                                                    </g:each>

                                                </td>
                                                <td>
                                                    %{--                                            <th>Network</th>--}%
                                                    ${experiment.network_type}
                                                </td>
                                                <td>
                                                    %{--                                            <th>Interface</th>--}%
                                                    <span class='description'>${experiment.isInline ? "paragraph" : "list"}</span>
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
                                                            class="btn btn-primary launch_experiment">Create session
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
                                    <g:each in="${trainings}" var="training">
                                        <div class="post">
                                            <div class="row training-row">
                                                <div class="user-block">
                                                    <span class='username'>
                                                        <g:link controller="admin" action="view"
                                                                params="[training: training.id]">${training.name}</g:link>
                                                        <g:link controller="admin" action="deleteExperiment"
                                                                class='pull-right btn-box-tool'
                                                                params="[trainingId: training.id, type: ExpType.TRAINING]">
                                                            <i class='fa fa-times'></i>
                                                        </g:link>
                                                    </span>
                                                    <g:each in="${training.trainings}" var="t">
                                                        <span class='description'>
                                                            <b>Training: ${t.name} (${t.id})</b></br>
                                                        ${t.stories.first().toString()}

                                                        </span>
                                                    </g:each>
                                                    <g:each in="${training.simulations}" var="s">
                                                        <span class='description'>
                                                            <b>Simulation: ${s.name} (${s.id})</b></br>
                                                        ${s.stories.first().toString()}

                                                        </span>
                                                    </g:each>



                                                    <span class='description'>
                                                        <b>URL: ${request.getScheme()}://${request.getServerName()}:${request.getServerPort()}${request.contextPath}/training/t/${training.id}?workerId=?</b></br>
                                                    </span>

%{--                                                    <span class='description training-payment-status'--}%
%{--                                                          hidden><b>Payment status: ${training.paid}/${training.total}</b>--}%
%{--                                                    </span>--}%
                                                </div>


                                                <ul class="list-inline">
                                                </ul>
                                            %{--                                            <g:link controller="admin" action="launchTraining" params="[trainingId: training.id]">--}%
%{--                                                <g:if test="${training.qualifier != "-;-;-"}">--}%
%{--                                                    <button type="button"--}%
%{--                                                            class="btn btn-primary launch_training">Launch Training HITs<span--}%
%{--                                                            hidden>${training.id}</span></button>--}%
%{--                                                    <button class='btn btn-primary check-training_payble'><i--}%
%{--                                                            class="check-training-payable-i"></i>Check Payable</button>--}%
%{--                                                    <button class='btn btn-primary pay-training'><i--}%
%{--                                                            class="pay-training-i"></i>Pay</button>--}%
%{--                                                --}%
%{--                                                </g:if>--}%
                                            </div>
                                        </div>

                                    </g:each>
                                </div>

                                %{--                                <div class="tab-pane" id="training-hit">--}%

                                %{--                                    <g:each in="${trainings}" var="training">--}%
                                %{--                                        <div class="post">--}%
                                %{--                                            <div class="row">--}%
                                %{--                                                <div class="user-block ">--}%
                                %{--                                                    <span>--}%
                                %{--                                                        <g:link controller="admin" action="view"--}%
                                %{--                                                                params="[training: training.id]">${training.name}</g:link>--}%
                                %{--                                                    </span>--}%



                                %{--                                                    <g:each in="${training.HITTypeId}" var="hit">--}%
                                %{--                                                        <span class='description hits'>https://workersandbox.mturk.com/mturk/preview?groupId=${hit}</span>--}%
                                %{--                                                    </g:each>--}%
                                %{--                                                </div>--}%

                                %{--                                            </div>--}%

                                %{--                                        </div>--}%
                                %{--                                    </g:each>--}%
                                %{--                                </div>--}%

                                <div class="tab-pane" id="stories">
                                    <g:each in="${stories}" var="story">
                                        <div class="post">
                                            <div class="user-block">
                                                <span class='username'>
                                                    <g:link controller="admin" action="view"
                                                            params="[training: story.id]">${story.title}</g:link>

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
    $(document).ready(function () {
        var hash = location.hash.replace(/^#/, '');  // ^ means starting, meaning only match the first hash
        if (hash) {
            $('.nav-tabs a[href="#' + hash + '"]').tab('show');
        }

        // Change hash for page-reload
        $('.nav-tabs a').on('shown.bs.tab', function (e) {
            window.location.hash = e.target.hash;
        });
    });

    function credentialsShowMTurkOptions(event) {
        if ($(event.target).val() === "${CrowdService.MTURK.toString()}") {
            $("#credentials-model-sandbox-options").show()
        } else {
            $("#credentials-model-sandbox-options").hide()
        }
    }


</script>


