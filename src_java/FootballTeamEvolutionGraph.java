import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;
import org.graphstream.ui.view.Viewer;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FootballTeamEvolutionGraph {
    public static void main(String[] args) {

        String defaultStyleSheet = "graph {padding:40px;fill-color:white;}"
            + " node {shape:box; fill-color:black; size:10px;}"
            + " edge {fill-color:grey;size:1px;}";

        String nodeLabelStyle = " text-alignment: under; "
            + "text-color: white; "
            + "text-style: bold; "
            + "text-background-mode: rounded-box; "
            + "text-background-color: #222C; "
            + "text-padding: 1px; text-offset: 0px, 2px;";

        String edgeLabelStyle = " text-alignment: under; "
            + "text-style: bold; "
            + "text-padding: 1px; text-offset: 0px, 2px;";

        // Create GraphStream graph
        Graph graph = new SingleGraph("FootballTeamEvolutionGraph");

        graph.setAttribute("ui.stylesheet",defaultStyleSheet);

        // Set the UI property to use Swing
        System.setProperty("org.graphstream.ui", "swing");

        // Display the graph
        Viewer viewer = graph.display(true);


        List<String[]> wholeFootballData = readFootballDataFromCSV("Graph_data_Barcelona_players.csv");


        for (var year = 1970; year < 2022; year++){

            List<String[]> footballData = findPlayersPerYear(wholeFootballData, year);

            // Add nodes to the graph with player names as labels
            String[] columnNames = footballData.get(0);
            for (int i = 1; i < columnNames.length; i++) {
                String playerName = columnNames[i];
                Node node = graph.addNode(playerName);
                node.setAttribute("ui.label", playerName);
                node.setAttribute("ui.style", nodeLabelStyle);
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
                        edge.setAttribute("ui.style", edgeLabelStyle);
                    }
                }
            }
            
            // Pause for a short duration to observe the changes
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            graph.clear();
            
        }

        viewer.close();
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

    private static List<String[]> findPlayersPerYear(List<String[]> wholeFootballData, int selectedYear){
        List<String[]> tempFootballData = new ArrayList<>();

        // remove rows with year (value[0]) != selectedYear
        for (int rowIndex = 0; rowIndex < wholeFootballData.size(); rowIndex++) {
            String[] row = wholeFootballData.get(rowIndex);

            if ("year".equals(row[0])){
                tempFootballData.add(row);
            }

            else if (Integer.parseInt(row[0]) == selectedYear){
                tempFootballData.add(row);
            }

            else if (Integer.parseInt(row[0]) > selectedYear){
                break;
            }
        }

        // removeColumnsWithAllZeros
        tempFootballData = removeColumnsWithAllZeros(tempFootballData);

        return tempFootballData;
    }

    private static List<String[]> removeColumnsWithAllZeros(List<String[]> footballData) {
        List<Integer> columnsToRemove = new ArrayList<>();
        
        if (footballData.isEmpty()) {
            return footballData;
        }
    
        int numColumns = footballData.get(0).length;
    
        for (int col = 1; col < numColumns; col++) {
            boolean allZeros = true;

            for (int rowIndex = 1; rowIndex < footballData.size(); rowIndex++) {
                String[] row = footballData.get(rowIndex);
        
                if (!"0".equals(row[col])) {
                    allZeros = false;
                    break;
                }
            }
    
            if (allZeros) {
                columnsToRemove.add(col);
            }
        }
    
        System.out.print(columnsToRemove);

        // Remove columns with all zeros
        List<String[]> updatedFootballData = new ArrayList<>();
        for (String[] row : footballData) {
            List<String> updatedRow = new ArrayList<>();
            for (int col = 0; col < numColumns; col++) {
                if (!columnsToRemove.contains(col)) {
                    updatedRow.add(row[col]);
                }
            }
            updatedFootballData.add(updatedRow.toArray(new String[0]));
        }

        return updatedFootballData;
    }
}
