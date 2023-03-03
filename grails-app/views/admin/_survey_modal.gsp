<div class="modal modal-info" style="padding-top: 140px" id="survey-upload-modal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Upload File</h4>
            </div>

            <g:form enctype="multipart/form-data" name="upload-form" controller="admin" action="uploadSurvey">
                <div class="modal-body">

                    <div class="form-group">
                        <label>File input</label>
                        <input type="file" id="surveyInputFile" name="inputFile" required>

                        <p class="help-block">Select survey file (*.json).</p>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    %{--                <input type="button" class="btn btn-primary" value="Upload" onclick="create_train()"/>--}%
                    <button type="submit" class="btn btn-primary">Create</button>
                </div>
            </g:form>
        </div>
    </div>
</div>
