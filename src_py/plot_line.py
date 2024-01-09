from plot_values import year_list, vds_list, eds_list


import matplotlib.pyplot as plt

team_name = 'Xerez'


plt.plot(year_list, vds_list, label="VDS")
plt.plot(year_list, eds_list, label="EDS")


plt.title(team_name)

plt.xlabel('Year')
plt.ylabel('Values')
plt.legend(loc='upper left')

plt.savefig(f'{team_name}_plot.png')
