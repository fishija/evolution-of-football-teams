import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FootballTeamEvolutionGraph {
    public static void main(String[] args) {
        List<String[]> footballData = readFootballDataFromCSV("football_data.csv");

        // Create GraphStream graph
        Graph graph = new SingleGraph("FootballTeamEvolutionGraph");

        // Add nodes to the graph with player names as labels
        String[] columnNames = footballData.get(0);
        for (int i = 1; i < columnNames.length; i++) {
            String playerName = columnNames[i];
            Node node = graph.addNode(playerName);
            node.setAttribute("ui.label", playerName);
        }

        // Add edges to the graph
        for (int i = 1; i < columnNames.length; i++) {
            String player1 = columnNames[i];
            Node node1 = graph.getNode(player1);

            for (int j = i + 1; j < columnNames.length; j++) {
                String player2 = columnNames[j];
                Node node2 = graph.getNode(player2);

                int gamesPlayedTogether = calculateGamesPlayedTogether(footballData, i, j);

                // Add an edge between player1 and player2 only if gamesPlayedTogether is not 0
                if (gamesPlayedTogether > 0) {
                    Edge edge = graph.addEdge(player1 + "-" + player2, node1, node2);
                    edge.setAttribute("ui.label", String.valueOf(gamesPlayedTogether));
                }
            }
        }

        // Set the UI property to use Swing
        System.setProperty("org.graphstream.ui", "swing");

        // Display the graph
        graph.display(true);
    }

    private static List<String[]> readFootballDataFromCSV(String fileName) {
        List<String[]> footballData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                footballData.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return footballData;
    }

    private static int calculateGamesPlayedTogether(List<String[]> footballData, int player1Index, int player2Index) {
        int gamesPlayedTogether = 0;

        for (int i = 1; i < footballData.size(); i++) {
            int player1Value = Integer.parseInt(footballData.get(i)[player1Index]);
            int player2Value = Integer.parseInt(footballData.get(i)[player2Index]);

            if (player1Value == 1 && player2Value == 1) {
                gamesPlayedTogether++;
            }
        }

        return gamesPlayedTogether;
    }
}
