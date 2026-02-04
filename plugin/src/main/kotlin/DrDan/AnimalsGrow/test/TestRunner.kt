package DrDan.AnimalsGrow.test

import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.server.npc.NPCPlugin
import com.hypixel.hytale.server.npc.entities.NPCEntity
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.component.RemoveReason

import DrDan.AnimalsGrow.AnimalsGrow
import DrDan.AnimalsGrow.grow_ecs.AnimalsGrowComponent

import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer

/**
 * Simple in-game test framework for AnimalsGrow plugin.
 * Outputs structured log messages that can be parsed by test.sh
 * 
 * Log format:
 *  [AG_TEST:START]
 *  [AG_TEST:START:${name}]
 *  [AG_TEST:END:${name}:PASS]
 *  [AG_TEST:END:${name}:FAIL:${reason}]
 *  [AG_TEST:END:${name}:FAIL:Exception:${reason}]"
 */
object TestRunner {
    private val tests = mutableListOf<TestCase>()
    
    init {
        // Register all tests
        tests.add(BabyGrowTest())
        tests.add(BabyHasGrowthComponentTest())
    }
    
    fun runAllTests(world: World, store: Store<EntityStore>, playerPosition: Vector3d) {
        println("[AG_TEST:START:${tests.size}]")
        println("Running ${tests.size} tests...")
        
        Thread {
            for (test in tests) {
                try {
                    test.run(world, store, playerPosition)
                } catch (e: Exception) {
                    println("[AG_TEST:END:${test.name}:FAIL:Exception:${e.message}]")
                }
            }
        }.start()
    }
    
    @JvmStatic
    fun cleanupTestEntities(store: Store<EntityStore>, name: String) {
        println("Cleaning up test entities with name: $name")
        store.forEachChunk(BiConsumer { chunk: ArchetypeChunk<EntityStore>, commandBuffer: CommandBuffer<EntityStore> ->
            for (i in 0 until chunk.size()) {
                val ref = chunk.getReferenceTo(i)
                val nameComponent: DisplayNameComponent = store.getComponent(ref, DisplayNameComponent.getComponentType()) ?: continue
                val displayName = nameComponent.displayName?.rawText ?: continue
                if (displayName == name) {
                    commandBuffer.removeEntity(ref, RemoveReason.REMOVE)
                }
            }
        })
    }
}

data class TestResult(
    val passed: Boolean,
    val reason: String = ""
)

abstract class TestCase(val name: String) {
    abstract fun run(world: World, store: Store<EntityStore>, playerPosition: Vector3d)
    fun start() {
        println("[AG_TEST:START:${name}]")
    }
    fun result(passed: Boolean, reason: String = "") {
        if (passed) {
            println("[AG_TEST:END:${name}:PASS]")
        } else {
            println("[AG_TEST:END:${name}:FAIL:$reason]")
        }
    }
}

class BabyGrowTest : TestCase("BabyGrowTest") {
    override fun run(world: World, store: Store<EntityStore>, playerPosition: Vector3d) {
        start()

        val testNPCName = "Test_BabyGrowTest"

        val spawnPos = Vector3d(playerPosition.x + 2, playerPosition.y, playerPosition.z)
        
        // Spawn a bunny
        world.execute {
            val spawnResult = NPCPlugin.get().spawnNPC(store, "Bunny", null, spawnPos, Vector3f(0f, 0f, 0f))
                ?: return@execute
                // ?: return@run TestResult(false, "spawnNPC returned null")
            
            val ref = spawnResult.first()
                ?: return@execute
                // ?: return@run TestResult(false, "spawnNPC ref is null")

            val message = Message.raw(testNPCName)
            store.replaceComponent(ref, DisplayNameComponent.getComponentType(), DisplayNameComponent(message))
        }

        println("[AG_TEST:COMMAND:time midday]")
        println("[AG_TEST:COMMAND:time midnight]")
        println("[AG_TEST:COMMAND:time midday]")

        Thread.sleep(5000) // sleep for 5 seconds to day night cycle to advance and growth to occur
        println("Checking if baby has grown...")

        val resultLatch = CountDownLatch(1)
        val found = arrayOf(false) // Use array to allow mutation within inner class
        world.execute {
            store.forEachChunk(BiConsumer { chunk: ArchetypeChunk<EntityStore>, commandBuffer: CommandBuffer<EntityStore> ->
                if (found[0]) return@BiConsumer  // Early exit if already found
                for (i in 0 until chunk.size()) {
                    if (found[0]) return@BiConsumer  // Early exit if already found

                    val entityRef = chunk.getReferenceTo(i)
                    val name: DisplayNameComponent = store.getComponent(entityRef, DisplayNameComponent.getComponentType()) ?: continue
                    val displayName = name.displayName?.rawText ?: continue

                    val npcComponentType = NPCEntity.getComponentType() as? ComponentType<EntityStore, NPCEntity> ?: continue
                    val npcEntity = store.getComponent(entityRef, npcComponentType) ?: continue
                    val npcName: String = npcEntity.roleName ?: continue

                    if (displayName == testNPCName && npcName == "Rabbit") {
                        println("Found $testNPCName as adult Rabbit!")
                        found[0] = true
                        return@BiConsumer
                    }
                }
            })
            resultLatch.countDown()
        }

        // Wait for the world thread check to complete (timeout to avoid hanging)
        try { resultLatch.await(5, TimeUnit.SECONDS) } catch (e: InterruptedException) {}

        if (found[0]) {
            result(true)
        } else {
            println("Did not find grown adult Rabbit for $testNPCName")
            result(false, "Baby did not grow into adult within expected time")
        }

        world.execute { TestRunner.cleanupTestEntities(store, testNPCName) }
    }
}

/**
 * Test: Verify spawned baby has AnimalsGrowComponent attached
 */
class BabyHasGrowthComponentTest : TestCase("BabyHasGrowthComponent") {
    override fun run(world: World, store: Store<EntityStore>, playerPosition: Vector3d) {
        start()
        val testNPCName = "Test_BabyHasGrowthComponent"

        val spawnPos = Vector3d(playerPosition.x + 2, playerPosition.y, playerPosition.z)
        
        // Spawn a bunny
        world.execute {
            val spawnResult = NPCPlugin.get().spawnNPC(store, "Bunny", null, spawnPos, Vector3f(0f, 0f, 0f))
                ?: return@execute
            
            val ref = spawnResult.first() ?: return@execute

            val message = Message.raw(testNPCName)
            store.replaceComponent(ref, DisplayNameComponent.getComponentType(), DisplayNameComponent(message))
        }

        Thread.sleep(1000) // Wait for the spawn to process

        val hasGrowthComponent = arrayOf(false)
        val checkLatch = CountDownLatch(1)

        world.execute {
            store.forEachChunk(BiConsumer { chunk: ArchetypeChunk<EntityStore>, commandBuffer: CommandBuffer<EntityStore> ->
                for (i in 0 until chunk.size()) {
                    val entityRef = chunk.getReferenceTo(i)
                    val name: DisplayNameComponent = store.getComponent(entityRef, DisplayNameComponent.getComponentType()) ?: continue
                    val displayName = name.displayName?.rawText ?: continue

                    if (displayName == testNPCName) {
                        val growthComponent = store.getComponent(entityRef, AnimalsGrow.getComponentType())
                        if (growthComponent != null) {
                            hasGrowthComponent[0] = true
                        }
                        break
                    }
                }
            })
            checkLatch.countDown()
        }

        try { checkLatch.await(5, TimeUnit.SECONDS) } catch (e: InterruptedException) {}

        if (hasGrowthComponent[0]) {
            result(true)
        } else {
            println("Spawned baby did not have AnimalsGrowComponent")
            result(false, "Spawned baby did not have AnimalsGrowComponent")
        }

        world.execute { TestRunner.cleanupTestEntities(store, testNPCName) }
    }
}