<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="experiment-content-wrapper">
            <g:render template="/home/experiment_content"
                      model="[userList: userList, experiment: experiment, roundNbr: roundNbr]"/>
        </div>
    </div>
</g:applyLayout>