var graphs = [];
let currentIndex = 0;

const cy = cytoscape({
    
  container: document.getElementById('cy'), // container to render in

  elements: [],

  style: [ // the stylesheet for the graph
    {
      selector: 'node',
      style: {
        'background-color': '#666',
        'label': 'data(id)'
      }
    },

    {
      selector: 'edge',
      style: {
        'width': 1,
        'line-color': '#ccc',
        'target-arrow-color': '#ccc',
        'curve-style': 'bezier'
      }
    }
  ],

  layout: {
    name: 'grid',
    rows: 1
  }

});

cy.layout({
    name: 'cola',
}).run();

function readFile(){
    var fr = new FileReader();
    fr.onload = function(){
        var abbas = fr.result;
        var count = 0;
        var nodes = [];
        var edges = [];
        var lines = abbas.split("\n");
        let time = 0;
        for(let i=0; i<lines.length; i++){
            if(lines[i].includes(".")){
                console.log(edges);
                graphs.push(Object.assign({},{
                    nodeSet:nodes, edgeSet: edges
                }));
                count = 0;
                nodes = [];
                edges = [];
                time ++;
            }
            else{
                var nums = lines[i].split(" ");
                nodes.push({id: count.toString()});
                for(let j=0; j<nums.length; j++){
                    if(nums[j] == 1 && count > j){
                        var name = count.toString()+ "-" + j.toString();    
                        edges.push({id: name, source: count.toString(), target: j.toString(), time: time});
                    }
                }
                count++;
            }
        }
        console.log(graphs);
        initializeNodes();
        drawGraph(currentIndex);
    }
    fr.readAsText(document.getElementById("inp").files[0]);   
}

function initializeNodes() {
    const nodes = graphs[0].nodeSet;
    nodes.forEach(node => {
        cy.add({
            group: 'nodes',
            data: node,
        });
    });
    for(i = 0 ; i < nodes.length - 1; ++i){
        cy.add({
            group: 'edges',
            data: {id: nodes[i].id + '-' + nodes[i+1].id, source: nodes[i].id, target: nodes[i+1].id}
        });
    }
    cy.add({
        group: 'edges',
        data: {id: nodes[nodes.length-1].id + '-' + nodes[0].id, source: nodes[nodes.length-1].id, target: nodes[0].id}
    })
    cy.layout({
        name: 'cola',
    }).run();
    cy.remove('edges');
}

function drawGraph(index){
    const edges = graphs[index].edgeSet;
    edges.forEach(edge => {
        cy.add({
            group: 'edges',
            data: edge,
        });
    });
}

function next(){
    const string = 'edge[time=' + currentIndex.toString() +']';
    const collection = cy.remove(string);
    currentIndex ++;
    drawGraph(currentIndex);
}

function previous(){
    const string = 'edge[time=' + currentIndex.toString() +']';
    const collection = cy.remove(string);
    currentIndex --;
    drawGraph(currentIndex);
}