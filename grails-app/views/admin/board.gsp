<%@ page import="edu.msu.mi.loom.ExpType" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper">
            <!-- Content Header (Page header) -->
            <section class="content-header">
                <h1>
                    Admin Board
                </h1>
                <ol class="breadcrumb">
                    <li><a href="javascript:void(0);"><i class="fa fa-dashboard"></i> Home</a></li>
                    <li class="active">Admin board</li>
                </ol>
            </section>

            <!-- Main content -->
            <section class="content">

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
                                        <b>Sessions</b> <a class="pull-right">${sessionCount}</a>
                                    </li>
                                    <li class="list-group-item">
                                        <b>Rooms</b> <a class="pull-right">${roomsCount}</a>
                                    </li>
                                </ul>

                                <a href="javascript:void(0);" id="create-experiment"
                                   class="btn btn-primary btn-block"><b>Create a session</b></a>

                                <g:link controller="admin" action="exportCSV" id="export-csv"
                                        class="btn btn-primary btn-block"><b>Export CSV</b></g:link>
                            </div><!-- /.box-body -->
                        </div><!-- /.box -->

                    </div><!-- /.col -->
                    <div class="col-md-9">
                        <div class="nav-tabs-custom">
                            <ul class="nav nav-tabs">
                                <li class="active"><a href="#activity" data-toggle="tab">Activity</a></li>
                                %{--<li><a href="#timeline" data-toggle="tab">Timeline</a></li>--}%
                                %{--<li><a href="#settings" data-toggle="tab">Settings</a></li>--}%
                            </ul>

                            <div class="tab-content">
                                <div class="active tab-pane" id="activity">
                                    <g:each in="${sessions}" var="session">
                                        <div class="post">
                                            <div class="user-block">
                                                <span class='username'>
                                                    <g:link controller="admin" action="view"
                                                            params="[session: session.id]">${session.name}</g:link>
                                                    <g:link controller="admin" action="deleteExperiment"
                                                            class='pull-right btn-box-tool'
                                                            params="[experimentId: session.id, type: ExpType.SESSION]">
                                                        <i class='fa fa-times'></i>
                                                    </g:link>
                                                </span>
                                                <span class='description'>Created - <g:formatDate
                                                        format="yyyy/MM/dd HH:mm"
                                                        date="${session.dateCreated}"/></span>
                                            </div>

                                            <p></p>
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
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
