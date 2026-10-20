# 3D Coin

The coin is an original double-sided model built in Blender. Its two faces were redesigned together: an eight-point astrolabe/compass relief with orbital graduations and scrollwork on the pattern face; a classical numeral in a fluted oval cartouche, flanked by acanthus branches, on the numeric face. Neither face retains the previous daisy relief or block numeral. These are original decorative compositions, not reproductions of official currency.

Interlaced guilloche, pearl borders, engine-turned lines, curved inscriptions, faceted relief and a milled edge are actual geometry. Five metallic-roughness PBR materials distinguish a frosted silver field, mirror-silver highlights, satin/polished champagne gold and recessed bronze details. No flat image substitutes for either face.

- Editable Blender source is generated locally, not committed: authoring files can retain workstation paths. Recreate it with the generator below; the standalone runtime GLB is included.
- Reproducible generator: `tools/build_coin_model.py --out <output-directory>` (run with Blender's Python). The bundled Cinzel Decorative Bold build font is the default; `--font` can select another licensed font explicitly.
- Runtime asset: `app/src/main/assets/models/zhifou_coin.glb`.
- Pattern normal: glTF +Z. Number normal: glTF -Z. The existing side-to-result mapping is unchanged.
- Ten nodes share one mesh and five materials; 36,852 triangles per visible coin, with a 40,000-triangle contract enforced by `CoinModelAssetTest`. The asset stays below 2 MiB and uses no external resources or image textures.
- Font attribution/license: `art/coin/fonts/OFL.txt` and `THIRD_PARTY_NOTICES.md`. Font outlines are converted to mesh, not loaded at runtime.

Google Filament renders all visible coins in one transparent GPU surface. The model is loaded from packaged assets without networking. A small studio reflection cubemap and directional lights illuminate the PBR surfaces. Bright neutral studio fill lifts the metallic midtones while the softboxes produce moving highlights on the polished relief and rim. Reflection lobes retain their energy as rougher mip levels broaden, so the frosted field is not washed out by progressively brighter reflections. Rotation changes the actual model transform, not a single image or a label. Rendering stops when the scene is idle or the activity is paused, and the scene releases its GPU resources when leaving the tool.

The result generator remains `SecureRandom.nextBoolean()`: each coin independently has a 50:50 chance. Rendering does not draw a second result. The existing one-to-ten count selector, shake trigger, haptics and recent results remain unchanged.
