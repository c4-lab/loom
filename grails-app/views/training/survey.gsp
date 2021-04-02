<g:applyLayout name="main">
%{--    <div class="wrapper">--}%
%{--        <div id="simulation-content-wrapper">--}%

%{--        </div>--}%
%{--    </div>--}%

    <div class="wrapper" >
        <div class="content-wrapper container">
            <section class="content">
                <div class="row">
                    <g:form controller="training" action="surveyComplete">

                    <g:each var="question" in="${surveyTask}" status="i">
                        <div class="col-md-12">


                                <p>Question ${i}: ${question.question}</p>

                                <g:each var="option" in="${question.options.sort{it.id}}" status="m">
                                    <g:radio name="question${question.id}" value="${option.score}" required=""/>
                                    <label>${option.answer}</label>
                                    <p></p>
%{--                                    <label>${option.answer}</label>--}%
%{--                                    <g:checkBox name="suropt${option.id}" value="${false}" />--}%

                                </g:each>
                                <p></p>




                        </div>

                    </g:each>




                            <g:hiddenField name="trainingSetId" value="${trainingSetId}"/>
                            <g:submitButton name="submit" class="btn btn-success" value="Submit"/>
                        </g:form>

                </div>
            </section>
        </div>
    </div>


</g:applyLayout>