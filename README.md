# ![image](https://user-images.githubusercontent.com/35769613/113513508-66e09b80-956a-11eb-90a9-280172115cbf.png)
A mod to execute macros command with a key

## Features :
- Define key to execute command. This mod is only needed on the client and work well on server too.
- When you press the key. The command is sent as a chat message and proceded by the server like any other command.
- Create as many macros as you want through a configuration file.

## Discord 
Feel free to join my discord if you need help or fi you have any issue, any questions or any suggestions : https://discord.gg/rzzd76c

## Create a macro
To open the configuration file, open the forge mod list GUI, select Keys4Macros and hit the Config button. Finally hit the Open config button.

Each [[Macros.Macro]] block define a macro. You can duplicate this block as much as you want.
- key is the number corresponding to the key you want. The list can be found here : https://www.glfw.org/docs/3.3/group__keys.html
- command is the command to execute when the key is pressed, including the '/'
When you're satisfied with your configuration, hit the **Reload Config** button or restart the game.

## Example configuration
The following configuration set the numpad key 0 to 3 to switch between all the gamemodes :
```toml
[Macros]
    [[Macros.Macro]]
        key = 320
        command = "/gamemode adventure"
    [[Macros.Macro]]
        key = 321
        command = "/gamemode survival"
    [[Macros.Macro]]
        key = 322
        command = "/gamemode creative"
    [[Macros.Macro]]
        key = 323
        command = "/gamemode spectator"
``` 

## Misc
Can I use this mod in my mod pack / make a video on or with this mod ?
     Yes, just remember to give credit and link back to the official download page, since they're the only links that are SAFE. You can use the mod freely, and put it in every modpack you want. You just need to put a link to this page, in the place where you tell people the mods in your modpack. If you make a video on this mod make sure to put a link to this page in the decription of the video.
