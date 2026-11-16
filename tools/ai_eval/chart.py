"""Draws results.png, the chart in README.md, from the results table there.

    python3 -m pip install matplotlib
    python3 tools/ai_eval/chart.py

The numbers are copied from the README table (test half). Change both
together.
"""
import os

import matplotlib.pyplot as plt

# (label, presses saved %, low, high, note)
ROWS = [
    ("n-gram (the app today)", 49.9, None, None, ""),
    ("Gemma 3 270M, blended", 50.9, None, None, "the AI build"),
    ("Gemma 3 270M, alone", 40.6, None, None, ""),
    ("Qwen 2.5 0.5B, blended", 49.6, None, None, ""),
    ("Gemma 3 1B, blended", 52.0, None, None, "too slow for a tablet"),
    ("Gemma 3 1B, once per word", 51.5, 51.2, 51.8, ""),
]

BAR = "#3b6fb6"
INK = "#1f2328"
MUTED = "#656d76"
GRID = "#d8dee4"

fig, ax = plt.subplots(figsize=(8, 3.6), dpi=200)
ys = list(range(len(ROWS)))[::-1]
for y, (label, value, low, high, note) in zip(ys, ROWS):
    ax.barh(y, value, height=0.6, color=BAR)
    if low is not None:
        ax.plot([low, high], [y, y], color=INK, linewidth=1.5)
        ax.plot([low, low], [y - 0.12, y + 0.12], color=INK, linewidth=1.5)
        ax.plot([high, high], [y - 0.12, y + 0.12], color=INK, linewidth=1.5)
        text = f"{low:.1f}–{high:.1f}%"
        end = high
    else:
        text = f"{value:.1f}%"
        end = value
    ax.text(end + 0.8, y, text, va="center", fontsize=9, color=INK)
    if note:
        ax.text(end + 7.5, y, note, va="center", fontsize=8.5, color=MUTED, style="italic")

ax.axvline(49.9, color=MUTED, linewidth=1, linestyle=(0, (3, 3)))
ax.set_yticks(ys, [r[0] for r in ROWS], fontsize=9, color=INK)
ax.set_xlim(0, 75)
ax.set_xticks(range(0, 61, 10))
ax.tick_params(axis="x", colors=MUTED, labelsize=8.5)
ax.tick_params(axis="y", length=0)
ax.grid(axis="x", color=GRID, linewidth=0.8)
ax.set_axisbelow(True)
for side in ("top", "right", "left"):
    ax.spines[side].set_visible(False)
ax.spines["bottom"].set_color(GRID)
ax.set_xlabel("presses saved on the 30 test messages (%)", fontsize=9, color=MUTED)
ax.set_title("Word prediction for Mercè: what the AI adds", fontsize=11, color=INK, loc="left")
fig.tight_layout()
fig.savefig(os.path.join(os.path.dirname(os.path.abspath(__file__)), "results.png"), facecolor="white")
