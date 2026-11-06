package vegabobo.dsusideloader.preparation

enum class InstallationStep {
    // Initial value
    NOT_INSTALLING,

    // When a DSU is detected on app startup
    DSU_ALREADY_INSTALLED,
    DSU_ALREADY_RUNNING_DYN_OS,

    // Preparation steps
    PROCESSING,
    COPYING_FILE,
    DECOMPRESSING_XZ,
    COMPRESSING_TO_GZ,
    DECOMPRESSING_GZIP,
    EXTRACTING_FILE,

    // Installation Steps
    REQUIRES_ADB_CMD_TO_CONTINUE,
    DISCARD_CURRENT_GSI,
    WAITING_USER_CONFIRMATION,
    PROCESSING_LOG_READABLE,
    INSTALLING,
    INSTALLING_ROOTED,
    CREATING_PARTITION,

    // Errors
    ERROR,
    ERROR_CANCELED,
    ERROR_REQUIRES_DISCARD_DSU,
    ERROR_ALREADY_RUNNING_DYN_OS,
    ERROR_CREATE_PARTITION,
    ERROR_EXTERNAL_SDCARD_ALLOC,
    ERROR_NO_AVAIL_STORAGE,
    ERROR_F2FS_WRONG_PATH,
    ERROR_EXTENTS,
    ERROR_SELINUX,
    ERROR_SELINUX_ROOTLESS,

    // Once installation is finished
    INSTALL_SUCCESS,
    INSTALL_SUCCESS_REBOOT_DYN_OS,
}

fun InstallationStep.isActiveInstallationStep(): Boolean = when (this) {
    InstallationStep.PROCESSING, InstallationStep.COPYING_FILE, InstallationStep.DECOMPRESSING_XZ,
    InstallationStep.COMPRESSING_TO_GZ, InstallationStep.DECOMPRESSING_GZIP, InstallationStep.EXTRACTING_FILE,
    InstallationStep.DISCARD_CURRENT_GSI, InstallationStep.WAITING_USER_CONFIRMATION,
    InstallationStep.PROCESSING_LOG_READABLE, InstallationStep.INSTALLING,
    InstallationStep.INSTALLING_ROOTED, InstallationStep.CREATING_PARTITION -> true
    else -> false
}
