![Showcase Banner](https://i.imgur.com/fnEMAzw.png)

## Overview
The Showcase plugin gives players a way to show off their prized possessions by storing them in a publicly viewable GUI.

Available on [Spigot](https://www.spigotmc.org/resources/showcase.101606/), [Modrinth](https://modrinth.com/plugin/showcase-plugin), and [Hangar](https://hangar.papermc.io/Naspo/ShowcaseIt).

## Features
- Display any of your prized possessions in a compact GUI.
- Display up to 54 items (6 rows).
- Works with offline players.
- Edit permission allows moderators/administrators to edit anyone's showcase.
- Cooldowns
    - Applies a cooldown to items placed in a showcase, so that they may not be removed until the cooldown is up. (Prevent players from using their showcase as extra storage).
    - _Note that cooldowns are not enabled by default._

## Commands
- `/showcase` - Open your showcase.
- `/showcase <user>` - View a player’s showcase.
- `/showcase reload` - Reloads the configuration.

## Permissions
- `showcase.use` - Allows players to use their own showcase, and view other players' showcase.
- `showcase.use.view` - Allows the player to view showcases (including their own), but not edit their own showcase.
- `showcase.size.<#>` - Amount of rows a player is allowed for their showcase. (2 through 6, default: 1).
- `showcase.cooldowns.bypass` - Allows the player to bypass the cooldown.
- `showcase.edit` - Allows the player to edit anyone’s showcase, and bypass coooldowns.
- `showcase.reload` - Reload the plugin.

## Dependencies
Vault is a dependency of this plugin. You need it in order for showcase to work properly. To install Vault, click [here](https://www.spigotmc.org/resources/vault.34315/).

<br/><br/>
![Showcase Demo Image](https://cdn.modrinth.com/data/cached_images/083c3a108b43d771e0acdb5c8c8486ab6e886f5d.png)
![Showcase Cooldowns Feature Demo](https://cdn.modrinth.com/data/cached_images/f86967b498e94c5534e597ad680c9b8595516877.png)