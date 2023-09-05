<%@ page import="edu.msu.mi.loom.CrowdService" %>
<div class="modal modal-info" style="padding-top: 140px" id="create-credentials-modal">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Create Credentials</h4>
            </div>

            <div class="modal-body">
                <div class="form-group">
                    <label for="credentialsName">Credentials Name</label>
                    <g:textField name="credentialsName" id="credentialsName" placeholder="Credentials Name"
                                 required=""/>

                </div>

                <div class="form-group">
                    <label for="serviceType">Service Type</label>
                    <g:select name="serviceType" id="serviceType" required=""
                              from="${CrowdService.values()}"
                              keys="${CrowdService.values()}"
                              onchange="credentialsShowMTurkOptions(event)"
                              style="color: black"
                              noSelection="${['': 'Select One...']}"/>
                </div>

                <div class="form-group">
                    <label for="accessKey">Access Key</label>
                    <g:textField name="accessKey" id="accessKey" placeholder="Access Key" required=""/>

                </div>

                <div class="form-group">
                    <label for="secretKey">Secret Key</label>
                    <g:textField name="secretKey" id="secretKey" placeholder="Secret Key" required=""/>

                </div>

                <div class="form-group" id="credentials-model-sandbox-options" style="display: none">
                    <div>
                        <label>Interface flag:</label>
                        <label><input type="radio" name="sandboxSetting" value="sandbox" required=""/>sandbox</label>
                        <label><input type="radio" name="sandboxSetting" value="production" required=""/>production
                        </label>
                        <label><input type="radio" name="sandboxSetting" value="both" required=""/>both</label>

                    </div>

                </div>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" id='submit-credentials'>Add Credentials</button>
            </div>

        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
         $("#submit-credentials").on('click', function (){
            var name = $("#credentialsName").val();
            var accessKey = $("#accessKey").val();
            var secretKey = $("#secretKey").val();
            var serviceName = $("#serviceType").val();
            var sandbox = $("input[name='sandboxSetting']:checked").val()

            if (name==='' || accessKey==='' || secretKey === '' || serviceName === '') {
                alert("All fields are required")
            } else {
                $.ajax({
                    url: "/loom/admin/createUserCredentials",
                    type: 'POST',
                    // contentType: "application/json;",
                    // data: JSON.stringify({ 'list': usernames }),
                    data:
                        {
                            credentialsName: name,
                            accessKey: accessKey,
                            secretKey: secretKey,
                            serviceType: serviceName,
                            sandboxSetting: sandbox


                        },
                    dataType: "json",
                    success: function (data) {
                        if (data.status === "duplicate") {
                            alert("Name already exists")
                        } else {
                            alert("Should create credentials")
                            $("#create-credentials-modal").modal('hide');
                            window.location.href="/loom/admin/board#credentials"
                            window.location.reload()
                            return false;
                        }
                    }
                });

            }

        });

    });

</script>
