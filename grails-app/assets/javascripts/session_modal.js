$(document).ready(function () {

    $("#create-session-button").click(function () {

        var experimentId = $(this).find('span.expid').text();
        $.ajax({
            url: "/loom/admin/getExperimentData",
            type: 'GET',
            data:
                {
                    experimentId: experimentId,

                },

            success: function (data) {
                console.log(data.data.constraints)
                $("#session-model .experiment-name").text(data.data.name)
                $("#session-model .experiment-id").text(data.data.id)
                for (let i = 0; i<data.data.constraints.length;i++) {
                    var constraint = data.data.constraints[i]
                    console.log(constraint['title'])
                    var name = constraint["title"]
                    var op = constraint["operator"]
                    var params = constraint["params"]
                    addInhereitedConstraint(name, op, params)
                }

            }
        });

        $("#session-experimentID").text(experimentId);
        $("#session-modal").modal('show');
    });

    $("#create-exp").on('click', function () {

        //Basic parameters
        var name = $("#session-name").val();
        var storySet = $("#StorySelect").val();
        var experimentId = $("#experiment-id").text();

        // var min_nodes = parseInt($("#min_nodes").val());
        // var max_nodes = parseInt($("#max_nodes").val());
        // var initialNbrOfTiles = parseInt($('#initialNbrOfTiles').val());
        // var rounds = parseInt($('#rounds').val());
        // var duration = parseInt($('#duration').val());
        // var uiflag = $('input:radio[name="UIflag"]:checked').val();


        //Network parameters
        var network_type = $('input:radio[name="network_type"]:checked').val();
        var min_degree;
        var max_degree;
        var m;
        var prob;

        if (network_type === 'Lattice') {
            min_degree = parseInt($("#Lattice_min_degree").val());
            max_degree = 0;
            m = 0;
            prob = 0;
        } else if (network_type === 'Newman_Watts') {
            min_degree = parseInt($("#Newman_min_degree").val());
            max_degree = parseInt($("#Newman_max_degree").val());
            m = 0;
            prob = $("#Newman_prob").val();

        } else if (network_type === 'Barabassi_Albert') {
            min_degree = parseInt($("#BA_min_degree").val());
            max_degree = parseInt($("#BA_max_degree").val());
            m = parseInt($("#BA_M").val());
            // prob = $("#BA_prob").val();
            prob = 0;
        }

        var constraints = $("#experiment-constraint-table select#constraint").map(function () {
            return $(this).val();
        }).toArray();
        var constraintoperators = $("#experiment-constraint-table select#operator").map(function () {
            return $(this).val();
        }).toArray();
        var constraintparams = $("#experiment-constraint-table input[name='parameters']").map(function () {
            return $(this).val();
        }).toArray();


        if (name === '' || storySet === 'null' || network_type == null || (prob === '' && network_type === 'Newman_Watts') ||
            (constraints && constraints.includes("null")) ||
            (constraintoperators && constraintoperators.includes("null")) ||
            (constraintparams && constraintparams === '')) {
            alert("please complete the required fields");
            $("#experiment-modal").modal('show');
        } else if (network_type === 'Barabassi_Albert' && max_degree < min_degree) {
            alert("total number of nodes must be at least equal to the initial set");
            $("#experiment-modal").modal('show');
        } else {
            $.ajax({
                url: "/loom/admin/uploadExperiment",
                type: 'POST',
                data:
                    {
                        name: name,
                        storySet: storySet,
                        network_type: network_type,
                        min_degree: min_degree,
                        max_degree: max_degree,
                        m: m,
                        prob: prob,
                        uiflag: uiflag,

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

                        alert("fail to create the session!");
                    }

                    if (data.message === "success") {
                        window.location = "/loom/admin/board#sessions";
                    }


                }
            });
        }

    });

    $(".create-constraint-table").on('click', '.remove-constraint', function () {
        $(this).parents("tr").remove();
    });


    $("#session-modal .add-constraint").on('click', function () {
        var tr = $("#constraint-source-inputs tr").clone()
        $("#session-constraint-table").append(tr)

    });

    function addInhereitedConstraint(constraint_name, operator, params) {
        $("#session-model .inherited-constraints").add(
            "<li>"+constraint_name+" : "+operator+" : "+params+"</li"
        )
        //alert("Attempt to set "+constraint_name+","+operator+","+params)

    }

});