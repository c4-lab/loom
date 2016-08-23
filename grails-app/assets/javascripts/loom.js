$(document).ready(function () {
    $("#create-experiment").click(function () {
        $("#experiment-file-upload-modal").modal('show');
    });

    $("#create-trainingset").click(function () {
        $("#training-set-file-upload-modal").modal('show');
    });

    $("#stop-waiting").click(function() {
       shouldLogout = false;
        window.location="/loom/session/stopWaiting?session="+$("#sessionId").val();
    });

    $("#clone-session").click(function () {
        $.ajax({
            url: "/loom/admin/cloneSession",
            type: 'POST',
            data: {
                session: $("#sessionId").val()
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

    initTraining();
    initSimulation();
    initExperiment();
});

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