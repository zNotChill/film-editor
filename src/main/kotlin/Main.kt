
import commands.CreateMovie
import de.articdive.jnoise.core.api.functions.Interpolation
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction
import de.articdive.jnoise.pipeline.JNoise
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.server.ServerListPingEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.generator.GenerationUnit
import net.minestom.server.ping.ResponseData
import net.minestom.server.utils.chunk.ChunkSupplier
import net.minestom.server.utils.time.TimeUnit
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("FilmEditor")

fun main(args: Array<String>) {
    val server = MinecraftServer.init()

    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    val noise = JNoise.newBuilder()
        .perlin(3301, Interpolation.COSINE, FadeFunction.QUINTIC_POLY)
        .build()

    instanceContainer.setGenerator { unit: GenerationUnit ->
//        unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK)
        val start = unit.absoluteStart()
        for (x in 0 until unit.size().x().toInt()) {
            for (z in 0 until unit.size().z().toInt()) {
                val bottom = start.add(x.toDouble(), 0.0, z.toDouble())

                synchronized(noise) {
                    val height = noise.evaluateNoise(bottom.x(), bottom.z()) * 16

                    unit.modifier().fill(bottom, bottom.add(1.0, 0.0, 1.1).withY(height), Block.CYAN_TERRACOTTA)
                }
            }
        }
    }

    instanceContainer.chunkSupplier = ChunkSupplier { i, x, z -> LightingChunk(i, x, z) }

    val globalEventHandler = MinecraftServer.getGlobalEventHandler()

    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        val player = event.player

        event.spawningInstance = instanceContainer
        player.respawnPoint = Pos(0.0, 42.0, 0.0)

        player.permissionLevel = 4
    }

    globalEventHandler.addListener(ServerListPingEvent::class.java) { event ->
        val responseData = ResponseData()

        responseData.description = Component.text("FilmEditor v1").color(TextColor.color(100, 255, 0))
        responseData.version = "1.21.3"
        responseData.maxPlayer = 100

        event.responseData = responseData
    }

    MinecraftServer.getCommandManager().register(CreateMovie())

    val scheduler = MinecraftServer.getSchedulerManager()
    scheduler.buildShutdownTask {
        logger.info("Server is shutting down. Saving all chunks.")
        instanceManager.instances.forEach { instance -> instance.saveChunksToStorage() }
    }

    scheduler.buildTask {
        logger.info("Saving all instances...")
        instanceManager.instances.forEach { instance -> instance.saveChunksToStorage() }
    }
        .repeat(30, TimeUnit.SECOND)
        .delay(1, TimeUnit.MINUTE)
        .schedule()

    MojangAuth.init()
    server.start("localhost", 25566)
    logger.info("Server started")
}