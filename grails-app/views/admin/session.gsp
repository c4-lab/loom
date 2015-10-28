<%@ page import="edu.msu.mi.loom.ExpType" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper">
            <section class="content-header">
                <div id="error-alert" class="alert alert-danger alert-dismissable hide">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                    <h4><i class="icon fa fa-ban"></i> Error!</h4>
                    Unable to clone the session. Please try again.
                </div>

                <div id="success-alert" class="alert alert-success alert-dismissable hide">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                    <h4><i class="icon fa fa-check"></i> Success!</h4>
                    <a id="session-link" href=""></a> has been created.
                </div>

                <div>
                    <h1>
                        Admin Board
                    </h1>
                    <ul class="breadcrumb ">
                        <li><a href="javascript:void(0);"><i class="fa fa-dashboard"></i> Home</a></li>
                        <li><g:link controller="admin" action="board">Admin board</g:link></li>
                        <li class="active">${session.name}</li>
                    </ul>
                </div>
            </section>

            <!-- Main content -->
            <section class="content">
            <g:hiddenField name="sessionId" value="${session.id}"/>
                <div class="row">
                    <div class="col-md-3">

                        <!-- Profile Image -->
                        <div class="box box-primary">
                            <div class="box-body box-profile">
                                <asset:image src="avatar.jpg" class="profile-user-img img-responsive img-circle"
                                             alt="User profile picture"/>
                                <h3 class="profile-username text-center">Admin</h3>

                                <p class="text-muted text-center">Super user</p>

                                <ul class="list-group list-group-unbordered">
                                    <li class="list-group-item">
                                        <b>Trainings</b> <a class="pull-right">${trainingsCount}</a>
                                    </li>
                                    <li class="list-group-item">
                                        <b>Simulations</b> <a class="pull-right">${simulationsCount}</a>
                                    </li>
                                    <li class="list-group-item">
                                        <b>Experimetns</b> <a class="pull-right">${experimentsCount}</a>
                                    </li>
                                </ul>

                                <g:if test="${session.experiments.getAt(0).enabled}">
                                    <a href="javascript:void(0);" id="clone-session"
                                       class="btn btn-primary btn-block"><b>Clone the session</b></a>
                                    <g:link controller="admin" action="publishAnonym" id="publish-anon-session"
                                            class="btn btn-block btn-success"
                                            params="[session: session.id]"><b>Publish anonymously</b></g:link>
                                    <a href="javascript:void(0);" id="clone-session"
                                       class="btn btn-block btn-success"><b>Publish by email</b></a>
                                </g:if>
                            </div>
                        </div>

                    </div><!-- /.col -->
                    <div class="col-md-9">
                        <div class="nav-tabs-custom">
                            <ul class="nav nav-tabs">
                                <li class="active"><a href="#activity" data-toggle="tab">${session.name}</a></li>
                                %{--<li><a href="#timeline" data-toggle="tab">Timeline</a></li>--}%
                                %{--<li><a href="#settings" data-toggle="tab">Settings</a></li>--}%
                            </ul>

                            <div class="tab-content">
                                <div class="active tab-pane" id="activity">
                                    <g:each in="${experiments}" var="experiment">
                                        <div class="post">
                                            <div class="user-block">
                                                <span class='username'>
                                                    <a href="#">${experiment.name}</a>
                                                    <g:link controller="admin" action="deleteExperiment"
                                                            class='pull-right btn-box-tool'
                                                            params="[experimentId: experiment.id, type: ExpType.EXPERIMENT]">
                                                        <i class='fa fa-times'></i>
                                                    </g:link>
                                                </span>
                                                <span class='description'>Created - <g:formatDate
                                                        format="yyyy/MM/dd HH:mm"
                                                        date="${experiment.dateCreated}"/></span>
                                            </div>

                                            <p>
                                                <g:each in="${experiment.stories[0].tails}" var="tail">
                                                    ${tail.text}
                                                </g:each>
                                            </p>
                                        </div>
                                    </g:each>

                                    <g:each in="${trainings}" var="training">
                                        <div class="post">
                                            <div class="user-block">
                                                <span class='username'>
                                                    <a href="#">${training.name}</a>
                                                    <g:link controller="admin" action="deleteExperiment"
                                                            class='pull-right btn-box-tool'
                                                            params="[experimentId: training.id, type: ExpType.TRAINING]">
                                                        <i class='fa fa-times'></i>
                                                    </g:link>
                                                </span>
                                                <span class='description'>Created - <g:formatDate
                                                        format="yyyy/MM/dd HH:mm"
                                                        date="${training.dateCreated}"/></span>
                                            </div>

                                            <p>
                                                <g:each in="${training.stories[0].tails}" var="tail">
                                                    ${tail.text}
                                                </g:each>
                                            </p>

                                        </div>
                                    </g:each>

                                    <g:each in="${simulations}" var="simulation">
                                        <div class="post">
                                            <div class="user-block">
                                                <span class='username'>
                                                    <a href="#">${simulation.name}</a>
                                                    <g:link controller="admin" action="deleteExperiment"
                                                            class='pull-right btn-box-tool'
                                                            params="[experimentId: simulation.id, type: ExpType.SIMULATION]">
                                                        <i class='fa fa-times'></i>
                                                    </g:link>
                                                </span>
                                                <span class='description'>Created - <g:formatDate
                                                        format="yyyy/MM/dd HH:mm"
                                                        date="${simulation.dateCreated}"/></span>
                                            </div>

                                            <p>
                                                <g:each in="${simulation.stories[0].tails}" var="tail">
                                                    ${tail.text}
                                                </g:each>
                                            </p>
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

<div class="modal modal-info" style="padding-top: 140px" id="file-upload-modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Upload a file</h4>
            </div>
            <g:form enctype="multipart/form-data" name="upload-form" controller="admin" action="upload">
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
        </div>
    </div>
</div>
