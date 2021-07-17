# Scoreboard
Scoreboard + Tablist + Prefix + Chat | Animated

Download: https://www.spigotmc.org/resources/scoreboard-tablist-prefix-chat-animated.73854/

## Developer-API

Don't forget to put "Scoreboard" as depend or softdepend

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
// Set the scoreboard for a specified player
ScoreboardAPI.setScoreboard(p);
ScoreboardAPI.removeScoreboard(p);
```
```java
// Set a complete custom Scoreboard
// You have to disable the scoreboard in the config.yml!

ScoreboardAPI.setScoreboard(p); // Enable the scoreboard - Yes, we have disabled it in the config.yml, but now we have to enable it again with the API
ScoreboardAPI.setScoreboardTitle(p, title, false); // The boolean indicates if the placeholders should be replaced

// You can set up to 14 scores (If you set more not all of them will show up)
// Set multiple scores
ArrayList<String> scores = new ArrayList<>();
scores.add("Score 1");
scores.add("Score 2");
scores.add("Score 3");
ScoreboardAPI.setScoreboardScores(p, scores, false); // The boolean indicates if the placeholders should be replaced

// Update a single score
ScoreboardAPI.setScoreboardScore(p, "Score 3 Updated", 2, false); // The score with the text "Score 3" has now been changed to "Score 3 Updated"
```
