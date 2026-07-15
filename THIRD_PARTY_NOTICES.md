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
