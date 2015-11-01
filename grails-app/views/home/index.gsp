<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <h1>Rooms</h1>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>

            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <g:each in="${rooms}" var="room">
                            <div class="col-md-4">
                                <div class="box box-success box-solid">
                                    <div class="box-header with-border">
                                        <h3 class="box-title">${room.name}</h3>

                                        <div class="box-tools pull-right"></div>
                                    </div>

                                    <div class="box-body">
                                        <loom:progressBar userMaxCount="${room.userMaxCount}"
                                                          userCount="${room?.users?.size()}"/>
                                        <g:if test="${room.users.size() != room.userMaxCount}">
                                            <g:link controller="home" action="joinRoom" params="[id: room.id]"
                                                    class="btn btn-block btn-success">Join</g:link>
                                        </g:if>
                                    </div>
                                </div>
                            </div>
                        </g:each>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>
        </div>
    </div>
</g:applyLayout>