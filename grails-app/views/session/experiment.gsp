<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="experiment-content-wrapper">
            <section class="content-header" id="expTemplate">
                <div class="row">
                    <div class="col-xs-1"></div>

                    <div class="col-xs-10">
                        <g:render template="/shared/alert-templates"/>
                        <h1 id="experiment-name">${loomSession.name}</h1>
                        <g:hiddenField id="session" name="session" value="${loomSession.id}"/>
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
                                        <h3 class="box-title">Round <span id="roundNumberTarget">...</span>
                                            of ${loomSession.experiment.roundCount}
                                        </h3>
                                    </div>

                                    <div class="col-xs-2">
                                        <span id="timerPanel"></span>
                                    </div>
                                </div>
                            </div>

                            <div class="box-body">
                                <div id="neighborsStories">
                                    <g:render template="experiment_content"
                                              model="[neighborState: neighborState, round: round,
                                                      timeRemaining: timeRemaining,
                                                      loomSession  : loomSession, paused: paused]"/>
                                </div>

                                <p>Your story:</p>

                                <div class="row"></div>

                                <div class="row">
                                    <div class="col-xs-1"></div>

                                    <div class="col-xs-10 table-bordered ui-widget-content" id="dvDest">
                                        <ul style="min-height: 200px !important;" id="sort2" class="g_list">
                                            <g:each in="${myState}" var="tt">

                                                <li class="ui-state-default purple"
                                                    drag-id="${tt.id}"><span class="tile-text">${tt.text}</span></li>

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
        </div>
    </div>
    <script type="text/javascript">
        jQuery(document).ready(function () {
            shouldLogout = true;
            window.onbeforeunload = logout;
            initMyDragNDrop();
            //removeTile();

        });
    </script>
</g:applyLayout>