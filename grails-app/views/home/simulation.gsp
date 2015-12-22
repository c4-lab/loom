<g:applyLayout name="main">
    <div class="wrapper" id="simulationMainContainer">
        <div class="content-wrapper" id="simulation-content-wrapper">
            <g:render template="/home/simulation_content"
                      model="[userList: userList, simulation: simulation, roundNbr: roundNbr]"/>
        </div>
    </div>

    <div class="wrapper" id="simulationScore" style="display: none">
        <div class="content-wrapper">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                    </div>

                    <div class="col-md-1"></div>
                </div>

                <div class="row">
                    <div class="col-md-2">Final score</div>

                    <div class="col-md-8">
                        %{--<g:render template="alert_finish" model="[score: score]"/>--}%
                        <span id="scorePanel"></span>
                    </div>

                    <div class="col-md-2">
                        <g:form controller="experiment" action="experiment">
                            <g:hiddenField name="session"/>
                            <g:hiddenField name="roundNumber"/>
                            <g:submitButton name="continue" class="btn btn-success" value="Continue"/>
                        </g:form>
                    </div>
                </div>
            </section>
        </div>
    </div>
</g:applyLayout>