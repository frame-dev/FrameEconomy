# FrameEconomy

FrameEconomy is a Spigot economy plugin for Minecraft servers. It integrates with Vault, supports player balances and banks, and can store data in local YAML, SQLite, MySQL, or MongoDB.

## Features

- Vault economy provider
- Player balance commands
- Player-to-player payments
- Bank creation, deposits, withdrawals, members, and balance checks
- Configurable command messages in `config.yml`
- Async command/storage work for better server-thread performance
- YAML, SQLite, MySQL, and MongoDB storage options

## Requirements

- Java 8+ target runtime
- Spigot/Paper API compatible with `1.16.1-R0.1-SNAPSHOT`
- Vault installed on the server
- Gradle wrapper included in this repository

## Build

```bash
git clone https://github.com/frame-dev/FrameEconomy
cd FrameEconomy
./gradlew build shadowJar
```

On Windows:

```powershell
.\gradlew.bat build shadowJar
```

The server plugin jar is generated at:

```text
build/libs/FrameEconomy-2.0-RELEASE.jar
```

## Installation

1. Build the plugin or download the jar.
2. Copy `FrameEconomy-2.0-RELEASE.jar` into your server `plugins` folder.
3. Make sure Vault is installed.
4. Start the server once to generate `config.yml`.
5. Edit the config and restart or run `/frameeconomy reload`.

## Configuration

Storage is configured in `config.yml`.

```yaml
MySQL:
  Use: false
SQLite:
  Use: false
MongoDB:
  Use: false
Currency:
  Plural: '$'
  Singular: '$'
Economy:
  MaxBalance: 1000000000
```

If MySQL, SQLite, and MongoDB are disabled, FrameEconomy uses local YAML storage in `plugins/FrameEconomy/money/eco.yml`.

## Messages

Player-facing messages live under `Messages:` in `config.yml`.

```yaml
Messages:
  general:
    no-permission: '&cNo Permissions!'
  pay:
    sent: '&aYou gave &6{player} {amount}{currency}'
```

Color codes can use `&`. Supported placeholders depend on the message and include `{player}`, `{amount}`, `{currency}`, `{bank}`, `{version}`, and `{enabled}`.

## Commands

| Command | Description |
| --- | --- |
| `/balance` or `/bal` | Show your balance |
| `/balance <player>` | Show another player's balance |
| `/balancetop` or `/baltop` | Show top balances |
| `/pay <amount> <player>` | Pay another player |
| `/pay <amount> <player> <percent>` | Pay another player with a percentage bonus |
| `/economy status` | Show Vault status |
| `/economy set <amount>` | Set your own balance |
| `/economy set <amount> <player>` | Set another player's balance |
| `/bank list` | List banks |
| `/bank create <name>` | Create a bank |
| `/bank remove <name>` | Remove a bank |
| `/bank balance <name>` | Show a bank balance |
| `/bank deposit <name> <amount>` | Deposit into a bank |
| `/bank withdraw <name> <amount>` | Withdraw from a bank |
| `/bank addmember <name> <player>` | Add a bank member |
| `/bank removemember <name> <player>` | Remove a bank member |
| `/bank listmembers <name>` | List bank members |
| `/frameeconomy reload` | Reload the plugin config |

## Permissions

The root permission is:

```text
frameeconomy.*
```

Common permissions include:

```text
frameeconomy.balance
frameeconomy.balance.others
frameeconomy.balancetop
frameeconomy.pay
frameeconomy.eco.status
frameeconomy.eco.set
frameeconomy.eco.set.others
frameeconomy.bank.create
frameeconomy.bank.remove
frameeconomy.bank.balance
frameeconomy.bank.deposit
frameeconomy.bank.withdraw
frameeconomy.bank.addmember
frameeconomy.bank.removemember
frameeconomy.bank.listmembers
frameeconomy.bank.list
frameeconomy.reload
```

## Developer Notes

- Gradle wrapper: `8.7`
- Kotlin: `1.9.24`
- Shadow jar plugin: `8.1.1`
- Java bytecode target: `1.8`

Most command storage work is scheduled asynchronously, while Bukkit-facing messages are sent back on the server thread.
