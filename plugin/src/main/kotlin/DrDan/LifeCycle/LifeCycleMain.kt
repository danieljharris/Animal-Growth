package DrDan.LifeCycle

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import DrDan.AnimalsGrow.AnimalsGrow
import org.slf4j.LoggerFactory

class LifeCycleMain(init: JavaPluginInit) : JavaPlugin(init) {
    private val logger = LoggerFactory.getLogger(LifeCycleMain::class.java)
    private lateinit var animalsGrow: AnimalsGrow
    private var animalsBreedInstance: Any? = null
    private var animalsBreedSetupMethod: java.lang.reflect.Method? = null
    private var animalsBreedStartMethod: java.lang.reflect.Method? = null

    override fun setup() {
        logger.info("LifeCycle wrapper setup")
        animalsGrow = AnimalsGrow(init)
        animalsGrow.setup()

        // Try to load AnimalsBreed reflectively — if its sources are excluded the class won't exist
        try {
            val breedClass = Class.forName("DrDan.AnimalsBreed.AnimalsBreed")
            val ctor = breedClass.getConstructor(com.hypixel.hytale.server.core.plugin.JavaPluginInit::class.java)
            animalsBreedInstance = ctor.newInstance(init)
            animalsBreedSetupMethod = breedClass.getMethod("setup")
            animalsBreedStartMethod = breedClass.getMethod("start")
            animalsBreedSetupMethod?.invoke(animalsBreedInstance)
            logger.info("AnimalsBreed loaded and setup via reflection")
        } catch (e: ClassNotFoundException) {
            logger.info("AnimalsBreed class not found on classpath — skipping")
        } catch (e: Exception) {
            logger.warn("Failed to initialize AnimalsBreed reflectively", e)
        }
    }

    override fun start() {
        logger.info("LifeCycle wrapper start")
        animalsGrow.start()
        try {
            animalsBreedStartMethod?.invoke(animalsBreedInstance)
        } catch (e: Exception) {
            if (animalsBreedInstance != null) logger.warn("Failed to start AnimalsBreed reflectively", e)
        }
    }

    override fun stop() {
        logger.info("LifeCycle wrapper stop")
        // Optional: call stop on sub-plugins if needed (not currently defined)
    }
}
