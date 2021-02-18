<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <div class="row">
                <div class="col-sm-1"></div>

                <div class="col-sm-10">

                    <section class="content">
                        <p class="text-center">
                            Sorry, this session is full! Note, this can happen even if you made it into the waiting room. Please look for more LOOM HITs in the future.
                        </p>
                        <g:if test="${params.code}">
                            <p class="text-center"> Please enter the following code into your HIT to receive credit for trying!</p>
                            <h1 class="text-center">${params.code}</h1>


                        </g:if>
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