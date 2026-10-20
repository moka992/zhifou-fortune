# Presentation Assets

The PNGs are captures of the actual Android UI on a physical device using [isolated showcase data](../../app/src/debug/java/com/zhifou/fortune/ShowcaseActivity.kt). They contain no real account profile, question, schedule or API credential. Review captures before publication; an activity guard is not a substitute for checking notification overlays.

- `home.png`: the offline daily overview.
- `calendar.png`: the Gregorian/lunar month view.
- `tarot-spread.png`: a completed four-card manual draw; the example outcome is not a promise about future draws.
- `coin-result.png`: the rendered original double-sided 3D coin.
- `dice-result.png`: the six-die outcome from a real simulation.
- `tarot-demo.gif`: an excerpt of the physical-device recording, resized and encoded for the README, not a rendered concept animation.
- `social-preview.svg`: editable code-native layout that places unretouched screenshots beside the project name. Recreate it with `python3 tools/render_promo.py`.
- `social-preview.jpg`: a 1280 x 640 raster export of that layout, below GitHub's 1 MB social-preview limit. Export with `python3 tools/render_promo.py --raster docs/media/social-preview.jpg --font /path/to/cjk-font.ttf` and Pillow installed. Font rendering can differ between systems. This is presentation artwork, not another application screen.

The MP4 recordings are attached to the v1.1.0 Release instead of being committed to Git. Container metadata is removed; there is no recorded audio. See [the showcase](../showcase.md) for links and limitations. Historical tarot artwork keeps its separate attribution in [third-party notices](../../THIRD_PARTY_NOTICES.md); arranging it inside screenshots does not change that attribution.

These changes do not grant an open-source license to the Android application's original code or assets.

## Set the Social Preview

GitHub's documented social-preview upload is in repository **Settings > General > Social preview > Edit**. Upload `social-preview.jpg`. Merely committing this file or adding it to the README does **not** set that account-level repository image.

Official format/size guidance: [GitHub Docs](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/customizing-your-repositorys-social-media-preview). This project does not claim that the setting has been changed automatically.
