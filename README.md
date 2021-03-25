# Bot_Lane_Ranking_Backend
This is the back end for my league of legends project that allows you to see what you win % you have as adc and what your winrate % is with each support. It communicates with the Riot API to fetch all of the matches for a summoner, filtering them matches down to ones where they played the adc role. It will then record if it was a win or not and then check who their supporting champion was and record statistics for that adc + support combo. 

You will need a riot api key to run this application, as well as a dynamo DB. You can get around this by running the application with a local dynamo DB.
