<div class="modal modal-info" style="padding-top: 140px" id="create-story-set">
    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Upload File</h4>
            </div>

            <div class="modal-body">

                <label>Title:</label>
                <input type="text" name="name" id="story-title" style="color: black">

                <p></p>
                <label>Write a story (each line is a tile):</label>
                <textarea id="story-text" style="color: black" rows="20" cols="60"></textarea>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" id='create-story'>Create</button>
            </div>

        </div>
    </div>
</div>