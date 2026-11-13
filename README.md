<p align="center">
  <img src="docs/assets/hero.svg" alt="DSU Sideloader — Material and Miuix. Your Android, another possibility." width="100%">
</p>

<p align="center">
  Try compatible Android system images with a cleaner workspace.<br>
  Material and Miuix interfaces. Your colors. Your icons.
</p>

<p align="center">
  <a href="https://github.com/faridbtw/DSU-Sideloader/releases/latest/download/Dsu-Sideloader.apk"><img src="docs/assets/download.svg" alt="Download APK — Dsu-Sideloader.apk" width="260"></a>
</p>

<p align="center">
  <a href="https://github.com/faridbtw/DSU-Sideloader/releases">All releases</a> &nbsp;·&nbsp;
  <a href="#get-started">Get started</a> &nbsp;·&nbsp;
  <a href="https://github.com/faridbtw/DSU-Sideloader/issues">Report an issue</a>
</p>

<p align="center"><strong>Android 10+</strong> &nbsp; / &nbsp; <strong>Material + Miuix</strong> &nbsp; / &nbsp; <strong>Open source</strong></p>

## A simpler way to sideload

Choose an image from your folder, adjust storage if needed, and follow the installation. This independent fork refreshes DSU Sideloader’s interface while retaining its original installation engine.

| Your systems, together | A workspace that feels yours |
| --- | --- |
| **Scrollable image library** with clear file types, sizes, selection indicators, and a manual file picker. | **Material or Miuix**, with a floating dock and smooth page transitions. |
| **Focused installation controls**, expandable storage options, progress, and logs. | **Light, dark, and AMOLED**, optional Monet, key colors, and palette styles. |
| **Operation-mode selection** for Automatic, ADB, Root, Shizuku, and System access. | **13 launcher icons**, including the original stock blue icon. |
| **Live SELinux status** with a read-only root check and manual refresh. | **Independent list scrolling** that keeps Home still while you browse images. |

## Get started

**[Download Dsu-Sideloader.apk](https://github.com/faridbtw/DSU-Sideloader/releases/latest/download/Dsu-Sideloader.apk)** from the latest release and install it on your phone.

1. Grant access to the folder containing your images.
2. Select an image from the list, or tap **Browse file**. Supported formats: `.img`, `.gz`, `.xz`, and DSU `.zip` packages on Android 11+.
3. Expand **Installation options** if you need a custom userdata allocation.
4. Tap **Install**, review the confirmation, and follow the prompts.
5. Once installation finishes, boot the image using the app or Android’s DSU notification.

A compatible GSI, dynamic partitions, and usually an unlocked bootloader are required. Device and image compatibility determine whether installation and boot succeed. A normal reboot returns to the original system unless sticky mode is enabled.

<details>
<summary><strong>Updating from another version</strong></summary>

Release APKs use this fork’s debug signing key. They can update matching previously supplied APKs. The original upstream app uses a different key; back up anything you need before replacing it. The upstream in-app updater is disabled in this fork. Download updates from this repository’s [Releases](https://github.com/faridbtw/DSU-Sideloader/releases).

</details>

<details>
<summary><strong>Operation modes</strong></summary>

**Automatic** selects the best available mode. You can also choose a mode under **Settings → Operation mode**. Applying a choice restarts the app to reconnect its service. Unavailable modes fall back to ADB; the active mode appears below the selector. Changes are locked during installation.

| Mode | Available behavior |
| --- | --- |
| **ADB** | Prepares the image and supplies the command for Android’s DSU installer. |
| **Shizuku** | Starts the system installer without a separate PC command. |
| **Root** | Adds DSU controls and access to the optional experimental built-in installer. |
| **System / Root + System** | Uses the upstream privileged integration for additional features. |

The built-in installer is off by default. The SELinux check runs `getenforce` in a verified root shell and only reads the current status.

See the [upstream guide](docs/UPSTREAM.md#operation-modes) for device requirements and limitations.

</details>

## Credits

Maintained by [faridbtw](https://github.com/faridbtw), based on [VegaBobo/DSU-Sideloader](https://github.com/VegaBobo/DSU-Sideloader). Original blue icon and upstream design by [WSTxda](https://github.com/WSTxda).

The appearance controls reference [KowSU Pro](https://github.com/KOWX712/KernelSU), independently implemented using [Miuix](https://github.com/miuix-kotlin-multiplatform/miuix) (Apache 2.0) and [MaterialKolor](https://github.com/jordond/MaterialKolor) (MIT). Thanks to the upstream contributors and translators.

<p align="center">
  <a href="LICENSE">Apache License 2.0</a> &nbsp;·&nbsp;
  <a href="docs/DEVELOPMENT.md">Developer guide</a> &nbsp;·&nbsp;
  <a href="docs/UPSTREAM.md">Upstream documentation</a>
</p>
