$(document).ready(function () {
    $("#create-experiment").click(function () {
        $("#experiment-file-upload-modal").modal('show');
    });

    $("#network_type").click(function () {
        var network_type=$("#select option:selected").val();
        alert(network_type);
    });




    $("#create-trainingset").click(function () {
        $("#training-set-file-upload-modal").modal('show');
    });

    $("#create-stories").click(function () {
        $("#create-story-set").modal('show');
    });

    $("#stop-waiting").click(function() {
       shouldLogout = false;
        window.location="/loom/session/stopWaiting?session="+$("#sessionId").val();
    });

    $("#clone-session").click(function () {
        alert(parseInt($("#sessionId").val(),16))
        $.ajax({
            url: "/loom/admin/cloneSession",
            type: 'POST',
            data: {
                session: parseInt($("#sessionId").val(),16)
            }
        }).success(function (data) {
            $("#success-alert").toggleClass('hide show');
            var session = $.parseJSON(data);
            $("#session-link").text(session.session.name);
            $("#session-link").attr('href', '/loom/session/' + session.session.id);
        }).error(function () {
            $("#error-alert").toggleClass('hide show');
        });
    });

    $("#publish-by-email").click(function () {
        $("#email-modal").modal('show');
    });

    $(".launch-experiment").click(function () {
        $("#sessionLaunchId").val($("span",this).text());
        $("#launch-modal").modal('show');
    });

    $('#complete-form').find('input').change(function () {

        var empty = false;
        $('#complete-form').find('input').each(function () {
            if ($(this).val() == '') {
                empty = true;
            }
        });

        if (empty) {
            $('#complete-btn').attr('disabled', 'disabled');
        } else {
            $('#complete-btn').removeAttr('disabled');
        }
    });

    $("#publish-anon-session").click(function () {
        $.ajax({
            url: "/loom/admin/publishAnonym",
            type: 'POST',
            data: {
                session: $("#sessionId").val()
            }
        }).success(function (data) {
            $("#success-publish-anon").toggleClass('hide show');
        }).error(function () {
            $("#error-publish-anon").toggleClass('hide show');
        });
    });

    var timeInterval;
    $("#sessions").on('click', '.session-action', function (){

        var el = $(this);
        var t = el.text();
        var sessionId = $(".sessionId",this.parentNode).text();
        var status = $(".session-span",this.parentNode.parentNode.parentNode);
        var setTimer = $(".set-timer",this.parentNode.parentNode.parentNode);

        if(t.includes(el.data("text-swap"))){
            el.text(el.data("text-original"));
        }else{
            el.text(el.data("text-swap"));
        }


        if(el.text() === el.data("text-swap")){

            $.ajax({
                url: "/loom/admin/cancelSession",
                type: 'POST',
                data:
                    {
                        sessionId: sessionId,

                    },

                success: function (){

                    status.text("Status: CANCEL");
                    setTimer.text();
                    setTimer.hide();
                    clearInterval(timeInterval);
                }
            });
        }else{
            $.ajax({
                url: "/loom/admin/validateSession",
                type: 'POST',
                data:
                    {
                        sessionId: sessionId,

                    },
                success: function (data){

                    status.text("Status: PENDING");
                    setTimer.text("");
                    setTimer.show();
                    // clearInterval(timeInterval);
                    timeInterval = setTimeer(data, setTimer, "Pending",status,null,null);

                }
            })
        }
    });

    $("#sessions").on('click', '.start-session', function (){
        var sessionId = $(".sessionId",this.parentNode).text();
        var setTimer = $(".set-timer",this.parentNode.parentNode.parentNode);
        var status = $(".session-span",this.parentNode.parentNode.parentNode);
        var startTime = Date.now();
        $.ajax({
            url: "/loom/admin/startSession",
            type: 'POST',
            data:
                {
                    sessionId: sessionId,

                },
            dataType:"json",
            success: function (data){
                // alert(data.less);
                if(data.status==="cancel"){
                    alert("Cannot start a canceled session. Please validate it first!");
                }
                if(data.status==="less"){

                    alert("Not enough users!");
                }
                if(data.status==="start"){
                    status.text("Status: Active");
                    clearInterval(timeInterval);
                    timeInterval = setTimeer(startTime, setTimer, "Active");
                }
                if(data.status==="fail"){
                    alert("Fail to start!");
                }

            }
        });
    });

    $("#sessions").on('click', '.pay-session', function (){
        var sessionId = $(".sessionId",this.parentNode).text();
        var setTimer = $(".set-timer",this.parentNode.parentNode.parentNode);
        var status = $(".session-span",this.parentNode.parentNode.parentNode);
        var startTime = Date.now();
        $.ajax({
            url: "/loom/admin/paySession",
            type: 'POST',
            data:
                {
                    sessionId: sessionId,

                },
            dataType:"json",
            success: function (data){
                if(data.status==="no_payable"){
                    alert("No payable assignment!");
                }
                if(data.status==="success"){


                    window.location = "/loom/admin/board/";
                }


            }
        });
    });

    $("#create-exp").on('click', function (){
        var name = $("#name").val();
        var storySet = $("#StorySelect").val();
        var trainingSet = $("#trainingSetSelect").val();
        var min_nodes = $("#min_nodes").val();
        var max_nodes = $("#max_nodes").val();
        var initialNbrOfTiles = $('#initialNbrOfTiles').val();
        var rounds = $('#rounds').val();
        var duration = $('#duration').val();
        var network_type = $('input:radio[name="network_type"]:checked').val();
        var min_degree;
        var max_degree;
        var m;
        var prob;
        var performance;
        var reading;
        var vaccine_min;
        var vaccine_max;
        var isQualifier =  $('input:radio[name="qualifier"]:checked').val();
        var accepting = $("#accepting").val();
        var completion = $("#completion").val();
        var waiting = $("#waiting").val();
        var score = $("#score").val();

        if (network_type === 'Lattice'){
            min_degree = $("#Lattice_min_degree").val();
            max_degree = 0;
            m = 0;
            prob = 0;
        }else if(network_type === 'Newman_Watts'){
            min_degree = $("#Newman_min_degree").val();
            max_degree = $("#Newman_max_degree").val();
            m = 0;
            prob = $("#Newman_prob").val();

        }else if(network_type === 'Barabassi_Albert'){
            min_degree = $("#BA_min_degree").val();
            max_degree = $("#BA_max_degree").val();
            m = $("#BA_M").val();
            // prob = $("#BA_prob").val();
            prob = 0;
        }

        if (isQualifier === 'yes'){
            performance = $("#performance").val();
            reading = $("#reading").val();
            vaccine_min = $("#vaccine_score_min").val();
            vaccine_max = $("#vaccine_score_max").val();
            if (vaccine_min>vaccine_max){
                alert("please use the correct range of vaccine score")
            }
        }else if(isQualifier === 'no'){
            performance = -1;
            reading = -1;
            vaccine_min = -1;
            vaccine_max = -1;
        }

        if (name==='' || storySet==='null' || trainingSet==='null' || network_type==null || (prob==='' && network_type==='Newman_Watts')
        || isQualifier==null || (vaccine_min && vaccine_max==null) || (vaccine_max && vaccine_min==null)
        || accepting==='' || completion==='' || waiting==='' || score===''){
            alert("please complete the required fields");
            $("#experiment-file-upload-modal").modal('show');
        }
        else if (network_type === 'Barabassi_Albert' && max_degree<min_degree){
            alert("total number of nodes must be at least equal to the initial set");
            $("#experiment-file-upload-modal").modal('show');
        }
        else if (max_nodes<min_nodes){
            alert("max allowed nodes must not be smaller than min allowed nodes");
            $("#experiment-file-upload-modal").modal('show');
        } else{
            $.ajax({
                url: "/loom/admin/uploadExperiment",
                type: 'POST',
                data:
                    {
                        name: name,
                        storySet:storySet,
                        trainingSet:trainingSet,
                        min_nodes:min_nodes,
                        max_nodes:max_nodes,
                        initialNbrOfTiles:initialNbrOfTiles,
                        rounds:rounds,
                        duration:duration,
                        network_type:network_type,
                        min_degree:min_degree,
                        max_degree:max_degree,
                        m:m,
                        prob:prob,
                        performance:performance,
                        reading:reading,
                        vaccine_min:vaccine_min,
                        vaccine_max:vaccine_max,
                        isQualifier:isQualifier,
                        accepting:accepting,
                        completion:completion,
                        waiting:waiting,
                        score:score

                    },
                dataType:"json",
                success: function (data){
                    if(data.message==="duplicate"){
                        alert("name already exists!");
                    }
                    if(data.message==="exp_error"){

                        alert("fail to create the experiment!");
                    }
                    // if(data.message==="network_error"){
                    //
                    //     alert("wrong network parameters!");
                    // }
                    if( data.message==="success"){
                        window.location = "/loom/admin/board/";

                    }


                }
            });
        }

    });


    // $("#create-training").on('click', function (){
    //     var name = $("#trainingSetName").val();
    //     var inputFile = $("#inputFile");
    //     var performance = $("#perform");
    //     var reading = $("#reading").val();
    //     var vaccine = $("#vaccine").val();
    //     var performance_score = $("#traing_performance").val();
    //
    //     alert(inputFile);
    //     if (name==='' || inputFile==null){
    //         alert("please complete the required fields");
    //         $("#training-set-file-upload-modal").modal('show');
    //     }
    //      else{
    //         $.ajax({
    //             url: "/loom/admin/uploadTrainingSet",
    //             type: 'POST',
    //             data:
    //                 {
    //                     name: name,
    //                     // inputFile:inputFile,
    //                     performance:performance.checked,
    //                     performance_score:performance_score,
    //                     reading:reading,
    //                     vaccine:vaccine,
    //
    //                 },
    //             dataType:"json",
    //             success: function (data){
    //                 if(data.message==="duplicate"){
    //                     alert("name already exists!");
    //                 }
    //                 // if(data.message==="exp_error"){
    //                 //
    //                 //     alert("fail to create the experiment!");
    //                 // }
    //                 // if(data.message==="network_error"){
    //                 //
    //                 //     alert("wrong network parameters!");
    //                 // }
    //                 if( data.message==="success"){
    //                     window.location = "/loom/admin/board/";
    //
    //                 }
    //
    //
    //             }
    //         });
    //     }
    //
    // });

    $("#create-story").on('click', function (){
        var title = $("#story-title").val();
        var tails = $("#story-text").val();
        // alert(tails);

        if (title==='' || tails===''){
            alert("please complete the required fields");
            // $("#experiment-file-upload-modal").modal('show');
        }
         else{
            $.ajax({
                url: "/loom/admin/uploadStorySet",
                type: 'POST',
                data:
                    {
                        title: title,
                        tails:tails,

                    },
                dataType:"json",
                success: function (data){
                    if(data.message==="duplicate"){
                        alert("title already exists!");
                    }
                    if(data.message==="error"){

                        alert("fail to create!");
                    }
                    if( data.message==="success"){
                        window.location = "/loom/admin/board/";

                    }


                }
            });
        }



    });

    initTraining();
    initSimulation();
    initExperiment();

    var ss = $('.session-row');

    ss.each(function (){
        var timeInterval;
        var sessionId = $(".sessionId",this.parentNode).text();
        var setTimer = $(".set-timer",this);
        var currentRound = $(".current-round",this);
        var count = $(".count",this);
        var status = $(".session-span",this);
        var completed = $(".completed",this);
        var pay_btn = $(".pay-session", this.parentNode);
        var pay_status = $(".payment-status",this);
        // alert(pay_btn.text());
        setInterval(function (){


            $.ajax({

                url: "/loom/admin/refresh",
                type: 'POST',
                data:
                    {
                        sessionId: sessionId,

                    },
                dataType:"json",
                success: function (data){
                    var current = Date.now();

                    if(data.sessionState === "PENDING"){
                        clearInterval(timeInterval);
                        setTimer.text( "Pending Time: "+Math.floor((current-data.startPending)/1000/60)+" minutes");
                        // timer.text(type + " Time: "+Math.floor((current-startTime))+" minutes");

                        status.text("Status: "+data.sessionState);

                        count.text("Connected users: "+data.count);


                        pay_btn.hide();
                        currentRound.text("Current round: "+data.round)

                        // timeInterval = setTimeer(data, setTimer, "Pending",status,count,currentRound);

                    }
                    else if(data.sessionState === "ACTIVE"){

                        clearInterval(timeInterval);
                        setTimer.text( "Active Time: "+Math.floor((current-data.startActive)/1000/60)+" minutes");
                        // timer.text(type + " Time: "+Math.floor((current-startTime))+" minutes");

                        status.text("Status: "+data.sessionState);

                        count.text("Connected users: "+data.count);
                        pay_btn.hide();



                        currentRound.text("Current round: "+data.round)
                    }
                    else if(data.sessionState === "CANCEL" || data.sessionState === "FINISHED"){
                        status.text("Status: "+data.sessionState);
                        // completed.text("payment status: "+data.payment_status);
                        // alert(pay_btn.style);
                        pay_btn.show();
                        pay_status.text("Payment status: "+data.payment_status);
                        pay_status.show();

                        // alert("sdfs");
                    }

                }
            });

        },2000);


    });


});

function create_train(){
    var name = $("#trainingSetName").val();
    var inputFile = document.getElementById("trainingInputFile").value;
    var performance = $("#perform");
    var reading = $("#reading").val();
    var vaccine = $("#vaccine").val();
    var performance_score = $("#traing_performance").val();

    alert(inputFile);
    if (name==='' || inputFile==null){
        alert("please complete the required fields");
        $("#training-set-file-upload-modal").modal('show');
    }
    else{
        $.ajax({
            url: "/loom/admin/uploadTrainingSet",
            type: 'POST',
            data:
                {
                    name: name,
                    // inputFile:inputFile,
                    performance:performance.checked,
                    performance_score:performance_score,
                    reading:reading,
                    vaccine:vaccine,

                },
            dataType:"json",
            success: function (data){
                if(data.message==="duplicate"){
                    alert("name already exists!");
                }
                // if(data.message==="exp_error"){
                //
                //     alert("fail to create the experiment!");
                // }
                // if(data.message==="network_error"){
                //
                //     alert("wrong network parameters!");
                // }
                if( data.message==="success"){
                    window.location = "/loom/admin/board/";

                }


            }
        });
    }
}


function setTimeer(data, timer, type,status,count,currentRound){
    var startTime = data.startActive;
    if (type==='Pending'){
        startTime = data.startPending;
    }
    return setInterval(
        function (){
            var current = Date.now();

            timer.text(type + " Time: "+Math.floor((current-startTime)/1000/60)+" minutes");
            // timer.text(type + " Time: "+Math.floor((current-startTime))+" minutes");

            status.text("Status: "+data.sessionState);
            if (count){
                count.text("Connected users: "+data.count);
            }
            if(currentRound){

                currentRound.text("Current round: "+data.round)
            }


        },1000);
}

function chooseType(tag){

    var s1 = document.getElementById('s1');
    var s2 = document.getElementById('s2');
    var s3 = document.getElementById('s3');
    if(tag==='Lattice'){
        s1.style.display = '';
        s2.style.display = 'none';
        s3.style.display = 'none';
    }
    else if(tag==='Newman-Watts'){
        s1.style.display = 'none';
        s2.style.display = '';
        s3.style.display = 'none';
    }else if(tag==='Barabassi-Albert'){
        s1.style.display = 'none';
        s2.style.display = 'none';
        s3.style.display = '';
    }
}


function chooseExperimentQualifier(tag){

    var s1 = document.getElementById('qualify');

    if(tag==='yes'){
        s1.style.display = '';

    }else if(tag==='no'){
        s1.style.display = 'none';
    }

}


function chooseTrainingQualifier(tag){

    var s1 = document.getElementById('perform');
    var s2 = document.getElementById('traing_perform');

    if(s1.checked){
        s2.style.display = '';

    }else{
        s2.style.display = 'none';
    }

}





var shouldLogout = true;

function logout() {
    if (shouldLogout) {
        $.ajax({
            url: "/loom/logout/index",
            type: 'GET',
            async: false

        });
    }
}

function initExperiment() {
    if ($("#experiment-content-wrapper").length > 0) {
        initDragNDrop();
        initTiles();
       //resetExperiment();
        //submitExperiment();
        localStorage.setItem('remainingTime', 'null');
        clearInterval(roundInterval);
        initRound();
        initExperimentTimer();
        blockIfPaused();

    }
}

function initTraining() {
    if ($("#training-content-wrapper").length > 0) {
        initTiles();
        initDragNDrop();
        initMyDragNDrop();
        //removeTile();
        resetTraining();
        submitTraining();
        updateTrainingScore();

    }
}

function initSimulation() {
    if ($("#simulationMainContainer").length > 0) {
        initDragNDrop();
        initMyDragNDrop();
        initTiles();
        resetSimulation();
        //removeTile();
        submitSimulation();
        localStorage.setItem('remainingTime', 'null');
        clearInterval(roundInterval);
        initSimulationTimer();
    }
}

function initSimulationTimer() {
    var duration = $("#simulationDuration").val(),
        display = $('#timerPanel');

    console.log("init time " + duration);

    startSimulationTimer(duration, display);
}

function initExperimentTimer() {
    var duration = $("#experimentDuration").val(),
        display = $('#timerPanel');
    startExperimentTimer(duration, display);
}

function initRound() {
    var round = Number($("#roundNumber").val())+1;
    $("#roundNumberTarget").text(""+round)
}

function blockIfPaused() {
    if ($("#paused").val()=="true") {
       $("#neighborsStories").block("<h1>Waiting for neighbors...</h1>")
    }
}

function initTilesOld() {
    console.log("Init tiles...");
    $("#dvSourceContainer").find(".tile-available").each(function () {
        var sourceTileId = $(this).attr('drag-id');
        console.log("Found "+sourceTileId);
        $("#sort2").find(".purple").each(function () {
            if ($(this).attr('drag-id') == sourceTileId) {
                $("#dvSourceContainer").find("[drag-id='" + sourceTileId + "']").removeClass('tile-available').addClass('blue');
            }

        });
    });
    //$("#dvDest").find("li.purple").each(function() {
    //    addRemoveBtn($(this).attr("drag-id"))
    //})

}

function initTiles() {
    $("#sort2").find(".purple").each(function () {
        var destTileId = $(this).attr('drag-id');
        var matched = $("#dvSourceContainer").find(".tile-available[drag-id='" + destTileId + "']");
        if (matched.length > 0) {
            matched.each(function () {
                $(this).removeClass('tile-available').addClass('blue');
            });
            addRemoveBtn($(this));
        } else {
            removeRemoveBtn($(this));
        }
    });
}

function markAsDropped(source) {
    $(".dvSource").find("[drag-id='" + source + "']").removeClass('tile-available').addClass('blue');
    $("#sort2").find("[drag-id='" + source + "']").removeClass('tile-available').addClass('purple');
    $("#sort2").find("[drag-id='" + source + "']").removeAttr("style");
}

function addRemoveBtn(elt) {
    if (!($(elt).find("a").length)) {
        elt.append("<span class='removeTile'>&nbsp;&nbsp;&nbsp;<a href='javascript:void(0);'><b>X</b></a></span>");
        removeTileEvent(elt);
    }
}

function removeRemoveBtn(elt) {
    $(elt).find("span.removeTile").remove();
}

function removeTile(elt) {
    var toremove = $(elt).closest("li");
    toremove.remove();
    console.log(toremove.attr('id'));
    var elem = $(".dvSource").find("[drag-id='" + toremove.attr('drag-id') + "']");
    elem.removeClass('blue');
    elem.addClass('tile-available');

}




function removeTileEvent(elt) {
    $(elt).find("a").click(function (e) {
        var toremove = $(this).closest("li");
        removeTile(toremove);
        updateTrainingScore();
        //toremove.remove();
        //console.log(toremove.attr('id'));
        //var elem = $(".dvSource").find("[drag-id='" + toremove.attr('drag-id') + "']");
        //elem.removeClass('blue');
        //elem.addClass('tile-available');
        //updateTrainingScore();
    });
}

function initDragNDrop() {
    $(".dvSource").find("li").draggable({
        helper: "clone",
        opacity: 0.5,
        cursor: "crosshair",
        connectToSortable: "#sort2",
        revert: "invalid",
        cancel: ".blue",
        placeholder: "ui-state-highlight",
        start: function (event,ui) {
            $('.ui-draggable-dragging').css("white-space", "nowrap");
        },
        stop: function (event,ui) {
            console.log($(event.target).attr("drag-id"));
            if ($("#sort2").find("[drag-id='" + $(event.target).attr("drag-id") + "']").length > 0) {
                var source = $(event.target).attr("drag-id");
                markAsDropped(source);
                addRemoveBtn($("#sort2").find("[drag-id='" + source + "']"));
                //removeTile();
                console.log('receive');
                //updateTrainingScore();
                //var elems = $("#dvDest").find('ul li span.tile-text');
                //var text_all = elems.map(function () {
                //    return $(this).text();
                //}).get().join(";");
                //$("#tails").val(text_all);
            }

           // console.log($("#trainingForm .ui-draggable").map(function() {return $(this).attr("drag-id")}).get().join(";"))


        }
    });


   // $(".dvSource, #sort2").disableSelection();
}

function initMyDragNDrop() {
    $("#sort2").sortable({
        opacity: 0.5,
        cursor: "crosshair",
        placeholder: "ui-state-highlight",
        forcePlaceholderSize: true,

        start: function (event,ui) {
            ui.placeholder.height(ui.item.height());
            ui.placeholder.width(ui.item.width());
            $(event.target).find('li').css("white-space", "nowrap");
        },
        stop: function (event,ui) {

            updateTrainingScore();
        }
    });
}

function updateTrainingScore() {
    if ($("#training-name").length > 0) {
        var elems = $("#dvDest").find('ul li span.tile-text');
        var text_all = elems.map(function () {
            return $(this).text();
        }).get().join(";");
        $("#tails").val(text_all);
        $.ajax({
            url: "/loom/training/getTrainingScore",
            type: 'POST',
            data: {
                userTiles: $("#trainingForm .ui-draggable").map(function () {
                    return $(this).attr("drag-id")
                }).get().join(";"),
                training: $("#training").val()
            },
            timeout: 999
        }).success(function (data) {
            $("#training-score").text(data);
            var orig = "black";
            $("#training-score").css("color", "red");
            $("#training-score").animate({color: orig}, 1000);
        }).error(function (data) {
            //check if something is going on here
        });
    }

}

function resetTraining() {
    $("#reset-training").click(function () {
        $("#sort2").find("li").each(function () {
            removeTile($(this));
            //$(this).parent().remove();
            //console.log($(this).parent().attr('id'));
            //var elem = $(".dvSource").find("[drag-id='" + $(this).parent().attr('drag-id') + "']");
            //
            //elem.removeClass('blue').addClass('tile-available');
        });
        updateTrainingScore();
        //$("#training-score").text("0.0")
    });

}

function submitTraining() {
    //$("#submit-training").click(function () {
    //    var elems = $("#dvDest").find('ul li span');
    //    var text_all = elems.map(function () {
    //        return $(this).text();
    //    }).get().join(";");
    //
    //    console.log(text_all);
    //    $.blockUI({
    //        message: '<h1>Processing!</h1>',
    //        timeout: 1000
    //    });
    //    $.ajax({
    //        url: "/loom/experiment/submitTraining",
    //        type: 'POST',
    //        data: {
    //            tails: text_all,
    //            training: $("#training").val(),
    //            trainingName: $("#training-name").text()
    //        }
    //    }).success(function (data) {
    //        if (data.indexOf("simulation") >= 0) {
    //            var session = JSON.parse(data).sesId;
    //            var roundNumber = 0;
    //            console.log("/loom/simulation/" + session + "/" + roundNumber);
    //            window.location = "/loom/simulation/" + session + "/" + roundNumber;
    //        } else {
    //            setTimeout(function () {
    //                $("#training-content-wrapper").html(data);
    //                initTraining();
    //            }, 1000);
    //        }
    //    }).error(function () {
    //        setTimeout(function () {
    //            $("#dvDest").css('border', 'solid 1px red');
    //            $("#warning-alert").addClass('show');
    //            $("#warning-alert").removeClass('hide');
    //        }, 1000);
    //    });
    //});
}

var after;
var before;
function calculateTime() {
    var seconds = Math.round((after - before) / 1000);

    console.log("seconds: " + seconds);
    //$("#simulationMainContainer").length > 0
    if (localStorage.remainingTime != 'null') {
        console.log("localStorage.remainingTime: " + localStorage.remainingTime);
        localStorage.remainingTime = localStorage.remainingTime - seconds;
        var display = $('#timerPanel');
        startSimulationTimer(localStorage.remainingTime, display);
    }
}

var roundInterval;
var pingTimer;

function startPingingForNextRound() {
    var session = $("#session").val();
    pingTimer = setInterval(function() {
        $.ajax({
            url: "/loom/session/checkExperimentRoundState/"+session,
            type: 'GET',
            timeout: 3000
        }).success(function (data) {
            if (data=="finishExperiment") {
                shouldLogout = false;
                clearInterval(pingTimer);
                console.log("/loom/experiment/finishExperiment/" + session);
                window.location = "/loom/session/finishExperiment/" + session;

            } else if (data!="pausing") {
                console.log("Processing round data");
                clearInterval(pingTimer);
                processRoundData(data)
            } else {
                console.log(data)
            }
        }).error(function (data) {
            //check if something is going on here
        })
    },1000)
}



function startSimulationTimer(duration, display) {
    var timer;
    if (isNaN(localStorage.remainingTime) || localStorage.remainingTime == 'null') {
        console.log('duration works');
        timer = duration;
    } else {
        console.log('localstorage works');
        timer = localStorage.remainingTime;
    }
    var minutes, seconds;

    console.log("simulation timer: " + timer);

    roundInterval = setInterval(function () {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;
        console.log('timer inside: ' + timer);
        display.text(minutes + ":" + seconds);

        if (--timer < 0) {
            timer = $("#simulationDuration").val();
            console.log("Submitting the form");
            submitSimulationAjax();
        }

        localStorage.setItem('remainingTime', timer);

    }, 1000);
}

function startExperimentTimer(duration, display) {
    var timer;
    if (isNaN(localStorage.remainingTime) || localStorage.remainingTime == 'null') {
        console.log('duration works');
        timer = duration;
    } else {
        console.log('localstorage works');
        timer = localStorage.remainingTime;
    }
    var minutes, seconds;

    console.log("experiment timer: " + timer);

    roundInterval = setInterval(function () {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;
        console.log('timer inside: ' + timer);
        display.text(minutes + ":" + seconds);

        if (--timer < 0) {
            timer = duration;
            console.log("Submitting the form");
            submitExperimentAjax();
        }

        localStorage.setItem('remainingTime', timer);

    }, 1000);
}

function submitSimulation() {
    $("#submit-simulation").click(function () {
        submitSimulationAjax();
    });
}

function updateProgressBar(count, max) {
    if (count == max) {
        $.blockUI("Please wait...")
    }
    var percent = count*100/max;
    $(".progress-bar").attr("aria-valuenow",percent).css("width",percent+"%");
    $(".sr-only").text(percent+"% Complete");
    $(".prog-bar-count").text(count);

}

function submitSimulationAjax() {
    $(".ui-draggable-dragging").remove();
    //if ($(".ui-draggable-dragging").length > 0) {
    //    $(".ui-draggable-dragging").remove();
    //    //$('html').on('mouseup', function () {
    //    //
    //    //    $(".ui-draggable-dragging").draggable("destroy");
    //    //});
    //}
    clearInterval(roundInterval);
    var elems = $("#dvDest").find('ul li');
    var text_all = elems.map(function () {
        return $(this).attr('drag-id');
    }).get().join(";");
    console.log(text_all);
    $.blockUI({
        message: '<h1>Waiting for other participants...</h1>',
        timeout: 1000
    });
    $.ajax({
        url: "/loom/training/submitSimulation",
        type: 'POST',
        data: {
            tails: text_all,
            simulation: $("#simulation").val(),
            roundNumber: $("#roundNumber").text()
        }
    }).success(function (data) {
        localStorage.setItem('remainingTime', 'null');
        if (data.indexOf("experiment_ready") >= 0) {
            confirmSimNav = false;
            window.location ="/loom/training/score/"+$("#simulation").val();
            //
            //var simulationScore = JSON.parse(data).simulationScore;
            //var roundNumber = 0;
            ////console.log("/loom/exper/" + session + "/" + roundNumber);
            ////window.location = "/loom/exper/" + session + "/" + roundNumber;
            //$("#simulationMainContainer").remove();
            //$("#simulationScore").css('display', 'block');
            //$("#scorePanel").text(simulationScore);
            //$("#roundNumber").val(roundNumber);
            ////$("#session").val(session);
        } else {
            setTimeout(function () {
                $("#simulation-content-wrapper").html(data);
                initSimulation();
            }, 1000);
        }
    }).error(function () {
        $("#dvDest").css('border', 'solid 1px red');
        $("#warning-alert").addClass('show');
        $("#warning-alert").removeClass('hide');
    });
}

function resetSimulation() {
    $("#reset-simulation").click(function () {
        $("#dvDest").find('ul li').remove();
    });
}

function submitExperiment() {
    $("#submit-experiment").click(function () {
        submitExperimentAjax();
    });
}

function processRoundData(data) {
    setTimeout(function () {
        $("#neighborsStories").html(data);
        initExperiment();
    }, 1000);
}

function submitExperimentAjax() {
    $(".ui-draggable-dragging").remove();
    console.log("ROUND: "+$("#roundNumber").val());
    clearInterval(roundInterval);
    var elems = $("#dvDest").find('ul li');
    var text_all = elems.map(function () {
        return $(this).attr('drag-id');
    }).get().join(";");

    var session =  $("#session").val();
    $("#neighborsStories").block({message:"<em>Refreshing neighbors...</em>"});
    $.ajax({
        url: "/loom/session/submitExperiment",
        type: 'POST',
        data: {
            tails: text_all,
            session: session,
            roundNumber: $("#roundNumber").val()
        }
    }).success(function (data) {
        localStorage.setItem('remainingTime', 'null');

        //This never happens
        if (data.indexOf("finishExperiment") >= 0) {
            shouldLogout = false;
            console.log("/loom/experiment/finishExperiment/" + session);
            window.location = "/loom/session/finishExperiment/" + session;

        } else {
            //processRoundData(data);
            startPingingForNextRound();

        }
    }).error(function () {
        $.unblockUI();
        $("#dvDest").css('border', 'solid 1px red');
        $("#warning-alert").addClass('show');
        $("#warning-alert").removeClass('hide');
    });
}

function resetExperiment() {
    //$("#reset-experiment").click(function () {
    //    $("#dvDest").find('ul li').remove();
    //});
}

