<%--
  Created by IntelliJ IDEA.
  User: josh
  Date: 5/8/16
  Time: 7:36 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Training Complete</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="wrapper">
    <div class="content-wrapper container">
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <p>
                        Congratulations, you have completed your training and will shortly be granted a qualification allowing you to participate in Loom games. Your confirmation code is:
                    </p>



                    <h1 class='text-center'>${confirmationCode}</h1>


                    <p>
                        Please enter your confirmation code into the HIT on the Mturk site to receive credit!!!
                    </p>

                    <p>
                        Please watch for new Loom games!  Note if at any time, your browser crashes or
                        you experience a problem with the site, you will need to re-connect using the original url (which has your worker id in it).
                    </p>

                    <p>
                        Please contact the requester, Joshua Introne, via email at <a
                            href="mailto:jintrone@msu.edu">jintrone@msu.edu</a> if you have any questions or concerns.
                    </p>

                </div>
            </div>
        </section>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        shouldLogout = true;
        logout();
    });
</script>

</body>


</html>