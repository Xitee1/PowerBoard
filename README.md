# Scoreboard
Scoreboard + Tablist + Prefix + Chat | Animated

Download: https://www.spigotmc.org/resources/scoreboard-tablist-prefix-chat-animated.73854/

## Developer-API

Don't forget to put "Scoreboard" as Depend or Softdepend

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
// Enable/Disable the scoreboard for a specified player
ScoreboardAPI.getPlayer(p).enableScoreboard();
ScoreboardAPI.getPlayer(p).disableScoreboard();
```
```java
// Set a complete custom Scoreboard
// The boolean indicates if the placeholders should be replaced
ScoreboardAPI.getPlayer(p).setScoreboardTitle(title, true);

// After that we can set the scores
// The index is the red number of the right side from the scoreboard
ScoreboardAPI.getPlayer(p).setScoreboardScore("score1", 2, true);
ScoreboardAPI.getPlayer(p).setScoreboardScore("score2", 1, true);
ScoreboardAPI.getPlayer(p).setScoreboardScore("score3", 0, true);
// And so on
```
