<div class="modal modal-info" style="padding-top: 140px" id="launch-training-modal">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Launch Training</h4>
            </div>

            <div class="modal-body">
                <!-- TODO: Need a real training id in here -->
                <span hidden id="trainingID"></span>
                <label>Number of HITs:</label>
                <input type="number" name='num_training_hits' id='num_training_hits' min="0" max="10000" value="0"
                       style="color:black;" required><br>
                <label>Available time (minutes):</label>
                <input type="number" name='available_time' id='available_time' min="0" max="10080" value="1440"
                       style="color:black;" required><br>
                <label>Assignment lifetime (minutes):</label>
                <input type="number" name='assignment_lifetime' id='assignment_lifetime' min="0" max="1440" value="120"
                       style="color:black;" required><br>
                <label>Additional qualifiers (comma separated):</label>
                <input type="text" name='other_quals' id='other_quals'
                       style="color:black;"><br>

                <p></p>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" id="launch_training_hits">Lanch HITs</button>
            </div>

        </div>
    </div>

</div>