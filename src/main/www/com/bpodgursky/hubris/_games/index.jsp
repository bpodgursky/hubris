<jsp:include page="/common/header.jsp"/>

<div id="page-container">
<h2> Games </h2>
<div id="game-index" class="well">
<img src="resources/img/load.gif" />
<h4>Loading...</h4>
</div>

<script type="text/javascript">
$(function() {

$.get('/games/index',
  function(data) {
    $('#game-index').html(data);
  });
});
</script>
</div>

<jsp:include page="/common/footer.jsp"/>
