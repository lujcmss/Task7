<jsp:include page="header.jsp" />

    <div class="container">
    
      <div class="page-header">
        <h1>Buy Fund</h1>
      </div>
      <jsp:include page="error-list.jsp" />
    <div class="search-bar">
    <form class="navbar-form navbar-center" role="search">
		<div class="form-group">
    		<input type="text" class="form-control" placeholder="Fund Name or Ticker" name="search">
  		</div>
  		<button type="submit" class="btn btn-default">Search</button>
	</form>
	
	  <form class="form-signin" method="post" action="buyFund.do">
	  <input type="text" class="form-control" placeholder="Fund Name or Ticker" name="fund">
        <input type="text" class="form-control" placeholder="Purchase Amount" required autofocus name="amount">
        <p></p>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Buy</button>
      </form>
	</div>

    </div> <!-- /container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="/Task7/js/bootstrap.min.js"></script>
  </body>
</html>
