<g:hiddenField id="roundNumber" name="roundNumber" value="${round}"/>
<g:hiddenField name="experimentDuration"
               value="${timeRemaining}"/>
<g:hiddenField id="paused" name="paused" value="${paused}"/>

<div class="row center-block">

    <div class="col-xs-11 col-centered">
        <ul class="nav nav-tabs" id="neighbors">
            <g:each in="${neighborState}" var="user">

                <li class="${user.key == 1 ?"active": ""}">
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
                <div class="tab-pane ${user.key != 2 ?: "active"}"
                     id="neighbour${user.key}">
                    <ul style="min-height: 200px !important;" class="${uiflag == 1?"dvSource":""} originalstory g_list">
                        <g:each in="${user.value}" var="tt">
                            <li class="ui-state-default tile-available"
                                drag-id="${tt.id}"
                                nei-id="neighbour${user.key}">${raw(tt.text)}</li>
                        </g:each>
                    </ul>
                </div>

        </g:each>
    </div>
 </div>



