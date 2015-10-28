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
                <div class="col-md-2"></div>

                <div class="col-md-8">
                    <g:each in="${rooms}" var="room">
                        <div class="col-md-5">
                            <div class="box box-success box-solid">
                                <div class="box-header with-border">
                                    <h3 class="box-title">${room.name}</h3>

                                    <div class="box-tools pull-right">
                                        <button class="btn btn-box-tool" data-widget="remove"><i
                                                class="fa fa-times"></i>
                                        </button>
                                    </div><!-- /.box-tools -->
                                </div><!-- /.box-header -->
                                <div class="box-body">
                                    The body of the box
                                </div><!-- /.box-body -->
                            </div><!-- /.box -->
                        </div>
                    </g:each>
                </div>

                <div class="col-md-2"></div>
            </section>
        </div>
    </div>
</g:applyLayout>