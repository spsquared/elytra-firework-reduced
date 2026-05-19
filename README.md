# Reduced Elytra Firework

A very creatively named mod, indeed. Reduced fireworks have less power, making them easier to use when flying in enclosed spaces or getting up a ledge when building.

Fireworks used to have a speed cap or "target speed" and would slow you down if you were above that speed. This has been removed because allowing the control of the power and speed is counterintuitive as power directly turns into speed. Instead, fireworks produce decreasing power up to a speed cap, after which they produce no thrust.

## Gamerules

| Gamerule | Effect | Default | Vanilla-ish |
| -------- | ------ | ------- | ----------- |
| `elytra_firework_reduced/firework_power` | "Thrust" provided by fireworks | 0.15 | 2 |
| `elytra_firework_reduced/firework_speed` | Maximum fireworks boost speed, in blocks per second | 40 | 33.5 |
| `elytra_firework_reduced/firework_time`  | Time multiplier for held fireworks | 0.5 | 1 |

## Mod Presence Enforcement

**This mod must be used on the server AND client!** If it is installed on the server, all clients must install the mod or they will be kicked! If you join a server without Reduced Elytra Firework, the changes will be **disabled** and *fireworks will work normally*! This is to avoid unfairly nerfing yourself, and also to comply with stricter anticheats. 

*Due to how player movement is processed, the mod must be on the client to actually affect flying mechanics.*

To disable this requirement if some players want gentler fireworks and others prefer vanilla, changing "enforce" to "false" in `config/elytra_firework_reduced.json` on the server will disable the mod requirement checks, but Reduced Elytra Fireworks must still be installed server-side for players to use it.
