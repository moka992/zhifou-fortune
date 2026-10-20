#!/usr/bin/env python3
"""Read actual GitHub counters; never manufacture visits, stars or user counts."""
import argparse
from datetime import datetime, timezone
import json
from pathlib import Path
import re
import subprocess

ROOT = Path(__file__).resolve().parents[1]


def query(path):
    response = subprocess.run(["gh", "api", path], capture_output=True, text=True)
    if response.returncode:
        return None
    return json.loads(response.stdout)


def collect(repo):
    if not re.fullmatch(r"[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+", repo):
        raise ValueError("Use an explicit owner/repository name")
    metadata = query("repos/" + repo)
    if metadata is None:
        raise RuntimeError("Repository metadata unavailable; check GitHub CLI access")
    traffic = {}
    for metric in ("views", "clones"):
        data = query(f"repos/{repo}/traffic/{metric}")
        traffic[metric] = {k: data[k] for k in ("count", "uniques")} if data is not None else None
    referrers = query(f"repos/{repo}/traffic/popular/referrers")
    release = query(f"repos/{repo}/releases/latest")
    return {
        "repository": repo,
        "collected_at_utc": datetime.now(timezone.utc).isoformat(),
        "stars": metadata["stargazers_count"],
        "forks": metadata["forks_count"],
        "traffic_last_14_days": traffic,
        "top_referrers": referrers,
        "latest_release": None if release is None else {
            "tag": release["tag_name"],
            "assets": [{"name": item["name"], "downloads": item["download_count"]} for item in release["assets"]],
        },
        "limits": "Counters can lag. Clones are not users; downloads are not installations. Null means unavailable, not zero.",
    }


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", required=True)
    parser.add_argument("--output", type=Path, help="Optional private snapshot outside the source repository")
    args = parser.parse_args()
    if args.output and args.output.resolve().is_relative_to(ROOT):
        parser.error("Keep owner-only analytics snapshots outside the source repository")
    data = json.dumps(collect(args.repo), ensure_ascii=False, indent=2) + "\n"
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(data, encoding="utf-8")
        print("Saved private analytics snapshot outside the source repository")
    else:
        print(data, end="")
