$(document).ready(function () {

    $("#create-experiment-button").click(function () {
        $("#experiment-modal").modal('show');
    });

    $("#create-exp").on('click', function () {

        //Basic parameters
        var name = $("#name").val();

        var min_nodes = parseInt($("#min_nodes").val());
        var max_nodes = parseInt($("#max_nodes").val());
        var initialNbrOfTiles = parseInt($('#initialNbrOfTiles').val());
        var rounds = parseInt($('#rounds').val());
        var duration = parseInt($('#duration').val());
        var uiflag = $('input:radio[name="UIflag"]:checked').val();




        if (name === '' || uiflag == null ||
            (constraints && constraints.includes("null")) ||
            (constraintoperators && constraintoperators.includes("null")) ||
            (constraintparams && constraintparams === '')) {
            alert("please complete the required fields");
            $("#experiment-modal").modal('show');
        } else if (max_nodes < min_nodes) {
            alert("max allowed nodes must not be smaller than min allowed nodes");
            $("#experiment-modal").modal('show');
        } else {
            $.ajax({
                url: "/loom/admin/createExperiment",
                type: 'POST',
                data:
                    {
                        name: name,
                        min_nodes: min_nodes,
                        max_nodes: max_nodes,
                        initialNbrOfTiles: initialNbrOfTiles,
                        rounds: rounds,
                        duration: duration,
                        constraints: constraints,
                        constraintoperators: constraintoperators,
                        constraintparams: constraintparams
                    },
                dataType: "json",
                success: function (data) {
                    if (data.message === "duplicate") {
                        alert("name already exists!");
                    }
                    if (data.message === "exp_error") {

                        alert("fail to create the experiment!");
                    }
                    // if(data.message==="network_error"){
                    //
                    //     alert("wrong network parameters!");
                    // }
                    if (data.message === "success") {
                        window.location = "/loom/admin/board#experiments";

                    }


                }
            });
        }

    });

    $(".create-constraint-table").on('click', '.remove-constraint', function () {
        $(this).parents("tr").remove();
    });

    $("#experiment-modal .add-constraint").on('click', function () {
        alert("Received click in add constraint")
        var tr = $("#constraint-source-inputs tr").clone()
        //var tr = "<tr><td></td><td></td><td></td><td></td></tr>"
        //console.log("Created "+tr)
        $(this).prev("table").append(tr);
    });

});