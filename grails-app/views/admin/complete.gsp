<g:applyLayout name="main">
    <div class="content-wrapper">
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
                            </ul>

                            <a href="javascript:void(0);" id="create-experiment"
                               class="btn btn-primary btn-block"><b>Create a session</b></a>
                        </div>
                    </div>
                </div>

                <div class="col-md-9">
                    <div class="box box-primary">
                        <div class="box-header with-border">
                            <h3 class="box-title">Complete session creation</h3>
                        </div>

                        <g:form name="complete-form" enctype="multipart/form-data" controller="admin"
                                action="completeExperimentCreation"
                                method="POST">
                            <g:hiddenField name="experimentId" value="${experiment}"/>
                            <div class="box-body">
                                <div class="form-group">
                                    <label for="initNbrOfTiles">Initial number of tiles</label>
                                    <g:textField class="form-control" name="initNbrOfTiles" value="${initNbrOfTiles}"
                                                 placeholder="Enter initial number of tiles"/>
                                </div>

                                <div class="form-group">
                                    <label for="graphmlFile">File input</label>
                                    <input type="file" id="graphmlFile" name="graphmlFile">

                                    <p class="help-block">Please upload *.graphml file.</p>
                                </div>
                            </div>

                            <div class="box-footer">
                                <button type="submit" disabled="disabled" id="complete-btn"
                                        class="btn btn-primary">Complete</button>
                            </div>
                        </g:form>
                    </div>
                </div>
            </div>
        </section>
    </div>
</g:applyLayout>