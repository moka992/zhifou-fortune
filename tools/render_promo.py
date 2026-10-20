#!/usr/bin/env python3
"""Arrange unretouched device captures into an editable social-preview SVG."""
import argparse
import base64
from pathlib import Path
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]


def render(output, raster=None, font=None):
    ns = "http://www.w3.org/2000/svg"
    ET.register_namespace("", ns)
    root = ET.Element("{" + ns + "}svg", {"viewBox": "0 0 1280 640", "width": "1280", "height": "640", "role": "img"})
    def node(tag, text=None, **props):
        element = ET.SubElement(root, "{" + ns + "}" + tag, {k.replace("_", "-"): str(v) for k, v in props.items()})
        element.text = text
        return element
    node("title", "Zhifou Fortune: offline Android readings, calendar and tools, with real device screenshots")
    node("rect", width=1280, height=640, fill="#F5F8F7")
    node("text", "ANDROID / OFFLINE-FIRST", x=56, y=78, font_family="system-ui, sans-serif", font_size=18, fill="#216451")
    node("text", "知否运势", x=56, y=190, font_family="system-ui, sans-serif", font_size=76, font_weight=700, fill="#202925")
    node("text", "Zhifou Fortune", x=60, y=245, font_family="system-ui, sans-serif", font_size=34, fill="#216451")
    node("text", "离线占卜 · 中国历法 · 互动小工具", x=60, y=320, font_family="system-ui, sans-serif", font_size=26, fill="#344C43")
    node("text", "I Ching. Tarot. The Chinese calendar.", x=60, y=365, font_family="system-ui, sans-serif", font_size=24, fill="#51655C")
    node("text", "Local first. AI optional.", x=60, y=406, font_family="system-ui, sans-serif", font_size=24, fill="#51655C")
    node("line", x1=60, y1=483, x2=640, y2=483, stroke="#CEDBD5")
    node("text", "moka992/zhifou-fortune", x=60, y=533, font_family="ui-monospace, monospace", font_size=23, fill="#202925")
    node("text", "真实实机画面 · 独立演示数据 / Recorded UI · demo data", x=60, y=584, font_family="system-ui, sans-serif", font_size=16, fill="#63756C")
    for index, name in enumerate(("home.png", "tarot-spread.png")):
        image = ROOT / "docs/media" / name
        encoded = base64.b64encode(image.read_bytes()).decode("ascii")
        node("image", x=742 + index * 253, y=60, width=234, height=520,
             href="data:image/png;base64," + encoded, preserveAspectRatio="xMidYMid meet")
    output.parent.mkdir(parents=True, exist_ok=True)
    ET.ElementTree(root).write(output, encoding="utf-8", xml_declaration=True)
    print("Rendered " + output.name)
    if raster:
        if font is None:
            raise ValueError("Raster export requires --font with a CJK-capable TrueType font")
        from io import BytesIO
        from PIL import Image, ImageDraw, ImageFont

        canvas = Image.new("RGB", (1280, 640), "#F5F8F7")
        draw = ImageDraw.Draw(canvas)
        for element in root:
            props = element.attrib
            tag = element.tag.rsplit("}", 1)[-1]
            if tag == "text":
                face = ImageFont.truetype(str(font), int(props["font-size"]))
                draw.text((int(props["x"]), int(props["y"])), element.text, font=face,
                          fill=props["fill"], anchor="ls")
            elif tag == "line":
                draw.line(tuple(int(props[key]) for key in ("x1", "y1", "x2", "y2")), fill=props["stroke"])
            elif tag == "image":
                source = Image.open(BytesIO(base64.b64decode(props["href"].split(",", 1)[1]))).convert("RGB")
                source.thumbnail((int(props["width"]), int(props["height"])), Image.Resampling.LANCZOS)
                canvas.paste(source, (int(props["x"]), int(props["y"])))
        raster.parent.mkdir(parents=True, exist_ok=True)
        canvas.save(raster, "JPEG", quality=90, optimize=True)
        print("Rendered " + raster.name)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, default=ROOT / "docs/media/social-preview.svg")
    parser.add_argument("--raster", type=Path, help="Optional 1280 x 640 JPEG export; requires Pillow")
    parser.add_argument("--font", type=Path, help="CJK-capable TrueType font for the optional raster export")
    args = parser.parse_args()
    render(args.output, args.raster, args.font)
