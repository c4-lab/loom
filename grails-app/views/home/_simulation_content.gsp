<section class="content-header">
    <div class="row">
        <div class="col-md-1"></div>

        <div class="col-md-10">
            <g:render template="/home/alert-templates"/>
            <h1 id="simulation-name">${simulation.name}</h1>
            <g:hiddenField name="simulation" value="${simulation.id}"/>
        </div>

        <div class="col-md-1"></div>
    </div>
</section>

<section class="content-header">
    <div class="row">
        <div class="col-md-1"></div>

        <div class="col-md-10">
            <div class="box box-success box-solid">
                <div class="box-header with-border">
                    <div class="col-md-2">
                        <h3 class="box-title" id="roundNumber">Round ${roundNbr + 1} of ${simulation.roundCount}</h3>
                    </div>

                    <div class="col-md-9">
                        <g:hiddenField name="simulationDuration" value="${simulation.roundTime}"/>
                    </div>

                    <div class="col-md-1">
                        <span id="timerPanel"></span>
                    </div>
                </div>

                <div class="box-body">
                    <div class="row">
                        <div class="col-md-1"></div>

                        <div class="col-md-2">
                            <ul class="nav nav-tabs tabs-left">
                                <g:each in="${userList}" var="user">
                                    <li class="${user.key != 1 ?: "active"}">
                                        <a href="#neighbour${user.key}"
                                           data-toggle="tab">${user.key == 1 ? "You" : "neighbour " + (user.key - 1)}</a>
                                    </li>
                                </g:each>
                            </ul>
                        </div>

                        <div class="col-xs-9">
                            <!-- Tab panes -->
                            <div class="tab-content">
                                <g:each in="${userList}" var="user">
                                    <div class="tab-pane ${user.key != 1 ?: "active"}"
                                         id="neighbour${user.key}">
                                        <ul class="dvSource">
                                            <g:each in="${user.value.tts}" var="tt">
                                                <li class="ui-state-default" id="${tt.id}">${tt.text}</li>
                                            </g:each>
                                        </ul>
                                    </div>
                                </g:each>
                            </div>
                        </div>
                    </div>
                    <hr/>

                    <div class="row"></div>

                    <div class="row">
                        <div class="col-md-1"></div>

                        <div class="col-md-10 table-bordered ui-widget-content" id="dvDest">
                            <ul style="min-height: 200px !important;">
                                <g:if test="${tempStory?.size() > 0}">
                                    <g:each in="${tempStory}" var="tail">
                                        <li class="ui-state-default purple" id="${tail.id}">${tail.text}</li>
                                    </g:each>
                                </g:if>
                                <g:else>
                                    <li class="placeholder">Add tails here</li>
                                </g:else>
                            </ul>
                        </div>

                        <div class="col-md-1"></div>
                    </div>

                    <hr/>

                    <div class="row">
                        <div class="col-lg-9"></div>

                        <div class="col-lg-3" id="btn-panel">
                            %{--<button type="submit" class="btn btn-success"--}%
                            %{--id="submit-simulation">Submit</button>--}%
                            <button type="reset" id="reset-simulation" class="btn btn-default">Reset</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-1"></div>
    </div>
</section>