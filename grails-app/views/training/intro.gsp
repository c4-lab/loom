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
                        <h1 class="text-center">Welcome to the Story Loom!</h1>

                        <p>
                            The Story Loom is a game you play with other people. The goal is to construct the
                            correct story using phrases you and your neighbors are given. Your score is based on how good your
                            story is in each round, and you will get a bonus based on your score.
                        </p>
                    </div>

                    <p>
                        The Story Loom is like a word-jumble, but instead of reordering letters to make words, you will reorder
                        short phrases to make stories.  For example...
                    </p>

                    <asset:image src="instruction1.png" alt="Loom" height="95" class="center-block"/>

                    <p>
                        Watch the video below to see how to play the game.
                    </p>
                    <iframe width="100%" height="700" src="https://www.youtube.com/embed/Ve1FerMPYbA" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

                    <h1 class="text-center">Training</h1>

                    <p>
                        Once you're finished with the above video, continue on to the next screen to try it out!
                    </p>

                    <p class="lead text-center">


                    <g:form controller="training" action="submitIntro">
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