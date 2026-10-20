# Tool Card Artwork

The coin and dice catalog images are offline Blender product renders, not screenshots or alternative runtime models. Both use square framing, soft contact shadows, a bright studio setup and restrained pastel backdrops that match the tool catalog. They contain no UI labels or embedded personal data.

- `tool_coin_refined.jpg` uses the actual master mesh from `art/coin/zhifou_coin.blend`. Both the astrolabe face and numeral face, including the guilloche rim and reeded edge, are unchanged. Only the still-image composition, background and lighting differ from the interactive scene.
- `tool_dice_refined.jpg` uses two rounded six-sided dice with recessed pips. All six faces are modeled, with opposite pairs 1/6, 2/5 and 3/4. Pip patterns and right-handed face bases are checked before rendering. The second die is rigidly rotated, never relabeled to suit the camera.

Rebuild using Blender's Python:

```sh
blender --background --factory-startup --python tools/render_tool_cards.py -- --out app/src/main/res/drawable-nodpi
```

`--asset coin` or `--asset dice` renders only that card. The default source is the bundled editable coin model; `--coin-source` can select it explicitly. Outputs are 768 x 768 JPEGs, retaining the existing catalog image dimensions and decode-memory footprint. Runtime tool logic, results, haptics and models are not modified by this renderer.
