<%@ page import="edu.msu.mi.loom.TrainingSet; edu.msu.mi.loom.ExpType" %>
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

                                <a href="javascript:void(0);" id="create-experiment"
                                   class="btn btn-primary btn-block"><b>Create a experiment</b></a>


                                <a href="javascript:void(0);" id="create-trainingset"
                                   class="btn btn-primary btn-block"><b>Create a training set</b></a>

                                <g:link controller="admin" action="exportCSV" id="export-csv"
                                        class="btn btn-primary btn-block"><b>Export CSV</b></g:link>
                            </div><!-- /.box-body -->
                        </div><!-- /.box -->

                    </div><!-- /.col -->
                    <div class="col-sm-9">
                        <div class="nav-tabs-custom">
                            <ul class="nav nav-tabs">
                                <li class="active"><a href="#sessions" data-toggle="tab">Sessions</a></li>
                                <li><a href="#experiments" data-toggle="tab">Experiments</a></li>
                                <li><a href="#trainings" data-toggle="tab">Training</a></li>
                            </ul>

                            <div class="tab-content">
                                <div class="active tab-pane" id="sessions">
                                    <g:each in="${sessions}" var="session">
                                        <div class="post">
                                            <div class="row">
                                                <div class="user-block col-xs-12">
                                                    <span class='username'>
                                                        <g:link controller="admin" action="view"
                                                                params="[session: session.id]">${session.name}</g:link>

                                                    </span>
                                                    <span class='description'>Created - <g:formatDate
                                                            format="yyyy/MM/dd HH:mm"
                                                            date="${session.dateCreated}"/></span>
                                                    <span class='description'>
                                                        Experiment: ${session.exp.name}(${session.exp.id}),
                                                        Training: ${session.trainingSet.name}(${session.trainingSet.id}),
                                                        Thread: ${sessionState[session.id] ? "Up" : "Down"}
                                                        Status: <b>${session.state ?: "INACTIVE"}</b>
                                                    </span>
                                                </div>
                                            </div>

                                            <div class="row">
                                                <div class="col-xs-12">
                                                    <g:link controller="admin" action="deleteExperiment"
                                                            class='btn btn-primary'
                                                            params="[sessionId: session.id, type: ExpType.SESSION]">
                                                        Delete
                                                    </g:link>
                                                    <g:link controller="admin" action="restartSession"
                                                            class='btn btn-primary'
                                                            params="[sessionId: session.id, type: ExpType.SESSION]">
                                                        Restart
                                                    </g:link>
                                                    <g:link controller="admin" action="launchExperiment"
                                                            class='btn btn-primary'
                                                            params="[sessionId: session.id, type: ExpType.SESSION]">
                                                        Launch
                                                    </g:link>
                                                    <g:link controller="admin" action="stopExperiment"
                                                            class='btn btn-primary'
                                                            params="[sessionId: session.id, type: ExpType.SESSION]">
                                                        Stop
                                                    </g:link>
                                                </div>
                                            </div>
                                        </div>
                                    </g:each>
                                </div>

                                <div class="tab-pane" id="experiments">
                                    <g:each in="${experiments}" var="experiment">
                                        <div class="post row">
                                            <div class="user-block col-xs-10">
                                                <span class='username'>
                                                    <g:link controller="admin" action="view"
                                                            params="[experiment: experiment.id]">${experiment.name}</g:link>

                                                </span>
                                                <span class='description'>Created - <g:formatDate
                                                        format="yyyy/MM/dd HH:mm"
                                                        date="${experiment.dateCreated}"/>, Rounds: ${experiment.roundCount}, Users: ${experiment.userCount}</span><br/>
                                                <span class='description'>${experiment.story.tails.sort {
                                                    it.text_order
                                                }.text.join(" ")}</span>
                                            </div>

                                            <div class="user-block col-xs-2">

                                                <a href="javascript:void(0);"
                                                   class="launch-experiment btn btn-primary btn-block"><span
                                                        style="display:none">${experiment.id}</span><b>Launch</b>
                                                </a> <br/>
                                            </div>
                                        </div>
                                    </g:each>
                                </div>

                                <div class="tab-pane" id="trainings">
                                    <g:each in="${trainings}" var="training">
                                        <div class="post">
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
                                                    ${t.stories.first().getText()}

                                                    </span>
                                                </g:each>
                                                <g:each in="${training.simulations}" var="s">
                                                    <span class='description'>
                                                        <b>Simulation: ${s.name} (${s.id})</b></br>
                                                    ${s.stories.first().getText()}

                                                    </span>
                                                </g:each>

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

<div class="modal modal-info" style="padding-top: 140px" id="experiment-file-upload-modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Upload a file</h4>
            </div>
            <g:form enctype="multipart/form-data" name="upload-form" controller="admin" action="uploadExperiment">
                <div class="modal-body">

                    <div class="form-group">
                        <label for="inputFile">File input</label>
                        <input type="file" id="inputFile" name="inputFile">

                        <p class="help-block">Select experiment file (*.json).</p>
                    </div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Upload</button>
                </div>
            </g:form>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<div class="modal modal-info" style="padding-top: 140px" id="launch-modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Launch Experiment</h4>
            </div>
            <g:form controller="admin" action="launchExperiment">
                <div class="modal-body">

                    <div class="form-group">
                        <label for="trainingSetSelect">TrainingSet</label>
                        <g:select name="trainingSet" id="trainingSetSelect"
                                  from="${trainings}" optionKey="id"
                                  optionValue="name" noSelection="${['null': 'Select One...']}"/>
                    </div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Launch Session</button>
                </div>
                <g:hiddenField id="sessionLaunchId" name="experimentId"/>

            </g:form>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<div class="modal modal-info" style="padding-top: 140px" id="training-set-file-upload-modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Upload File</h4>
            </div>
            <g:form enctype="multipart/form-data" name="upload-form" controller="admin" action="uploadTrainingSet">
                <div class="modal-body">

                    <div class="form-group">
                        <label for="trainingSetName">TrainingSet</label>
                        <g:textField name="name" id="trainingSetName" placeholder="Training Set Name"/>
                    </div>

                    <div class="form-group">
                        <label for="inputFile">File input</label>
                        <input type="file" id="trainingInputFile" name="inputFile">

                        <p class="help-block">Select experiment file (*.json).</p>
                    </div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Upload</button>
                </div>
            </g:form>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->