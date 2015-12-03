$(document).ready(function () {
    $('#complete-btn').pro
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
            console.log(session.session.name);
            console.log(session.session.id);
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
    initDragNDrop();
    removeTile();
    resetExperiment();
    submitExperiment();
}

function initTraining() {
    initDragNDrop();
    removeTile();
    resetTraining();
    submitTraining();
}

function initSimulation() {
    initDragNDrop();
    resetSimulation();
    submitSimulation();
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
            removeTile();
        }
    }).sortable({
        items: "li:not(.placeholder)",
        sort: function () {
            $(this).removeClass("ui-state-default");
        },
        cursorAt: {left: 10, top: -1}
    });
}

function removeTile() {
    $("#dvDest").find("li a").click(function (e) {
        $(this).parent().remove();
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
                console.log("/loom/experiment/simulation/" + session);
                window.location = "/loom/experiment/simulation/" + session;
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

function submitSimulation() {
    $("#submit-simulation").click(function () {
        var elems = $("#dvDest").find('ul li');
        var text_all = elems.map(function () {
            return $(this).attr('id');
        }).get().join(";");

        console.log(text_all);
        $.ajax({
            url: "/loom/experiment/submitSimulation",
            type: 'POST',
            data: {
                tails: text_all,
                simulation: $("#simulation").val(),
                roundNumber: $("#roundNumber").text()
            }
        }).success(function (data) {
            if (data.indexOf("experiment") >= 0) {
                var session = JSON.parse(data).sesId;
                console.log("/loom/experiment/experiment/" + session);
                window.location = "/loom/experiment/experiment/" + session;
            } else {
                $("#simulation-content-wrapper").html(data);
                initSimulation();
            }
        }).error(function () {
            $("#dvDest").css('border', 'solid 1px red');
            $("#warning-alert").addClass('show');
            $("#warning-alert").removeClass('hide');
        });
    });
}

function resetSimulation() {
    $("#reset-simulation").click(function () {
        $("#dvDest").find('ul li').remove();
    });
}

function submitExperiment() {
    $("#submit-experiment").click(function () {
        var elems = $("#dvDest").find('ul li');
        var text_all = elems.map(function () {
            return $(this).attr('id');
        }).get().join(";");

        console.log(text_all);
        $.ajax({
            url: "/loom/experiment/submitExperiment",
            type: 'POST',
            data: {
                tails: text_all,
                experiment: $("#experiment").val(),
                roundNumber: $("#roundNumber").text()
            }
        }).success(function (data) {
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
    });
}

function resetExperiment() {
    $("#reset-experiment").click(function () {
        $("#dvDest").find('ul li').remove();
    });
}