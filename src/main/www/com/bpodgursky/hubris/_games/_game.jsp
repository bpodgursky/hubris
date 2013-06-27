<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="game_state" scope="request" class="com.bpodgursky.hubris.universe.GameState"/>
<jsp:include page="/common/header.jsp"/>

<div id="game-container">
<div class="well" id="game-controller">
<h3>Controls</h3>
<h4>
  <i class="icon-repeat"></i>
  Syncs
</h4>
<p>When enabled, Hubris will persist game states on a regular basis.</p>
<c:choose>
  <c:when test="${has_sync}">
    <button id="syncs-btn" type="button" class="btn active">
      <i class="icon-repeat"></i>
      <span>Syncs enabled</span>
    </button>
  </c:when>

  <c:otherwise>
    <button id="syncs-btn" type=backwar"button" class="btn">
      <i class="icon-repeat"></i>
      <span>Syncs disabled</span>
    </button>
  </c:otherwise>
</c:choose>
<p>&nbsp;</p>
<h4>
  <i class="icon-time"></i>
  History
</h4>
  <div id="history-ctl-info">
  <img src="/resources/img/load.gif" alt="Loading"/> Loading...
  </div>

  <div id="history-ctl">
    <div class="btn-toolbar">
      <div class="btn-group">
        <a href="#" id="history-ctl-first" class="btn"><i class="icon-fast-backward"></i></a>
        <a href="#" id="history-ctl-back" class="btn"><i class="icon-backward"></i></a>
        <a href="#" id="history-ctl-stop" class="btn"><i class="icon-stop"></i></a>
        <a href="#" id="history-ctl-play" class="btn"><i class="icon-play"></i></a>
        <a href="#" id="history-ctl-forward" class="btn"><i class="icon-forward"></i></a>
        <a href="#" id="history-ctl-last" class="btn"><i class="icon-fast-forward"></i></a>
      </div>
    </div>
    <div id="history-slider"></div>
  </div>
</div>
</div>

<script type="text/javascript">
$(function() {

var hubrisGameState = ${game_state}
  , spacecaseCanvas = spacecase()
  , fullGameStateHistory
  , currentIndex = 0
  , playMovie = false;

spacecaseCanvas.update(hubrisGameState);

function updateState(index) {
  spacecaseCanvas.update(fullGameStateHistory[index]);
  currentIndex = index;
  $('#history-slider').slider('value', currentIndex);
}

function startMovie() {
  playMovie = true;
  play();
}

function stopMovie() {
  playMovie = false;
}

function play() {
  if ((currentIndex < (fullGameStateHistory.length - 1)) && playMovie) {
    updateState(currentIndex + 1);
    setTimeout(play, 100);
  }
  else if (playMovie) {
    stopMovie();
  }
}

$('#history-ctl-first').click(function() { updateState(0); return false; });
$('#history-ctl-back').click(function() { updateState(currentIndex - 1); return false; });
$('#history-ctl-stop').click(function() { stopMovie(); return false; });
$('#history-ctl-play').click(function() { startMovie(); return false; });
$('#history-ctl-forward').click(function() { updateState(currentIndex + 1); return false; });
$('#history-ctl-last').click(function() { updateState(fullGameStateHistory.length - 1); return false; });

$('#syncs-btn').click(function() {
  var $this = $(this);

  $.get('?syncs=' + ((!$(this).hasClass('active')) ? 1 : 0),
    function() {
      $this.toggleClass('active');
      $('span', $this).html('Syncs ' + ($this.hasClass('active') ? 'enabled' : 'disabled'));
    });

});

$.getJSON('/games/states_batch/?gameId=' + hubrisGameState.gameData.gameNumber,
  function(data) {
    fullGameStateHistory = data;
    fullGameStateHistory.push(hubrisGameState);
    currentIndex = fullGameStateHistory.length - 1;

    $('#history-ctl-info').html('<p>There are ' + fullGameStateHistory.length + ' snapshots available.');
    $('#history-ctl').show();
    $('#history-slider').slider({
      value: fullGameStateHistory.length - 1,
      min: 0,
      max: fullGameStateHistory.length - 1
    });
  });

});
</script>

<jsp:include page="/common/footer.jsp"/>