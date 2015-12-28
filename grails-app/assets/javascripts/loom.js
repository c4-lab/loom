$(document).ready(function () {
    $("#create-experiment").click(function () {
        $("#file-upload-modal").modal('show');
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

function initExperiment() {
    if ($("#experiment-content-wrapper").length > 0) {
        initDragNDrop();
        initTiles();
        removeTile();
        resetExperiment();
        submitExperiment();
        localStorage.setItem('remainingTime', 'null');
        clearInterval(int);
        initExperimentTimer();
    }
}

function initTraining() {
    initDragNDrop();
    removeTile();
    resetTraining();
    submitTraining();
}

function initSimulation() {
    if ($("#simulationMainContainer").length > 0) {
        initDragNDrop();
        initTiles();
        resetSimulation();
        removeTile();
        submitSimulation();
        localStorage.setItem('remainingTime', 'null');
        clearInterval(int);
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

function initTiles() {
    $("#dvSourceContainer").find(".ui-state-default").each(function () {
        var sourceTileId = $(this).attr('id');
        $("#dvDest").find(".purple").each(function () {
            if ($(this).attr('id') == sourceTileId) {
                $("#dvSourceContainer #" + sourceTileId).addClass('blue');
                $("#dvSourceContainer #" + sourceTileId).addClass('ui-draggable-disabled');
                $("#dvSourceContainer #" + sourceTileId).draggable("disable");
            }
        });
    });
}

function markAsDropped(source) {
    $("#" + source).addClass('blue');
}

function initDragNDrop() {
    $(".dvSource").find("li").draggable({
        appendTo: "body",
        helper: "clone"
    });
    $("#dvDest").find("ul").droppable({
        activeClass: "ui-state-default",
        hoverClass: "ui-state-hover",
        accept: ":not(.ui-sortable-helper)",
        drop: function (event, ui) {
            $(this).find(".placeholder").remove();
            $("<li class='ui-state-default ui-draggable ui-draggable-handle purple' id='" + ui.draggable.attr("id") + "'></li>")
                .html("<span>" + ui.draggable.text() + "</span>&nbsp;&nbsp;&nbsp;<a href='javascript:void(0);'>x</a>").appendTo(this);
            markAsDropped(ui.draggable.attr("id"));
            $(".dvSource #" + ui.draggable.attr("id")).draggable("disable");
            removeTile();
        }
    }).sortable({
        items: "li:not(.placeholder)",
        placeholder: "ui-state-highlight",
        sort: function () {
            $(this).removeClass("ui-state-default");
        }
    }).disableSelection();
    if (/chrom(e|ium)/.test(navigator.userAgent.toLowerCase())) {
        $("#dvDest").find("ul").sortable({
            items: "li:not(.placeholder)",
            placeholder: "ui-state-highlight",
            sort: function () {
                $(this).removeClass("ui-state-default");
            },
            cursorAt: {top: -35, left: 5}
        }).disableSelection();
    }
}

function removeTile() {
    $("#dvDest").find("li a").click(function (e) {
        $(this).parent().remove();
        console.log($(this).parent().attr('id'));
        $(".dvSource #" + $(this).parent().attr('id')).draggable("enable");
        $(".dvSource #" + $(this).parent().attr('id')).css("backgroundColor", "#e6e6e6");
        $(".dvSource #" + $(this).parent().attr('id')).removeClass('blue');
    });
}

function resetTraining() {
    $("#reset-training").click(function () {
        $("#dvDest").find('ul li').remove();
    });
}

function submitTraining() {
    $("#submit-training").click(function () {
        var elems = $("#dvDest").find('ul li span');
        var text_all = elems.map(function () {
            return $(this).text();
        }).get().join(";");

        console.log(text_all);
        $.ajax({
            url: "/loom/experiment/submitTraining",
            type: 'POST',
            data: {
                tails: text_all,
                training: $("#training").val(),
                trainingName: $("#training-name").text()
            }
        }).success(function (data) {
            if (data.indexOf("simulation") >= 0) {
                var session = JSON.parse(data).sesId;
                var roundNumber = 0;
                console.log("/loom/simulation/" + session + "/" + roundNumber);
                window.location = "/loom/simulation/" + session + "/" + roundNumber;
            } else {
                $("#training-content-wrapper").html(data);
                initTraining();
            }
        }).error(function () {
            $("#dvDest").css('border', 'solid 1px red');
            $("#warning-alert").addClass('show');
            $("#warning-alert").removeClass('hide');
        });
    });
}

var after;
var before;
function calculateTime() {
    var seconds = Math.round((after - before) / 1000);

    console.log("seconds: " + seconds);
    //$("#simulationMainContainer").length > 0
    if (localStorage.remainingTime != 'null') {
        console.log("localStorage.remainingTime: "+localStorage.remainingTime);
        localStorage.remainingTime = localStorage.remainingTime - seconds;
        var display = $('#timerPanel');
        startSimulationTimer(localStorage.remainingTime, display);
    }
}

var int;
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

    int = setInterval(function () {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;
        console.log('timer inside: '+timer);
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

    int = setInterval(function () {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;
        console.log('timer inside: '+timer);
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

function submitSimulationAjax() {
    if ($(".ui-draggable-dragging").length > 0) {
        $('html').on('mouseup', function () {
            $(".ui-draggable-dragging").remove();
            $(".ui-draggable-dragging").draggable("destroy");
        });
    }
    clearInterval(int);
    var elems = $("#dvDest").find('ul li');
    var text_all = elems.map(function () {
        return $(this).attr('id');
    }).get().join(";");
    $.ajax({
        url: "/loom/experiment/submitSimulation",
        type: 'POST',
        data: {
            tails: text_all,
            simulation: $("#simulation").val(),
            roundNumber: $("#roundNumber").text()
        }
    }).success(function (data) {
        localStorage.setItem('remainingTime', 'null');
        $("#dvDest").find("ul").droppable("option", "disabled", false);
        if (data.indexOf("experiment") >= 0) {
            var session = JSON.parse(data).sesId;
            var simulationScore = JSON.parse(data).simulationScore;
            var roundNumber = 0;
            console.log("/loom/exper/" + session + "/" + roundNumber);
            //window.location = "/loom/exper/" + session + "/" + roundNumber;
            $("#simulationMainContainer").remove();
            $("#simulationScore").css('display', 'block');
            $("#scorePanel").text(simulationScore);
            $("#roundNumber").val(roundNumber);
            $("#session").val(session);
        } else {
            $("#simulation-content-wrapper").html(data);
            initSimulation();
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

function submitExperimentAjax() {
    $(".ui-draggable-dragging").remove();
    clearInterval(int);
    var elems = $("#dvDest").find('ul li');
    var text_all = elems.map(function () {
        return $(this).attr('id');
    }).get().join(";");

    $.ajax({
        url: "/loom/experiment/submitExperiment",
        type: 'POST',
        data: {
            tails: text_all,
            experiment: $("#experiment").val(),
            roundNumber: $("#roundNumber").text()
        }
    }).success(function (data) {
        localStorage.setItem('remainingTime', 'null');
        if (data.indexOf("finishExperiment") >= 0) {
            var session = JSON.parse(data).sesId;
            console.log("/loom/experiment/finishExperiment/" + session);
            window.location = "/loom/finish/" + session;
        } else {
            $("#experiment-content-wrapper").html(data);
            initExperiment();
        }
    }).error(function () {
        $("#dvDest").css('border', 'solid 1px red');
        $("#warning-alert").addClass('show');
        $("#warning-alert").removeClass('hide');
    });
}

function resetExperiment() {
    $("#reset-experiment").click(function () {
        $("#dvDest").find('ul li').remove();
    });
}