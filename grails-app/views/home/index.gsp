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

                    <div class="col-md-10" id="roomsTemplate">
                        <g:render template="rooms" model="[rooms: rooms]"/>
                    </div>

                    <div class="col-md-1"></div>
                </div>
            </section>
        </div>
    </div>

    <script type="text/javascript">
        jQuery(document).ready(function () {
            setInterval(function () {
                jQuery.ajax({
                    url: "/loom/home/updateRooms",
                    type: 'POST',
                    data: {}
                }).success(function (data) {
                    jQuery("#roomsTemplate").html(data);
                }).error(function () {
                });
            }, 5000);
        });
    </script>
</g:applyLayout>