<%@ page import="edu.msu.mi.loom.CrowdServiceCredentials" %>
<div class="modal modal-info" style="padding-top: 140px" id="session-modal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Create Session</h4>
            </div>
            <%
                def basicParams = [["class": "story", "label": "Story:"],
                                   ["class": "constraintTests", "label": "Constraints:"],
                                   ["class": "minNode", "label": "Min Nodes:"],
                                   ["class": "maxNode", "label": "Max Nodes:"],
                                   ["class": "initialNbrOfTiles", "label": "Initial Number of Tiles:"],
                                   ["class": "roundCount", "label": "Rounds:"],
                                   ["class": "roundTime", "label": "Time per round:"],
                                   ["class": "isInline", "label": "Interface mode:"],
                                   ["class": "networkTemplate", "label": "Network Type:"]]

                def paymentParams = [["class": "paymentBase", "label": "Acceptance Payment"],
                                     ["class": "paymentWaitingBonusPerMinute", "label": "Waiting Payment / min"],
                                     ["class": "paymentMaxScoreBonus", "label": "Max Bonus Payment"]]

                def mturkParams = [["class": "mturkHitLifetimeInSeconds", "label": "Hit lifetime in seconds"],
                                   ["class": "mturkAssignmentLifetimeInSeconds", "label": "Assignment lifetime in seconds"],
                                   ["class": "mturkAdditionalQualifications", "label": "Additional qualifications"]]


            %>
            <g:form enctype="multipart/form-data" name="session-upload-form" controller="admin" action="createSession">
                <div class="modal-body">

                    <div class="nav-tabs-custom">
                        <ul class="nav nav-tabs">
                            <li class="active"><a href="#session-basic" data-toggle="tab">General</a></li>
                            <li><a href="#session-mturk" data-toggle="tab">Mturk</a></li>
                            <li><a href="#session-payments" data-toggle="tab">Payments</a></li>
                        </ul>

                        <div class="tab-content">
                            <div class="active tab-pane" id="session-basic">
                                <div class="form-group">
                                    <div class="panel panel-default">
                                        <div class="panel-body default-session-parameters">
                                            <table class="table">
                                                <thead>
                                                <tr><td>Parameter</td><td>Inherited</td></tr>
                                                </thead>
                                                <tbody>
                                                <tr class="name"><td>Experiment Name</td><td></td></tr>
                                                <g:each in="${basicParams}" var="param">
                                                    <tr class="${param['class']}"><td>${param.label}</td><td></td>
                                                    </tr>
                                                </g:each>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="tab-pane" id="session-mturk">
                                <div class="form-group">
                                    <div class="panel panel-default">
                                        <div class="panel-body default-session-parameters">
                                            <table class="table">
                                                <thead>
                                                <tr><td>Parameter</td><td>Inherited</td></tr>
                                                </thead>
                                                <tbody>
                                                <g:each in="${mturkParams}" var="param">
                                                    <tr class="${param['class']}"><td>${param.label}</td><td></td>
                                                    </tr>
                                                </g:each>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="tab-pane" id="session-payments">
                                <div class="form-group">
                                    <div class="panel panel-default">
                                        <div class="panel-body default-session-parameters">
                                            <table class="table">
                                                <thead>
                                                <tr><td>Parameter</td><td>Inherited</td></tr>
                                                </thead>
                                                <tbody>
                                                <g:each in="${paymentParams}" var="param">
                                                    <tr class="${param['class']}"><td>${param.label}</td><td></td>
                                                    </tr>
                                                </g:each>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="panel panel-default">
                        <div class="panel-heading">Session Configuration</div>

                        <div class="panel-body">
                            <label for="session-name">Session name:</label>
                            <input type="text" name="name" id="session-name" style="color: black">

                            <p></p>
                            <input name="experimentId" type="hidden" value=""/>
                            <input name="sessiondata" type="hidden" value=""/>
                            <input class="pull-left" type="file" id="session-input-file"
                                   name="inputFile" required>
                            <span class="pull-right validation-message">Invalid file</span>

                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary" id='create-session' disabled>Create</button>
                </div>
            </g:form>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal modal-info" style="padding-top: 140px" id="session-launch-modal">
    <div class="modal-dialog modal-md">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Launch Session</h4>
            </div>

            <g:form enctype="multipart/form-data" name="session-launch-form" controller="admin" action="launchSession">
                <div class="modal-body">
                    <div class="panel panel-default">
                        <div class="panel-body session-launch-parameters">
                            <input hidden name="sessionId" id="launchSessionId" value=""/>
                            <label>Enable MTurk?</label>
                            <input type="checkbox" name="enableMturk" id="sessionEnableMturk" value="false"><br>
                            <label>Credentials</label>
                            <g:select class="sessionLaunchParam" from="${CrowdServiceCredentials.list()}"
                                      name="mturkSelectCredentials"
                                       optionKey="id"
                                      noSelection="${['': 'Select One...']}"/><br>
                            <label>Single hit?</label>
                            <input type="checkbox" class="sessionLaunchParam" name='mturk_method' id='mturk_method' value="0"><br>

                            <label>Number of Tasks:</label>
                            <input type="number" class="sessionLaunchParam" name='num_hits' id='num_session_hits' min="0" max="10000"
                                   value="0"
                                   style="color:black;" required><br>
                            <label>Available time (minutes):</label>
                            <input type="number" class="sessionLaunchParam" name='hit_time' id='hit_session_time' min="0" max="10080"
                                   value="1440"
                                   style="color:black;" required><br>
                            <label>Assignment lifetime (minutes):</label>
                            <input type="number" class="sessionLaunchParam" name='assignment_time' id='assignment_session_time' min="0"
                                   max="1440" value="120"
                                   style="color:black;" required><br>
                            <input type="number" class="sessionLaunchParam" name='payment' id='session_payment' min="0.0" value="0.0" step=".01"
                                   style="color:black;" required><br>
                            <label>Additional qualifiers (comma separated):</label>
                            <input type="text" class="sessionLaunchParam" name='other_quals' id='other_session_quals' style="color:black;"><br>
                        </div>
                    </div>
                </div>


                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary" id='launch-session'>Create</button>
                </div>
            </g:form>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>

<script type="text/javascript">

    function validate() {
        console.log("Running validation")
        if ($("#session-name").val()=='' || !($("#session-modal .validation-message").hasClass("valid"))) {
            $("#create-session").prop('disabled',true)
            console.log("Invalid!")
        } else {
            console.log("Valid!")
            $("#create-session").prop('disabled',false)
        }
    }

    function refreshSessionDetails(data) {
        $('.session-row').each(function() {
            const sessionId = $(".sessionId",this.parentNode).text();
            if (sessionId in data) {
                $(".current-round",this).text(data[sessionId]['round'])
                $(".connected",this).text(data[sessionId]['connected'])
                $(".session-span",this).text(data[sessionId]['status'])
            }
        });
    }

    $(document).ready(function () {

        $(".show-session-launch-modal").click(function (e){
            $("#session-launch-form")[0].reset()
            const id = $(".session-id",this.parentNode).text()
            $("#launchSessionId").val(id)
            $("#session-launch-modal .sessionLaunchParam").attr('disabled','disabled');
            $("#session-launch-modal").modal('show');

        });

        $("#sessionEnableMturk").change(function (e) {
            if (e.target.checked) {
                $(".sessionLaunchParam").removeAttr('disabled');
            } else {
                $(".sessionLaunchParam").attr('disabled','disabled');

            }

        })

        $("#session-name").on('input',function () {
            validate()
        });





        $(".create-session-button").click(function (e) {
            expid = $(e.target).find(".expid").text()
            $("#session-modal input[name=experimentId]").val(expid)

            $.ajax({
                type: "GET",
                url: "/loom/admin/getExperimentData",
                data: {
                    experimentId: expid
                },
                dataType: "json",
                success: function (result) {
                    console.log(result)
                    for (const [key, value] of Object.entries(result.data)) {
                        $("tr." + key + " td:nth-child(2)").text(value);
                    }
                }
            });
            $("#session-upload-form")[0].reset()
            $("#session-modal").modal('show');


        });


        $("#session-input-file").change(function () {
            fd = new FormData()
            fd.append("inputFile", $("#session-input-file")[0].files[0])
            $.ajax({
                type: "POST",
                url: "/loom/admin/validateParametersFile",
                data: fd,

                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    if (data.status === "error") {
                        alert("Invalid file:" + data.message)
                        $("#validation-message").text("Validated").removeClass("valid")
                    } else {
                        $("#session-modal input[name=sessiondata]").val(data)
                        $("#session-modal .validation-message").text("Validated").addClass("valid")
                    }
                    validate()
                }
            });
        });




        // setInterval(
        //     function () {
        //         //console.log("Would refresh")
        //         $.ajax({
        //
        //             url: "/loom/admin/refresh",
        //             type: 'GET',
        //             dataType: "json",
        //             success: function (data) {
        //                 //console.log("Refresshing data",data)
        //                 refreshSessionDetails(data)
        //
        //             }
        //         });
        //     },1000);




        // $("#sessions").on('click', '.pay-session', function (){
        //     var sessionId = $(".sessionId",this.parentNode).text();
        //     var payment_status = $(".payment-status",this.parentNode.parentNode.parentNode);
        //     var pay_btn = $(".pay-session", this.parentNode);
        //     var pay_i = $(".pay-i", this.parentNode);
        //     pay_btn.addClass("buttonload");
        //     pay_btn.attr("disabled",true)
        //     pay_i.addClass("fa fa-spinner fa-spin");
        //
        //     $.ajax({
        //         url: "/loom/admin/paySession",
        //         type: 'POST',
        //         data:
        //             {
        //                 sessionId: sessionId,
        //
        //             },
        //         dataType:"json",
        //         success: function (data){
        //             payment_status.text("Payment status: "+data.payment_status);
        //             pay_btn.removeClass("buttonload");
        //             pay_btn.attr("disabled",false)
        //             pay_i.removeClass("fa fa-spinner fa-spin");
        //             if(data.status==="no_payable"){
        //                 alert("No payable assignment!");
        //             }
        //             // if(data.status==="success"){
        //             //
        //             //
        //             //     window.location = "/loom/admin/board/";
        //             // }
        //
        //
        //         }
        //     });
        // });
        //
        // $("#sessions").on('click', '.check-payble', function (){
        //     var sessionId = $(".sessionId",this.parentNode).text();
        //     var payment_status = $(".payment-status",this.parentNode.parentNode.parentNode);
        //
        //     var check_btn = $(".check-payble", this.parentNode);
        //     var check_i = $(".check-payable-i", this.parentNode);
        //     check_btn.addClass("buttonload");
        //     check_btn.attr("disabled",true)
        //     check_i.addClass("fa fa-spinner fa-spin");
        //
        //     $.ajax({
        //         url: "/loom/admin/checkSessionPayble",
        //         type: 'POST',
        //         data:
        //             {
        //                 sessionId: sessionId,
        //
        //             },
        //         dataType:"json",
        //         success: function (data){
        //             payment_status.text("Payment status: "+data.payment_status);
        //             check_btn.removeClass("buttonload");
        //             check_btn.attr("disabled",false)
        //             check_i.removeClass("fa fa-spinner fa-spin");
        //             if(data.check_greyed){
        //                 alert("No payable assignment!");
        //                 // check_btn.attr("disabled",true);
        //             }
        //             if(data.pay_greyed){
        //                 // pay_btn.attr("disabled",true);
        //             }
        //             // if(data.status==="success"){
        //             //     window.location = "/loom/admin/board/";
        //             // }
        //
        //
        //         }
        //     });
        // });


    });
</script>