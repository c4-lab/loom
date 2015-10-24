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
                                        <b>Experiments</b> <a class="pull-right">${experimentsCount}</a>
                                    </li>
                                    <li class="list-group-item">
                                        <b>Trainings</b> <a class="pull-right">${trainingsCount}</a>
                                    </li>
                                    <li class="list-group-item">
                                        <b>Simulations</b> <a class="pull-right">${simulationsCount}</a>
                                    </li>
                                </ul>

                                <a href="javascript:void(0);" id="create-experiment"
                                   class="btn btn-primary btn-block"><b>Create a session</b></a>
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
                                                <g:each in="${experiment.task}" var="task">
                                                    ${task.text}
                                                </g:each>
                                            </p>
                                            <ul class="list-inline">
                                                %{--<li><a href="#" class="link-black text-sm"><i--}%
                                                %{--class="fa fa-share margin-r-5"></i> Share</a></li>--}%
                                                %{--<li class="pull-right"><a href="#" class="link-black text-sm"><i--}%
                                                %{--class="fa fa-comments-o margin-r-5"></i> Comments (5)</a></li>--}%
                                            </ul>
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
                                                <g:each in="${training.task}" var="task">
                                                    ${task.text}
                                                </g:each>
                                            </p>
                                            <ul class="list-inline">
                                                %{--<li><a href="#" class="link-black text-sm"><i--}%
                                                %{--class="fa fa-share margin-r-5"></i> Activate</a></li>--}%
                                                %{--<li><a href="#" class="link-black text-sm"><i--}%
                                                %{--class="fa fa-thumbs-o-up margin-r-5"></i> Like</a></li>--}%
                                                %{--<li class="pull-right"><a href="#" class="link-black text-sm"><i--}%
                                                %{--class="fa fa-comments-o margin-r-5"></i> Comments (5)</a></li>--}%
                                            </ul>

                                            %{--<input class="form-control input-sm" type="text"--}%
                                            %{--placeholder="Type a comment">--}%
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
                                                <g:each in="${simulation.task}" var="task">
                                                    ${task.text}
                                                </g:each>
                                            </p>
                                            <ul class="list-inline">
                                                %{--<li><a href="#" class="link-black text-sm"><i--}%
                                                %{--class="fa fa-share margin-r-5"></i> Activate</a></li>--}%
                                                %{--<li class="pull-right"><a href="#" class="link-black text-sm"><i--}%
                                                %{--class="fa fa-comments-o margin-r-5"></i> Comments (5)</a></li>--}%
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
        <aside class="control-sidebar control-sidebar-dark">
            <!-- Create the tabs -->
            <ul class="nav nav-tabs nav-justified control-sidebar-tabs">
                <li><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-home"></i></a></li>
                <li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-gears"></i></a></li>
            </ul>
            <!-- Tab panes -->
            <div class="tab-content">
                <!-- Home tab content -->
                <div class="tab-pane" id="control-sidebar-home-tab">
                    <h3 class="control-sidebar-heading">Recent Activity</h3>
                    <ul class="control-sidebar-menu">
                        <li>
                            <a href="javascript:void(0);">
                                <i class="menu-icon fa fa-birthday-cake bg-red"></i>

                                <div class="menu-info">
                                    <h4 class="control-sidebar-subheading">Langdon's Birthday</h4>

                                    <p>Will be 23 on April 24th</p>
                                </div>
                            </a>
                        </li>
                        <li>
                            <a href="javascript::;">
                                <i class="menu-icon fa fa-user bg-yellow"></i>

                                <div class="menu-info">
                                    <h4 class="control-sidebar-subheading">Frodo Updated His Profile</h4>

                                    <p>New phone +1(800)555-1234</p>
                                </div>
                            </a>
                        </li>
                        <li>
                            <a href="javascript::;">
                                <i class="menu-icon fa fa-envelope-o bg-light-blue"></i>

                                <div class="menu-info">
                                    <h4 class="control-sidebar-subheading">Nora Joined Mailing List</h4>

                                    <p>nora@example.com</p>
                                </div>
                            </a>
                        </li>
                        <li>
                            <a href="javascript::;">
                                <i class="menu-icon fa fa-file-code-o bg-green"></i>

                                <div class="menu-info">
                                    <h4 class="control-sidebar-subheading">Cron Job 254 Executed</h4>

                                    <p>Execution time 5 seconds</p>
                                </div>
                            </a>
                        </li>
                    </ul><!-- /.control-sidebar-menu -->

                    <h3 class="control-sidebar-heading">Tasks Progress</h3>
                    <ul class="control-sidebar-menu">
                        <li>
                            <a href="javascript::;">
                                <h4 class="control-sidebar-subheading">
                                    Custom Template Design
                                    <span class="label label-danger pull-right">70%</span>
                                </h4>

                                <div class="progress progress-xxs">
                                    <div class="progress-bar progress-bar-danger" style="width: 70%"></div>
                                </div>
                            </a>
                        </li>
                        <li>
                            <a href="javascript::;">
                                <h4 class="control-sidebar-subheading">
                                    Update Resume
                                    <span class="label label-success pull-right">95%</span>
                                </h4>

                                <div class="progress progress-xxs">
                                    <div class="progress-bar progress-bar-success" style="width: 95%"></div>
                                </div>
                            </a>
                        </li>
                        <li>
                            <a href="javascript::;">
                                <h4 class="control-sidebar-subheading">
                                    Laravel Integration
                                    <span class="label label-warning pull-right">50%</span>
                                </h4>

                                <div class="progress progress-xxs">
                                    <div class="progress-bar progress-bar-warning" style="width: 50%"></div>
                                </div>
                            </a>
                        </li>
                        <li>
                            <a href="javascript::;">
                                <h4 class="control-sidebar-subheading">
                                    Back End Framework
                                    <span class="label label-primary pull-right">68%</span>
                                </h4>

                                <div class="progress progress-xxs">
                                    <div class="progress-bar progress-bar-primary" style="width: 68%"></div>
                                </div>
                            </a>
                        </li>
                    </ul><!-- /.control-sidebar-menu -->

                </div><!-- /.tab-pane -->
            <!-- Stats tab content -->
                <div class="tab-pane" id="control-sidebar-stats-tab">Stats Tab Content</div><!-- /.tab-pane -->
            <!-- Settings tab content -->
                <div class="tab-pane" id="control-sidebar-settings-tab">
                    <form method="post">
                        <h3 class="control-sidebar-heading">General Settings</h3>

                        <div class="form-group">
                            <label class="control-sidebar-subheading">
                                Report panel usage
                                <input type="checkbox" class="pull-right" checked>
                            </label>

                            <p>
                                Some information about this general settings option
                            </p>
                        </div><!-- /.form-group -->

                        <div class="form-group">
                            <label class="control-sidebar-subheading">
                                Allow mail redirect
                                <input type="checkbox" class="pull-right" checked>
                            </label>

                            <p>
                                Other sets of options are available
                            </p>
                        </div><!-- /.form-group -->

                        <div class="form-group">
                            <label class="control-sidebar-subheading">
                                Expose author name in posts
                                <input type="checkbox" class="pull-right" checked>
                            </label>

                            <p>
                                Allow the user to show his name in blog posts
                            </p>
                        </div><!-- /.form-group -->

                        <h3 class="control-sidebar-heading">Chat Settings</h3>

                        <div class="form-group">
                            <label class="control-sidebar-subheading">
                                Show me as online
                                <input type="checkbox" class="pull-right" checked>
                            </label>
                        </div><!-- /.form-group -->

                        <div class="form-group">
                            <label class="control-sidebar-subheading">
                                Turn off notifications
                                <input type="checkbox" class="pull-right">
                            </label>
                        </div><!-- /.form-group -->

                        <div class="form-group">
                            <label class="control-sidebar-subheading">
                                Delete chat history
                                <a href="javascript:void(0);" class="text-red pull-right"><i class="fa fa-trash-o"></i>
                                </a>
                            </label>
                        </div><!-- /.form-group -->
                    </form>
                </div><!-- /.tab-pane -->
            </div>
        </aside><!-- /.control-sidebar -->
    <!-- Add the sidebar's background. This div must be placed
           immediately after the control sidebar -->
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
