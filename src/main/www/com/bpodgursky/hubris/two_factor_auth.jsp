<jsp:useBean id="uuid" scope="request" class="java.lang.String"/>
<jsp:useBean id="username" scope="request" class="java.lang.String"/>
<jsp:include page="common/header.jsp"/>

<div id="page-container">

<h2> Two-Factor Authentication Required </h2>
<p>
You have two-factor authentication enabled. Please enter the two-factor authentication token to continue.
</p>

<div class="well">
<form action="/login" method="post">
  <input type="hidden" name="state" value="two_factor"/>
  <input type="hidden" name="uuid" value="${uuid}"/>
  <input type="hidden" name="username" value="${username}"/>

  <div>
    <input type="text" name="auth_token" placeholder="Two-Factor Auth Token" />
  </div>
  <div>
    <input type="submit" class="btn btn-primary" name="submit" value="Login" />
  </div>
</form>
</div>

</div>

<jsp:include page="common/footer.jsp"/>
