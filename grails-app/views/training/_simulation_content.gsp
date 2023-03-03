<section class="content-header" id="simTemplate">
    <div class="row">
        <div class="col-xs-1"></div>

        <div class="col-xs-10">
            <g:render template="/shared/alert-templates"/>
            <h1 id="simulation-name">${simulation.name}</h1>
            <g:hiddenField id="simulationid" name="simulation" value="${simulation.id}"/>
        </div>

        <div class="col-xs-1"></div>
    </div>
</section>

<section class="content-header">
    <div class="row center-block container-fluid">
        <div class="col-xs-1"></div>
        <div class="col-xs-10 col-centered">
            <div class="box box-success box-solid">
                <div class="box-header with-border">
                    <div class="row col-centered">
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
                    <div class="row center-block">

                        <div class="col-xs-11 col-centered">
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
                    </div>

                    <!---  THIS IS THE NEIGHBOR INFO ------>
                    <div class="row center-block ">


                            <div class="tab-content col-xs-11 table-bordered dvSourceContainer col-centered">
                                <g:each in="${userList}" var="user">
                                    <g:if test="${user.key != 1}">
                                        <div class="tab-pane ${user.key != 2 ?: "active"}"
                                             id="neighbour${user.key}">
                                            <ul class="${uiflag == 1?"dvSource":""} originalstory g_list">
                                                <g:each in="${user.value.tts}" var="tt">
                                                    <li class="ui-state-default tile-available"
                                                        drag-id="${tt.text_order}"
                                                        nei-id="neighbour${user.key}">${raw(tt.text)}</li>
                                                </g:each>
                                            </ul>
                                        </div>
                                    </g:if>
                                </g:each>
                            </div>
                    </div>

                    <!---  THIS IS YOUR STORY ------>
                    <div class="row">
                        <div class="col-xs-2 center-block">
                            <p>Your story:</p>
                        </div>
                    </div>

                    <div class="row center-block">

                            <div class="col-xs-11 table-bordered ui-widget-content dvDest col-centered">
                                <ul style="min-height: 200px !important;" id="sort2" class="${uiflag == 1?"dvSource":""}  g_list">

                                    <g:if test="${tempStory?.size() > 0}">
                                        <g:each in="${tempStory}" var="tt">
                                            <li class="ui-state-default purple"
                                                drag-id="${tt.text_order}">${raw(tt.text)}</li>
                                        </g:each>
                                    </g:if>

                                </ul>
                            </div>

                    </div>


                    <!---  THIS IS YOUR PRIVATE INFO ------>
                    <div class="row col-centered">
                        <div class="col-xs-4">
                            <p>Your private info:</p>
                        </div>
                    </div>



                    <div class="row">
                            <div class="col-xs-11 table-bordered ui-widget-content dvSourceContainer col-centered">
                                <ul style="min-height: 200px !important;" id="sort4" class="${uiflag == 1?"dvSource":""} g_list privateinfo">
                                    <g:each in="${privateTiles}" var="tt">
                                        <li class="ui-state-default tile-available"
                                            drag-id="${tt.text_order}">${raw(tt.text)}</li>
                                    </g:each>

                                </ul>
                            </div>

                    </div>

                </div>
            </div>
        </div>

    </div>
</section>


