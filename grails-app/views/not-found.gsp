<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <div class="row">
                <div class="col-sm-1"></div>

                <div class="col-sm-10">

                    <section class="content">
                        <g:if test="${flash.message}">
                            <p>
                                ${flash.message}
                            </p>


                        </g:if>
                        <g:else>
                        <div class="err-container">

                            <div class="text-center err-message">
                                <h2>Page unavailable?</h2>
                            </div>

                            <div class="err-body">
                                In order to access this site you <b>must</b> use the unique url that was provided to you by the task requester.
                            If you feel you have reached this page in error, please email <a
                                    href="mailto:jintrone@msu.edu">jintrone@msu.edu</a>.
                            </div>

                        </div>
                        </g:else>

                    </section>

                    <div class="col-sm-1"></div>
                </div>
            </div>
        </div>

    </div>
</g:applyLayout>