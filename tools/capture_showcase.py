#!/usr/bin/env python3
"""Capture the real debug ShowcaseActivity UI, backed by isolated blank preferences.

Requires an unlocked, explicitly selected ADB device. Review every capture before publication.
No device serial, unlock credential, account content or network service is embedded here.
"""
import argparse
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

PACKAGE = "com.zhifou.fortune"
COMPONENT = PACKAGE + "/.ShowcaseActivity"
UI_PATH = "/data/local/tmp/zhifou-showcase.xml"
MOVIE_PATH = "/data/local/tmp/zhifou-showcase.mp4"


class Capture:
    def __init__(self, adb, serial, output):
        self.command = [adb, "-s", serial]
        self.output = output
        output.mkdir(parents=True, exist_ok=True)

    def adb(self, *args, binary=False):
        result = subprocess.run(self.command + list(args), check=True, capture_output=True)
        return result.stdout if binary else result.stdout.decode("utf-8", errors="replace")

    def guard(self):
        state = self.adb("shell", "dumpsys", "activity", "activities")
        resumed = [line for line in state.splitlines() if "ResumedActivity" in line or "topResumedActivity" in line]
        if not any("ShowcaseActivity" in line for line in resumed):
            raise RuntimeError("ShowcaseActivity is not foreground; refusing to capture another page")

    def nodes(self):
        self.guard()
        self.adb("shell", "uiautomator", "dump", UI_PATH)
        return list(ET.fromstring(self.adb("shell", "cat", UI_PATH)).iter("node"))

    def choose(self, label):
        rows = self.nodes()
        for exact in (True, False):
            for node in rows:
                texts = [node.get("text", ""), node.get("content-desc", "")]
                if node.get("enabled") == "true" and any(value == label if exact else label in value for value in texts):
                    return node
        return None

    def tap_node(self, node):
        self.guard()
        if node is None:
            raise RuntimeError("Expected showcase control missing")
        left, top, right, bottom = map(int, re.findall(r"\d+", node.get("bounds", "")))
        self.adb("shell", "input", "tap", str((left + right) // 2), str((top + bottom) // 2))

    def tap(self, label, pause=.6):
        self.tap_node(self.choose(label))
        time.sleep(pause)

    def shot(self, name):
        self.guard()
        path = self.output / (name + ".png")
        path.write_bytes(self.adb("exec-out", "screencap", "-p", binary=True))
        print("Captured " + path.name)

    def open_tool(self, name):
        self.tap("小工具")
        for _ in range(5):
            found = self.choose(name)
            if found is not None:
                self.tap_node(found)
                time.sleep(.9)
                return
            size = list(map(int, re.findall(r"\d+", self.adb("shell", "wm", "size"))))[-2:]
            width, height = size
            self.adb("shell", "input", "swipe", str(width // 2), str(height * 3 // 4),
                     str(width // 2), str(height // 3), "400")
            time.sleep(.4)
        raise RuntimeError("Showcase tool not visible")

    def record(self, seconds, action, name):
        self.guard()
        process = subprocess.Popen(self.command + ["shell", "screenrecord", "--bit-rate", "1600000",
                                   "--time-limit", str(seconds), MOVIE_PATH], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        try:
            time.sleep(.5)
            action()
            output, error = process.communicate(timeout=seconds + 15)
            if process.returncode:
                raise RuntimeError("Screen recording did not complete")
            self.guard()
            self.adb("pull", MOVIE_PATH, str(self.output / (name + ".mp4")))
            print("Recorded " + name + ".mp4")
        finally:
            if process.poll() is None:
                process.terminate()
                process.communicate(timeout=5)
            self.adb("shell", "rm", "-f", MOVIE_PATH)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--adb", default="adb")
    parser.add_argument("--serial", required=True, help="Authorized physical device; never commit this value")
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--scene", choices=("home", "calendar", "tools", "tarot", "casting", "coin", "dice"), required=True)
    parser.add_argument("--dark", action="store_true")
    args = parser.parse_args()
    capture = Capture(args.adb, args.serial, args.output)
    try:
        capture.adb("shell", "am", "start", "-S", "-W", "-n", COMPONENT, "--ez", "dark", str(args.dark).lower())
        time.sleep(2.5)
        if args.scene == "home":
            capture.shot("home")
        elif args.scene in ("calendar", "tools"):
            capture.tap("日历" if args.scene == "calendar" else "小工具")
            time.sleep(1)
            capture.shot(args.scene)
        elif args.scene == "tarot":
            capture.open_tool("四牌塔罗")
            capture.shot("tarot-ready")
            def draw():
                capture.tap("开始抽牌", pause=2.2)
                for _ in range(4):
                    cards = [node for node in capture.nodes() if "未翻开" in node.get("content-desc", "") and node.get("enabled") == "true"]
                    capture.tap_node(cards[0] if cards else None)
                    time.sleep(2.3)
                capture.shot("tarot-spread")
            capture.record(30, draw, "tarot-demo")
        elif args.scene == "casting":
            capture.tap("占卜")
            capture.tap("选择占卜功能")
            capture.tap("铜钱卦")
            def cast():
                capture.tap("使用铜钱卦", pause=1.35)
                capture.shot("casting")
                time.sleep(6)
                capture.shot("cast-result")
            capture.record(12, cast, "casting-demo")
        elif args.scene in ("coin", "dice"):
            name = "抛硬币" if args.scene == "coin" else "摇骰子"
            capture.open_tool(name)
            if args.scene == "dice":
                capture.tap("1枚")
                capture.tap("6枚")
            capture.shot(args.scene + "-ready")
            def play():
                controls = [n for n in capture.nodes() if n.get("enabled") == "true" and
                            name in (n.get("text", ""), n.get("content-desc", ""))]
                capture.tap_node(controls[-1] if controls else None)
                time.sleep(4)
                if capture.choose("等待抛掷") is not None or capture.choose("等待结果") is not None:
                    raise RuntimeError("The tool action was not triggered; recording is not publishable")
                capture.shot(args.scene + "-result")
            capture.record(8, play, args.scene + "-demo")
    finally:
        capture.adb("shell", "rm", "-f", UI_PATH)


if __name__ == "__main__":
    main()
