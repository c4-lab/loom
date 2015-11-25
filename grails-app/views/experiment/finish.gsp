<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="experiment-content-wrapper">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <g:render template="alert_finish"/>
                        <h1 id="experiment-name">${experiment.name}</h1>
                        <g:hiddenField name="experiment" value="${experiment.id}"/>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>
        </div>
    </div>
</g:applyLayout>