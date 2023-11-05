import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.SingleGraph;

public class GraphExample {
    public static void main(String[] args) {
        // Set the UI package explicitly
        System.setProperty("org.graphstream.ui", "swing");

        // Create a new Graph
        Graph graph = new SingleGraph("SimpleGraph");

        // Add nodes and set labels
        Node nodeA = graph.addNode("A");
        nodeA.setAttribute("ui.label", "Node A");
        
        Node nodeB = graph.addNode("B");
        nodeB.setAttribute("ui.label", "Node B");
        
        Node nodeC = graph.addNode("C");
        nodeC.setAttribute("ui.label", "Node C");

        // Add edges
        Edge edgeAB = graph.addEdge("AB", "A", "B");
        Edge edgeBC = graph.addEdge("BC", "B", "C");
        Edge edgeCA = graph.addEdge("CA", "C", "A");

        // Add a subtitle to the "AB" edge
        edgeAB.setAttribute("ui.label", "Subtitle for AB Edge");

        // Display the graph
        graph.display();
    }
}
