<%@tag description="Cryptic Page template" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="description" content="A cryptic crossword solver">
<meta name="author"
	content="Leanne Butcher, Luke Hackett, Stuart Leader, Mohammad Rahman">
<title>Cryptic Crossword Solver</title>
<link rel="apple-touch-icon" href="images/icons/rounded/AppIcon.png" />
<link rel="apple-touch-icon" sizes="72x72"
	href="images/icons/rounded/AppIcon72x72.png" />
<link rel="apple-touch-icon" sizes="114x114"
	href="images/icons/rounded/AppIcon57x57.png" />
<link rel="apple-touch-icon" sizes="144x144"
	href="images/icons/rounded/AppIcon72x72@2x.png" />
<!-- Bootstrap core CSS -->
<link rel="stylesheet" href="css/bootstrap.min.css" />
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/bootstrap.hidenseek.js"></script>
<script src="js/jquery.autotab.min.js"></script>
<script src="js/jquery.cryptic.dynobox.js"></script>
<script src="js/jquery.cryptic.form.js"></script>
<script src="js/cryptic.js"></script>
</head>
<body>
	<div id="container" class="container">
		<!-- Static navbar -->
		<div class="navbar navbar-cryptic" role="navigation">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse"
						data-target=".navbar-collapse">
						<span class="sr-only">Toggle navigation</span> <span
							class="glyphicon glyphicon-th-list"></span>
					</button>
					<a class="navbar-brand" href="index.jsp">Cryptic Crossword
						Solver</a>
				</div>
				<div class="navbar-collapse collapse">
					<ul class="nav navbar-nav navbar-right">
						<li><a href="index.jsp">Home</a></li>
						<li><a href="solver.jsp">Solver</a>
						<li><a href="help.jsp">Help</a>
						<li><a href="changelog.jsp">Changelog</a></li>
					</ul>
				</div>
			</div>
		</div>
		<!-- Main Body -->
		<jsp:doBody />
	</div>
</body>
</html>
