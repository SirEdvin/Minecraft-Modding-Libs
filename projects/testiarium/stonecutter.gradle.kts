plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.20.1"

stonecutter parameters {
    val loader = node.branch.id.ifEmpty { "common" }
    constants.match(loader, "common", "fabric", "forge")
    constants["neoforge"] = loader == "forge" && current.parsed >= "1.21"
    constants["legacyforge"] = loader == "forge" && current.parsed < "1.21"
}
