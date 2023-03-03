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

</section>

<section class="content-header">
    <div class="row">
        <div class="col-sm-1"></div>

        <div class="col-sm-10">
            <div class="row">

                <div class="col-sm-3">
                    <h1 class="text-left" id="training-name">${training.name}</h1>
                    <g:hiddenField name="training" id="training" value="${training.id}"/>
                </div>

                <div class="col-sm-3 pull-right text-right">
                    <h1>SCORE:<span id="training-score">0.0</span></h1>
                </div>

            </div>
            <div class="box box-success box-solid">
                <div class="box-header with-border">
                    <div class="col-md-8">
                        <h3 class="box-title">Keep an eye on your score!</h3>
                    </div>

                    <div class="col-md-10 text-right">

                    </div>
                </div>

                <div class="box-body">
                    <g:form name="trainingForm" method="post" controller="training" action="submitTraining">
                        <div class="row col-centered">
                            <div class="col-xs-4">
                                <p>Your story:</p>
                            </div>
                        </div>

                        <div class="row center-block">

                            <div class="col-xs-11 table-bordered ui-widget-content dvDest col-centered">
                                <ul style="min-height: 200px !important;" id="sort2"
                                    class="${uiflag == 1 ? "dvSource" : ""}  g_list">
                                    <g:if test="${storyTiles}">
                                        <g:each in="${storyTiles}" var="tt">
                                            <li class="ui-state-default purple"
                                                drag-id="${tt.text_order}"><span class="tile-text">${raw(tt.text)}</span></li>
                                        </g:each>
                                    </g:if>

                                </ul>
                            </div>
                            <g:hiddenField name = "storyTiles" value=''/>
                            <g:hiddenField name="uiflag" value="${uiflag}"/>
                            <g:hiddenField name="trainingSetId" value="${trainingSet.id}"/>
                            <g:hiddenField name="trainingId" value="${training.id}"/>
                            <g:hiddenField name="assignmentId" value="${assignmentId}"/>
                            <div id="userTiles"></div>

                        </div>


                        <!---  THIS IS YOUR PRIVATE INFO ------>
                        <div class="row col-centered">
                            <div class="col-xs-4">
                                <p>Your private info:</p>
                            </div>
                        </div>


                        <div class="row">
                            <div class="col-xs-11 table-bordered ui-widget-content dvSourceContainer col-centered">
                                <ul style="min-height: 200px !important;" id="sort4"
                                    class="${uiflag == 1 ? "dvSource" : ""} g_list privateinfo">
                                    <g:each in="${allTiles}" var="tt">
                                        <li class="ui-state-default tile-available"
                                            drag-id="${tt.text_order}"><span class="tile-text">${raw(tt.text)}</span></li>
                                    </g:each>

                                </ul>
                            </div>

                        </div>

                        <div class="row">
                            <p></p>
                        </div>
                        <div class="row">


                            <div class="col-sm-3 pull-right center-block text-center">
                                <button type="submit" class="btn btn-success">Submit</button>
                                <button type="reset" class="btn btn-default reset-training"
                                        onclick="resetTraining(${uiflag})">Reset</button>
                            </div>
                        </div>
                    </g:form>


                </div>
            </div>
        </div>

        <div class="col-sm-1"></div>
    </div>
</section>
<script>
    // new Sortable(sort1, {
    //     group: 'shared', // set both lists to same group
    //     animation: 150,
    //     onEnd: function (/**Event*/evt) {
    //         updateTrainingScore();
    //     },
    // });
    //
    // new Sortable(sort3, {
    //     group: 'shared',
    //     animation: 150,
    // });
    var initial = [];
    $('#sort1').each(function (index, anchor) {
        initial.push(anchor.innerHTML);
    });

    function res() {

        $('#sort1').each(function (index, anchor) {
            anchor.innerHTML = initial[index];
        });
        var toremove = $("#sort3").find("li");
        toremove.remove();

    }



</script>