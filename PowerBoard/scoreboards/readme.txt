How to use multiple scoreboards:
1. Set the default scoreboard in the config.yml. The name of the scoreboard is the filename.

2. Now copy the default scoreboard file and rename it to whatever you want. The filename will become the scoreboard name.

3. Open the copied file and go to the end. There you can configurate the conditions to apply the scoreboard. Examples are below.

4. The scoreboard you've set as default doesn't really needs conditions because it will automatically be set when a player joins the server.


Examples:
Apply if a player is in creative mode OR has a permission:
- 'gamemode:creative'
- 'permission:some.permission'

Apply if a player is in creative mode OR surival mode AND has a permission:
- 'gamemode:creative AND permission:some.permission'
- 'gamemode.survival AND permission:some.permission'

Apply the above, but only in a specific world:
- 'gamemode:creative AND permission:some.permission AND world:world_nether'
- 'gamemode.survival AND permission:some.permission AND world:world_nether'

Now you should understand how it works. If you want OR, add a new line. If you want AND, write 'AND' followed by the condition.

Here are all conditions you can use:
- world:<world>
- permission:<permission>
- gamemode:<survival/creative/adventure/spectator>