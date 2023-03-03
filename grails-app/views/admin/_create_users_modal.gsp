<div class="modal modal-info" style="padding-top: 140px" id="create-users-modal">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Create Users</h4>
            </div>

            <div class="modal-body">
                <div class="text-center">

                    <table class="table table-striped table-bordered grid" border="1" id="create-user-table">

                        <th style="text-align:center; background-color: #23272b">Username</th>
                        <th style="text-align:center; background-color: #23272b">Action</th>

                        <tbody style="background-color: #23272b">

                        </tbody>
                    </table>
                    %{--                <label>Username: </label>--}%
                    %{--                <input type="text" name="username" id="username" style="color: black"><button  type="button" class="btn btn-primary" id="create-default">user default</button>--}%

                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>

                <button type="button" class="btn btn-primary" id="create-username">Add a username</button>
                <button type="submit" class="btn btn-primary" id='submit-users'>Submit</button>
            </div>

        </div>
    </div>
</div>