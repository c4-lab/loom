<%@ page import="edu.msu.mi.loom.CrowdServiceCredentials" %>
<div class="modal modal-info" style="padding-top: 140px" id="training-set-file-upload-modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Upload File</h4>
            </div>
        %{--            <form method="post" enctype="multipart/form-data" >--}%
            <g:form enctype="multipart/form-data" name="upload-form" controller="admin" action="uploadTrainingSet">
                <div class="modal-body">

                    <div class="form-group">
                        <label for="trainingSetName">TrainingSet</label>
                        <g:textField name="name" id="trainingSetName" placeholder="Training Set Name" required=""/>

                    </div>

                    <div>
                        <label>Interface flag:</label>
                        <label><input type="radio" name="UIflag" value="0" required=""/>paragraphs</label>
                        <label><input type="radio" name="UIflag" value="1" required=""/>draggable lists</label>
                    </div>
                    %{--                    <label>number of HITs: </label>--}%
                    %{--                    <input type="number"  name='hit_num' id='hit_num' min="0" max="100" value="0" style="color:black;" required>--}%
                    <p></p>
                    <label>File input</label>
                    <input type="file" id="trainingInputFile" name="inputFile" required>

                    <p class="help-block">Select trainingset file (*.json).</p>



                </div>


                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    %{--                <input type="button" class="btn btn-primary" value="Upload" onclick="create_train()"/>--}%
                    <button type="submit" class="btn btn-primary">Create</button>
                </div>
            </g:form>
        %{--            </form>--}%

        </div>
    </div>
</div>

<div class="modal modal-info" style="padding-top: 140px" id="training-launch-modal">
    <div class="modal-dialog modal-md">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Launch Training</h4>
            </div>

            <g:form enctype="multipart/form-data" name="training-launch-form" controller="admin" action="launchTraining">
                <div class="modal-body">
                    <div class="panel panel-default">
                        <div class="panel-body training-launch-parameters">
                            <input hidden name="trainingId" id="launchTrainingId" value=""/>
                            <label>Enable MTurk?</label>
                            <input type="checkbox" name="enableMturk" id="trainingEnableMturk" value="false"><br>
                            <label>Credentials</label>
                            <g:select class="trainingLaunchParam" from="${CrowdServiceCredentials.list()}"
                                      name="mturkSelectCredentials"
                                      optionKey="id"
                                      noSelection="${['': 'Select One...']}"/><br>
                            <label>Number of HITs:</label>
                            <input type="number" class="trainingLaunchParam" name='num_hits' id='num_training_hits' min="0" max="10000"
                                   value="0"
                                   style="color:black;" required><br>
                            <label>Available time (minutes):</label>
                            <input type="number" class="trainingLaunchParam" name='hit_time' id='hit_training_time' min="0" max="10080"
                                   value="1440"
                                   style="color:black;" required><br>
                            <label>Assignment lifetime (minutes):</label>
                            <input type="number" class="trainingLaunchParam" name='assignment_time' id='assignment_training_time' min="0"
                                   max="1440" value="120"
                                   style="color:black;" required><br>
                            <label>Payment:</label>
                            <input type="number" class="trainingLaunchParam" name='payment' id='training_payment' min="0.0" value="0.0" step=".01"
                                   style="color:black;" required><br>
                            <label>Additional qualifiers (comma separated):</label>
                            <input type="text" class="trainingLaunchParam" name='other_quals' id='other_training_quals' style="color:black;"><br>
                        </div>
                    </div>
                </div>


                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary" id='launch-training'>Create</button>
                </div>
            </g:form>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>


<script type="text/javascript">
    $(document).ready(function () {



        $(".show-training-launch-modal").click(function (e){
            const id = $(".trainingId",this.parentNode).text()
            const mode = $(".mode",this.parentNode).text()

            if (mode === "launch") {
                $("#training-launch-form")[0].reset()
                $("#launchTrainingId").val(id)

                $("#training-launch-modal .trainingLaunchParam").prop('disabled', true);
                $("#training-launch-modal").modal('show');
            } else {

                $.ajax({
                    type: "GET",
                    url: "/loom/admin/cancelTraining",
                    data: {
                        trainingId: id
                    },
                    dataType: "json",

                    success: function (result) {
                        //console.log("I am successful")
                        window.location.reload()
                    }

                })
            }


        });

        $("#launch-training").click(function (e) {
            e.preventDefault();

            if ($("#trainingEnableMturk").is(':checked') && $("select[name='mturkSelectCredentials']").val() === "") {
                alert("Please select credentials when enabling mTurk.");
                return;
            }

            // If the validation passes, submit the form
            $("form[name='training-launch-form']").submit();
        });

        $("#trainingEnableMturk").change(function (e) {
            if (e.target.checked) {
                $(".trainingLaunchParam").removeAttr('disabled');
            } else {
                $(".trainingLaunchParam").attr('disabled','disabled');

            }

        });
    });
</script>
