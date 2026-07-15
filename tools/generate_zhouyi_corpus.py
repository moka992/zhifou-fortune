#!/usr/bin/env python3
"""Generate the offline Zhouyi corpus used by the copper-coin divination flow.

The input is Kanseki Repository KR1a0001 (周易正文). The generator deliberately
fails on incomplete data so a malformed source update cannot silently ship.
"""

from __future__ import annotations

import argparse
import re
import subprocess
from dataclasses import dataclass
from pathlib import Path


SOURCE_REPOSITORY = "https://github.com/kanripo/KR1a0001"
SOURCE_COMMIT = "8284adbf9e3435d713180e24f05bf75f8b7d1d96"
SOURCE_LICENSE = "CC BY-SA 4.0"

TRIGRAM_BITS = {
    "乾": "111",
    "兌": "110",
    "離": "101",
    "震": "100",
    "巽": "011",
    "坎": "010",
    "艮": "001",
    "坤": "000",
}

TITLE_RE = re.compile(r"《(.+?)第[一二三四五六七八九十]+》")
TRIGRAM_RE = re.compile(r"^([䷀-䷿])([乾兌離震巽坎艮坤])下([乾兌離震巽坎艮坤])上$")
LINE_HEADER_RE = re.compile(r"^(初[六九]|[六九][二三四五]|上[六九]|用[六九])[：、]?$")
SIX_LINE_HEADER_RE = re.compile(r"^(初[六九]|[六九][二三四五]|上[六九])[：、]?$")
SECTION_PREFIXES = ("《彖》曰", "《象》曰", "《文言》曰")


@dataclass(frozen=True)
class HexagramRecord:
    number: int
    name: str
    glyph: str
    pattern: str
    judgment: str
    lines: tuple[str, ...]
    line_commentaries: tuple[str, ...]
    use_text: str | None
    use_commentary: str | None
    tuan: str
    image: str


def clean_lines(path: Path) -> list[str]:
    result: list[str] = []
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.replace("¶", "").strip()
        if not line or line.startswith("#+") or line.startswith("# -*-"):
            continue
        if line.startswith("<pb:"):
            continue
        result.append(line)
    return result


def is_header(line: str) -> bool:
    return LINE_HEADER_RE.fullmatch(line) is not None


def is_section(line: str) -> bool:
    return line.startswith(SECTION_PREFIXES)


def normalize_header(header: str) -> str:
    return LINE_HEADER_RE.fullmatch(header).group(1) + "："


def join_text(parts: list[str]) -> str:
    return "".join(parts).strip()


def collect_after(tokens: list[str], start: int) -> str:
    parts: list[str] = []
    for token in tokens[start:]:
        if is_header(token) or is_section(token):
            break
        parts.append(token)
    return join_text(parts)


def section_text(tokens: list[str], prefix: str, after: int = 0) -> tuple[str, int]:
    index = next(
        (i for i in range(after, len(tokens)) if tokens[i].startswith(prefix)),
        -1,
    )
    if index < 0:
        return "", -1
    marker = tokens[index]
    inline = marker[len(prefix):].lstrip("：")
    parts = [inline] if inline else []
    for token in tokens[index + 1:]:
        if is_header(token) or is_section(token):
            break
        parts.append(token)
    label = prefix + "："
    return label + join_text(parts), index


def split_qian_image(image: str) -> tuple[str, tuple[str, ...], str]:
    label = "《象》曰："
    body = image.removeprefix(label)
    anchors = (
        "「潛龍勿用」",
        "「見龍在田」",
        "「終日乾乾」",
        "「或躍在淵」",
        "「飛龍在天」",
        "「亢龍有悔」",
        "「用九」",
    )
    offsets = [body.find(anchor) for anchor in anchors]
    if any(offset < 0 for offset in offsets) or offsets != sorted(offsets):
        raise ValueError("乾卦《象》分段失败")
    sections = [
        label + body[offsets[index]:offsets[index + 1] if index + 1 < len(offsets) else None]
        for index in range(len(offsets))
    ]
    return label + body[:offsets[0]], tuple(sections[:6]), sections[6]


def parse_hexagram(path: Path, number: int) -> HexagramRecord:
    tokens = clean_lines(path)
    title_match = next((TITLE_RE.search(token) for token in tokens if TITLE_RE.search(token)), None)
    if title_match is None:
        raise ValueError(f"{path.name}: missing title")
    name = title_match.group(1)

    trigram_match = next((TRIGRAM_RE.fullmatch(token) for token in tokens if TRIGRAM_RE.fullmatch(token)), None)
    if trigram_match is None:
        raise ValueError(f"{path.name}: missing trigram declaration")
    glyph, lower, upper = trigram_match.groups()
    trigram_index = tokens.index(trigram_match.group(0))
    pattern = TRIGRAM_BITS[lower] + TRIGRAM_BITS[upper]

    judgment_parts: list[str] = []
    for token in tokens[trigram_index + 1:]:
        if is_header(token) or is_section(token):
            break
        judgment_parts.append(token)
    judgment = re.sub(r"^《[^》]+》", "", join_text(judgment_parts))

    line_texts: list[str] = []
    line_commentaries: list[str] = []
    use_text: str | None = None
    use_commentary: str | None = None
    for index, token in enumerate(tokens):
        if not is_header(token):
            continue
        body = collect_after(tokens, index + 1)
        normalized = normalize_header(token) + body
        boundary = index + 1
        while boundary < len(tokens) and not is_header(tokens[boundary]) and not is_section(tokens[boundary]):
            boundary += 1
        commentary = ""
        if boundary < len(tokens) and tokens[boundary].startswith("《象》曰"):
            commentary, _ = section_text(tokens, "《象》曰", boundary)
        if SIX_LINE_HEADER_RE.fullmatch(token):
            line_texts.append(normalized)
            line_commentaries.append(commentary)
        else:
            use_text = normalized
            use_commentary = commentary or None

    tuan, tuan_index = section_text(tokens, "《彖》曰")
    image, _ = section_text(tokens, "《象》曰", max(tuan_index + 1, 0))
    if number == 1:
        image, qian_line_commentaries, qian_use_commentary = split_qian_image(image)
        line_commentaries = list(qian_line_commentaries)
        use_commentary = qian_use_commentary

    if not judgment:
        raise ValueError(f"{path.name}: empty judgment")
    if len(line_texts) != 6:
        raise ValueError(f"{path.name}: expected 6 line texts, got {len(line_texts)}")
    if len(line_commentaries) != 6 or any(not text for text in line_commentaries):
        raise ValueError(f"{path.name}: incomplete line commentary")
    if not tuan or not image:
        raise ValueError(f"{path.name}: missing Tuan or Xiang commentary")

    return HexagramRecord(
        number=number,
        name=name,
        glyph=glyph,
        pattern=pattern,
        judgment=judgment,
        lines=tuple(line_texts),
        line_commentaries=tuple(line_commentaries),
        use_text=use_text,
        use_commentary=use_commentary,
        tuan=tuan,
        image=image,
    )


def kotlin_string(value: str) -> str:
    escaped = (
        value.replace("\\", "\\\\")
        .replace('"', '\\"')
        .replace("$", "\\$")
        .replace("\n", "\\n")
    )
    return f'"{escaped}"'


def render(records: list[HexagramRecord]) -> str:
    entries: list[str] = []
    for record in records:
        lines = ",\n".join(f"                {kotlin_string(line)}" for line in record.lines)
        line_commentaries = ",\n".join(
            f"                {kotlin_string(commentary)}"
            for commentary in record.line_commentaries
        )
        use_text = "null" if record.use_text is None else kotlin_string(record.use_text)
        use_commentary = (
            "null" if record.use_commentary is None else kotlin_string(record.use_commentary)
        )
        entries.append(
            f"""        ZhouyiHexagramText(
            number = {record.number},
            name = {kotlin_string(record.name)},
            glyph = {kotlin_string(record.glyph)},
            pattern = {kotlin_string(record.pattern)},
            judgment = {kotlin_string(record.judgment)},
            lineTexts = listOf(
{lines},
            ),
            lineCommentaries = listOf(
{line_commentaries},
            ),
            useText = {use_text},
            useCommentary = {use_commentary},
            tuan = {kotlin_string(record.tuan)},
            image = {kotlin_string(record.image)},
        )"""
        )

    corpus = ",\n".join(entries)
    return f"""// Generated by tools/generate_zhouyi_corpus.py. Do not edit manually.
// Source: {SOURCE_REPOSITORY} @ {SOURCE_COMMIT}
// Source license: {SOURCE_LICENSE}; see THIRD_PARTY_NOTICES.md.
package com.zhifou.fortune

internal data class ZhouyiHexagramText(
    val number: Int,
    val name: String,
    val glyph: String,
    /** Six bits from the bottom line to the top line; 1 is yang and 0 is yin. */
    val pattern: String,
    val judgment: String,
    val lineTexts: List<String>,
    val lineCommentaries: List<String>,
    val useText: String?,
    val useCommentary: String?,
    val tuan: String,
    val image: String,
) {{
    fun lineText(position: Int): String = lineTexts[position - 1]
    fun lineCommentary(position: Int): String = lineCommentaries[position - 1]
}}

internal object ZhouyiClassics {{
    const val SOURCE_LABEL = "《周易》经文（Kanseki Repository KR1a0001）"
    const val SOURCE_REPOSITORY = "{SOURCE_REPOSITORY}"
    const val SOURCE_COMMIT = "{SOURCE_COMMIT}"
    const val SELECTION_METHOD = "朱熹《易学启蒙·考变占》变占框架"

    val all: List<ZhouyiHexagramText> = listOf(
{corpus},
    )

    private val byPattern = all.associateBy(ZhouyiHexagramText::pattern)
    private val byNumber = all.associateBy(ZhouyiHexagramText::number)

    fun fromLines(lines: List<Boolean>): ZhouyiHexagramText {{
        require(lines.size == 6) {{ "A hexagram requires exactly six lines" }}
        val pattern = lines.joinToString(separator = "") {{ if (it) "1" else "0" }}
        return checkNotNull(byPattern[pattern]) {{ "Missing Zhouyi text for pattern $pattern" }}
    }}

    fun byNumber(number: Int): ZhouyiHexagramText =
        checkNotNull(byNumber[number]) {{ "Missing Zhouyi text for hexagram $number" }}
}}
"""


def validate(records: list[HexagramRecord]) -> None:
    if len(records) != 64:
        raise ValueError(f"expected 64 hexagrams, got {len(records)}")
    if len({record.pattern for record in records}) != 64:
        raise ValueError("hexagram line patterns are not unique")
    if [record.number for record in records] != list(range(1, 65)):
        raise ValueError("hexagram numbering is incomplete")
    use_records = [(record.number, record.use_text) for record in records if record.use_text]
    if [number for number, _ in use_records] != [1, 2]:
        raise ValueError(f"unexpected 用九/用六 records: {use_records}")
    if not records[0].use_text.startswith("用九："):
        raise ValueError("乾卦 is missing 用九")
    if not records[1].use_text.startswith("用六："):
        raise ValueError("坤卦 is missing 用六")
    if any(len(record.line_commentaries) != 6 for record in records):
        raise ValueError("line commentary is incomplete")
    if not records[0].use_commentary or not records[1].use_commentary:
        raise ValueError("乾坤用九/用六 commentary is incomplete")


def verify_source_revision(source: Path) -> None:
    revision = subprocess.run(
        ["git", "-C", str(source), "rev-parse", "HEAD"],
        check=True,
        capture_output=True,
        text=True,
    ).stdout.strip()
    if revision != SOURCE_COMMIT:
        raise ValueError(
            f"KR1a0001 revision mismatch: expected {SOURCE_COMMIT}, got {revision}"
        )
    source_files = [f"KR1a0001_{number:03d}.txt" for number in range(1, 65)]
    diff = subprocess.run(
        ["git", "-C", str(source), "diff", "--quiet", "HEAD", "--", *source_files],
        check=False,
    )
    if diff.returncode != 0:
        raise ValueError("KR1a0001 source files contain local modifications")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", type=Path, help="Path to a KR1a0001 checkout")
    parser.add_argument("output", type=Path, help="Generated Kotlin file")
    args = parser.parse_args()

    verify_source_revision(args.source)
    records = [
        parse_hexagram(args.source / f"KR1a0001_{number:03d}.txt", number)
        for number in range(1, 65)
    ]
    validate(records)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(render(records), encoding="utf-8")
    print(f"Generated {len(records)} hexagrams at {args.output}")


if __name__ == "__main__":
    main()
