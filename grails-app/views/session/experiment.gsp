<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper" id="experiment-content-wrapper">
            <section class="content-header" id="expTemplate">
                <div class="row">
                    <div class="col-xs-1"></div>

                    <div class="col-xs-10">
                        <g:render template="/shared/alert-templates"/>
                        <h3 ><a id="help-link" href="#" onclick="toggleHelp()">Show Help</a></h3>

                        <g:hiddenField id="session" name="session" value="${loomSession.id}"/>

                    </div>

                    <div class="col-xs-1"></div>
                </div>
                <div class="row">
                    <div class="col-xs-1"></div>
                    <div id="help-text" class="col-xs-10" style="display:none;">
                        <ol>
                            <li>Drag items from your private area (bottom) or neighbors' stories (top) to "your story" area (middle) to create your story.</li>
                            <li>New items will appear in your neighbors' areas on each round as they construct their stories.</li>
                            <li>Try to build the best story you can with what you have. </li>
                            <li>Your score (and bonus) depends on the both the number of items you have and their order. Having <em>many items out of order</em>, might yield a lower score than <em>few items in order</em>.</li>
                            <li>If you encounter an error, first try reloading the page. If that doesn't work, please contact the experimenter!</li>
                        </ol>
                    </div>
                    <div class="col-xs-1"></div>
                </div>
            </section>

            <section class="content-header">
                <div class="row">
                    <div class="col-xs-1"></div>

                    <div class="col-xs-10">
                        <div class="box box-success box-solid">
                            <div class="box-header with-border">
                                <div class="row">
                                    <div class="col-xs-10">
                                        <h3 class="box-title">Round <span id="roundNumberTarget">...</span>
                                            of ${loomSession.sessionParameters.safeGetRoundCount()}
                                        </h3>
                                    </div>

                                    <div class="col-xs-2">
                                        <span id="timerPanel"></span>
                                    </div>
                                </div>
                            </div>

                            <div class="box-body">
                                <div id="neighborStoryContainer">
                                    <div id="neighborsStories">

                                        <g:render template="experiment_content"
                                                  model="[neighborState: neighborState,
                                                          round: round,
                                                          timeRemaining: timeRemaining,
                                                          loomSession  : loomSession,
                                                          paused: paused,
                                                          startTime: startTime,
                                                          roundDuraction: roundDuration,
                                                          serverTime: serverTime,
                                                          uiflag:uiflag]"/>
                                    </div>
                                </div>

                                <!---  THIS IS YOUR STORY ------>
                                <div class="row">
                                    <div class="col-xs-2 center-block">
                                        <p>Your story:</p>
                                    </div>
                                </div>

                                <div class="row center-block">

                                    <div class="col-xs-11 table-bordered ui-widget-content dvDest col-centered">
                                        <ul style="min-height: 200px !important;" id="sort2" class="${uiflag == 1?"dvSource":""}  g_list">

                                            <g:if test="${myState?.size() > 0}">
                                                <g:each in="${myState}" var="tt">
                                                    <li class="ui-state-default purple"
                                                        drag-id="${tt.id}"><div class="drag-item-text">${raw(tt.text)}</div></li>
                                                </g:each>
                                            </g:if>

                                        </ul>
                                    </div>

                                </div>

                                <!---  THIS IS YOUR PRIVATE INFO ------>
                                <div class="row col-centered">
                                    <div class="col-xs-4">
                                        <p>Your private info:</p>
                                    </div>
                                </div>



                                <div class="row">
                                    <div class="col-xs-11 table-bordered ui-widget-content dvSourceContainer col-centered">
                                        <ul style="min-height: 200px !important;" id="sort4" class="${uiflag == 1?"dvSource":""} g_list privateinfo">
                                            <g:each in="${myInitialState}" var="tt">
                                                <li class="ui-state-default tile-available"
                                                    drag-id="${tt.id}"><div class="drag-item-text">${raw(tt.text)}</div></li>
                                            </g:each>

                                        </ul>
                                    </div>

                                </div>



                                <hr/>

                            </div>
                        </div>
                    </div>

                    <div class="col-xs-1"></div>
                </div>

            </section>
        </div>
    </div>
    <script type="text/javascript">

        var activeTabGlobal = "";

        jQuery(document).ready(function () {
            document.title = "Session Active!"
            shouldLogout = true;
            window.onbeforeunload = logout;
            initMyDragNDrop();
            //removeTile();

            //Set the first tab as active if no tab is active.
            if (activeTabGlobal === "") {
                setActiveTab($('.nav-tabs li:first-child a').attr('href').substring(1));
            }

            //The following code is no longer necessary - setting the tab within the fragment appears to work
            // const container = document.getElementById('neighborStoryContainer');
            //
            // const observer = new MutationObserver((mutationsList) => {
            //     for(const mutation of mutationsList) {
            //         console.log("Checking mutations "+mutation)
            //         if (mutation.target.id === "neighborsStories") {
            //             console.log('Section replaced');
            //             setActiveTab(activeTabGlobal)
            //
            //         }
            //
            //     }
            // });

            //observer.observe(container, { childList: true, subtree: true });


        });

        function storeActiveTab() {
            var activeTab = $('.nav-tabs .active a');
            if (activeTab.length === 0) return null;
            var href = activeTab.attr('href');
            var result = href ? href.substring(1) : null; // Return the id without the '#'
            console.log("Returning active tab: "+result)
            activeTabGlobal= result;
        }

        function toggleHelp() {
            $("#help-text").toggle()
            if ($("#help-text").is(":visible")) {
                $("#help-link").text("Hide help")
            } else {
                $("#help-link").text("Show help")
            }
        }


        function setActiveTab(tabId) {

            console.log("Manually setting active tab: "+tabId)
            var tab = $('.nav-tabs a[href="#' + tabId + '"]');


            if (tab.length === 0) {
                console.log("No tab to set")
                return;
            }

            $('.nav-tabs [aria-expanded="true"]').attr('aria-expanded', 'false');
            $('.nav-tabs li').removeClass('active'); // remove active class from all tabs
            $('.tab-content .tab-pane').removeClass('active'); // remove active class from all panes

            tab.parent('li').addClass('active'); // add active class to the selected tab
            tab.parent('li').attr('aria-expanded', 'true');
            $('#' + tabId).addClass('active'); // add active class to the corresponding pane
        }



    </script>


</g:applyLayout>