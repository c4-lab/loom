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

                                <a href="javascript:void(0);" id="create-experiment"
                                   class="btn btn-primary btn-block"><b>Create an experiment</b></a>


                                <a href="javascript:void(0);" id="create-trainingset"
                                   class="btn btn-primary btn-block"><b>Create a training set</b></a>

                                <a href="javascript:void(0);" id="create-stories"
                                   class="btn btn-primary btn-block"><b>Create a story set</b></a>

                                <a href="javascript:void(0);" id="create-users"
                                   class="btn btn-primary btn-block"><b>Create users</b></a>

                                <g:link controller="admin" action="exportCSV" id="export-csv"
                                        class="btn btn-primary btn-block"><b>Export CSV</b></g:link>
                            </div><!-- /.box-body -->
                        </div><!-- /.box -->

                    </div><!-- /.col -->

                    <div class="col-sm-9">
                        <div class="nav-tabs-custom">
                            <ul class="nav nav-tabs">
                                <li class="active"><a href="#sessions" data-toggle="tab">Sessions</a></li>
                                <li><a href="#session-hit" data-toggle="tab">Sessions-HIT</a></li>
                                <li><a href="#experiments" data-toggle="tab">Experiments</a></li>
                                <li><a href="#trainings" data-toggle="tab">Trainings</a></li>
                                <li><a href="#training-hit" data-toggle="tab">Trainings-HIT</a></li>
                                <li><a href="#stories" data-toggle="tab">Stories</a></li>
                                <li><a href="#users" data-toggle="tab">Users</a></li>

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
                                                        Training: ${session.trainingSet.name}(${session.trainingSet.id}),
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

                                <div class="tab-pane" id="session-hit">

                                    <g:each in="${sessions}" var="session">
                                        <div class="post">
                                            <div class="row">
                                                <div class="user-block ">
                                                    <span>
                                                        <g:link controller="admin" action="view"
                                                                params="[session: session.id]">${session.name}</g:link>

                                                    </span>

                                                    <g:each in="${session.HITTypeId}" var="hit">
                                                        <span class='description hits'>https://workersandbox.mturk.com/mturk/preview?groupId=${hit}</span>
                                                    </g:each>
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
                                                <span class='description'>Qualifier: ${experiment.qualifier}</span>
                                                <span class='description'>Created - <g:formatDate
                                                        format="yyyy/MM/dd HH:mm"
                                                        date="${experiment.dateCreated}"/>, Rounds: ${experiment.roundCount}, Max allowed connections: ${experiment.max_node}</span><br/>
                                                <span class='description'>${experiment.story.tails.sort {
                                                    it.text_order
                                                }.text.join(" ")}</span>

                                                <span class='description'>${experiment.uiflag ? "paragraph" : "list"}</span>

                                            </div>

                                            <div class="user-block clo-xs-2">

                                                    <button type="button"
                                                            class="btn btn-primary launch_experiment">Launch session
                                                        <span
                                                            class="expid" hidden>${experiment.id}</span>
                                                        <span class="numhits" hidden>${experiment.max_node}</span></button>

                                            </div>

                                        </div>
                                    </g:each>
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
                                                        <b>Qualifier: ${training.qualifier}</b></br>
                                                    </span>

                                                    <span class='description'>
                                                        <b>URL: ${request.getScheme()}://${request.getServerName()}:${request.getServerPort()}${request.contextPath}/training/t/${training.id}?workerId=?</b></br>
                                                    </span>

                                                    <span class='description training-payment-status'
                                                          hidden><b>Payment status: ${training.paid}/${training.total}</b>
                                                    </span>
                                                </div>


                                                <ul class="list-inline">
                                                </ul>
                                            %{--                                            <g:link controller="admin" action="launchTraining" params="[trainingId: training.id]">--}%
                                                <g:if test="${training.qualifier != "-;-;-"}">
                                                    <button type="button"
                                                            class="btn btn-primary launch_training">Launch Training HITs<span
                                                            hidden>${training.id}</span></button>
                                                    <button class='btn btn-primary check-training_payble'><i
                                                            class="check-training-payable-i"></i>Check Payable</button>
                                                    <button class='btn btn-primary pay-training'><i
                                                            class="pay-training-i"></i>Pay</button>
                                                %{--                                            </g:link>--}%
                                                </g:if>
                                            </div>
                                        </div>

                                    </g:each>
                                </div>

                                <div class="tab-pane" id="training-hit">

                                    <g:each in="${trainings}" var="training">
                                        <div class="post">
                                            <div class="row">
                                                <div class="user-block ">
                                                    <span>
                                                        <g:link controller="admin" action="view"
                                                                params="[training: training.id]">${training.name}</g:link>
                                                    </span>



                                                    <g:each in="${training.HITTypeId}" var="hit">
                                                        <span class='description hits'>https://workersandbox.mturk.com/mturk/preview?groupId=${hit}</span>
                                                    </g:each>
                                                </div>

                                            </div>

                                        </div>
                                    </g:each>
                                </div>

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
                <h4 class="modal-title">Create an Experiment</h4>
            </div>

            <div class="modal-body">

                <div class="form-group">

                    <label for="name">Experiment name:</label>
                    <input type="text" name="name" id="name" style="color: black">

                    <p></p>
                    <label for="StorySelect">StorySet:</label>
                    <g:select name="storySet" id="StorySelect"
                              from="${stories}" optionKey="id" style="color: black"
                              optionValue="title" noSelection="${['null': 'Select One...']}"/>

                    <p></p>
                    <label for="trainingSetSelect">TrainingSet:</label>
                    <g:select name="trainingSet" id="trainingSetSelect"
                              from="${trainings}" optionKey="id" style="color: black"
                              optionValue="name" noSelection="${['null': 'Select One...']}"/>
                    <p></p>

                    <label>Minimum allowed nodes:</label>
                    <input type="number" name='min_nodes' id='min_nodes' min="2" max="100" value="2"
                           style="color:black;">

                    <p></p>
                    <label>Maximum allowed nodes:</label>
                    <input type="number" name='max_nodes' id='max_nodes' min="2" max="100" value="2"
                           style="color:black;">

                    <p></p>
                    <label>Initial number of tiles:</label>
                    <input type="number" name='initialNbrOfTiles' id='initialNbrOfTiles' min="2" max="100" value="2"
                           style="color:black;">

                    <p></p>
                    <label>Number of rounds:</label>
                    <input type="number" name='rounds' id='rounds' min="1" max="100" value="2" style="color:black;">

                    <p></p>
                    <label>Round duration:</label>
                    <input type="number" name='duration' id='duration' min="1" max="100" value="2"
                           style="color:black;"><label>seconds</label>

                    <p></p>
                    %{--                        <label  >Qualifier string (optional): </label>--}%
                    %{--                        <input type="text" style="color:black;" name="qualifier">--}%

                </div>

                <div>
                    <label>Interface flag:</label>
                    <label><input type="radio" name="UIflag" value="0"/>draggable lists</label>
                    <label><input type="radio" name="UIflag" value="1"/>paragraphs</label>
                </div>

                <p></p>

                <div>
                    <label>Qualifier string:</label>
                    <label><input type="radio" onclick="chooseExperimentQualifier('no')" name="qualifier"
                                  value="no"/>ignore</label>
                    <label><input type="radio" onclick="chooseExperimentQualifier('yes')" name="qualifier"
                                  value="yes"/>setting</label>
                </div>

                <p></p>

                <div id="qualify" style="display:none">
                    <label>Performance >=</label>
                    <select style="display:none">
                        <option><input type="number" name='performance' id='performance' min="0" max="5" value="0"
                                       style="color:black;" required=False></option>
                    </select>

                    <label>Reading performance >=</label>
                    <select style="display:none">
                        <option><input type="number" name='reading' id='reading' min="0" max="5" value="0"
                                       style="color:black;" required=False></option>
                    </select>

                    <label>range of vaccine score (include):</label>

                    <input type="number" name='vaccine_score_min' id='vaccine_score_min' min="1" max="15" value="1"
                           style="color:black;"/>
                    <input type="number" name='vaccine_score_max' id='vaccine_score_max' min="1" max="15" value="1"
                           style="color:black;"/>

                </div>

                <div>
                    <label>Network type:</label>
                    <label><input type="radio" onclick="chooseType('Lattice')" name="network_type"
                                  value="Lattice"/>1D Lattice</label>
                    <label><input type="radio" onclick="chooseType('Newman-Watts')" name="network_type"
                                  value="Newman_Watts"/>Newman-Watts</label>
                    <label><input type="radio" onclick="chooseType('Barabassi-Albert')" name="network_type"
                                  value="Barabassi_Albert"/>Barabassi-Albert</label>
                </div>


                <div id="s1" style="display:none">
                    <label>degree:</label>
                    <select style="display:none">

                        <option><input type="number" name='min_degree' id='Lattice_min_degree' min="2" max="100"
                                       value="0" style="color:black;" required=False></option>
                    </select>
                </div>

                <div id="s2" style="display:none">

                    <label>degree:</label>
                    <input type="number" min="3" max="100" name='min_degree' value="3" id='Newman_min_degree'
                           style="color:black;">

                    <p></p>
                    <label>k-nearest neighbors:</label>
                    <input type="number" min="3" max="100" name='max_degree' value="3" id='Newman_max_degree'
                           style="color:black;">

                    <p></p>
                    <label>shortcut probability:</label>
                    <input type="number" step="0.1" name='prob' id='Newman_prob' oninput="if (value > 1) value = 1;
                    if (value.length > 4) value = value.slice(0, 4);
                    if (value <= 0) value = 0.1" style="color:black;"/>

                </div>

                <div id="s3" style="display:none">
                    <label>initial nodes:</label>
                    <input type="number" min="2" max="100" name='min_degree' value="2" id='BA_min_degree'
                           style="color:black;">

                    <p></p>
                    <label>max degree:</label>
                    <input type="number" min="2" max="100" name='max_degree' value="2" id='BA_max_degree'
                           style="color:black;">

                    <p></p>
                    <label>edges per node:</label>
                    <input type="number" min="2" max="100" name='M' id='BA_M' value="2" style="color:black;">

                    <p></p>

                </div>

                <p></p>

                <label>Payment</label>

                <label for="name">accepting the HIT:</label>
                <input type="number" step="0.1" name='accepting' id='accepting' value="0.1"
                       oninput="if (value.length > 4) value = value.slice(0, 4);
                       if (value <= 0) value = 0" style="color:black;"/>

                <p></p>
                <label for="name">completion:</label>
                <input type="number" step="0.1" name='completion' id='completion' value="0.1"
                       oninput="if (value.length > 4) value = value.slice(0, 4);
                       if (value <= 0) value = 0" style="color:black;"/>

                <p></p>
                <label for="name">waiting:</label>
                <input type="number" step="0.1" name='waiting' id='waiting' value="0.1"
                       oninput="if (value.length > 4) value = value.slice(0, 4);
                       if (value <= 0) value = 0" style="color:black;"/>

                <p></p>
                <label for="name">score:</label>
                <input type="number" step="0.1" name='score' id='score' value="0.1"
                       oninput="if (value.length > 4) value = value.slice(0, 4);
                       if (value <= 0) value = 0" style="color:black;"/>

                <p></p>

            </div>


            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" id='create-exp'>Create</button>
            </div>

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
        %{--            <form method="post" enctype="multipart/form-data" >--}%
            <g:form enctype="multipart/form-data" name="upload-form" controller="admin" action="uploadTrainingSet">
                <div class="modal-body">

                    <div class="form-group">
                        <label for="trainingSetName">TrainingSet</label>
                        <g:textField name="name" id="trainingSetName" placeholder="Training Set Name" required=""/>

                    </div>

                    <div>
                        <label>Interface flag:</label>
                        <label><input type="radio" name="UIflag" value="0" required=""/>paragraphs</label>
                        <label><input type="radio" name="UIflag" value="1" required=""/>draggable lists</label>
                    </div>
                    %{--                    <label>number of HITs: </label>--}%
                    %{--                    <input type="number"  name='hit_num' id='hit_num' min="0" max="100" value="0" style="color:black;" required>--}%
                    <p></p>
                    <label for="name">payment:</label>
                    <input type="number" step="0.1" name='training_payment' id='training_payment'
                           oninput="if (value.length > 4) value = value.slice(0, 4);
                           if (value <= 0) value = 0" style="color:black;"/>

                    <p></p>
                    <label>File input</label>
                    <input type="file" id="trainingInputFile" name="inputFile" required>

                    <p class="help-block">Select trainingset file (*.json).</p>

                    <label>check simulation:</label>
                    <g:checkBox name="simulation" value="${false}"/>
                    %{--                        <div id="traing_perform" style="display:none">--}%
                    %{--                            <label >Simulation Score >=</label>--}%

                    %{--                            <select  style="display:none" >--}%
                    %{--                                <option><input type="number"  name='performance' id='simulation_score' min="0" max="5" value="0" style="color:black;" required></option>--}%
                    %{--                            </select>--}%

                    %{--                        </div>--}%
                    <p></p>

                    <label>check reading:</label>
                    <g:checkBox name="read" value="${false}"/>
                    %{--                        <div id="traing_reading" style="display:none">--}%
                    %{--                            <label >Reading score >=</label>--}%

                    %{--                            <select  style="display:none" >--}%
                    %{--                                <option><input type="number"  name='reading' id='reading_score' min="0" max="5" value="0" style="color:black;" required></option>--}%
                    %{--                            </select>--}%

                    %{--                        </div>--}%
                    <p></p>
                    <label>check survey:</label>
                    <g:checkBox name="survey" id="survey" value="${false}"/>
                    %{--                        <div id="traing_survey" style="display:none">--}%
                    %{--                            <label >Survey score >=</label>--}%

                    %{--                            <select  style="display:none" >--}%
                    %{--                                <option><input type="number"  name='survey' id='survey_score' min="0" max="5" value="0" style="color:black;" required></option>--}%
                    %{--                            </select>--}%

                    %{--                        </div>--}%

                </div>


                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    %{--                <input type="button" class="btn btn-primary" value="Upload" onclick="create_train()"/>--}%
                    <button type="submit" class="btn btn-primary">Create</button>
                </div>
            </g:form>
        %{--            </form>--}%

        </div>
    </div>
</div>


<div class="modal modal-info" style="padding-top: 140px" id="create-story-set">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Upload File</h4>
            </div>

            <div class="modal-body">

                <label>Title:</label>
                <input type="text" name="name" id="story-title" style="color: black">

                <p></p>
                <label>Write a story (each line is a tile):</label>
                <textarea id="story-text" style="color: black" rows="20" cols="60"></textarea>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" id='create-story'>Create</button>
            </div>

        </div>
    </div>
</div>
<div class="modal modal-info" style="padding-top: 140px" id="launch-experiment-modal">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Launch Session</h4>
            </div>

            <div class="modal-body">
                <!-- TODO: Need a real training id in here -->
                <span hidden id="experimentID"></span>
                <label>Number of HITs:</label>
                <input type="number" name='num_hits' id='num_exp_hits' min="0" max="10000" value=0
                       style="color:black;" required>
                <p></p>
                <label>Available time (minutes):</label>
                <input type="number" name='available_time' id='exp_available_time' min="0" max="10080" value="1440"
                       style="color:black;" required>
                <p></p>
                <label>Assignment lifetime (minutes):</label>
                <input type="number" name='assignment_lifetime' id='exp_assignment_lifetime' min="0" max="1440" value="120"
                       style="color:black;" required>

                <p></p>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" id="launch_experiment_hits">Lanch HITs</button>
            </div>

        </div>
    </div>

</div>
<div class="modal modal-info" style="padding-top: 140px" id="launch-training-modal">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Launch Training</h4>
            </div>

            <div class="modal-body">
                <!-- TODO: Need a real training id in here -->
                <span hidden id="trainingID"></span>
                <label>Number of HITs:</label>
                <input type="number" name='num_training_hits' id='num_training_hits' min="0" max="10000" value="0"
                       style="color:black;" required>
                <label>Available time (minutes):</label>
                <input type="number" name='available_time' id='available_time' min="0" max="10080" value="1440"
                       style="color:black;" required>
                <label>Assignment lifetime (minutes):</label>
                <input type="number" name='assignment_lifetime' id='assignment_lifetime' min="0" max="1440" value="120"
                       style="color:black;" required>

                <p></p>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" id="launch_training_hits">Lanch HITs</button>
            </div>

        </div>
    </div>

</div>

<div class="modal modal-info" style="padding-top: 140px" id="create-users-modal">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Create Users</h4>
            </div>

            <div class="modal-body">
                <div class="text-center">

                    <table class="table table-striped table-bordered grid" border="1" id="create-user-table">

                        <th style="text-align:center; background-color: #23272b">Username</th>
                        <th style="text-align:center; background-color: #23272b">Action</th>

                        <tbody style="background-color: #23272b">

                        </tbody>
                    </table>
                    %{--                <label>Username: </label>--}%
                    %{--                <input type="text" name="username" id="username" style="color: black"><button  type="button" class="btn btn-primary" id="create-default">user default</button>--}%

                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>

                <button type="button" class="btn btn-primary" id="create-username">Add a username</button>
                <button type="submit" class="btn btn-primary" id='submit-users'>Submit</button>
            </div>

        </div>
    </div>
</div>


