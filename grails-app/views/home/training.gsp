<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <h1>${training.name}</h1>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>

            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <div class="box box-success box-solid">
                            <div class="box-header with-border">
                                <h3 class="box-title">Story</h3>
                            </div>

                            <div class="box-body">
                                <div class="row">
                                    <div class="col-md-1"></div>

                                    <div class="col-md-10 table-bordered" style="min-height: 200px !important;">
                                        <ul id="dvSource">
                                            <g:each in="${training.stories.getAt(0).tails}" var="tail">
                                                <li class="ui-state-default">${tail.text}</li>
                                            </g:each>
                                        </ul>
                                    </div>

                                    <div class="col-md-1"></div>
                                </div>
                                <hr/>

                                <div class="row"></div>

                                <div class="row">
                                    <div class="col-md-1"></div>

                                    <div class="col-md-10 table-bordered ui-widget-content" id="dvDest">
                                        <ul style="min-height: 200px !important;">
                                            <li class="placeholder">Add tails here</li>
                                        </ul>
                                    </div>

                                    <div class="col-md-1"></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>
        </div>
    </div>
</g:applyLayout>