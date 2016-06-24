<g:hiddenField id="roundNumber" name="roundNumber" value="${round}"/>
<g:hiddenField name="experimentDuration"
               value="${timeRemaining}"/>
<g:hiddenField id="paused" name="paused" value="${paused}"/>
<div class="row">
    <div class="col-xs-1"></div>

    <div class="col-xs-10">
        <ul class="nav nav-tabs" id="neighbors">
            <g:each in="${neighborState}" var="n" status="i">
                <li class="${n.key != 1 ?: 'active'}">
                    <a href='#neighbour${n.key}' data-toggle='tab'>neighbour${n.key}</a>
                </li>
            %{--<loom:currentUserTab userKey="${user.key}"/>--}%
            </g:each>
        </ul>
    </div>

    <div class="col-xs-1"></div>
</div>

<div class="row">
    <div class="col-xs-1"></div>

    <div class="tab-content col-xs-10 table-bordered" id="dvSourceContainer">
        <g:each in="${neighborState}" var="n" status="i">

            <div class="tab-pane ${n.key != 1 ?: "active"}" id="neighbour${n.key}">
                <ul class="dvSource">
                    <g:each in="${n.value}" var="tt">
                        <li class="ui-state-default tile-available"
                            drag-id="${tt.id}">${tt.text}</li>
                    </g:each>
                </ul>
            </div>

        %{--<loom:currentUserTabContent userKey="${user.key}" userValue="${user.value}"/>--}%
        </g:each>
    </div>

    <div class="col-xs-1"></div>
</div>

