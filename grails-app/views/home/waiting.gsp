<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <h1>${room.name}</h1>
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
                                <h3 class="box-title">${room.name}</h3>

                                <div class="box-tools pull-right"></div>
                            </div>

                            <div class="box-body">
                                <loom:progressBar userCount="${room?.users?.size()}"
                                                  userMaxCount="${room.userMaxCount}"/>
                                <g:link controller="home" action="stopWaiting" params="[id: room.id]"
                                        class="btn btn-block btn-success">Stop waiting</g:link>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>
        </div>
    </div>
</g:applyLayout>