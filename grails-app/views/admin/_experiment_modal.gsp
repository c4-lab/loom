<div class="modal modal-info" style="padding-top: 140px" id="experiment-modal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Create an Experiment</h4>
            </div>

            <div class="modal-body">

                <div class="form-group">
                    <div class="panel panel-default">
                        <div class="panel-heading">Experiment Info</div>

                        <div class="panel-body">

                            <label for="name">Experiment name:</label>
                            <input type="text" name="name" id="name" style="color: black">

                            <p></p>
                            <label for="StorySelect">Story:</label>
                            <g:select name="storySet" id="StorySelect"
                                      from="${stories}" optionKey="id" style="color: black"
                                      optionValue="title" noSelection="${['null': 'Select One...']}"/>

                            <p></p>

                            <label>Min nodes:</label>
                            <input type="number" name='min_nodes' id='min_nodes' min="2" max="100" value="2"
                                   style="color:black;">

                            <label>Max nodes:</label>
                            <input type="number" name='max_nodes' id='max_nodes' min="2" max="100" value="2"
                                   style="color:black;">

                            <p></p>
                            <label>Init tiles:</label>
                            <input type="number" name='initialNbrOfTiles' id='initialNbrOfTiles' min="2"
                                   max="100"
                                   value="2"
                                   style="color:black;">

                            <label>Number of rounds:</label>
                            <input type="number" name='rounds' id='rounds' min="1" max="100" value="2"
                                   style="color:black;">

                            <p></p>
                            <label>Round duration:</label>
                            <input type="number" name='duration' id='duration' min="1" max="100" value="2"
                                   style="color:black;"><label>seconds</label>

                            <p></p>
                            %{--                        <label  >Qualifier string (optional): </label>--}%
                            %{--                        <input type="text" style="color:black;" name="qualifier">--}%

                            <label>Interface flag:</label>
                            <label><input type="radio" name="UIflag" value="0"/>list of items (larger information items)</label>
                            <label><input type="radio" name="UIflag" value="1"/>inline phrases (smaller information items)</label>

                        </div>
                    </div>
                </div>


                <p></p>


                <div class="panel panel-default">
                    <div class="panel-heading">Constraints</div>

                    <div class="panel-body">
                        <table class="table table-bordered grid create-constraint-table"
                               id="experiment-constraint-table" border="1">


                            <th style="text-align:center">Constraint</th>
                            <th style="text-align:center">Operator</th>
                            <th style="text-align:center">Parameters</th>
                            <th style="text-align:center">Action</th>



                            <tbody style="background-color: #23272b">

                            </tbody>
                        </table>
                        <button type="button" class="btn btn-primary pull-left add-constraint">Add constraint</button>

                    </div>
                </div>





            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" id='create-exp'>Create</button>
            </div>

        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
