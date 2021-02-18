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
                        The game proceeds in timed rounds. In each round, try to make the best story you can using small phrases
                        you and your neighbors have. There is one correct story, and all the phrases you find will be part of it.
                    </p>

                    <asset:image src="instruction2.png" alt="Loom" width="95%" class="center-block"/>


                    <p>
                        The main way to get new phrases is to drag them from your neighbors into your own story area (see above).  <b>But don't drag
                        phrases into your story if you don't know where they go!</b> Phrases that are out of order lower your score.
                    </p>

                    <p>
                        For example...
                    </p>

                    <asset:image src="instruction3.png" alt="Loom" width="500px" class="center-block"/>


                    <h1 class="text-center">Training</h1>

                    <p>
                        In the following screens you will be given some sample problems so you can get used to the interface.
                        After the training has been satisfactorily completed, you will be granted a qualification that let's you
                        participate in actual games; games will be posted as seperate HITs.
                    </p>

                    <p>
                        Good luck and have fun!
                    </p>

                    <p class="lead text-center">
                        <em><a href="${trainingId}?begin=true">Continue--&gt;</a></em>
                    </p>

                </div>
            </div>
        </section>
    </div>
</div>

</body>
</html>