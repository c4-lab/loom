<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <div class="row">
                <div class="col-sm-1"></div>

                <div class="col-sm-10">

                    <section class="content">
                        <p class="text-center">
                            Sorry, this session is now done<g:if test="${params.code}">, but you get credit for trying!
                            Please enter the following code into your HIT to receive credit.</p>
                            <h1 class="text-center">${params.code}</h1></g:if><g:else>!</p></g:else>
                        <p class="text-center">
                            If you feel you have reached this page in error, please email <a
                                href="mailto:jintrone@msu.edu">jintrone@msu.edu</a>.
                        </p>

                    </section>

                    <div class="col-sm-1"></div>
                </div>
            </div>
        </div>

    </div>
</g:applyLayout>