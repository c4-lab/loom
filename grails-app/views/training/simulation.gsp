<g:applyLayout name="main">
    <div class="wrapper" id="simulationMainContainer">
        <div id="simulation-content-wrapper">
            <g:render template="simulation_content"
                      model="[userList: userList, simulation: simulation, roundNbr: roundNbr]"/>
        </div>
    </div>

    <div class="wrapper" id="simulationScore" style="display: none">
        <div class="content-wrapper container">
            <section class="content">
                <div class="row">
                    <div class="col-md-12">

                    <div class="col-md-2">Final score</div>

                    <div class="col-md-8">
                        %{--<g:render template="alert_finish" model="[score: score]"/>--}%
                        <span id="scorePanel"></span>
                    </div>

                    <div class="col-md-2">

                        <g:form controller="training" action="trainingComplete">

                            <g:hiddenField name="roundNumber"/>
                            <g:submitButton name="continue" class="btn btn-success" value="Continue"/>
                        </g:form>
                    </div>
                </div>
            </section>
        </div>
    </div>

    <script type="text/javascript">
        var timeout;
        var confirmSimNav = true;
        $(window).on('beforeunload', function () {
            if (confirmSimNav && jQuery("#simTemplate").length > 0) {
                before = new Date();
                timeout = setTimeout(function () {
                    after = new Date();
                    clearInterval(roundInterval);
                    calculateTime();
                }, 10);
                return "Your work will be lost.";
            }
        });
    </script>
</g:applyLayout>