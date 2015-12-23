<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="experiment-content-wrapper">
            <g:render template="/home/experiment_content"
                      model="[userList: userList, experiment: experiment, roundNbr: roundNbr]"/>
        </div>
    </div>
    <script type="text/javascript">
        var timeout;
        $(window).on('beforeunload', function () {
            if (jQuery("#expTemplate").length > 0) {
                before = new Date();
                timeout = setTimeout(function () {
                    after = new Date();
                    clearInterval(int);
                    calculateTime();
                }, 10);
                return "Your work will be lost.";
            }
        });
    </script>
</g:applyLayout>