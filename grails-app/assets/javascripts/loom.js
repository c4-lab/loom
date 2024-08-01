$(document).ready(function () {

    /**
     * ADMINISTRATATIVE STUFF
     */

    $("#network_type").click(function () {
        var network_type = $("#select option:selected").val();
        alert(network_type);
    });


    $("#clone-session").click(function () {
        alert(parseInt($("#sessionId").val(), 16))
        $.ajax({
            url: "/loom/admin/cloneSession", type: 'POST', data: {
                session: parseInt($("#sessionId").val(), 16)
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


    // $(".launch_experiment").click(function () {
    //
    //     // alert($("#launch_training").find('span').text());
    //     $("#session-modal").modal('show');
    //     $("#experimentID").text(experimentId);
    // });

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
            url: "/loom/admin/publishAnonym", type: 'POST', data: {
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


    $("#launch_experiment_hits").on('click', function () {
        var experimentID = $("#experimentID").text();
        var num_hits = $("#num_exp_hits").val();
        var hit_lifetime = $("#exp_available_time").val();
        var assignment_lifetime = $("#exp_assignment_lifetime").val();


        $.ajax({
            url: "/loom/admin/launchExperiment", type: 'POST', data: {
                experimentID: experimentID,
                num_hits: num_hits,
                hit_lifetime: hit_lifetime,
                assignment_lifetime: assignment_lifetime,


            }, // dataType:"json",
            success: function (data) {
                $("#launch-training-modal").modal('hide');
                // $("#launch-training").hide();
                // if(data.message==="duplicate"){
                //     alert("title already exists!");
                // }
                // if(data.message==="error"){
                //
                //     alert("fail to create!");
                // }
                // if( data.message==="success"){
                //     window.location = "/loom/admin/board/";
                //
                // }


            }
        });
    });

    $("#trainings").on('click', '.pay-training', function () {
        var trainingsetId = $(".launch_training", this.parentNode).find('span').text();
        var payment_status = $(".training-payment-status", this.parentNode.parentNode);
        var pay_btn = $(".pay-training", this.parentNode);
        var pay_i = $(".pay-training-i", this.parentNode);
        pay_btn.addClass("buttonload");
        pay_btn.attr("disabled", true)
        pay_i.addClass("fa fa-spinner fa-spin");

        $.ajax({
            url: "/loom/admin/payTrainingHIT", type: 'POST', data: {
                trainingsetId: trainingsetId,

            }, dataType: "json", success: function (data) {
                payment_status.text("Payment status: " + data.payment_status);
                pay_btn.removeClass("buttonload");
                pay_btn.attr("disabled", false)
                pay_i.removeClass("fa fa-spinner fa-spin");
                if (data.status === "no_payable") {
                    alert("No payable assignment!");
                }
                // if(data.status==="success"){
                //
                //
                //     window.location = "/loom/admin/board/";
                // }


            }
        });
    });

    $("#trainings").on('click', '.check-training_payble', function () {
        var trainingsetId = $(".launch_training", this.parentNode).find('span').text();
        var payment_status = $(".training-payment-status", this.parentNode.parentNode);

        var check_btn = $(".check-training_payble", this.parentNode);
        var check_i = $(".check-training-payable-i", this.parentNode);
        check_btn.addClass("buttonload");
        check_btn.attr("disabled", true)
        check_i.addClass("fa fa-spinner fa-spin");

        $.ajax({
            url: "/loom/admin/checkTrainingsetPayble", type: 'POST', data: {
                trainingsetId: trainingsetId,

            }, dataType: "json", success: function (data) {
                payment_status.text("Payment status: " + data.payment_status);
                check_btn.removeClass("buttonload");
                check_btn.attr("disabled", false)
                check_i.removeClass("fa fa-spinner fa-spin");
                if (data.check_greyed) {
                    alert("No payable assignment!");
                    // check_btn.attr("disabled",true);
                }


            }
        });
    });


    $("#create-username").on('click', function () {
        var tr = "<tr style=\"background-color: #23272b\"><td><input type=\"text\" name=\"username\" id=\"username\" value='default-user'></td>" + "<td><button  type=\"button\" class=\"btn btn-primary remove-username\">Remove</button></tr>"
        $("#create-user-table").append(tr);
    });


    $("#create-user-table").on('click', '.remove-username', function () {
        $(this).parents("tr").remove();
    });


    $("#submit-users").on('click', function () {
        var usernames = [];

        $("table#create-user-table tr").each(function (i, v) {

            usernames.push($(this).children('td').eq(0).find('input').val());


            // usernames[i] = ;
            // {
            //     userSettings[i][ii] = $(this).text();

            // });
        })

        // var username = $("#username").val();
        if (usernames.length === 0) {
            alert("please provide a username")
        } else {
            $.ajax({
                url: "/loom/admin/createUser", type: 'POST', // contentType: "application/json;",
                // data: JSON.stringify({ 'list': usernames }),
                data: {
                    usernames: usernames,

                }, dataType: "json", success: function (data) {
                    // alert(data.username);
                    // var list = eval('([' + data.username + '])');
                    // alert(list[0]);
                    // for(var i=0;i<list.length;i++){
                    //     var name=list[i];
                    //     alert(name);
                    // }
                    var usernames = data.username
                    // alert(usernames[1]);
                    if (data.status === "duplicate") {
                        // alert(usernames);
                        $("table#create-user-table tr").each(function (i, v) {
                            // userSettings[i] = [];
                            if (i >= 1) {

                                if (usernames[i - 1] === 1) {
                                    alert("found duplicated or existent ones highlighted in red!");
                                    $(this).children('td').eq(0).find('input').css("color", "red");
                                }
                            }

                            // usernames[i-1] = $(this).children('td').eq(0).find('input').val();
                            // {
                            //     userSettings[i][ii] = $(this).text();

                            // });
                        })
                    } else {

                        $("#create-users-modal").modal('hide');
                        alert("successfully create " + usernames);
                    }

                }
            });
        }

    });

});


//TODO - need to check this logic; see the "logout" call in thw waiting room
var shouldLogout = true;
var serverDelta = undefined
var roundStart = undefined
var roundDuration = undefined

function logout() {
    if (shouldLogout) {
        $.ajax({
            url: "/loom/logout/index", type: 'GET', async: false

        }).success(function (data, textStatus, jqXHR) {
            // This will handle the response data after any redirects are resolved
        }).error(function (jqXHR, textStatus, errorThrown) {
            // Error handling
        });
    }
}

function initExperiment() {
    if ($("#experiment-content-wrapper").length > 0) {
        initDragNDrop();
        initTiles();
        initRound();
        initExperimentTimer($('#timerPanel'));
        blockIfPaused();

    }
}

function initTraining() {
    if ($("#training-content-wrapper").length > 0) {
        initTiles();
        initDragNDrop();
        initMyDragNDrop();
        resetTraining();
        //submitTraining();
        updateTrainingScore();

    }
}

function initSimulation() {
    if ($("#simulationMainContainer").length > 0) {
        initDragNDrop();
        initMyDragNDrop();
        initTiles();
        resetSimulation();
        submitSimulation();
        localStorage.setItem('remainingTime', 'null');
        clearInterval(roundInterval);
        initSimulationTimer();
    }
}

function initSimulationTimer() {
    var duration = $("#simulationDuration").val(), display = $('#timerPanel');

    console.log("init time " + duration);

    startSimulationTimer(duration, display);
}


function initRound() {
    var round = Number($("#roundNumber").val()) + 1;
    console.log("Initializing round: " + round)
    $("#roundNumberTarget").text("" + round)
}

function blockIfPaused() {
    if ($("#paused").val() == "true") {
        $("#neighborsStories").block("<h1>Waiting for neighbors...</h1>")
    }
}


function initTiles() {
    //This function just takes care of marking tiles as being avialable or not
    //and adds remove buttons as necessary

    $("#sort2").find(".purple").each(function () {
        var destTileId = $(this).attr('drag-id');
        var matched = $(".dvSourceContainer").find("[drag-id='" + destTileId + "']");
        if (matched.length > 0) {
            matched.each(function () {
                $(this).removeClass('tile-available').addClass('blue');
            });
            addRemoveBtn($(this));
        } else {
            removeRemoveBtn($(this));
        }
    });
    $("#sort3").find(".purple").each(function () {
        var destTileId = $(this).attr('drag-id');
        var matched = $(".dvSourceContainer").find("[drag-id='" + destTileId + "']");
        if (matched.length > 0) {
            matched.each(function () {
                // $(this).remove();
                $(this).removeClass('tile-available').addClass('blue');
            });
            addRemoveBtn($(this));
        } else {
            removeRemoveBtn($(this));
        }
    });

    $("#sort4").find(".purple").each(function () {
        var destTileId = $(this).attr('drag-id');
        var matched = $(".dvSourceContainer").find("[drag-id='" + destTileId + "']");
        if (matched.length > 0) {
            matched.each(function () {
                // $(this).remove();
                $(this).removeClass('tile-available').addClass('blue');
            });
            addRemoveBtn($(this));
        } else {
            removeRemoveBtn($(this));
        }
    });
}

function markAsDropped(source) {
    $(".originalstory").find("[drag-id='" + source + "']").removeClass('tile-available').addClass('blue');
    $(".privateinfo").find("[drag-id='" + source + "']").removeClass('tile-available').addClass('blue');

    $("#sort2").find("[drag-id='" + source + "']").removeClass('tile-available').addClass('purple');
    $("#sort2").find("[drag-id='" + source + "']").removeAttr("style");

}

function addRemoveBtn(elt) {
    if (!($(elt).find("a").length)) {
        elt.append("<span class='removeTile' >&nbsp;&nbsp;&nbsp;<a href='javascript:void(0);' style=\"color:#ff0000\"><b>X</b></a></span>");
        removeTileEvent(elt);
    }
}

function removeRemoveBtn(elt) {
    $(elt).find("span.removeTile").remove();
}

function removeTile(elt) {
    var toremove = $(elt).closest("li");
    toremove.remove();

    var elem = $(".g_list").find("[drag-id='" + toremove.attr('drag-id') + "']");
    elem.removeClass('blue');
    elem.removeClass('static');
    elem.addClass('tile-available');

}


function removeTileEvent(elt) {
    $(elt).find("a").click(function (e) {
        var toremove = $(this).closest("li");
        removeTile(toremove);
        updateTrainingScore();
    });
}

function initDragNDrop() {
    $(".originalstory").find("li").draggable({
        helper: "clone",
        opacity: 0.5,
        cursor: "crosshair",
        connectToSortable: "#sort2",
        revert: "invalid",
        cancel: ".blue",
        placeholder: "ui-state-highlight",
        start: function (event, ui) {
            var width = $(event.target).width()
            var height = $(event.target).height()
            $('.ui-draggable-dragging').width(width)
            $('.ui-draggable-dragging').height(height)

        },
        stop: function (event, ui) {
            if ($("#sort2").find("[drag-id='" + $(event.target).attr("drag-id") + "']").length > 0) {
                var source = $(event.target).attr("drag-id");
                markAsDropped(source);
                addRemoveBtn($("#sort2").find("[drag-id='" + source + "']"));
            }

            // console.log($("#trainingForm .ui-draggable").map(function() {return $(this).attr("drag-id")}).get().join(";"))


        }

    });

    $(".privateinfo").find("li").draggable({
        helper: "clone",
        opacity: 0.5,
        cursor: "crosshair",
        connectToSortable: "#sort2",
        revert: "invalid",
        cancel: ".blue",
        placeholder: "ui-state-highlight",
        start: function (event, ui) {
            console.log("Start dragging")

            var width = $(event.target).width()
            var height = $(event.target).height()
            $('.ui-draggable-dragging').width(width)
            $('.ui-draggable-dragging').height(height)
        },
        stop: function (event, ui) {
            console.log($(event.target).attr("drag-id"));
            if ($("#sort2").find("[drag-id='" + $(event.target).attr("drag-id") + "']").length > 0) {
                var source = $(event.target).attr("drag-id");
                markAsDropped(source);
                addRemoveBtn($("#sort2").find("[drag-id='" + source + "']"));
            }

        }

    });


}

function initMyDragNDrop() {
    $("#sort2").sortable({
        opacity: 0.5, cursor: "crosshair", placeholder: "ui-state-highlight", forcePlaceholderSize: true,

        start: function (event, ui) {
            console.log("Detecting a drag event")
            ui.placeholder.height(ui.item.height());
            ui.placeholder.width(ui.item.width());
            // $(event.target).find('li').css("white-space", "nowrap");
        }, stop: function (event, ui) {
            updateTrainingScore();
        }
    })

}

function updateTrainingScore() {
    if ($("#training-name").length > 0) {
        var elems = $("#sort2").find('li');
        var tile_ids = $("#sort2 li").map(function () {
            return $(this).attr("drag-id")
        }).get().join(",")
        console.log("Got " + tile_ids)
        $("input[name='storyTiles']").val(tile_ids);


        $.ajax({
            url: "/loom/training/getTrainingScore", type: 'POST', data: {
                userTiles: tile_ids, trainingId: $("#training").val()
            }, timeout: 999
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
    $(".reset-training").click(function () {
        // if(uiflag===1){
        $("#sort2").find("li").each(function () {
            removeTile($(this));
            //$(this).parent().remove();
            //console.log($(this).parent().attr('id'));
            //var elem = $(".dvSource").find("[drag-id='" + $(this).parent().attr('drag-id') + "']");
            //
            //elem.removeClass('blue').addClass('tile-available');
        });
        $("#sort3").find("li").each(function () {
            removeTile($(this));
            //$(this).parent().remove();
            //console.log($(this).parent().attr('id'));
            //var elem = $(".dvSource").find("[drag-id='" + $(this).parent().attr('drag-id') + "']");
            //
            //elem.removeClass('blue').addClass('tile-available');
        });
        // }else if(uiflag===0){
        //     var initial = [];
        //     $('#sort1').each(function(index, anchor) {
        //         initial.push(anchor.innerHTML);
        //     });
        //     $('#sort1').each(function(index, anchor) {
        //         anchor.innerHTML = initial[index];
        //     });
        //     var toremove = $("#sort3").find("li");
        //     toremove.remove();
        //
        // }


        updateTrainingScore();
        //$("#training-score").text("0.0")
    });

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
        //console.log('timer inside: ' + timer);
        display.text(minutes + ":" + seconds);

        if (--timer < 0) {
            timer = $("#simulationDuration").val();
            console.log("Submitting the form");
            submitSimulationAjax();
        }

        localStorage.setItem('remainingTime', timer);

    }, 1000);
}


let isSubmitting = false; // Global flag to prevent multiple submissions



function startWaitingTimer(fx) {
    var worker = new Worker('/loom/assets/second-timer.js');
    worker.onmessage = function () {
        fx(worker);
    }
    worker.postMessage('Start');
    return worker;
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
    var percent = count * 100 / max;
    $(".progress-bar").attr("aria-valuenow", percent).css("width", percent + "%");
    $(".sr-only").text(percent + "% Complete");
    $(".prog-bar-count").text(count);

}



function resetSimulation() {
    $("#reset-simulation").click(function () {
        $(".dvDest").find('ul li').remove();
    });
}


/**
 * After we've received the data, we process it here
 * updating the user's experiment view, and then start the
 * experiment timer again
 * @param data
 */
function processRoundData(data) {
    setTimeout(function () {
        $("#neighborsStories").html(data);
        initExperiment();
    }, 1000);
}

function initExperimentTimer(display) {
    let serverDelta, roundDuration, roundStart;

    function readTimingData() {
        serverDelta = new Date().getTime() - parseInt($("#serverTime").val(), 10);
        roundDuration = parseInt($("#currentRoundDuration").val(), 10);
        roundStart = parseInt($("#roundStart").val(), 10);
    }

    readTimingData();

    const worker = new Worker('/loom/assets/second-timer.js');

    worker.onmessage = function (event) {
        console.log("Receive message from worker");
        const adjustedLocalTime = new Date().getTime() + serverDelta;
        if (adjustedLocalTime >= roundDuration * 1000 + roundStart) {
            console.log("Time's up, preparing to submit");
            worker.terminate();
            if (!isSubmitting) {
                submitExperimentAjax();
            } else {
                console.log("ExperimentTimer: Already submitting...")
            }
        } else {
            const remainingSeconds = roundDuration - Math.floor((adjustedLocalTime - roundStart) / 1000);
            const minutes = String(Math.floor(remainingSeconds / 60)).padStart(2, '0');
            const seconds = String(remainingSeconds % 60).padStart(2, '0');
            display.text(`${minutes}:${seconds}`);
        }
    };

    console.log("Initiating Timer");
    worker.postMessage('Start');
}


function startPingingForNextRound() {
    console.log("Start pinging for next round...")
    const session = $("#session").val();
    const worker = new Worker('/loom/assets/second-timer.js');
    let isPinging = false;

    worker.onmessage = function () {
        if (isPinging) return; // Prevent multiple simultaneous pings
        isPinging = true;
        const currentRound = $("#roundNumber").val();
        $.ajax({
            url: "/loom/session/checkExperimentRoundState/" + session+"?currentRound="+currentRound,
            type: 'GET',
            timeout: 1000
        }).done(function (data) {
            switch(data) {
                case "finished":
                    shouldLogout = false;
                    worker.terminate();
                    window.location = "/loom/session/finishExperiment/" + session;
                    break;
                case "cancelled":
                    window.location.href = `/loom/session/cancelNotification?loomsession=${session}`;
                    break;
                case "paused":
                    console.log("Still paused");
                    break;
                default:
                    console.log("Processing round data");
                    worker.terminate();
                    storeActiveTab();
                    processRoundData(data);
            }
        }).fail(function (jqXHR, errorText, errorThrown) {
            console.error("Error pinging for next round:", errorText, errorThrown);
        }).always(function() {
            isPinging = false;
        });
    };

    worker.postMessage('Start');
}

function submitExperimentAjax() {
    if (isSubmitting) {
        console.log("In submit ajax, but already sumitting, so just ignoring")
        return;
    } // Prevent multiple submissions
    isSubmitting = true;

    $(".ui-draggable-dragging").remove();
    console.log("Submitting experiment round: " + $("#roundNumber").val());

    const elems = $(".dvDest").find('ul li');
    const text_all = elems.map(function () {
        return $(this).attr('drag-id');
    }).get().join(";");
    const session = $("#session").val();
    const currentRound = $("#roundNumber").val();
    $("#neighborsStories").block({message: "<em>Refreshing neighbors...</em>"});

    if (sessionStorage.getItem("submittedRound") == currentRound) {
        console.log("Already submitted round " + currentRound);
        isSubmitting = false;
        return;
    }

    $.ajax({
        url: "/loom/session/submitExperiment",
        type: 'POST',
        data: {
            tails: text_all,
            session: session,
            roundNumber: currentRound
        }
    }).done(function (data) {
        localStorage.setItem('remainingTime', 'null');
        sessionStorage.setItem('submittedRound', currentRound);

        if (!data.continue) {
            shouldLogout = false;
            let url;
            switch(data.reason) {
                case "finished":
                    url = `/loom/session/finishExperiment?session=${session}`;
                    break;
                case "waiting":
                    url = `/loom/error?message=The experiment has not yet begun`;
                    break;
                case "cancellation":
                    url = `/loom/session/cancelNotification?loomsession=${session}`;
                    break;
                default:
                    console.log("Unknown error:", data);
                    url = `/loom/error?message=Unknown error: ${encodeURIComponent(data.reason)}`;
            }
            window.location.href = url;
        } else {
            startPingingForNextRound();
        }
    }).fail(function (jqXHR, errorText, errorThrown) {
        shouldLogout = false;
        const encodedMessage = encodeURIComponent(errorText);
        const encodedError = encodeURIComponent(errorThrown);
        window.location.href = `/loom/error?message=${encodedMessage}&error=${encodedError}`;
    }).always(function() {
        isSubmitting = false;
    });
}

