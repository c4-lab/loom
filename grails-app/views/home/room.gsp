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
                                <h3 class="box-title">Turn 1</h3>

                                <div class="box-tools pull-right">Time remaining:</div>
                            </div>

                            <div class="box-body">
                                <div class="row">
                                    <div class="col-md-3 border-right">
                                        <g:each in="${room?.users}" var="user">
                                            <button class="btn btn-block btn-info">${user.alias}</button>
                                        </g:each>
                                    </div>

                                    <div class="col-md-9 border-left"></div>
                                </div>

                                <div class="row"></div>
                                %{--<loom:progressBar userCount="${room?.users?.size()}"--}%
                                %{--userMaxCount="${room.userMaxCount}"/>--}%
                                %{--<g:link controller="home" action="stopWaiting" params="[id: room.id]"--}%
                                %{--class="btn btn-block btn-success">Stop waiting</g:link>--}%
                            </div>
                        </div>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>
        </div>
    </div>
</g:applyLayout>