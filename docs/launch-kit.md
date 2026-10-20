# Launch Materials

These are project-owned presentation materials and draft announcements, **not** a record of external posts or endorsements. Verify a venue's current rules and obtain account-owner approval before posting. Do not mass-post, buy stars, ask for coordinated votes, or describe public source as an open-source license.

## Links and Assets

- Repository: https://github.com/moka992/zhifou-fortune
- Evaluation APK: https://github.com/moka992/zhifou-fortune/releases/latest
- Real-device demos and privacy notes: [showcase](showcase.md)
- Independent MIT-licensed skill: https://github.com/moka992/zhifou-oracle-skills
- [Preview artwork and provenance](media/README.md)

The Android UI is primarily Simplified Chinese. The APK is debug-signed and currently targets `arm64-v8a`. The app's original code/assets remain all rights reserved; do not advertise it as MIT or imply that all included datasets share one license.

## Short Chinese Introduction

**知否运势：一款离线优先的 Android 占卜与传统历法应用**

把铜钱卦、韦特塔罗、公历农历、日程和几款互动小工具放进了一个原生 Android 应用。本地程序负责抽取和计算，不配置 AI 也能使用基础流程。需要进一步解读时，可以选择自己的模型服务。

这次放出了真实手机上的四牌抽取、3D 硬币和六枚骰子演示。特别希望收到小屏、大字体、横屏和不同马达设备上的使用反馈。下载仍是调试签名体验包，不是生产商店包；资料保存在本机，截图与视频使用独立演示数据。

演示与下载：https://github.com/moka992/zhifou-fortune

## Short English Introduction

**Zhifou Fortune: offline Android I Ching, tarot and the Chinese calendar**

A native Android app combining local I Ching casting, Rider-Waite-Smith tarot, the Gregorian/lunar calendar, schedules and tactile 3D utilities. Local code owns draws and calculations; optional AI interpretation uses the user's own provider.

The repository includes real-device recordings of manual four-card tarot, original 3D coins and a six-die roll. Feedback on compact screens, larger fonts, landscape layouts and different actuators is particularly useful. The UI is primarily Simplified Chinese, the source is public for inspection rather than open-source licensed, and the APK remains a debug-signed evaluation build.

Demo and download: https://github.com/moka992/zhifou-fortune

## Developer-Focused Angle

Lead with one inspectable implementation, not a list of features:

1. Explain how fixed local outcomes remain separate from model-generated interpretation.
2. Show a real multi-die roll or card reveal, then link to the relevant geometry/motion tests.
3. Explain capability-aware haptic fallbacks without promising iPhone-identical output.
4. Link to the independent skill for readers who want reusable, permissively licensed tools instead of an Android application.

## Venue Review — 2026-10-20

| Venue | Suitability and action |
| --- | --- |
| Own GitHub repository and Release | Publish the real captures, accurate metadata, download path and a privacy-aware feedback form. This is the current distribution surface. |
| Hacker News | A possible later submission about an actual runnable project, not merely a version bump. Read [Show HN rules](https://news.ycombinator.com/showhn.html) and [current account restrictions](https://news.ycombinator.com/showlim). Do not ask for votes; the maker needs an eligible account and time to answer questions. |
| V2EX | Its [current rules](https://v2ex.com/about) prohibit AI-generated posts. Do not paste this generated introduction there. The maker may independently write about firsthand experience, subject to the site's rules. |
| Curated skill directories | Submit the independent skill, not the Android app. Follow that repository's format and maturity requirements; see the skill's [launch kit](https://github.com/moka992/zhifou-oracle-skills/blob/main/docs/launch-kit.md). |

There are no claims of directory acceptance or community adoption. No external post or PR is authorized by this document alone.

## Review Outcomes

Use GitHub's actual recent page views, package downloads, usable feedback and reproducible integrations, not just stars. Counts can lag, and clones may include development or automation. Keep owner-only analytics snapshots out of public documentation. Review after a real post has had time to reach its audience; change one part of the presentation at a time.

After authenticating GitHub CLI, [repository_metrics.py](../tools/repository_metrics.py) reads these values without generating traffic. Run it for either project and save snapshots outside the repository:

```sh
python3 tools/repository_metrics.py --repo moka992/zhifou-fortune
python3 tools/repository_metrics.py --repo moka992/zhifou-oracle-skills
```

The script does not schedule background checks or publish private traffic reports. Missing access is represented as unavailable, not a zero counter.
