<div class="modal modal-info" style="padding-top: 140px" id="experiment-modal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Create an Experiment</h4>
            </div>
            <g:form enctype="multipart/form-data" name="experiment-upload-form" controller="admin" action="createExperiment">
                <div class="modal-body">

                    <div class="form-group">
                        <div class="panel panel-default">
                            <div class="panel-heading">Experiment Info</div>

                            <div class="panel-body">

                                <label for="experiment-name">Experiment name:</label>
                                <input type="text" name="name" id="experiment-name" style="color: black">

                                <p></p>
                                <input name="experimentdata" type="hidden" value=""/>
                                <input class="pull-left" type="file" id="experiment-input-file" name="inputFile" required>
                                <span class="pull-right" id="validation-message">Invalid file</span>

                            </div>
                        </div>
                    </div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary" id='create-exp'>Create</button>
                </div>
            </g:form>



        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script type="text/javascript">
    $(document).ready(function() {



        $("#experiment-name").keypress(function() {
            validate()
        })

        $("#create-experiment-button").click(function () {
            $("#experiment-upload-form")[0].reset()
            $("#experiment-modal").modal('show');


        });

        function validate() {
           if ($("#experiment-name").val()=='' ||
               !($("#validation-message").hasClass("valid"))) {
               $("#create-exp").prop('disabled',true)
           } else {
               $("#create-exp").prop('disabled',false)
           }
        }

        $("#experiment-input-file").change(function() {
            fd = new FormData()
            fd.append("inputFile",$("#experiment-input-file")[0].files[0])
            $.ajax({
                type: "POST",
                url: "/loom/admin/validateParametersFile",
                data:  fd,

                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    if (data.status === "error") {
                        alert("Invalid file:"+data.messages)
                        $("#validation-message").text("Validated").removeClass("valid")
                    } else {
                        $("#experiment-upload-form input[name=experimentdata]").val(data)
                        $("#validation-message").text("Validated").addClass("valid")
                    }
                    validate()
                }
            });
        });

        validate()
    })
</script>
