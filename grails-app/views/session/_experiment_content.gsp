<section class="content-header" id="expTemplate">
    <div class="row">
        <div class="col-xs-1"></div>

        <div class="col-xs-10">
            <g:render template="/shared/alert-templates"/>
            <h1 id="experiment-name">${session.name}</h1>
            <g:hiddenField id="session" name="session" value="${session.id}"/>
        </div>

        <div class="col-xs-1"></div>
    </div>
</section>

<section class="content-header">
    <div class="row">
        <div class="col-xs-1"></div>

        <div class="col-xs-10">
            <div class="box box-success box-solid">
                <div class="box-header with-border">
                    <div class="row">
                        <div class="col-xs-10">
                            <h3 class="box-title">Round ${round + 1} of ${session.experiment.roundCount}</h3>
                            <g:hiddenField id="roundNumber" name="roundNumber" value="${round}"/>
                            <g:hiddenField name="experimentDuration"
                                           value="${timeRemaining ? timeRemaining : session.experiment.roundTime}"/>
                        </div>

                        <div class="col-xs-2">
                            <span id="timerPanel"></span>
                        </div>
                    </div>
                </div>

                <div class="box-body">
                    <div class="row">
                        <div class="col-xs-1"></div>

                        <div class="col-xs-10">
                            <ul class="nav nav-tabs" id="neighbors">
                                <g:each in="${userList}" var="user" status="i">
                                    <g:if test="${user.key != 0}">
                                        <li class="${user.key != 1 ?: 'active'}">
                                            <a href='#neighbour${user.key}' data-toggle='tab'>neighbour${user.key}</a>
                                        </li>
                                    </g:if>
                                %{--<loom:currentUserTab userKey="${user.key}"/>--}%
                                </g:each>
                            </ul>
                        </div>

                        <div class="col-xs-1"></div>
                    </div>

                    <div class="row">
                        <div class="col-xs-1"></div>

                        <div class="tab-content col-xs-10 table-bordered" id="dvSourceContainer">
                            <g:each in="${userList}" var="user" status="i">
                                <g:if test="${user.key != 0}">
                                    <div class="tab-pane ${user.key != 1 ?: "active"}" id="neighbour${user.key}">
                                        <ul class="dvSource">
                                            <g:each in="${user.value}" var="tt">
                                                <li class="ui-state-default tile-available"
                                                    drag-id="${tt.id}">${tt.text}</li>
                                            </g:each>
                                        </ul>
                                    </div>
                                </g:if>
                            %{--<loom:currentUserTabContent userKey="${user.key}" userValue="${user.value}"/>--}%
                            </g:each>
                        </div>

                        <div class="col-xs-1"></div>
                    </div>

                    <p>Your story:</p>

                    <div class="row"></div>

                    <div class="row">
                        <div class="col-xs-1"></div>

                        <div class="col-xs-10 table-bordered ui-widget-content" id="dvDest">
                            <ul style="min-height: 200px !important;" id="sort2" class="g_list">
                                <g:each in="${userList}" var="user">
                                    <g:if test="${user.key == 0}">
                                        <g:each in="${user.value}" var="tt">
                                            <li class="ui-state-default purple" drag-id="${tt.id}">${tt.text}</li>
                                        </g:each>
                                    </g:if>
                                </g:each>


                            %{--<g:else>--}%
                            %{--<li class="placeholder">Add tails here</li>--}%
                            %{--</g:else>--}%
                            </ul>
                        </div>

                        <div class="col-xs-1"></div>
                    </div>

                    <hr/>

                </div>
            </div>
        </div>

        <div class="col-xs-1"></div>
    </div>
</section>