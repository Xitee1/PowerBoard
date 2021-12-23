# PowerBoard
Scoreboard + Tablist + Prefix + Chat | Animated

Download: https://www.spigotmc.org/resources/scoreboard-tablist-prefix-chat-animated.73854/

Wiki: https://github.com/Xitee1/PowerBoard/wiki

## Developer-API

Don't forget to put "PowerBoard" as depend or softdepend

```java
// Set custom placeholders
CustomPlaceholders ph = new CustomPlaceholders() {
    @Override
    public String replace(Player p, String s) {
 
        if(s.contains("%my_placeholder%")) { // To save a bit performance
            s = s.replace("%my_placeholder%", "It works!"); // Replace the placeholder
        }
 
        return s; // Return the modified string
    }
};
ScoreboardAPI.registerCustomPlaceholders(ph);
```
```java
// set / remove the scoreboard for a specified player
ScoreboardAPI.setScoreboard(p);
ScoreboardAPI.removeScoreboard(p);
```
```java
// Set a whole custom Scoreboard
// You have to disable the scoreboard in the config.yml!

ScoreboardAPI.setScoreboard(p); // Set the scoreboard for the player
ScoreboardAPI.setScoreboardTitle(p, title, false); // The boolean indicates if the placeholders should be replaced or not

// You can set up to 14 scores. If you set more, not all of them will show up!
ArrayList<String> scores = new ArrayList<>();
scores.add("Score 1");
scores.add("Score 2");
scores.add("Score 3");
ScoreboardAPI.setScoreboardScores(p, scores, false); // The boolean indicates if the placeholders should be replaced or not

// Update a single score
ScoreboardAPI.setScoreboardScore(p, "Score 3 Updated", 2, false); // The score with the text "Score 3" has now been changed to "Score 3 Updated"
```
# Donate
[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/donate?hosted_button_id=6XBBMV2PAQT5S)


Bitcoin: bc1q6eh8mpkyu5pne7m72f4lyh6fw4k4ugpvzjfljd

![image](https://user-images.githubusercontent.com/59659167/147228233-1b2ed89c-f9ab-499a-862a-30a9520cd7c6.png)

Note to Bitcoin:
1. I hope this works. I have a Bitcoin wallet and know how to use it. But I'm not exactly an expert.
2. I can't give you the Donator rank in the Discord server because.. well.. it's anonym, so I can't verify you actually donated.
