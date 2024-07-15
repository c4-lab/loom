<%@ page import="edu.msu.mi.loom.UserSession; edu.msu.mi.loom.User" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-1"></div>

                    <div class="col-md-10">
                        <g:if test="${sessionList.isEmpty()}">
                            <h1>No sessions are currently available.</h1>
                        </g:if>
                        <g:else>
                            <h1>Available Sessions</h1>
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th>Session ID</th>
                                    <th>State</th>
                                    <th>Qualified</th>
                                    <th>Link</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each var="item" in="${sessionList}">
                                    <tr>
                                        <td>${item.session.id}</td>
                                        <td>${item.session.state}</td>
                                        <td>${(item.qualified || item.canjoin) ? 'Yes' : 'No'}</td>
                                        <g:if test = "${item.canjoin || (item.qualified && item.session.state == edu.msu.mi.loom.Session.State.WAITING)}">
                                            <td><a href="${createLink(uri: item.link)}">Join Session</a></td>
                                        </g:if>
                                        <g:elseif test = "${!item.canjoin}">
                                            <td>Session full</td>
                                        </g:elseif>
                                        <g:else>
                                            <td>Unavailable</td>
                                        </g:else>
                                    </tr>
                                </g:each>
                                </tbody>
                            </table>
                        </g:else>


                    </div>
                    <div class="col-md-1"></div>
                </div>
            </section>


        </div>
    </div>

    <script type="text/javascript">



        jQuery(document).ready(function () {
           shouldLogout = true;
            logout();
        });
    </script>
</g:applyLayout>