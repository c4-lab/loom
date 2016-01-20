<%@ page import="edu.msu.mi.loom.Room" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="experiment-content-wrapper">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1">Actual story</div>

                    <div class="col-md-10">
                        <g:each in="${rightStory}" var="tail">
                            <span>${tail.text}</span>
                        </g:each>
                    </div>

                    <div class="col-md-1"></div>
                </div>

                <div class="row">
                    <div class="col-md-1">Final score</div>

                    <div class="col-md-10">
                        %{--<g:render template="alert_finish" model="[score: score]"/>--}%
                        <span>${score}</span>
                        <g:hiddenField name="experiment" value="${experiment.id}"/>
                    </div>

                    <div class="col-md-1"></div>
                </div>

                <div class="row">
                    <div class="col-md-2"></div>

                    <div class="col-md-8">
                    </div>

                    <div class="col-md-2">
                        <g:link controller="home" action="leaveExperiment" class="btn btn-success"
                                params="[room: Room.findBySession(experiment.session).id]">Finish</g:link>
                    </div>
                </div>
            </section>
        </div>
    </div>
</g:applyLayout>