<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="game_state" scope="request" class="com.bpodgursky.hubris.universe.GameState"/>
<jsp:include page="/common/header.jsp"/>

<div id="game-container">
<div class="well" id="game-controller">
<h3>Controls</h3>
<h4>Syncs</h4>
<p>When enabled, Hubris will persist game states on a regular basis.</p>
<c:choose>
  <c:when test="${has_sync}">
    <button id="syncs-btn" type="button" class="btn active">
      <i class="icon-repeat"></i>
      <span>Disable syncs</span>
    </button>
  </c:when>

  <c:otherwise>
    <button id="syncs-btn" type="button" class="btn">
      <i class="icon-repeat"></i>
      <span>Enable syncs</span>
    </button>
  </c:otherwise>
</c:choose>
</div>
</div>

<script type="text/javascript">
$(function() {

var hubrisGameState = ${game_state};
var spacecaseCanvas = spacecase();
spacecaseCanvas.update(hubrisGameState);

$('#syncs-btn').click(function() {
  var $this = $(this);

  $.get('?syncs=' + ((!$(this).hasClass('active')) ? 1 : 0),
    function() {
      $this.toggleClass('active');
      $('span', $this).html(($this.hasClass('active') ? 'Disable' : 'Enable') + ' syncs');
    });

});

});
</script>

<jsp:include page="/common/footer.jsp"/>