<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="simulation-content-wrapper">
            <g:render template="/home/simulation_content"
                      model="[userList: userList, simulation: simulation, roundNbr: roundNbr]"/>
        </div>
    </div>
</g:applyLayout>