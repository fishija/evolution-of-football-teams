import pandas as pd
import numpy as np

def find_unique_players(name_of_csv):
    list_of_players = []
    df = pd.read_csv(f"../Football_team_evolution_csv/{name_of_csv}", sep=';')
    df_for_getting_players_names=df.iloc[:,5:].dropna(axis=1).astype(str).values
    list_of_players = np.unique(df_for_getting_players_names)
    list_of_players = np.insert(list_of_players,0,"year",axis=0)
    
    new_df_with_all_players_and_only_won_matches = pd.DataFrame(columns=list_of_players)


    for match in df.iterrows():
        print(match[1]["year"])
        if match[1]["WinOrLost"]==1:
            list_of_one_match = [match[1]["year"]]
            for player in list_of_players[1:]:
                if player in match[1]["players":].astype(str).values:
                    list_of_one_match.append(1)
                else:
                    list_of_one_match.append(0)
            new_df_with_all_players_and_only_won_matches.loc[len(new_df_with_all_players_and_only_won_matches)]=list_of_one_match

    # Save new_df_with_all_players_and_only_won_matches to a CSV file
    new_df_with_all_players_and_only_won_matches.to_csv(f'../Football_team_evolution_csv/Graph_data_{name_of_csv}', index=False)  # Set index=False to exclude row indices

    print(f"DataFrame saved to '{name_of_csv}'")


find_unique_players("Atlético_de_Madrid_players.csv")
find_unique_players("Barcelona_players.csv")
find_unique_players("Real_Madrid_players.csv")

