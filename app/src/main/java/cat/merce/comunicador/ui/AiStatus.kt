package cat.merce.comunicador.ui

/** What the optional AI suggestions are doing, shown under their setting. */
enum class AiStatus {
    /** Turned off, or never turned on. The normal suggestions only. */
    Off,

    /** Turned on and loading; the normal suggestions carry on meanwhile. */
    Loading,

    /** Loaded and reordering the suggestions. */
    On,

    /** This device has too little memory, so it was not started. */
    NotEnoughMemory,

    /** It could not be loaded. The normal suggestions carry on. */
    Failed,

    /** It answered too slowly too often on this device, so it switched off. */
    TooSlow,
}
