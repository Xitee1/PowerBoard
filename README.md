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
ScoreboardAPI.enableScoreboard(p);
ScoreboardAPI.disableScoreboard(p);
```
```java
// Set a complete custom Scoreboard
// You have to disable the scoreboard in the config.yml!

// The boolean indicates if the placeholders should be replaced
ScoreboardAPI.enableScoreboard(p); // Enable the scoreboard - Yes, we have disabled it in the config.yml, but now we have to enable it again with the API
ScoreboardAPI.setScoreboardTitle(p, title, false);
ScoreboardAPI.setScoreboardScore(p, score, 3, false);
ScoreboardAPI.setScoreboardScore(p, score, 2, false);
ScoreboardAPI.setScoreboardScore(p, score, 1, false);
ScoreboardAPI.setScoreboardScore(p, score, 0, false);
// and so on
```
