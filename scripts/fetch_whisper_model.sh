#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
model_dir="$repo_root/app/src/main/assets/asr/sherpa-onnx-whisper-tiny-int8"
base_url="https://huggingface.co/csukuangfj/sherpa-onnx-whisper-tiny/resolve/main"

mkdir -p "$model_dir"

download() {
    local name="$1"
    curl --fail --location --retry 3 --output "$model_dir/$name" "$base_url/$name"
}

download tiny-decoder.int8.onnx
download tiny-encoder.int8.onnx
download tiny-tokens.txt

cd "$model_dir"
shasum -a 256 -c <<'CHECKSUMS'
d2fece8dd42771f1df975c6c0445770d0c292bf7547c2cae04a6c0cc57540925  tiny-decoder.int8.onnx
d24fb083ae3b1041fc24e97971d60e280c9342201fbb67b0ab428a8b4a51a434  tiny-encoder.int8.onnx
b34b360dbb493e781e479794586d661700670d65564001f23024971d1f2fa126  tiny-tokens.txt
CHECKSUMS
