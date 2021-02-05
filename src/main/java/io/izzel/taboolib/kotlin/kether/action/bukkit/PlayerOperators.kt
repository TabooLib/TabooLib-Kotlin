package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.taboolib.TabooLibAPI

enum class PlayerOperators(val operator: PlayerOperator) {

    LOCATION(
        PlayerOperator(
            read = {
                it.location
            },
            write = { p, _, v ->
                p.teleport(v as org.bukkit.Location)
            }
        )
    ),

    LOCALE(
        PlayerOperator(
            read = {
                it.locale
            })
    ),

    WORLD(
        PlayerOperator(
            read = {
                it.location.world?.name
            })
    ),

    X(
        PlayerOperator(
            read = {
                it.location.x
            })
    ),

    Y(
        PlayerOperator(
            read = {
                it.location.y
            })
    ),

    Z(
        PlayerOperator(
            read = {
                it.location.z
            })
    ),

    YAW(
        PlayerOperator(
            read = {
                it.location.yaw
            },
            write = { p, a, v ->
                p.teleport(p.location.also {
                    if (a == Symbol.ADD) {
                        it.yaw += io.izzel.taboolib.util.Coerce.toFloat(v)
                    } else if (a == Symbol.SET) {
                        it.yaw = io.izzel.taboolib.util.Coerce.toFloat(v)
                    }
                })
            })
    ),

    PITCH(
        PlayerOperator(
            read = {
                it.location.pitch
            },
            write = { p, a, v ->
                p.teleport(p.location.also {
                    if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                        it.pitch += io.izzel.taboolib.util.Coerce.toFloat(v)
                    } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                        it.pitch = io.izzel.taboolib.util.Coerce.toFloat(v)
                    }
                })
            })
    ),

    BLOCK_X(
        PlayerOperator(
            read = {
                it.location.blockX
            })
    ),

    BLOCK_Y(
        PlayerOperator(
            read = {
                it.location.blockY
            })
    ),

    BLOCK_Z(
        PlayerOperator(
            read = {
                it.location.blockZ
            })
    ),

    COMPASS_X(
        PlayerOperator(
            read = {
                it.compassTarget.x
            })
    ),

    COMPASS_Y(
        PlayerOperator(
            read = {
                it.compassTarget.y
            })
    ),

    COMPASS_Z(
        PlayerOperator(
            read = {
                it.compassTarget.z
            })
    ),

    COMPASS_TARGET(
        PlayerOperator(
            read = {
                it.compassTarget
            },
            write = { p, _, v ->
                p.compassTarget = v as org.bukkit.Location
            }
        )
    ),

    BED_SPAWN_X(
        PlayerOperator(
            read = {
                it.bedSpawnLocation?.x
            }
        )
    ),

    BED_SPAWN_Y(
        PlayerOperator(
            read = {
                it.bedSpawnLocation?.y
            }
        )
    ),

    BED_SPAWN_Z(
        PlayerOperator(
            read = {
                it.bedSpawnLocation?.z
            }
        )
    ),

    BED_SPAWN(
        PlayerOperator(
            read = {
                it.bedSpawnLocation
            },
            write = { p, _, v ->
                p.bedSpawnLocation = v as org.bukkit.Location
            }
        )
    ),

    NAME(
        PlayerOperator(
            read = {
                it.name
            })
    ),

    DISPLAY_NAME(
        PlayerOperator(
            read = {
                it.displayName
            },
            write = { p, _, v ->
                p.setDisplayName(v.toString())
            })
    ),

    LIST_NAME(
        PlayerOperator(
            read = {
                it.playerListName
            },
            write = { p, _, v ->
                p.setPlayerListName(v.toString())
            })
    ),

    GAMEMODE(
        PlayerOperator(
            read = {
                it.gameMode.name
            },
            write = { p, _, v ->
                when (v.toString()) {
                    "SURVIVAL", "0" -> p.gameMode = org.bukkit.GameMode.SURVIVAL
                    "CREATIVE", "1" -> p.gameMode = org.bukkit.GameMode.CREATIVE
                    "ADVENTURE", "2" -> p.gameMode = org.bukkit.GameMode.ADVENTURE
                    "SPECTATOR", "3" -> p.gameMode = org.bukkit.GameMode.SPECTATOR
                }
            })
    ),

    ADDRESS(
        PlayerOperator(
            read = {
                it.address?.hostName
            }
        )
    ),

    SNEAKING(
        PlayerOperator(
            read = {
                it.isSneaking
            }
        )
    ),

    SPRINTING(
        PlayerOperator(
            read = {
                it.isSprinting
            }
        )
    ),

    BLOCKING(
        PlayerOperator(
            read = {
                it.isBlocking
            }
        )
    ),

    GLIDING(
        PlayerOperator(
            read = {
                it.isGliding
            }
        )
    ),

    SWIMMING(
        PlayerOperator(
            read = {
                it.isSwimming
            }
        )
    ),

    RIPTIDING(
        PlayerOperator(
            read = {
                it.isRiptiding
            }
        )
    ),

    SLEEPING(
        PlayerOperator(
            read = {
                it.isSleeping
            }
        )
    ),

    SLEEP_TICKS(
        PlayerOperator(
            read = {
                it.sleepTicks
            }
        )
    ),

    DEAD(
        PlayerOperator(
            read = {
                it.isDead
            }
        )
    ),

    CONVERSING(
        PlayerOperator(
            read = {
                it.isConversing
            }
        )
    ),

    LEASHED(
        PlayerOperator(
            read = {
                it.isLeashed
            }
        )
    ),

    ON_GROUND(
        PlayerOperator(
            read = {
                it.isOnGround
            }
        )
    ),

    INSIDE_VEHICLE(
        PlayerOperator(
            read = {
                it.isInsideVehicle
            }
        )
    ),

    OP(
        PlayerOperator(
            read = {
                it.isOp
            },
            write = { p, _, v ->
                p.isOp = io.izzel.taboolib.util.Coerce.toBoolean(v)
            }
        )
    ),

    GRAVITY(
        PlayerOperator(
            read = {
                it.hasGravity()
            },
            write = { p, _, v ->
                p.setGravity(io.izzel.taboolib.util.Coerce.toBoolean(v))
            }
        )
    ),

    ATTACK_COOLDOWN(
        PlayerOperator(
            read = {
                it.attackCooldown
            }
        )
    ),

    PLAYER_TIME(
        PlayerOperator(
            read = {
                it.playerTime
            }
        )
    ),

    FIRST_PLAYED(
        PlayerOperator(
            read = {
                it.firstPlayed
            }
        )
    ),

    LAST_PLAYED(
        PlayerOperator(
            read = {
                it.lastPlayed
            }
        )
    ),

    ABSORPTION_AMOUNT(
        PlayerOperator(
            read = {
                it.absorptionAmount
            },
            write = { p, a, v ->
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.absorptionAmount += io.izzel.taboolib.util.Coerce.toDouble(v)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.absorptionAmount = io.izzel.taboolib.util.Coerce.toDouble(v)
                }
            }
        )
    ),

    NO_DAMAGE_TICKS(
        PlayerOperator(
            read = {
                it.noDamageTicks
            },
            write = { p, a, v ->
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.noDamageTicks += io.izzel.taboolib.util.Coerce.toInteger(v)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.noDamageTicks = io.izzel.taboolib.util.Coerce.toInteger(v)
                }
            }
        )
    ),

    REMAINING_AIR(
        PlayerOperator(
            read = {
                it.remainingAir
            },
            write = { p, a, v ->
                val d = io.izzel.taboolib.util.Coerce.toInteger(v)
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.remainingAir = (p.remainingAir + d).coerceAtMost(20).coerceAtLeast(0)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.remainingAir = d.coerceAtMost(20).coerceAtLeast(0)
                }
            }
        )
    ),

    MAXIMUM_AIR(
        PlayerOperator(
            read = {
                it.maximumAir
            },
            write = { p, a, v ->
                val d = io.izzel.taboolib.util.Coerce.toInteger(v)
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.maximumAir = (p.maximumAir + d).coerceAtMost(20).coerceAtLeast(0)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.maximumAir = d.coerceAtMost(20).coerceAtLeast(0)
                }
            }
        )
    ),

    EXP_UNTIL_NEXT_LEVEL(
        PlayerOperator(
            read = {
                io.izzel.taboolib.cronus.CronusUtils.getExpUntilNextLevel(it)
            }
        )
    ),

    EXP_AT_LEVEL(
        PlayerOperator(
            read = {
                io.izzel.taboolib.cronus.CronusUtils.getExpAtLevel(it.level)
            }
        )
    ),

    EXP_TO_LEVEL(
        PlayerOperator(
            read = {
                io.izzel.taboolib.cronus.CronusUtils.getExpToLevel(it.level)
            }
        )
    ),

    EXP(
        PlayerOperator(
            read = {
                io.izzel.taboolib.cronus.CronusUtils.getTotalExperience(it)
            },
            write = { p, a, v ->
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.giveExp(io.izzel.taboolib.util.Coerce.toInteger(v))
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    io.izzel.taboolib.cronus.CronusUtils.setTotalExperience(p, io.izzel.taboolib.util.Coerce.toInteger(v))
                }
            }
        )
    ),

    LEVEL(
        PlayerOperator(
            read = {
                it.level
            },
            write = { p, a, v ->
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.level += io.izzel.taboolib.util.Coerce.toInteger(v)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.level = io.izzel.taboolib.util.Coerce.toInteger(v)
                }
            }
        )
    ),

    EXHAUSTION(
        PlayerOperator(
            read = {
                it.exhaustion
            },
            write = { p, a, v ->
                val f = io.izzel.taboolib.util.Coerce.toFloat(v)
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.exhaustion = (p.exhaustion + f).coerceAtMost(20f).coerceAtLeast(0f)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.exhaustion = f.coerceAtMost(20f).coerceAtLeast(0f)
                }
            }
        )
    ),

    SATURATION(
        PlayerOperator(
            read = {
                it.saturation
            },
            write = { p, a, v ->
                val f = io.izzel.taboolib.util.Coerce.toFloat(v)
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.saturation = (p.saturation + f).coerceAtMost(20f).coerceAtLeast(0f)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.saturation = f.coerceAtMost(20f).coerceAtLeast(0f)
                }
            }
        )
    ),

    FOOD_LEVEL(
        PlayerOperator(
            read = {
                it.foodLevel
            },
            write = { p, a, v ->
                val d = io.izzel.taboolib.util.Coerce.toInteger(v)
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.foodLevel = (p.foodLevel + d).coerceAtMost(20).coerceAtLeast(0)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.foodLevel = d.coerceAtMost(20).coerceAtLeast(0)
                }
            }
        )
    ),

    HEALTH(
        PlayerOperator(
            read = {
                it.health
            },
            write = { p, a, v ->
                val d = io.izzel.taboolib.util.Coerce.toDouble(v)
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.health = (p.health + d).coerceAtMost(p.maxHealth).coerceAtLeast(0.0)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.health = d.coerceAtMost(p.maxHealth).coerceAtLeast(0.0)
                }
            }
        )
    ),

    MAX_HEALTH(
        PlayerOperator(
            read = {
                it.maxHealth
            },
            write = { p, a, v ->
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.maxHealth += io.izzel.taboolib.util.Coerce.toDouble(v)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.maxHealth = io.izzel.taboolib.util.Coerce.toDouble(v)
                }
            }
        )
    ),

    ALLOW_FLIGHT(
        PlayerOperator(
            read = {
                it.allowFlight
            },
            write = { p, _, v ->
                p.allowFlight = io.izzel.taboolib.util.Coerce.toBoolean(v)
            }
        )
    ),

    FLYING(
        PlayerOperator(
            read = {
                it.isFlying
            },
            write = { p, _, v ->
                p.isFlying = io.izzel.taboolib.util.Coerce.toBoolean(v)
            }
        )
    ),

    FLY_SPEED(
        PlayerOperator(
            read = {
                it.flySpeed
            },
            write = { p, a, v ->
                val f = io.izzel.taboolib.util.Coerce.toFloat(v)
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.flySpeed = (p.flySpeed + f).coerceAtMost(0.99f).coerceAtLeast(0f)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.flySpeed = f.coerceAtMost(0.99f).coerceAtLeast(0f)
                }
            }
        )
    ),

    WALK_SPEED(
        PlayerOperator(
            read = {
                it.walkSpeed
            },
            write = { p, a, v ->
                val f = io.izzel.taboolib.util.Coerce.toFloat(v)
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    p.walkSpeed = (p.walkSpeed + f).coerceAtMost(0.99f).coerceAtLeast(0f)
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    p.walkSpeed = f.coerceAtMost(0.99f).coerceAtLeast(0f)
                }
            }
        )
    ),

    BALANCE(
        PlayerOperator(
            read = {
                TabooLibAPI.getPluginBridge().economyLook(it)
            },
            write = { p, a, v ->
                if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.ADD) {
                    io.izzel.taboolib.module.compat.EconomyHook.add(p, io.izzel.taboolib.util.Coerce.toDouble(v))
                } else if (a == io.izzel.taboolib.kotlin.kether.action.bukkit.Symbol.SET) {
                    io.izzel.taboolib.module.compat.EconomyHook.set(p, io.izzel.taboolib.util.Coerce.toDouble(v))
                }
            }
        )
    ),

    REGION(
        PlayerOperator(
            read = {
                val region = io.izzel.taboolib.TabooLibAPI.getPluginBridge().worldguardGetRegion(it.world, it.location)
                if (region!!.isEmpty()) "__global__" else region
            }
        )
    ),

    VERSION(
        PlayerOperator(
            read = {
                io.izzel.taboolib.TabooLibAPI.getPluginBridge().viaVersionPlayerVersion(it)
            }
        )
    ),
}