<%@ page import="edu.msu.mi.loom.Session; java.time.format.DateTimeFormatter" %>
<g:applyLayout name="main">
    <div class="wrapper">
        <div class="content-wrapper container">
            <section class="content-header">
                <div class="row">
                    <div class="col-md-12">
                        <h1>Participation History</h1>
                        <g:if test="${participationCount == 0}">
                            <p>
                                You have not yet participated in any sessions.
                            </p>
                        </g:if>
                        <g:else>
                        <ul>
                            <g:each in="${participatedSessions}" var="session">
                                <li style="${session.state == Session.State.FINISHED ? 'color: green; font-weight: bold;' : ''}">
                                    <g:if test="${session.startWaiting}">
                                    <g:formatDate date="${session.startWaiting}"
                                                             format="MMM dd, yyyy 'at' HH:mm:ss z"
                                                             timeZone="${TimeZone.getTimeZone('EST')}"/>
                                </g:if>
                                -- Session status: ${session.state}
                                </li>
                            </g:each>
                        </ul>
                        </g:else>
                        <p>
                            <g:if test="${participationCount < 3}">
                                You can participate in ${3-participationCount} more sessions.
                            </g:if>
                            <g:else>
                                You have participated in three sessions, which is the maximum allowable at this point.  Thanks for playing!
                            </g:else>
                        </p>
                        <g:if test="${participationCount < 3}">
                        <h1>Available Sessions</h1>
                        <div id="session-list">
                            <!-- Session list will be dynamically updated here -->
                        </div>
                        </g:if>
                    </div>
                </div>
            </section>
        </div>
    </div>
    <g:if test="${participationCount < 3}">
        <style>
        /* Increase specificity and use !important for higher priority */
        .table-striped > tbody > tr.available-session {
            background-color: rgb(255, 230, 64) !important;
        }
        /* Ensure hover effect doesn't override our custom background */
        .table-striped > tbody > tr.available-session:hover {
            background-color: #ffe640 !important;
        }
        </style>
    <script>
        function updateSessionList() {
            $.ajax({
                url: "${createLink(controller: 'session', action: 'available')}",
                dataType: 'json',
                success: function(data) {
                    var sessionsByDate = groupSessionsByDate(data);
                    var html = '';

                    for (var date in sessionsByDate) {
                        html += '<h2>' + date + '</h2>';
                        html += '<table class="table table-striped">';
                        html += '<thead><tr><th>Session ID</th><th>State</th><th>Time (ET)</th><th>Qualified</th><th>Action</th></tr></thead>';
                        html += '<tbody>';

                        sessionsByDate[date].forEach(function(session) {
                            html += '<tr class="' + (session.link ? 'available-session' : '') + '">' +

                                '<td>' + session.id + '</td>' +
                                '<td>' + session.state + '</td>' +
                                '<td>' + session.scheduledTime + '</td>' +
                                '<td>' + (session.qualified ? 'Yes' : 'No') + '</td>' +
                                '<td>' + (session.link ? '<a href="' + session.link + '">Join Session</a>' : session.message) + '</td>' +
                                '</tr>';
                        });

                        html += '</tbody></table>';
                    }

                    $('#session-list').html(html);
                }
            });
        }

        function groupSessionsByDate(sessions) {
            let groupedSessions = {};
            let today = new Date().toISOString().split('T')[0];

            sessions.forEach(function(session) {
                let dateKey = session.scheduledDate === today ? 'Today' : session.scheduledDate;

                if (!groupedSessions[dateKey]) {
                    groupedSessions[dateKey] = [];
                }
                groupedSessions[dateKey].push(session);
            });

            return groupedSessions;
        }

        // Initial load

        updateSessionList();

        // Refresh every 10 seconds
        setInterval(updateSessionList, 10000);


    </script>
    </g:if>
</g:applyLayout>
