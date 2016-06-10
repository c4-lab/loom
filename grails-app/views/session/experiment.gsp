<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="experiment-content-wrapper">
            <g:render template="experiment_content"
                      model="[userList: userList, session: session, roundNbr: roundNbr, timeRemaining: timeRemaining]"/>
        </div>
    </div>
    <script type="text/javascript">
        jQuery(document).ready(function () {
            shouldLogout = true;
            window.onbeforeunload = logout;

        });
    </script>
</g:applyLayout>