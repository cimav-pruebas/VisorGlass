function d3_grafoReferencias(nodoFuente, links) {

    // http://bl.ocks.org/mbostock/1153292

    var nodes = {};
 
// Compute the distinct nodes from the links.
    links.forEach(function(link) {
        link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
        link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
    });

    var width = 400, height = 500;
    
    var force = d3.layout.force()
            .nodes(d3.values(nodes))
            .links(links)
            .size([width, height])
            .linkDistance(60)
            .charge(-300)
            .on('tick', tick)
            .start();

    var elementSvg = document.getElementById("svg-id");
    while (elementSvg.firstChild) {
        elementSvg.removeChild(elementSvg.firstChild);
    }            

    var svg = d3.select('svg');

    svg.append('defs').selectAll('marker')
            .data(["Hacia", "Desde"])
            .enter().append('marker')
            .attr('id', function(d) { return d;})
            .attr('viewBox', '0 -5 10 10')
            .attr('refX', 15)
            .attr('refY', -1.5)
            .attr('markerWidth', 6)
            .attr('markerHeight', 6)
            .attr('orient', 'auto')
            .append('path')
            .attr('d', 'M0,-5L10,0L0,5'); 

    var path = svg.append('g').selectAll('path')
            .data(force.links())
            .enter().append('path')
            .attr('class', function(d) { return 'link ' + d.type;})
            .attr('marker-end', function(d) {return 'url(#' + d.type + ')';});

    var circle = svg.append('g').selectAll('circle')
            .data(force.nodes())
            .enter().append('circle')
            .attr('r', 6)
            .attr("fill", function(d) { return (nodoFuente === d.name) ? "#333" : "#ccc"; })
            .attr("stroke", function(d) { return (nodoFuente === d.name) ? "#ccc" : "#333"; })
            .on("mouseover", mouseoverCircle)
            .on("mouseout", mouseoutCircle)
            // .on("click", function(d,i) { alert(d3.select(this).r; }) // this will print out the radius })
            //.on("click", function(d) { alert(d.name); })
            .on("dblclick", doubleClickCircle)
            .call(force.drag);

    var text = svg.append('g').selectAll('text')
            .data(force.nodes())
            .enter().append('text')
            .attr('x',8)
            .attr('y', '.31em')
            .text(function(d) {return d.name;});
    
        
// Use elliptical arc path segments to doubly-encode directionality.
    function tick() {
        path.attr('d', linkArc);
        circle.attr('transform', transform);
        text.attr('transform', transform);
    }

    function linkArc(d) {
        var dx = d.target.x - d.source.x,
                dy = d.target.y - d.source.y,
                dr = Math.sqrt(dx * dx + dy * dy);
        return 'M' + d.source.x + ',' + d.source.y + 'A' + dr + ',' + dr + ' 0 0,1 ' + d.target.x + ',' + d.target.y;
    }

    function transform(d) {
        return 'translate(' + d.x + ',' + d.y + ')';
    }
    
    function mouseoverCircle() {
        d3.select(this).transition()
            .duration(750)
            .attr("r", 12);
    }
    
    function mouseoutCircle() {
        d3.select(this).transition()
            .duration(750)
            .attr("r", 8);
    }
    function doubleClickCircle() {
        //alert(d.name);
        //filterGraph("refDesde", "hidden");
    }
//    function doubleClickCircle() {
//        d3.select(this).transition()
//            .duration(750)
//            .attr("r", 24);
//    }

}
