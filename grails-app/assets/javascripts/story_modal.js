$(document).ready(function () {
    $("#create-stories").click(function () {
        $("#create-story-set").modal('show');
    });

    $("#create-story").on('click', function () {
        var title = $("#story-title").val();
        var tiles = $("#story-text").val();
        // alert(tails);

        if (title === '' || tiles === '') {
            alert("please complete the required fields");
            // $("#experiment-modal").modal('show');
        } else {
            $.ajax({
                url: "/loom/admin/uploadStorySet",
                type: 'POST',
                data:
                    {
                        title: title,
                        tiles: tiles,

                    },
                dataType: "json",
                success: function (data) {
                    if (data.message === "duplicate") {
                        alert("title already exists!");
                    }
                    if (data.message === "error") {

                        alert("fail to create!");
                    }
                    if (data.message === "success") {
                        alert("Successful story creation!")
                        window.location.replace("/loom/admin/board#stories");
                        window.location.reload()
                    }


                }
            });
        }


    });

});