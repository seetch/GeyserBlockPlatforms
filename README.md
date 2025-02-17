# ![image](https://github.com/user-attachments/assets/8778b16e-1c8f-43e6-a8d9-7ad9692926aa)

Prohibit certain platforms from connecting to your server!

## Features

* Clean code, free support
* Monitor each proxy server separately
* Switch modes between whitelist and blacklist
* Add exempt players without permissions

## Commands

| Description          | Command      |
|----------------------|--------------|
| Reload plugin config | `/gbpreload` |

## Permissions

| Description             | Permission                    |
|-------------------------|-------------------------------|
| Give bypass to player   | `geyserblockplatforms.exempt` |
| Allow to use /gbpreload | `geyserblockplatforms.reload` |

## Config

```yaml
# Use 'Java' to regulate connect from Java Edition

# Use key in servers section
# You can change values so that it is displayed correctly by players
deviceNames: 
  Windows Phone: Windows Phone
  Java: Java
  Windows x86: Windows x86
  Xbox One: Xbox One
  Hololens: Hololens
  Windows: Windows
  iOS: iOS
  Apple TV: Apple TV
  Dedicated: Dedicated
  Android: Android
  PS4: PS4
  Gear VR: Gear VR
  Unknown: Unknown
  Switch: Switch
  Amazon: Amazon
  macOS: macOS

# A list of players who can connection anywhere
exemptPlayers:
- seetch

# Messages displayed to players in two mode cases
messages:
  blacklist: '&cYou can''t connect from &f%platforms%'
  whitelist: '&aYou can connect only from &f%platforms%'

# Server setup
servers:
  lobby: # Name from velocity.toml
    # Use blacklist mode to ban platforms below 
    # OR whitelist to allow connection only from certain platforms
    mode: blacklist 
    platforms:
    - Java
    - Unknown
  server-2:
    mode: whitelist
    platforms:
    - Android
    - iOS
  server-1:
    mode: blacklist
    platforms:
    - Java
    - Unknown
```

