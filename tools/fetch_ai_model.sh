#!/bin/sh
# Gets the AI build ready: downloads the model (Gemma 3 270M, ONNX, int8,
# about 450 MB) and builds the app assets that go with it.
#
#   tools/fetch_ai_model.sh
#
# Only needed for the "ai" build. The standard build needs none of this.
# The model is not in git (too big); this puts it in
# app/src/ai/assets/ai-model/, which git ignores.
#
# Gemma is released under Google's Gemma Terms of Use:
# https://ai.google.dev/gemma/terms
set -e

cd "$(dirname "$0")/.."
EVAL=tools/ai_eval
MODEL=$EVAL/models/gemma3-270m
REPO=https://huggingface.co/onnx-community/gemma-3-270m-ONNX/resolve/main

if [ ! -d "$EVAL/.venv" ]; then
    echo "Making a Python environment for the tools..."
    python3 -m venv "$EVAL/.venv"
    "$EVAL/.venv/bin/pip" install -q onnxruntime tokenizers numpy onnx
fi

mkdir -p "$MODEL/onnx"
for f in config.json tokenizer.json tokenizer_config.json onnx/model_int8.onnx; do
    if [ ! -s "$MODEL/$f" ]; then
        echo "Downloading $f..."
        curl -sfL -o "$MODEL/$f" "$REPO/$f"
    fi
done

"$EVAL/.venv/bin/python" -I tools/build_ai_assets.py "$MODEL" app/src/ai/assets/ai-model
echo "Done. Build the AI version with: ./gradlew assembleAiRelease"
