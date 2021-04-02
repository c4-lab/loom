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
                    <g:if test="${uiflag == 1}">
                        <div class="row">

                            <div class="col-sm-1"></div>

                            <div class="col-sm-10 table-bordered dvSourceContainer"
                                 style="min-height: 200px !important;">
                                <ul class="dvSource g_list originalstory" >
                                    <g:each in="${tts}" var="tt">
                                        <li class="draggable ui-state-default tile-available ui-draggable"
                                            drag-id="${tt.id}"><span class="tile-text">${tt.text}<g:hiddenField name="userTails" value="${tt.text}"/></span></li>
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

                                <div class="col-sm-10 table-bordered ui-widget-content dvDest">
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
                                <g:hiddenField name="uiflag" value="${uiflag}"/>
                                <g:hiddenField name="training" value="${training.id}"/>

                            </div>

                            <hr/>



                            <div class="row">
                                <div class="col-sm-9"></div>

                                <div class="col-sm-3">
                                    <button type="submit" class="btn btn-success"
                                            >Submit</button>
                                    <button type="reset" class="btn btn-default reset-training" onclick="resetTraining(${uiflag})">Reset</button>
                                </div>
                            </div>
                        </g:form>
                    </g:if>
                    <g:else>
%{--                        <ul class="g_list sortable" id="sort1">--}%
%{--                        <g:each in="${tts}" var="tt">--}%

%{--                            <li class="ui-state-default draggable tile-available ui-draggable" drag-id="${tt.id}"><span class="ui-icon-arrowthick-2-n-s tile-text">${tt.text}<g:hiddenField name="userTails" value="${tt.text}"/></span></li>--}%
%{--                        </g:each>--}%
%{--                        </ul>--}%
%{--                        <ul class="g_list sortable" id="sort3">--}%
%{--                            <g:each in="${tts}" var="tt">--}%

%{--                                <g:if test="${tailsList}">--}%
%{--                                    <g:each in="${tailsList}" var="tail">--}%
%{--                                        <li class="draggable ui-state-default ui-draggable ui-draggable-handle purple"--}%
%{--                                            drag-id="${tail.id}">--}%
%{--                                            <span class="tile-text">${tail.text}<g:hiddenField name="userTails" value="${tail.text}"/></span>--}%
%{--                                        </li>--}%
%{--                                    </g:each>--}%
%{--                                </g:if>--}%
%{--                            </g:each>--}%
%{--                        </ul>--}%

                        <div class="row">

                            <div class="col-sm-1"></div>

                            <div class="col-sm-10 table-bordered dvSourceContainer"
                                 style="min-height: 200px !important;">
                                <ul class="g_list" id="sort1">
                                    <g:each in="${tts}" var="tt">
                                        <li class="draggable ui-state-default tile-available ui-draggable"
                                            drag-id="${tt.id}"><span class="tile-text">${tt.text}<g:hiddenField name="userTails" value="${tt.text}"/></span></li>
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

                                <div class="col-sm-10 table-bordered ui-widget-content" >
                                    <ul style="min-height: 200px !important;" id="sort3" class="g_list">
%{--                                        <g:if test="${tailsList}">--}%
%{--                                            <g:each in="${tailsList}" var="tail">--}%
%{--                                                <li class="draggable ui-state-default ui-draggable ui-draggable-handle purple"--}%
%{--                                                    drag-id="${tail.id}">--}%
%{--                                                    <span class="tile-text">${tail.text}</span>--}%
%{--                                                </li>--}%
%{--                                            </g:each>--}%
%{--                                        </g:if>--}%
                                    </ul>
                                </div>

                                <div class="col-sm-1"></div>
                                <g:hiddenField name="uiflag" value="${uiflag}"/>
                                <g:hiddenField name="tails" value="${rawTails}"/>
                                <g:hiddenField name="training" value="${training.id}"/>

                            </div>

                            <hr/>



                            <div class="row">
                                <div class="col-sm-9"></div>

                                <div class="col-sm-3">
                                    <button type="submit" class="btn btn-success"
                                            >Submit</button>
                                    <button type="reset" class="btn btn-default reset-training" onclick="res()">Reset</button>
                                </div>
                            </div>
                        </g:form>
                    </g:else>
                </div>
            </div>
        </div>

        <div class="col-sm-1"></div>
    </div>
</section>
<script>
    new Sortable(sort1, {
        group: 'shared', // set both lists to same group
        animation: 150,
        onEnd: function (/**Event*/evt) {
            updateTrainingScore();
        },
    });

    new Sortable(sort3, {
        group: 'shared',
        animation: 150,
    });
    var initial = [];
    $('#sort1').each(function(index, anchor) {
        initial.push(anchor.innerHTML);
    });
    function res(){

        $('#sort1').each(function(index, anchor) {
            anchor.innerHTML = initial[index];
        });
        var toremove = $("#sort3").find("li");
        toremove.remove();

    }



</script>