<%--
  Created by IntelliJ IDEA.
  User: josh
  Date: 5/19/16
  Time: 7:42 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Introduction to the Story Loom</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="wrapper">
    <div class="content-wrapper container">
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <div class="jumbotron">
                        <h1 class="text-center">Playing the Story Loom game with others</h1>

                        <p>
                            Congrats on making it through the first tutorial!  During an actual game, you will play with other people. In addition to
                            your private info, you will also be able to use information from your neighbors.  You will be able to see their stories and
                            and drag phrases from their stories into your own. Your neighbors will have information you don't have, so
                            be sure to keep an eye on their stories!
                        </p>
                    </div>

                    <p>
                        The following video will help you learn how to play the game with others.  After the video, you'll be able to try out the game with
                        simulated players.  The simulation only lasts for a few rounds, but an actual game will be 10 - 12 rounds.
                    </p>
                    <iframe width="100%" height="1100" src="https://www.youtube.com/embed/rG55ocRfMuk" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>



                    <h1 class="text-center">Try it out!</h1>

                    <p>
                        On the following screen, you will play a three round game with other players.  Do your best!  You will receive a summary of your scores at the end.
                    </p>

                    <p>
                        Good luck and have fun!
                    </p>

                    <p class="lead text-center">


                    <g:form controller="training" action="submitSimIntro">
                        <g:hiddenField name="assignmentId" value="${assignmentId}"/>
                        <g:hiddenField name="trainingSetId" value="${trainingSetId}"/>
                        <g:submitButton name="continue" class="btn btn-success" value="Continue"/>
                    </g:form>

                    </p>

                </div>
            </div>
        </section>
    </div>
</div>

</body>
</html>