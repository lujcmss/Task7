<jsp:include page="header.jsp" />

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="container">
	<div class="page-header">
		<h1>
			Welcome
			<c:out value="${user.screen_name}" />
		</h1>
	</div>
	<jsp:include page="success-list.jsp" />
	
	<div id="imagecontainer">
	</div>
</div>
<!-- /container -->


<!-- Bootstrap core JavaScript
    ================================================== -->
<jsp:include page="flickrPicsforHome.jsp" />
<!-- Placed at the end of the document so the pages load faster -->
</body>
</html>