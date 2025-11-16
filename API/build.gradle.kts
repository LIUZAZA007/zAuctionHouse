plugins {
    id("re.alwyn974.groupez.publish") version "1.0.0"
}

rootProject.extra.properties["sha"]?.let { sha ->
    version = sha
}

tasks {
    shadowJar {

        relocate("fr.maxlego08.sarah", "fr.maxlego08.zauctionhouse.libs.sarah")
        relocate("com.tcoded.folialib", "fr.maxlego08.zauctionhouse.libs.folialib")

        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }
}

publishConfig {
    githubOwner.set("GroupeZ-dev")
}
