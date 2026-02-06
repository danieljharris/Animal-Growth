# Life Cycle - Hytale Plugin

# 🐾 Life Cycle – Animals Grow

[![GitHub Repo](https://img.shields.io/badge/GitHub-Animal_Growth-blue?logo=github)](https://github.com/danieljharris/Animal-Growth)

A Hytale mod that brings animal growth to life by letting baby animals mature naturally over time — just like crops 🌱

---

## ✨ Features

### 1) Animals Grow Over Time
- Growth is based on **in-game time** — sleeping speeds it up!
- Uses the **same time system as crops** in Hytale
- Animals take **1 in-game day** to fully grow
- Applies to **all animals**, whether:
  - Naturally spawned by the world
  - Manually spawned by players

### 2) Fence Detection
- Animals **stay inside pens** as they grow
- No growing into or clipping through:
  - Fences
  - Walls
  - Other solid blocks

### 3) Groth Particle Effects
- A **green glow effect** when animals grow up
- Makes it easy to spot when babies mature

---

## 🚧 Planned Features

- 🧬 **Breeding system**
- 📘 **Breeding book**
  - Shows which food each animal needs
- 🏷️ **Nameplates**
  - Name and identify your animals


# Details About the Repo & Code/Test Structure
## Scripts
### Quick Start (Build, Deploy, and Run Server)
```bash
./run.sh
```
### Deploy to server mods (and automatically reload the plugin on the server):
```bash
./deploy.sh
```
### Build in test framework
Launch Command Forwarder (allows plugins to send OP commands to server, use with caution and never on public servers!)
```bash
./commandForwarder.sh 
```
Run Tests
```bash
./test.sh
```

## Connecting to the Server
To find the server IP address:
```bash
hostname -I
```
Connect your Hytale client to the **first IP address** returned (typically the container's network IP).

Example: If `hostname -I` returns `172.17.0.2 172.18.0.1`, connect to `172.17.0.2:5520`


## Useful Commands For Developing Mods in Hytale
### Search for Hytale import
Swap out "DefaultEntityStatTypes|EntityStatTypes"
```bash
find . -type f -name "*.jar" -print0 | xargs -0 -n1 sh -c 'jar tf "$0" 2>/dev/null | rg "DefaultEntityStatTypes|EntityStatTypes" -n --no-line-number && echo "-- in: $0"'
```
```
jar tf /workspace/.local-assets/HytaleServer.jar | rg "particle"
```
```
jar tf /workspace/.local-assets/HytaleServer.jar | rg -i "particle" | sed -n '1,200p'
```

# Credits
This project was started from the dev container example repo by Jacob Cohen (farakovengineering.com) here: https://github.com/farakov-engineering/bazel-hytale-plugin-pipeline
