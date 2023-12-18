<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container survey">
            <section class="content">
                <div class="row">
                    <%-- Progress Bar --%>
                    <div class="col-md-12">
                        <div class="progress">
                            <div class="progress-bar" role="progressbar" aria-valuenow="${(completed / total) * 100}"
                                 aria-valuemin="0" aria-valuemax="100" style="width:${(completed / total) * 100}%">
                            </div>
                            <div class="progress-text">
                                ${completed} / ${total} Completed
                            </div>

                        </div>

                    </div>
                    <g:form controller="training" action="surveyComplete">
                        <p>
                            ${survey.instructions}
                        </p>
                        <g:each var="question" in="${survey.surveyItems as List}" status="i">
                            <div class="col-md-12">
                                <p>Question ${i + 1}: ${question.question}</p>
                            <%-- Check if likert is true --%>
                                <g:if test="${survey.likert}">
                                    <div class="likert-container">
                                        <g:each var="option" in="${question.options.sort { it.id }}" status="m">
                                            <div class="likert-option" style="width: ${100 / question.options.size()}%;">
                                                <g:radio name="question${question.id}" value="${option.id}" required=""/>
                                                <label for="question${question.id}_${option.id}">${option.answer}</label>
                                            </div>
                                        </g:each>
                                    </div>
                                </g:if>
                            <%-- Render normally if not likert --%>
                                <g:else>
                                    <g:each var="option" in="${question.options.sort { it.id }}" status="m">
                                        <g:radio name="question${question.id}" value="${option.id}" required=""/>
                                        <label for="question${question.id}_${option.id}">${option.answer}</label>
                                        <p></p>
                                    </g:each>
                                </g:else>
                                <p></p>
                            </div>
                        </g:each>
                        <g:hiddenField name="surveyId" value="${survey.id}"/>
                        <g:hiddenField name="trainingSetId" value="${trainingSetId}"/>
                        <g:hiddenField name="assignmentId" value="${assignmentId}"/>
                        <g:submitButton name="submit" class="btn btn-success" value="Submit"/>
                    </g:form>
                </div>
            </section>
        </div>
    </div>
</g:applyLayout>
