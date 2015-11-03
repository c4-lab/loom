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

    $("#dvSource").find("li").draggable({
        appendTo: "body",
        helper: "clone"
    });
    $("#dvDest").find("ul").droppable({
        activeClass: "ui-state-default",
        hoverClass: "ui-state-hover",
        accept: ":not(.ui-sortable-helper)",
        drop: function (event, ui) {
            $(this).find(".placeholder").remove();
            $("<li class='ui-state-default ui-draggable ui-draggable-handle'></li>").text(ui.draggable.text()).appendTo(this);
        }
    }).sortable({
        items: "li:not(.placeholder)",
        sort: function () {
            $(this).removeClass("ui-state-default");
        }
    });

    //var inFormOrLink;
    //$('a').on('click', function() { inFormOrLink = true; });
    //$('form').on('submit', function() { inFormOrLink = true; });
    //$(document).keydown(function(e) {
    //    if (e.keyCode == 65 && e.ctrlKey) {
    //        inFormOrLink = true;
    //    }
    //    if (e.keyCode == 116 && e.ctrlKey) {
    //        inFormOrLink = true;
    //    }
    //});
    //
    //$(window).on('beforeunload', function(){
    //    if (!inFormOrLink) {
    //        $.ajax({
    //            url: "/loom/admin/deleteUser",
    //            type: 'POST',
    //            data: {}
    //        }).success(function (data) {
    //        }).error(function () {
    //        });
    //    }
    //});
});