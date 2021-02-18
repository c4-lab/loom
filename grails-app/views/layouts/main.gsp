<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="Loom"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">

    <asset:stylesheet src="bootstrap.css"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
    <asset:stylesheet src="application.css"/>
    <asset:stylesheet src="blue.css"/>
    <asset:stylesheet src="_all-skins.min.css"/>
    <asset:stylesheet src="morris.css"/>
    <asset:stylesheet src="dragndrop.css"/>
    <asset:stylesheet src="jquery-ui.css"/>
    <asset:stylesheet src="bootstrap.vertical-tabs.css"/>

    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>

    <asset:javascript src="jquery-1.11.3.js"/>
    <asset:javascript src="jquery-ui.js"/>
    <asset:javascript src="bootstrap.min.js"/>
    <asset:javascript src="jquery.blockUI.js"/>
    <asset:javascript src="application.js"/>
    <asset:javascript src="loom.js"/>
    <g:layoutHead/>
</head>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

    <header class="main-header">
        <nav class="navbar navbar-static-top" role="navigation">

                <asset:image src="loom-icon.png" alt="Loom" height="95" class="center-block"/>
                <sec:ifAllGranted roles="ROLE_ADMIN">

                        <p class="text-center"><g:link controller="logout">Logout</g:link></p>

                </sec:ifAllGranted>

                %{--<div class="navbar-custom-menu">--}%
                    %{--<ul class="nav navbar-nav">--}%
                        %{--<sec:ifLoggedIn>--}%
                            %{--<li>--}%

                            %{--</li>--}%
                        %{--</sec:ifLoggedIn>--}%
                    %{--</ul>--}%
                %{--</div>--}%

        </nav>
    </header>
</div>
<g:layoutBody/>
<footer class="main-footer">
    <div class="pull-right hidden-xs">
        <b>Version</b> 1.0.0
    </div>
    <strong>Copyright &copy; 2016 <a href="javascript:void(0);">Loom</a>.
    </strong> All rights reserved.
</footer>
</body>
</html>
