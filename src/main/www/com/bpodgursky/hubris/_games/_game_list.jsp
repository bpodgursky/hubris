<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="active_games" scope="request" class="java.util.List<com.bpodgursky.hubris.account.GameMeta>"/>

<ol>
  <c:forEach items="${active_games}" var="entry">
    <li> <a href="/games/${entry.id}">${entry.name}</a> </li>
  </c:forEach>
</ol>
