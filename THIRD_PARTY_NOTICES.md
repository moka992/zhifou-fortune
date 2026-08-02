# Third-Party Notices

## Kanseki Repository: KR1a0001 周易(正文)

- Project page: https://www.kanripo.org/text/KR1a0001/
- Source repository: https://github.com/kanripo/KR1a0001
- Source revision: `8284adbf9e3435d713180e24f05bf75f8b7d1d96`
- License: Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)
- License URL: https://creativecommons.org/licenses/by-sa/4.0/

The offline corpus in
`app/src/main/java/com/zhifou/fortune/ZhouyiClassics.kt` extracts the hexagram
judgments, six line texts, 用九/用六, 彖 commentary, and 象 commentary from this
source, including the 小象 commentary associated with each line. Changes
consist of removing page markers and whitespace, joining text fragments,
normalizing section labels, and generating Kotlin data structures.
The adapted corpus is distributed under CC BY-SA 4.0. Kanseki Repository does
not endorse this application.

The changing-line selection behavior is identified in the application as the
framework recorded in Zhu Xi's public-domain `易学启蒙·考变占`. It is presented
as one traditional framework rather than the only method of interpretation.

## Rider-Waite-Smith tarot deck and The Pictorial Key to the Tarot

- Card collection: https://commons.wikimedia.org/wiki/Category:Rider-Waite_tarot_deck_(Roses_%26_Lilies)
- Card-back file: https://commons.wikimedia.org/wiki/File:Waite%E2%80%93Smith_Tarot_Roses_and_Lilies_cropped.jpg
- Interpretive source: https://en.wikisource.org/wiki/The_Pictorial_Key_to_the_Tarot
- Artwork: Pamela Colman Smith, first published by Rider in 1909
- Text: Arthur Edward Waite, first published in 1910
- Status: Public domain

The application bundles the complete 78-card Rider-Waite-Smith deck and the
Roses and Lilies card back. Wikimedia Commons marks these source files as
public domain. They were published before 1931 and are public domain in the
United States; Commons also identifies them as public domain in their country
of origin.

The ten spread positions and historical baseline meanings are based on
Waite's public-domain *The Pictorial Key to the Tarot*. Chinese descriptions
and meanings in `TarotDeck.kt` are original concise modern summaries rather
than text copied from a contemporary commercial translation. A copy of this
notice is bundled at `app/src/main/assets/licenses/RIDER_WAITE_SMITH.txt`.

## sherpa-onnx

- Project: https://github.com/k2-fsa/sherpa-onnx
- Version: 1.13.2
- License: Apache License 2.0

## Whisper tiny multilingual model

- Model files: `tiny-encoder.int8.onnx`, `tiny-decoder.int8.onnx`, `tiny-tokens.txt`
- Export source: https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny
- Upstream project: https://github.com/openai/whisper
- License: MIT License

The Whisper MIT License text is included in `app/src/main/assets/licenses/Whisper-MIT.txt`.

## lunar-java

- Project: https://github.com/6tail/lunar-java
- Version: 1.7.7
- License: MIT License

The MIT License text is included in `app/src/main/assets/licenses/lunar-java-MIT.txt`.
