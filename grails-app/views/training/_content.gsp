<section class="alert-section">
    <div class="row">
        <div class="col-md-1"></div>

        <div class="col-md-10">
            <g:render template="/shared/alert-templates"/>
        </div>

        <div class="col-md-1"></div>
    </div>
</section>

<section class="content-header">
    <div class="row">
        <div class="col-sm-1"></div>


        <div class="col-sm-7">
            <h1 class="text-left" id="training-name">${training.name}</h1>
            <g:hiddenField name="training" id="training-id" value="${training.id}"/>
        </div>

        <div class="col-sm-4">
            <h1>SCORE:<span id="training-score">0.0</span></h1>
        </div>




    </div>
</section>

<section class="content-header">
    <div class="row">
        <div class="col-sm-1"></div>

        <div class="col-sm-10">
            <div class="box box-success box-solid">
                <div class="box-header with-border">
                    <div class="col-md-8">
                        <h3 class="box-title">Keep an eye on your score!</h3>
                    </div>

                    <div class="col-md-10 text-right">

                    </div>
                </div>

                <div class="box-body">
                    <div class="row">

                        <div class="col-sm-1"></div>

                        <div id="dvSourceContainer" class="col-sm-10 table-bordered"
                             style="min-height: 200px !important;">
                            <ul class="dvSource g_list" id="sort1">
                                <g:each in="${tts}" var="tt">
                                    <li class="draggable ui-state-default tile-available"
                                        drag-id="${tt.id}"><span class="tile-text">${tt.text}</span></li>
                                </g:each>
                            </ul>
                        </div>

                        <div class="col-sm-1"></div>

                    </div>

                    <p>Your story:</p>
                    <div class="row"></div>

                    <g:form name="trainingForm" method="post" controller="training" action="submitTraining">
                        <div class="row">
                            <div class="col-sm-1"></div>

                            <div class="col-sm-10 table-bordered ui-widget-content" id="dvDest">
                                <ul style="min-height: 200px !important;" id="sort2" class="g_list">
                                    <g:if test="${tailsList}">
                                        <g:each in="${tailsList}" var="tail">
                                            <li class="draggable ui-state-default ui-draggable ui-draggable-handle purple"
                                                drag-id="${tail.id}">
                                                <span class="tile-text">${tail.text}</span>
                                            </li>
                                        </g:each>
                                    </g:if>
                                </ul>
                            </div>

                            <div class="col-sm-1"></div>
                            <g:hiddenField name="tails" value="${rawTails}"/>
                            <g:hiddenField name="training" value="${training.id}"/>

                        </div>

                        <hr/>



                        <div class="row">
                            <div class="col-sm-9"></div>

                            <div class="col-sm-3" id="btn-panel">
                                <button type="submit" class="btn btn-success"
                                        id="submit-training">Submit</button>
                                <button type="reset" id="reset-training" class="btn btn-default">Reset</button>
                            </div>
                        </div>
                    </g:form>
                </div>
            </div>
        </div>

        <div class="col-sm-1"></div>
    </div>
</section>