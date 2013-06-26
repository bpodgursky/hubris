var spacecase = null;

(function($) { 

var VP_MAX_WIDTH = 1600;
var VP_MAX_HEIGHT = 1200;
var STAR_CLOSENESS_THRESHOLD = 3;

spacecase = function() {
  // Setup canvas
  var x = d3.scale.linear()
            .domain([0, VP_MAX_WIDTH])
            .range([0, VP_MAX_WIDTH])
    , y = d3.scale.linear()
            .domain([0, VP_MAX_HEIGHT])
            .range([0, VP_MAX_HEIGHT])
    , zoom = d3.behavior.zoom()
               .scaleExtent([1, 5])
               .x(x).y(y)
               .on('zoom', zoomed)
    , $data = null;

  var svg = d3.select('#game-container')
              .append('svg:svg')
                .call(zoom)
                .attr('class', 'spacecase-canvas')
                .attr('pointer-events', 'all')
                .attr('viewBox', '0 0 ' + VP_MAX_WIDTH + ' ' + VP_MAX_HEIGHT)
              .append('svg:g');

  // Rectangle to capture all input events
  svg.append('svg:rect')
    .attr('class', 'events-rect')
    .attr('width', '100%')
    .attr('height', '100%');

  // Update data
  function my() {
  }

  // Update game data
  my.update = function(data) {
    var ndata = normalizeData(data)
      , carrierSelector = svg.selectAll('.carrier-container').data(ndata)
      , starSelector = svg.selectAll('.star-container').data(ndata);
    $data = ndata;
    updateStars( starSelector, ndata )
      .exit().remove();
    updateCarriers( carrierSelector, ndata )
      .exit().remove();

    // This enables click events to be caught for layered things
    $('g', $(svg[0])).on('click.passThrough', function (e, ee) {
      var $el = $(this).hide();
      try {
        var carriers = []
          , planets = [];

        ee = ee || {
          pageX: e.pageX,
          pageY: e.pageY
        };
        
        var next = document.elementFromPoint(ee.pageX, ee.pageY);
        next = (next.nodeType == 3) ? next.parentNode : next //Opera
        $(next).trigger('click.passThrough', ee);
      } catch (err) {
          console.log("click.passThrough failed: " + err.message);
      } finally {
        $el.show();
      }
    });
  };

  var lastScale = 0;
  var lastTranslate = [0,0];
  function zoomed() {
    var origScale = d3.event.scale;
    var scale = origScale;
    var translateX = d3.event.translate[0]
      , translateY = d3.event.translate[1];

    if (origScale != lastScale) {
      lastScale = scale;
      zoom.scale(scale);
      zoom.translate(lastTranslate);
      svg.attr('transform', 'translate(' + lastTranslate + ') scale(' + scale +')');
    }
    if ((scale > 1 && scale < 5) || origScale == lastScale) {
      lastTranslate = [translateX, translateY];
      zoom.translate(lastTranslate);
      svg.attr('transform', 'translate(' + lastTranslate + ') scale(' + scale +')');
    }
  }

  return my;
}

function updateStars(selector, data) {
  container = selector
    .data(data.stars)
    .enter()
      .append('svg:g')
      .attr('class', 'star-container')
      .attr('transform', function(c) { return 'translate(' + c.coords.x + ',' + c.coords.y + ')' });

  // Plot the center of the star -- indicates whether or not this star is in scanning range
  container
    .append('circle')
    .attr('class', function (s) { return 'star-center star-center-' + (s.resources ? 'visible' : 'invisible') })
    .attr('r', 1);

  // If star is owned by someone, draw a circle around it with their color
  container
    .append('circle')
    .attr('class', function(s) { return 'star-player-circle player' + ((s.playerNumber == -1) ? '-none' : s.playerNumber) })
    .attr('r', 2.5);

  // Star resources
  container
    .append('circle')
    .attr('class', 'star-resources')
    .attr('r', function(star) { if (star.resources) { return (star.resources/50)*15 } else { return 0 } });

  // Planet's name
  container
    .append('text')
    .text(function(star) { return star.name })
    .attr('class', 'planet-label')
    .attr('text-anchor', 'middle')
    .attr('dy', 6);

  // Planets resources (E/I/S)
  container
    .append('text')
    .text(function(star) { 
      if (star.resources) { 
        return star.economy + " " + star.industry + " " + star.science 
      } 
    })
    .attr('class', 'planet-specs')
    .attr('text-anchor', 'middle')
    .attr('dy', 9);

  // # Fleets available
  container
    .append('text')
    .attr('class', 'planet-fleets')
    .attr('text-anchor', 'middle')
    .text(function(star) { 
      if (star.resources) { 
        var carrierFleets = 0;
        if (data.starsWithCarriers[star.id]) {
          for (var i in data.starsWithCarriers[star.id]) {
            carrierFleets += data.carriersById[data.starsWithCarriers[star.id][i]].ships;
          }
        }
        return star.ships + carrierFleets
      } 
    })
    .attr('dy', 12);

  return selector;
}

function updateCarriers(selector, data) {
  var determineClass = function(c) {
    var s = 'carrier-container';
    if (data.carriersAtStars[c.id]) {
      s += ' orbiting-carrier';
    }
    return s;
  };

  container = selector
    .data(data.carriers)
    .enter()
      .append('svg:g')
      .attr('class', determineClass)
      .attr('transform', function(c) { 
        var x, y
          , star = data.carriersAtStars[c.id]
          , clusterId = data.clustersByCarrierId[c.id];

        if ( star >= 0 ) {
          x = star.coords.x;
          y = star.coords.y;
        }
        else if ( clusterId >= 0 ) {
          var center = data.clusterCenters[clusterId];
          x = center[0];
          y = center[1];
        }
        else {
          x = c.coords.x;
          y = c.coords.y;
        }

        return 'translate(' + x + ',' + y + ')' 
      });

  // Draw shape, rotate towards next destination
  container
    .append('polygon')
    .attr('points', '-2,2 2,2, 0,-3')
    .attr('transform', function(carrier) { 
      if (carrier.destinations.length > 0) {
        var star = data.starsById[carrier.destinations[0]]
          , dx = carrier.coords.x - star.coords.x
          , dy = carrier.coords.y - star.coords.y
          , h = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2))
          , d = 0
          , theta = (Math.acos(Math.abs(dy)/h)*(180/Math.PI));

          if (dy < 0) { 
            theta = (180 - theta);
          }
          if (dx > 0) {
            theta = -theta;
          }

          return( "rotate(" + theta + " 0 0)" );
      }
    });

  var clusterCounter = {};
  // Player indicator
  container
    .append('circle')
      .attr('r', function(c) {
        var clusterId = data.clustersByCarrierId[c.id]
          , r = 0;
        
        if ( clusterId >= 0 ) {
          var n = clusterCounter[clusterId] ? clusterCounter[clusterId]+1 : 1;
          clusterCounter[clusterId] = n;
          r = (n - 1);
        }
        return 3.5 + r;
      })
      .attr('class', function(c) { return 'carrier-circle ' + 'player' + c.player });

  // Show number of fleets assigned to carrier
  seenClusters = {};
  container
    .append('text')
      .attr('class', 'fleet-count')
      .attr('text-anchor', 'middle')
      .attr('dy', function (c) {
        var clusterId = data.clustersByCarrierId[c.id]
          , extra = 0;

        if ( clusterId >= 0 ) {
          extra = (data.carrierClusters[clusterId].length)*1.5;
        }

        return 6 + extra;
      })
      .text(function(c) { 
        var clusterId = data.clustersByCarrierId[c.id];
        if (!data.carriersAtStars[c.id]) {
          if ( clusterId >= 0) {
            if ( !seenClusters[clusterId] ) {
              seenClusters[clusterId] = 1;
              return data.fleetsInClusters[clusterId];
            }
          }
          else {
            return c.ships;
          }
        }
      });

  return selector;
}

function normalizeData(rawData) {
  var stars = []
    , starsById = {}
    , carriers = []
    , carriersById = {}
    , carriersAtStars = {}
    , starsWithCarriers = {}
    , carrierClusters = []
    , clustersByCarrierId = {}
    , clusterCenters = {}
    , fleetsInClusters = {}
    , minX = Infinity, minY = Infinity, maxX = -Infinity, maxY = -Infinity
    , dx, dy
    , totalFleets = 0;

  // Find bounds
  for (var i in rawData.currentStarData.starsByID) {
    var star = rawData.currentStarData.starsByID[i];

    maxX = Math.max(maxX, star.coords.x);
    maxY = Math.max(maxY, star.coords.y);
    minX = Math.min(minX, star.coords.x);
    minY = Math.min(minY, star.coords.y);
  }

  var xRange = (maxX - minX)
    , yRange = (maxY - minY)
    , xm, ym;
  if ( xRange >= yRange ) {
    xm = VP_MAX_WIDTH;
    ym = VP_MAX_HEIGHT * (yRange / xRange);
  }
  else {
    ym = VP_MAX_HEIGHT;
    xm = VP_MAX_WIDTH * (xRange / yRange);
  }

  function transform(o) {
    if (!o.normalized) {
      o.coords.x = ((o.coords.x - minX) / maxX)*xm;
      o.coords.y = ((o.coords.y - minY) / maxY)*ym;
      o.normalized = 1;
    }
    return o;
  }

  // Stars
  for (var i in rawData.currentStarData.starsByID) {
    var star = transform(rawData.currentStarData.starsByID[i]);

    stars.push(star);
    starsById[star.id] = star;

    if (star.ships) {
      totalFleets += star.ships;
    }
  }

  // Process carriers
  for (var i in rawData.fleetsByID) {
    var c = transform(rawData.fleetsByID[i]);
    carriers.push(c);
    carriersById[c.id] = c;

    for (var i in stars) {
      var star = stars[i];
      var d = Math.sqrt(Math.pow(star.coords.x - c.coords.x, 2) + Math.pow(star.coords.y - c.coords.y, 2));
      
      if ( d < STAR_CLOSENESS_THRESHOLD ) {
        carriersAtStars[c.id] = star;
        if (!starsWithCarriers[star.id]) {
          starsWithCarriers[star.id] = [];
        }
        starsWithCarriers[star.id].push(c.id);
      }
    }
  }
  
  // Find carrier clusters so that multiple circles can be drawn
  for (var i = 0; i < carriers.length; i++) {
    var c1 = carriers[i];

    for (var j = i+1; j < carriers.length; j++) {
      var c2 = carriers[j]
        , d = Math.sqrt(Math.pow(c1.coords.x - c2.coords.x, 2) + Math.pow(c1.coords.y - c2.coords.y, 2));
      
      if ( d < STAR_CLOSENESS_THRESHOLD ) {
        if ( !clustersByCarrierId[c1.id] ) {
          carrierClusters.push([c1.id, c2.id]);
          var clusterId = carrierClusters.length-1;
          clustersByCarrierId[c1.id] = clusterId;
          clustersByCarrierId[c2.id] = clusterId;
          fleetsInClusters[clusterId] = c1.ships + c2.ships;
        }
        else {
          var clusterId = clustersByCarrierId[c1.id];

          carrierClusters[clusterId].push(c2.id);
          clustersByCarrierId[c2.id] = clusterId;
          fleetsInClusters[clusterId] += c2.ships;
        }
      }
    }
  }

  // Compute cluster centers
  for (var clusterId in carrierClusters) {
    var cluster = carrierClusters[clusterId]
      , x = 0
      , y = 0;

    for (var i in cluster) {
      var carrier = carriersById[cluster[i]];
      x += carrier.coords.x;
      y += carrier.coords.y;
    }

    clusterCenters[clusterId] = [(x / cluster.length), (y / cluster.length)];
  }

  return { 
    stars : stars, 
    starsById : starsById, 
    carriers : carriers, 
    carriersById : carriersById,
    carriersAtStars : carriersAtStars,
    starsWithCarriers : starsWithCarriers,
    carrierClusters : carrierClusters,
    clusterCenters : clusterCenters,
    fleetsInClusters : fleetsInClusters,
    clustersByCarrierId : clustersByCarrierId,
    totalFleets : totalFleets
  };
}

})(jQuery);
