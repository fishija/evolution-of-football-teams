import pandas as pd
import numpy as np

def find_unique_players(name_of_csv):
    list_of_players = []
    df = pd.read_csv(f"../Football_team_evolution_csv/{name_of_csv}", sep=';')
    df_for_getting_players_names = df.iloc[:, 5:].astype(str).values
    print(df_for_getting_players_names)
    list_of_players = np.unique(df_for_getting_players_names)
    #print(list_of_players)
    list_of_players = np.insert(list_of_players, 0, "year", axis=0)
    list_of_players = np.insert(list_of_players, 1, "WinOrLost", axis=0)  # New column

    new_df_with_all_players_matches = pd.DataFrame(columns=list_of_players)
    # print(new_df_with_all_players_matches)

    for match in df.iterrows():
        print(match[1]["year"])
        list_of_one_match = [match[1]["year"]]
        list_of_one_match.append(match[1]["WinOrLost"])  # Appending WinOrLost value

        for player in list_of_players[2:]:  # Starting from index 2 to avoid year and WinOrLost
            if player in match[1]["players":].astype(str).values:
                list_of_one_match.append(1)  # Player participated
            else:
                # print(player)
                # print(match[1]["players":].astype(str).values)
                list_of_one_match.append(0)  # Player did not participate

        new_df_with_all_players_matches.loc[len(new_df_with_all_players_matches)] = list_of_one_match

    # Save new_df_with_all_players_matches to a CSV file
    new_df_with_all_players_matches.to_csv(f'../Football_team_evolution_csv/Graph_data_{name_of_csv}', index=False)

    print(f"DataFrame saved to 'Graph_data_{name_of_csv}'")


find_unique_players("Numancia Players.csv")
# find_unique_players("Barcelona_players.csv")
# find_unique_players("Real_Madrid_players.csv")
