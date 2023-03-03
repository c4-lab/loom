<div class="modal modal-info" style="padding-top: 140px" id="session-modal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Create Session</h4>
            </div>

            <g:form name="session-form" controller="admin" action="createSession">
                <div class="modal-body">
                    <div class="nav-tabs-custom">
                        <ul class="nav nav-tabs">
                            <li class="active"><a href="#session-basic" data-toggle="tab">General</a></li>
                            %{--                               <li><a href="#session-hit" data-toggle="tab">Sessions-HIT</a></li>--}%
                            <li><a href="#session-mturk" data-toggle="tab">Mturk</a></li>
                            <li><a href="#session-payments" data-toggle="tab">Payments</a></li>
                            %{--                                <li><a href="#training-hit" data-toggle="tab">Trainings-HIT</a></li>--}%
                        </ul>

                        <div class="tab-content">
                            <div class="active tab-pane" id="session-basic">
                                <div class="form-group">
                                    <div class="panel panel-default">
                                        <div class="panel-heading">Session Info</div>

                                        <div class="panel-body">
                                            <em>Experiment name:</em><span class="experiment-name"></span>
                                            <label for="session-name">Session name:</label>
                                            <input type="text" name="name" id="session-name" style="color: black">

                                            <p></p>
                                            <label for="StorySelect">Story:</label>
                                            <g:select name="storySet" id="StorySelect"
                                                      from="${stories}" optionKey="id" style="color: black"
                                                      optionValue="title" noSelection="${['null': 'Select One...']}"/>

                                            <p></p>

                                        </div>
                                    </div>
                                </div>


                                <p></p>

                                <div class="panel panel-default">
                                    <div class="panel-heading">Base Constraints</div>
                                    <ul class="inherited-constraints"></ul>
                                </div>


                                <div class="panel panel-default">
                                    <div class="panel-heading">Additional Constraints</div>

                                    <div class="panel-body">
                                        <table class="table table-bordered grid create-constraint-table"
                                               id="session-constraint-table"
                                               border="1">

                                            <th style="text-align:center">Constraint</th>
                                            <th style="text-align:center">Operator</th>
                                            <th style="text-align:center">Parameters</th>
                                            <th style="text-align:center">Action</th>



                                            <tbody style="background-color: #23272b">

                                            </tbody>
                                        </table>
                                        <button type="button"
                                                class="btn btn-primary pull-left add-constraint">Add constraint</button>

                                    </div>
                                </div>

                                <div class="panel panel-default">
                                    <div class="panel-heading">Network</div>

                                    <div class="panel-body">
                                        <label>Network type:</label>
                                        <label><input type="radio" onclick="chooseType('Lattice')" name="network_type"
                                                      value="Lattice"/>1D Lattice</label>
                                        <label><input type="radio" onclick="chooseType('Newman-Watts')" name="network_type"
                                                      value="Newman_Watts"/>Newman-Watts</label>
                                        <label><input type="radio" onclick="chooseType('Barabassi-Albert')" name="network_type"
                                                      value="Barabassi_Albert"/>Barabassi-Albert</label>


                                        <div id="s1" style="display:none">
                                            <label>degree:</label>
                                            <select style="display:none">

                                                <option><input type="number" name='min_degree' id='Lattice_min_degree' min="2"
                                                               max="100"
                                                               value="0" style="color:black;" required=False></option>
                                            </select>
                                        </div>

                                        <div id="s2" style="display:none">

                                            <label>degree:</label>
                                            <input type="number" min="3" max="100" name='min_degree' value="3"
                                                   id='Newman_min_degree'
                                                   style="color:black;">

                                            <p></p>
                                            <label>k-nearest neighbors:</label>
                                            <input type="number" min="3" max="100" name='max_degree' value="3"
                                                   id='Newman_max_degree'
                                                   style="color:black;">

                                            <p></p>
                                            <label>shortcut probability:</label>
                                            <input type="number" step="0.1" name='prob' id='Newman_prob'
                                                   oninput="if (value > 1) value = 1;
                                                   if (value.length > 4) value = value.slice(0, 4);
                                                   if (value <= 0) value = 0.1" style="color:black;"/>

                                        </div>

                                        <div id="s3" style="display:none">
                                            <label>initial nodes:</label>
                                            <input type="number" min="2" max="100" name='min_degree' value="2"
                                                   id='BA_min_degree'
                                                   style="color:black;">

                                            <p></p>
                                            <label>max degree:</label>
                                            <input type="number" min="2" max="100" name='max_degree' value="2"
                                                   id='BA_max_degree'
                                                   style="color:black;">

                                            <p></p>
                                            <label>edges per node:</label>
                                            <input type="number" min="2" max="100" name='M' id='BA_M' value="2"
                                                   style="color:black;">

                                            <p></p>

                                        </div>
                                    </div>
                                </div>

                            </div>

                            <span hidden id="experiment-id"></span>


                            </div>
                            <div class="tab-pane" id="session-mturk">
                                <label>Number of HITs:</label>
                                <input type="number" name='num_hits' id='num_exp_hits' min="0" max="10000" value=0
                                       style="color:black;" required>

                                <p></p>
                                <label>Available time (minutes):</label>
                                <input type="number" name='available_time' id='exp_available_time' min="0" max="10080"
                                       value="1440"
                                       style="color:black;" required>

                                <p></p>
                                <label>Assignment lifetime (minutes):</label>
                                <input type="number" name='assignment_lifetime' id='exp_assignment_lifetime' min="0"
                                       max="1440"
                                       value="120"
                                       style="color:black;" required>

                                <p></p>
                                <label>Additional qualifiers (comma separated):</label>
                                <input type="text" name='other_quals' id='exp_other_quals'
                                       style="color:black;"><br>
                            </div>

                            <div class="tab-pane" id="session-payments">
                                <p>
                                    Placeholder text
                                </p>
                            </div>

                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                    <button id="create-session" type="submit" class="btn btn-primary">Lanch HITs</button>
                </div>
            </g:form>

        </div>
    </div>

</div>