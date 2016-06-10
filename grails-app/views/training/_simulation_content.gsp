<section class="content-header" id="simTemplate">
    <div class="row">
        <div class="col-xs-1"></div>

        <div class="col-xs-10">
            <g:render template="/shared/alert-templates"/>
            <h1 id="simulation-name">${simulation.name}</h1>
            <g:hiddenField name="simulation" value="${simulation.id}"/>
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
                            <h3 class="box-title" id="roundNumber">Round ${roundNbr} of ${simulation.roundCount}</h3>
                            <g:hiddenField name="simulationDuration" value="${simulation.roundTime}"/>
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
                                <g:each in="${userList}" var="user">
                                    <g:if test="${user.key != 1}">
                                        <li class="${user.key != 2 ?: "active"}">
                                            <a href="#neighbour${user.key}"
                                               data-toggle="tab">${"neighbour " + (user.key - 1)}</a>
                                        </li>
                                    </g:if>
                                </g:each>
                            </ul>
                        </div>

                        <div class="col-xs-1">
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-xs-1"></div>


                        <div class="tab-content col-xs-10 table-bordered" id="dvSourceContainer">
                            <g:each in="${userList}" var="user">
                                <g:if test="${user.key != 1}">
                                    <div class="tab-pane ${user.key != 2 ?: "active"}"
                                         id="neighbour${user.key}">
                                        <ul class="dvSource">
                                            <g:each in="${user.value.tts}" var="tt">
                                                <li class="ui-state-default tile-available"
                                                    drag-id="${tt.text_order}">${tt.text}</li>
                                            </g:each>
                                        </ul>
                                    </div>
                                </g:if>
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
                                    <g:if test="${user.key == 1}">
                                        <g:each in="${user.value.tts}" var="tt">
                                            <li class="ui-state-default purple"
                                                drag-id="${tt.text_order}">${tt.text}</li>
                                        </g:each>
                                    </g:if>
                                </g:each>

                                <g:if test="${tempStory?.size() > 0}">
                                    <g:each in="${tempStory}" var="tail">
                                        <li class="ui-state-default purple"
                                            drag-id="${tail.text_order}">${tail.text}</li>
                                    </g:each>
                                </g:if>
                            %{--<g:else>--}%
                            %{--<li class="placeholder">Add tails here</li>--}%
                            %{--</g:else>--}%
                            </ul>
                        </div>

                        <div class="col-xs-1"></div>
                    </div>

                </div>
            </div>
        </div>

    </div>
</section>