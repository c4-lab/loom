<html>
<head>
    <title>Training</title>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="wrapper">
    <div id="training-content-wrapper">
        <g:render template="content"
                  model="[trainingSet: trainingSet, training: training, storyTiles: storyTiles, allTiles: allTiles, roomUrl: roomUrl, uiflag: uiflag, assignmentId: assignmentId]"/>
    </div>
</div>
</body>
</html>