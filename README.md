# Reduced Elytra Firework

A very creatively named mod, indeed. Reduced fireworks have less power without reducing their top speed, so you can still go fast, but they don't instantly put you up to max speed anymore. Fireworks fired as projectiles are unaffected.

This is especially useful when building or flying in enclosed spaces and makes it easier to avoid faceplanting a wall. It does make you use more fireworks, though.

## Gamerules

| Gamerule | Effect | Default | Vanilla-ish |
| -------- | ------ | ------- | ----------- |
| `elytra_firework_reduced/firework_power` | "Thrust" provided by fireworks | 0.25 | 1 |
| `elytra_firework_reduced/firework_speed` | Top speed of fireworks | 1.5 | 1.5 |
| `elytra_firework_reduced/firework_time`  | Time multiplier for held fireworks | 0.5 | 1 |

## Mod Presence Enforcement

**This mod must be used on the server AND client!** If it is installed on the server, all clients must install the mod or they will be kicked! If you join a server without Reduced Elytra Firework, the changes will be **disabled** and *fireworks will work normally*! This is to avoid unfairly nerfing yourself, and also to comply with stricter anticheats. 

*Due to how player movement is processed, the mod must be on the client to actually affect flying mechanics.*

To disable this requirement if some players want gentler fireworks and others prefer vanilla, changing "enforce" to "false" in `config/elytra_firework_reduced.json` on the server will disable the mod requirement checks, but Reduced Elytra Fireworks must still be installed server-side for players to use it.
