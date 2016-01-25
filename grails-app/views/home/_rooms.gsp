<%@ page import="edu.msu.mi.loom.UserRoom" %>
<g:each in="${rooms}" var="room">
    <div class="col-md-4">
        <div class="box box-success box-solid">
            <div class="box-header with-border">
                <h3 class="box-title">${room.name}</h3>

                <div class="box-tools pull-right"></div>
            </div>

            <div class="box-body">
                <loom:progressBar userMaxCount="${room.userMaxCount}"
                                  userCount="${UserRoom.countByRoomAndUserAliasIsNotNull(room)}"/>
                <g:if test="${UserRoom.countByRoomAndUserAliasIsNotNull(room) != room.userMaxCount}">
                    <g:if test="${!loom.checkCurrentUserAndRoomConnection([room: room.id])}">
                        <g:link controller="home" action="joinRoom" params="[id: room.id]"
                                class="btn btn-block btn-success">Join</g:link>
                    </g:if>
                    <g:else>
                        <g:link controller="home" action="rejoinRoom" params="[id: room.id]"
                                class="btn btn-block btn-success">Rejoin</g:link>
                    </g:else>
                </g:if>
            </div>
        </div>
    </div>
</g:each>