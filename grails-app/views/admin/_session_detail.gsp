<%@ page import="edu.msu.mi.loom.Session" %>
<div id="session-info-${loomSession.id}" class="post session-info panel panel-info">

    <div class="panel-heading" role="tab" id="heading-${loomSession.id}">

        <h2 class="panel-title">
            <a role="button" data-toggle="collapse" href="#collapse-${loomSession.id}" aria-expanded="true"
               aria-controls="collapse-${loomSession.id}">
                Session: ${loomSession.name}
            </a>
        </h2>
    </div>

    <div id="collapse-${loomSession.id}" class="panel-collapse collapse in" role="tabpanel"
         aria-labelledby="heading-${loomSession.id}">

        <div class="row session-row">
            <div class="user-block col-md-5">
                <div class="panel panel-default session-internal-panel">
                    <!-- Panel Heading -->
                    <div class="panel-heading">
                        <h4 class="panel-title">

                            Info

                        </h4>
                    </div>
                    <!-- Panel Body -->

                    <div class="panel-body">
                        <span class='description'>Name:<span class="description-text">${loomSession.name}</span></span>
                        <span class='description'>Created:<span class="description-text created"><g:formatDate
                                format="yyyy/MM/dd HH:mm"
                                date="${loomSession.created}"/></span></span>
                        <span class='description'>Num participants:<span
                                class="description-text">${loomSession.sessionParameters.safeGetMinNode()} - ${loomSession.sessionParameters.safeGetMaxNode()}</span>
                        </span>
                        <span class='description'>Network:<span
                                class="description-text">${loomSession.sessionParameters.safeGetNetworkTemplate()}</span>
                        </span>
                        <span class='description'>Story:<span
                                class="description-text">${loomSession.sessionParameters.safeGetStory().name}</span>
                        </span>
                        <span class='description'>Type:<span
                                class="description-text">${loomSession.sessionParameters.safeGetSessionType()}</span>
                        </span>
                        <span class='description'>Rounds:<span
                                class="description-text">${loomSession.sessionParameters.safeGetRoundCount()}</span>
                        </span>
                        <span class='description'>Round duration:<span
                                class="description-text">${loomSession.sessionParameters.safeGetRoundTime()} seconds</span>
                        </span>
                        <span class='description'>URL:<span class="description-text url"><a
                                href="${request.getScheme()}://${request.getServerName()}:${request.getServerPort()}${request.contextPath}/session/s/${loomSession.id}">Session ${loomSession.id}</a>
                        </span></span>

                    </div>

                </div>
            </div>

            <div class="user-block col-md-4 session-dynamic-info">
                <div class="panel panel-default session-internal-panel">
                    <!-- Panel Heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">Dynamic</h3>
                    </div>
                    <!-- Panel Body -->
                    <div class="panel-body">
                        <span class='description'>Status:<span
                                class="description-text  session-status">${loomSession.state ?: "INACTIVE"}</span>
                        </span>

                        <div class="session-waiting-block hidden">
                            <span class='description'>Started:<span
                                    class="description-text session-wait-started"></span>
                            </span>
                            <span class='description'>Elapsed:<span
                                    class="description-text session-wait-elapsed"></span>
                            </span>
                            <span class='description'>Connected:<span
                                    class="description-text session-wait-connected"></span></span>
                            <span class='description'>Stopped:<span
                                    class="description-text session-wait-stopped"></span>
                            </span>
                            <span class='description'>Missing:<span
                                    class="description-text session-wait-missing"></span>
                            </span>
                            <span class='description'>Thread Status:<span
                                    class="description-text session-wait-thread"></span>
                            </span>
                        </div>

                        <div class="session-active-block hidden">
                            <span class='description'>Started:<span
                                    class="description-text session-active-started"></span>
                            </span>
                            <span class='description'>Round:<span
                                    class="description-text session-active-round"></span>
                            </span>
                            <span class='description'>Connected:<span
                                    class="description-text session-active-connected"></span>
                            </span>
                            <span class='description'>Missing:<span
                                    class="description-text session-active-missing"></span>
                            </span>
                            <span class='description'>Round Status:<span
                                    class="description-text session-active-round-status"></span></span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="user-block col-md-3 session-actions">
                <div class="panel panel-default session-internal-panel">
                    <!-- Panel Heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">Action</h3>
                    </div>
                    <!-- Panel Body -->
                    <div class="panel-body">
                        <span class="session-id hidden">${loomSession.id}</span>

                        <button class="btn btn-primary full-width-btn show-session-launch-modal"  <%= loomSession.state != Session.State.PENDING ? 'disabled' : '' %>>Enable</button>
                        <button class="btn btn-primary full-width-btn  show-session-cancel" <%= loomSession.state in [Session.State.WAITING, Session.State.ACTIVE] ? '' : 'disabled' %>>Cancel</button>
                        <button class="btn btn-primary full-width-btn  show-session-delete" <%= loomSession.state in [Session.State.WAITING, Session.State.ACTIVE] ? 'disabled' : '' %>>Delete</button>
                        <button class="btn btn-primary full-width-btn  show-session-clone">Clone</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

