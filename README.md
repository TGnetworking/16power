# CustomPowers — 15 Powers + Crown of the Void King

A Paper plugin with 15 unique, dangerous relics plus a 16th god-tier power —
the **Crown of the Void King**, a golden helmet.

## The two zip files — what to do with each

- **`powers_resourcepack.zip`** → this one you do **NOT** extract. Drop the
  whole `.zip` file, exactly as it is, straight into your
  `.minecraft/resourcepacks` folder. Minecraft reads it directly.
- **`CustomPowers-plugin-source.zip`** → this is the plugin's *source code*,
  not a ready `.jar`. It has to be compiled first — see "Getting the jar"
  below. Once you have the `.jar`, that's the file that goes in
  `E:\MCServer\plugins\` (not the zip, not the source folder).

## ⚠️ One thing to check first

Your server is Paper **1.21.11** — that exact version number doesn't exist
in Paper's own numbering (their versions go 1.21, 1.21.1, 1.21.2 … up to
whatever's newest). I've pointed `pom.xml` at `1.21.1-R0.1-SNAPSHOT`, almost
certainly what you're really running. Your server console prints the exact
version on startup — if it's different, open `pom.xml` and change the
`<version>` under `paper-api` to match.

## Getting the jar (I can't compile it myself here)

This chat sandbox has no internet access to the site plugins get compiled
against (repo.papermc.io), so I can only hand you the source, not a working
`.jar`. Two ways to turn it into one:

**Option A — GitHub builds it for you for free, no installs needed:**
1. Unzip `CustomPowers-plugin-source.zip` on your PC.
2. Make a free account at github.com if you don't have one, then click
   "New repository" — name it anything, e.g. `custompowers`.
3. On the new repo's page, click "uploading an existing file" and drag in
   everything from the unzipped folder (`pom.xml`, the `src` folder, the
   `.github` folder, `README.md`) — then click "Commit changes".
4. Click the **Actions** tab at the top of the repo. A build will already
   be running (started automatically) — wait for the yellow dot to turn
   into a green check, usually under a minute.
5. Click into that finished run, scroll down to **Artifacts**, and download
   `CustomPowers-jar`. That download is a small zip — extract it and
   you'll have `CustomPowers.jar`.
6. Copy `CustomPowers.jar` into `E:\MCServer\plugins\` and restart the
   server.

**Option B — build it locally**, if you (or whoever set up the server) has
Java 21 + Maven installed: open a terminal in the unzipped folder and run
`mvn package`. The jar appears at `target/CustomPowers.jar`.

## Commands

- `/listpowers` — lists every power's id
- `/givepower <player> <powerId>` — gives that player the item, e.g.
  `/givepower VoidGamerZ_ crown`

## The 16 powers

| # | Power | Base item | Type | How it triggers |
|---|-------|-----------|------|------------------|
| 1 | Ember Core | Blaze Powder | Active (12s) | Right-click: ignites + 10 dmg to everyone within 6 blocks |
| 2 | Drakon's Maw | Dragon's Breath | Active (15s) | Right-click: 10 dmg + heavy wither cone in front of you |
| 3 | Umbral Step | Ender Pearl | Active (10s) | Right-click: teleport behind nearest enemy, 12 dmg sneak hit |
| 4 | Reaper's Mark | Wither Skeleton Skull | Passive, on-hit | 10% chance per hit to instantly kill the target |
| 5 | Stormcaller | Trident | Passive, on-hit | 25% chance per hit to strike lightning on the target |
| 6 | Glacial Grasp | Blue Ice | Active (14s) | Right-click: freezes solid + 8 dmg to the nearest enemy |
| 7 | Gravitum Pull | Eye of Ender | Active (14s) | Right-click: yanks every nearby enemy toward you |
| 8 | Phantom Wings | Phantom Membrane | Active (20s) | Right-click: launches you into a short burst of flight |
| 9 | Crimson Thirst | Redstone | Passive, on-hit | Every hit heals you for 35% of the damage dealt |
| 10 | Seismic Fist | Netherite Ingot | Active (14s) | Right-click: ground slam, 9 dmg + launches nearby enemies |
| 11 | Venomous Bite | Spider Eye | Passive, on-hit | Every hit applies heavy poison + wither |
| 12 | Mirror Walker | Amethyst Shard | Active (20s) | Right-click: go invisible, spawn 3 glowing decoys |
| 13 | Void Ripple | Gunpowder | Active (14s) | Right-click: 6 dmg AoE knockback + blindness |
| 14 | Berserker's Wrath | Totem of Undying | Passive, low HP | Below 30% HP: Strength III + Speed III + heal (45s internal cooldown) |
| 15 | Soul Shackle | Chain | Active (18s) | Right-click: 5 dmg + roots the nearest enemy in place |
| 16 | **Crown of the Void King** | Golden Helmet | **Passive, worn** | While worn: Fire Resistance, Regen III, Resistance IV (≈80% less damage), Speed + Strength — and every 25s unleashes **Royal Decree**, a 12-dmg AoE fire+poison+knockback burst around the wearer |

All 16 are tagged items (not recipes) — the only way to get one right now is
`/givepower`. If you want some of them craftable or found in loot chests
instead, tell me and I'll wire that up.

## Resource pack

`powers_resourcepack.zip` gives 13 of the 16 powers their own real 3D model
and themed texture — not a flat icon, an actual shape that rotates in your
hand:

- **A proper sword** — pommel, handle, crossguard, blade and a tapered tip —
  for Ember Core, Umbral Step, Gravitum Pull, Crimson Thirst, Venomous Bite,
  Mirror Walker and Void Ripple, each with its own glowing edge colour
  running down a dark steel blade instead of a plain icon
- **A vial** for Drakon's Maw, **wings** for Phantom Wings, a **rod** for
  Soul Shackle, and re-coloured **cubes** for Glacial Grasp and Seismic Fist
- The **Crown of the Void King**'s golden-crown icon

(First pass on these was floating crystal shards — swapped for swords
since that's what actually reads as "dangerous weapon" rather than "gem".)

Reaper's Mark (Wither Skeleton Skull), Stormcaller (Trident) and Berserker's
Wrath (Totem of Undying) are left on their normal vanilla look on purpose —
those three already have their own built-in 3D models in the game and
already look suitably epic; vanilla items like these use special hardcoded
rendering that a plain resource pack can't safely reskin without live
testing, which isn't possible in the sandbox I built this in.

**Heads-up — I couldn't preview any of this in an actual Minecraft client**,
only check that every texture and model file is validly formed. The shapes
are deliberately simple (straight-edged shards, cubes, a vial, wings) to
keep the risk of something rendering oddly low, but you're the first real
test. If anything looks off in-game, tell me what you're seeing and I'll
fix it.

Install: drop the zip straight into `.minecraft/resourcepacks` (don't
extract it) and enable it from the in-game resource pack menu. For everyone
on the server to see it automatically without installing it themselves,
you'd host the zip somewhere with a direct link and set
`resource-pack=<url>` in `server.properties` — not necessary for
solo/local testing.

Note: this reskins the **item icon** (what you see in your hand, on the
ground, and in inventory). Changing how the Crown looks *on your head*
while worn needs a separate armor-layer texture — say the word if you want
that built next.
