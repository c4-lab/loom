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
                                                drag-id="${tt.id}"><span class="tile-text">${raw(tt.text)}</span></li>
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
                                            drag-id="${tt.id}"><span class="tile-text">${raw(tt.text)}</span></li>
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

    // function initMyDragNDrop() {
    //     $("#sort2").sortable({
    //         opacity: 0.5,
    //         cursor: "crosshair",
    //         placeholder: "ui-state-highlight",
    //         forcePlaceholderSize: true,
    //
    //         start: function (event,ui) {
    //             console.log("Detecting a drag event")
    //             ui.placeholder.height(ui.item.height());
    //             ui.placeholder.width(ui.item.width());
    //             // $(event.target).find('li').css("white-space", "nowrap");
    //         },
    //         stop: function (event,ui) {
    //             updateTrainingScore();
    //         }
    //     })
    //
    // }
    //
    // function updateTrainingScore() {
    //     if ($("#training-name").length > 0) {
    //         var elems = $("#sort2").find('li');
    //         var tile_ids = $("#sort2 li").map(function () {
    //             return $(this).attr("drag-id")
    //         }).get().join(",")
    //         console.log("Got "+tile_ids)
    //         $("input[name='storyTiles']").val(tile_ids);
    //
    //
    //
    //
    //         $.ajax({
    //             url: "/loom/training/getTrainingScore",
    //             type: 'POST',
    //             data: {
    //                 userTiles: tile_ids,
    //                 trainingSetId: $("#training").val()
    //             },
    //             timeout: 999
    //         }).success(function (data) {
    //             $("#training-score").text(data);
    //             var orig = "black";
    //             $("#training-score").css("color", "red");
    //             $("#training-score").animate({color: orig}, 1000);
    //         }).error(function (data) {
    //             //check if something is going on here
    //         });
    //     }
    //
    // }
    //
    // function resetTraining() {
    //     $(".reset-training").click(function () {
    //         // if(uiflag===1){
    //         $("#sort2").find("li").each(function () {
    //             removeTile($(this));
    //             //$(this).parent().remove();
    //             //console.log($(this).parent().attr('id'));
    //             //var elem = $(".dvSource").find("[drag-id='" + $(this).parent().attr('drag-id') + "']");
    //             //
    //             //elem.removeClass('blue').addClass('tile-available');
    //         });
    //         $("#sort3").find("li").each(function () {
    //             removeTile($(this));
    //             //$(this).parent().remove();
    //             //console.log($(this).parent().attr('id'));
    //             //var elem = $(".dvSource").find("[drag-id='" + $(this).parent().attr('drag-id') + "']");
    //             //
    //             //elem.removeClass('blue').addClass('tile-available');
    //         });
    //         // }else if(uiflag===0){
    //         //     var initial = [];
    //         //     $('#sort1').each(function(index, anchor) {
    //         //         initial.push(anchor.innerHTML);
    //         //     });
    //         //     $('#sort1').each(function(index, anchor) {
    //         //         anchor.innerHTML = initial[index];
    //         //     });
    //         //     var toremove = $("#sort3").find("li");
    //         //     toremove.remove();
    //         //
    //         // }
    //
    //
    //         updateTrainingScore();
    //         //$("#training-score").text("0.0")
    //     });
    //
    // }
    //
    // function initTraining() {
    //     if ($("#training-content-wrapper").length > 0) {
    //         initTiles();
    //         initDragNDrop();
    //         initMyDragNDrop();
    //         resetTraining();
    //         submitTraining();
    //         updateTrainingScore();
    //
    //     }
    // }



</script>