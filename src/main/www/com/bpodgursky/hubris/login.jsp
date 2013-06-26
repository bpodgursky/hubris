<jsp:include page="common/header.jsp"/>

<div id="page-container">
<h2> Login </h2>
<div class="well">
<form action="/login" method="post">
  <input type="hidden" name="state" value="login"/>

  <div>
    <input type="text" name="username" placeholder="Username" />
  </div>
  <div>
    <input type="password" name="password" placeholder="Password" />
  </div>

  <div>
    <input type="submit" class="btn btn-primary" name="submit" value="Login" />
  </div>
</form>
</div>

</div>

<jsp:include page="common/footer.jsp"/>
