<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <div class="row">
                <div class="col-sm-1"></div>

                <div class="col-sm-10">

                    <section class="content">

                        <div class="err-container">

                            <div class="text-center err-message">
                                <h2>Session unavailable</h2>
                            </div>


                            <div class="err-body">
                                <g:if test="${flash.message}">
                                        ${flash.message}
                                </g:if>
                                If you feel you have reached this page in error, please email <a
                                    href="mailto:sunyqs@gmail.com">sunyqs@gmail.com</a>.
                            </div>

                        </div>


                    </section>

                    <div class="col-sm-1"></div>
                </div>
            </div>
        </div>

    </div>
</g:applyLayout>