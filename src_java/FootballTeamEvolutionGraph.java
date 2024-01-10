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
import java.util.Arrays;

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

        List<String[]> wholeFootballData = readFootballDataFromCSV("../Football_team_evolution_graph_data/Graph_data_Xerez_players.csv");

        String[] prevColumnNames = new String[0];

        List<Float> vdsList = new ArrayList<>();
        List<Float> edsList = new ArrayList<>();
        List<Integer> yearList = new ArrayList<>();

        int edgeCount = 0;

        for (var year = 1970; year < 2022; year++){
            int removedNodesCount = 0;
            int addedNodesCount = 0;
            int removedEdgesCount = 0;
            int addedEdgesCount = 0;

            System.out.println("Year: " + year + ", Max Year: " + (year + 0));

            List<String[]> footballData = findPlayersPerYear(wholeFootballData, year, year + 0);

            // Find current players names/labels
            String[] columnNames = footballData.get(0);
            footballData = findWonPlayers(footballData);


            // Remove useless Nodes/players
            for (int i = 0; i < prevColumnNames.length; i++){
                String playerName = prevColumnNames[i];

                if (Arrays.asList(columnNames).contains(playerName)) {
                    continue;
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                graph.removeNode(playerName);
                removedNodesCount += 1;
            }

            // Add nodes to the graph with player names as labels if not in previous generation
            for (int i = 0; i < columnNames.length; i++) {
                String playerName = columnNames[i];

                if (Arrays.asList(prevColumnNames).contains(playerName)) {
                    continue;
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Node node = graph.addNode(playerName);
                node.setAttribute("ui.label", playerName);
                node.setAttribute("ui.style", nodeLabelStyle);
                addedNodesCount += 1;
            }

            if (graph.getNodeCount()!=0){
                yearList.add(year);
                float vds = (float) (removedNodesCount + addedNodesCount) / (graph.getNodeCount()+removedNodesCount);
                vdsList.add((float)vds);
            }

            // Add edges to the graph
            for (int i = 0; i < columnNames.length; i++) {
                String player1 = columnNames[i];
                Node node1 = graph.getNode(player1);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int j = i + 1; j < columnNames.length; j++) {
                    String player2 = columnNames[j];
                    Node node2 = graph.getNode(player2);

                    int gamesPlayedTogether = calculateGamesPlayedTogether(footballData, i, j);

                    // Check if the edge already exists
                    Edge existingEdge = graph.getEdge(player1 + "-" + player2);

                    if (existingEdge != null) {
                        // Edge already exists, modify it
                        if (gamesPlayedTogether == 0){
                            graph.removeEdge(existingEdge);
                            removedEdgesCount += 1;
                            edgeCount -= 1;
                        }
                        else{
                            existingEdge.setAttribute("ui.label", String.valueOf(gamesPlayedTogether));
                            existingEdge.setAttribute("ui.style", edgeLabelStyle);
                        }
                        
                    } else {
                        // Edge doesn't exist, add a new one
                        if (gamesPlayedTogether > 0) {
                            Edge newEdge = graph.addEdge(player1 + "-" + player2, node1, node2);
                            newEdge.setAttribute("ui.label", String.valueOf(gamesPlayedTogether));
                            newEdge.setAttribute("ui.style", edgeLabelStyle);
                            addedEdgesCount += 1;
                            edgeCount += 1;
                        }
                    }
                }
            }

            if (graph.getNodeCount()!=0){
                float eds = (float) (removedEdgesCount + addedEdgesCount) / (edgeCount + removedEdgesCount);
                edsList.add((float)eds);
            }

            prevColumnNames = columnNames;

            // Pause for a short duration to observe the changes
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("year_list = [");

        for (int value : yearList) {
            System.out.println(value+",");
        }
        System.out.println("]");


        System.out.println("vds_list = [");

        for (float value : vdsList) {
            System.out.println(value+",");
        }
        System.out.println("]");


        System.out.println("eds_list = [");

        for (float value : edsList) {
            System.out.println(value+",");
        }
        System.out.println("]");

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

    private static List<String[]> findPlayersPerYear(List<String[]> wholeFootballData, int selectedMinYear, int selectedMaxYear){
        List<String[]> tempFootballData = new ArrayList<>();

        // remove rows with year (value[0]) != selectedMinYear
        for (int rowIndex = 0; rowIndex < wholeFootballData.size(); rowIndex++) {
            String[] row = wholeFootballData.get(rowIndex);

            if ("year".equals(row[0])){
                tempFootballData.add(row);
            }

            else if (Integer.parseInt(row[0]) >= selectedMinYear){
                if (Integer.parseInt(row[0]) <= selectedMaxYear){
                    tempFootballData.add(row);
                }
                else if (Integer.parseInt(row[0]) > selectedMaxYear){
                    break;
                }
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
    
        for (int col = 2; col < numColumns; col++) {
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

        // Remove columns with all zeros
        List<String[]> updatedFootballData = new ArrayList<>();
        for (String[] row : footballData) {
            List<String> updatedRow = new ArrayList<>();
            for (int col = 2; col < numColumns; col++) {
                if (!columnsToRemove.contains(col)) {
                    updatedRow.add(row[col]);
                }
            }
            updatedFootballData.add(updatedRow.toArray(new String[0]));
        }

        return updatedFootballData;
    }

    private static List<String[]> findWonPlayers(List<String[]> footballData) {
        List<String[]> tempFootballData = new ArrayList<>();

        tempFootballData.add(footballData.get(0));

        for (int rowIndex = 1; rowIndex < footballData.size(); rowIndex++) {
            String[] row = footballData.get(rowIndex);
    
            if ("1".equals(row[1])) {
                tempFootballData.add(row);
            }
        }

        return tempFootballData;
    }
    
}
