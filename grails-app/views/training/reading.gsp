<g:applyLayout name="main">
%{--    <div class="wrapper" id="simulationMainContainer">--}%

%{--    </div>--}%

    <div class="wrapper">
        <div class="content-wrapper container">
            <section class="content training-survey">
                <div class="row">
                    <div class="col-md-12">
                        <g:form controller="training" action="readingComplete">

                            <div class="col-md-12">

                                <div class="row story-passage">
                                    <div class="col-md-8 mx-auto">

                                        <g:each var="section" in="${reading.passage.split("\\n")}">
                                            <p>${section}</p>
                                        </g:each>
                                    </div>
                                </div>
                                <g:each var="question" in="${reading.questions.sort { it.id }}" status="j">

                                    <p><b>Question ${j + 1}:</b> ${question.question}</p>

                                    <g:each var="option" in="${question.options}" status="m">

                                        <g:radio name="question${question.id}" value="${m}" required=""/>
                                        <label>${option}</label>

                                        <p></p>

                                    </g:each>
                                    <p></p>

                                </g:each>

                            </div>


                            <g:hiddenField name="readingId" value="${reading.id}"/>
                            <g:hiddenField name="trainingSetId" value="${trainingSetId}"/>
                            <g:hiddenField name="assignmentId" value="${assignmentId}"/>

                            <g:submitButton name="submit" class="btn btn-success" value="Submit"/>
                        </g:form>
                    </div>
                </div>
            </section>
        </div>
    </div>

</g:applyLayout>