<g:hiddenField id="roundNumber" name="roundNumber" value="${round}"/>
<g:hiddenField name="experimentDuration"
               value="${timeRemaining}"/>
<g:hiddenField id="roundStart" name="roundStart"
               value="${startTime}"/>
<g:hiddenField id="currentRoundDuration" name="currentRoundDuration"
               value="${roundDuration}"/>
<g:hiddenField id="serverTime" name="serverTime"
               value="${serverTime}"/>
<g:hiddenField id="paused" name="paused" value="${paused}"/>

<div class="row center-block">

    <div class="col-xs-11 col-centered">
        <ul class="nav nav-tabs" id="neighbors">
            <g:each in="${neighborState}" var="user">

                <li>
                    <a href="#neighbour${user.key}"
                       data-toggle="tab">${"neighbour " + user.key}</a>
                </li>

            </g:each>
        </ul>
    </div>
</div>

<!---  THIS IS THE NEIGHBOR INFO ------>
<div class="row center-block ">

    <div class="tab-content col-xs-11 table-bordered dvSourceContainer col-centered">
        <g:each in="${neighborState}" var="user">
            <div class="tab-pane"
                 id="neighbour${user.key}">
                <g:if test="${user.value.size() == 0}">
                 <div class="center-block">
                     <h1>Neighbor ${user.key} has not created a public story!</h1>
                 </div>
                </g:if>
                <g:else>
                <ul style="min-height: 200px !important;" class="${uiflag == 1 ? "dvSource" : ""} originalstory g_list">
                    <g:each in="${user.value}" var="tt">
                        <li class="ui-state-default tile-available"
                            drag-id="${tt.id}"
                            nei-id="neighbour${user.key}"><div class="drag-item-text">${raw(tt.text)}</div></li>
                    </g:each>
                </ul>
                </g:else>
            </div>

        </g:each>
    </div>
</div>

<script type="text/javascript">
    if (activeTabGlobal === "") {
        setActiveTab($('.nav-tabs li:first-child a').attr('href').substring(1));
    } else {
        setActiveTab(activeTabGlobal);
    }
</script>





