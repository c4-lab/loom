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
                    <label for="name">payment:</label>
                    <input type="number" step="0.1" name='training_payment' id='training_payment'
                           oninput="if (value.length > 4) value = value.slice(0, 4);
                           if (value <= 0) value = 0" style="color:black;"/>

                    <p></p>
                    <label>File input</label>
                    <input type="file" id="trainingInputFile" name="inputFile" required>

                    <p class="help-block">Select trainingset file (*.json).</p>

                    <label>check simulation:</label>
                    <g:checkBox name="simulation" value="${false}"/>
                    %{--                        <div id="traing_perform" style="display:none">--}%
                    %{--                            <label >Simulation Score >=</label>--}%

                    %{--                            <select  style="display:none" >--}%
                    %{--                                <option><input type="number"  name='performance' id='simulation_score' min="0" max="5" value="0" style="color:black;" required></option>--}%
                    %{--                            </select>--}%

                    %{--                        </div>--}%
                    <p></p>

                    <label>check reading:</label>
                    <g:checkBox name="read" value="${false}"/>
                    %{--                        <div id="traing_reading" style="display:none">--}%
                    %{--                            <label >Reading score >=</label>--}%

                    %{--                            <select  style="display:none" >--}%
                    %{--                                <option><input type="number"  name='reading' id='reading_score' min="0" max="5" value="0" style="color:black;" required></option>--}%
                    %{--                            </select>--}%

                    %{--                        </div>--}%
                    <p></p>
                    <label>check survey:</label>
                    <g:checkBox name="survey" id="survey" value="${false}"/>
                    %{--                        <div id="traing_survey" style="display:none">--}%
                    %{--                            <label >Survey score >=</label>--}%

                    %{--                            <select  style="display:none" >--}%
                    %{--                                <option><input type="number"  name='survey' id='survey_score' min="0" max="5" value="0" style="color:black;" required></option>--}%
                    %{--                            </select>--}%

                    %{--                        </div>--}%

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