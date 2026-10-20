# AI provider configuration

Provider presets are a local, curated catalog of text-generation APIs, checked against official documentation on **2026-10-18**. They are not an exhaustive model directory. Availability, regional access, billing, and model entitlements are controlled by the provider, not by this app. Consumer chat subscriptions and coding-plan subscriptions do not necessarily include general API access.

## Presets

The URLs below are complete request endpoints, not SDK base URLs.

| Platform | Endpoint | Model choices |
| --- | --- | --- |
| DeepSeek | `https://api.deepseek.com/chat/completions` | `deepseek-flash`, `deepseek-v4-pro` |
| Qwen, mainland China | `https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions` | `qwen3.8-flash`, `qwen3.7-plus`, `qwen3.8-max` |
| Qwen, Singapore | `https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions` | `qwen3.8-flash`, `qwen3.7-plus`, `qwen3.8-max` |
| Zhipu GLM | `https://open.bigmodel.cn/api/paas/v4/chat/completions` | `glm-4.7-flash`, `glm-4.7` |
| Kimi, mainland China | `https://api.moonshot.cn/v1/chat/completions` | `kimi-k2.6` |
| Kimi, international | `https://api.moonshot.ai/v1/chat/completions` | `kimi-k2.6` |
| MiniMax, mainland China | `https://api.minimax.cn/v1/chat/completions` | `MiniMax-M3` |
| MiniMax, international | `https://api.minimax.io/v1/chat/completions` | `MiniMax-M3` |
| SiliconFlow | `https://api.siliconflow.cn/v1/chat/completions` | `deepseek-ai/DeepSeek-V4-Flash`, `Pro/deepseek-ai/DeepSeek-V4` |
| OpenAI | `https://api.openai.com/v1/chat/completions` | `gpt-5.4-mini`, `gpt-5.4`, `gpt-5.5` |
| Google Gemini | `https://generativelanguage.googleapis.com/v1beta/openai/chat/completions` | `gemini-3.8-flash`, `gemini-3.1-flash-lite` |
| Anthropic Claude | `https://api.anthropic.com/v1/messages` | `claude-haiku-5-5`, `claude-sonnet-5-5` |

Each preset also accepts a manually entered model ID. The custom option retains editable OpenAI-compatible endpoint, model ID, and API key fields. Existing installations retain their exact saved configuration as a custom configuration; upgrading does not replace the endpoint, model, key, or successful-validation fingerprint.

The GLM preset deliberately lists established hybrid models rather than automatically selecting the latest thinking-only model. Models that cannot disable reasoning may exhaust a short output budget before producing a final answer. Manually entered models are not assumed to support parameters merely because their names resemble another model.

## Request compatibility

- Chat, daily insights, divination, and connection tests share a transport adapter. Their prompts and output budgets remain owned by the original feature.
- DeepSeek's official endpoint receives `thinking.type=disabled`. Hosted DeepSeek models use the hosting platform's schema instead.
- Qwen hybrid models use `enable_thinking=false`; GLM 4.7 uses `thinking.type=disabled`.
- Kimi K2.6 uses non-thinking mode and omits sampling temperature, whose permitted values differ from the app's usual values.
- MiniMax M3 disables thinking, separates reasoning from final text, and uses `max_completion_tokens`.
- The listed OpenAI GPT-5 models use `max_completion_tokens` and `reasoning_effort=none`, without sampling overrides.
- Gemini 3 Flash models use the documented `minimal` reasoning level. They do not support disabling reasoning entirely.
- Claude uses native Messages authentication and response blocks. System instructions are sent separately from the conversation. Haiku 5.5 disables thinking; Sonnet 5.5 uses `between_tools` at low effort. Sampling parameters are omitted.
- Private reasoning blocks are not displayed as final answers. Claude output-limit responses are normalized so the existing divination continuation logic still works.

## Saving, validation, and privacy

Edits remain local drafts until Save or Connection Test is pressed. One save updates provider, endpoint, model, and key together. Saving an unchanged configuration does not invalidate its successful test.

Connection validation still runs once after a changed configuration is saved, following the existing debounce. Restarting the app or changing the date does not retest a previously tested configuration. Manual Connection Test explicitly retries. Network absence is reported separately from invalid credentials. The small test requests only `OK`: normally at most 16 output tokens, with a 512-token ceiling for Gemini to accommodate its mandatory reasoning. Provider billing rules still apply to this request.

Keys remain in the app's existing private local preferences; the application disables Android Auto Backup. Draft secrets are not stored in Activity saved-state bundles. Changing credential origin (scheme, host, or port), including a regional platform switch, clears the draft key. Cancelling a draft leaves the active configuration unchanged. Presets use HTTPS; custom HTTP URLs display a warning.

Authenticated requests do not follow redirects, preventing credentials from being forwarded to another host. Preset endpoints cannot be replaced without selecting Custom. API errors redact the configured key, and no request headers or bodies are logged by this adapter. No credentials, user records, or private configuration files are part of the provider catalog or its tests.

## Official references

- [DeepSeek models and API](https://api-docs.deepseek.com/zh-cn/)
- [Qwen models](https://help.aliyun.com/zh/model-studio/models), [regional endpoints](https://help.aliyun.com/zh/model-studio/base-url), [Chat Completions parameters](https://help.aliyun.com/en/model-studio/qwen-api-via-openai-chat-completions)
- [GLM 4.7 Flash](https://docs.bigmodel.cn/cn/guide/models/free/glm-4.7-flash), [GLM model overview](https://docs.bigmodel.cn/cn/guide/start/model-overview)
- [Kimi K2.6](https://platform.kimi.com/docs/guide/kimi-k2-6-quickstart), [international API](https://platform.kimi.ai/docs/overview)
- [MiniMax mainland API](https://platform.minimax.cn/docs/api-reference/text-openai-api), [international API](https://platform.minimax.io/docs/api-reference/text-openai-api)
- [SiliconFlow Chat Completions](https://docs.siliconflow.cn/docs/api/chat-completions-post)
- [OpenAI model directory](https://developers.openai.com/api/docs/models), [Chat Completions](https://developers.openai.com/api/reference/resources/chat/subresources/completions/methods/create)
- [Gemini OpenAI compatibility](https://ai.google.dev/gemini-api/docs/openai), [model directory](https://ai.google.dev/gemini-api/docs/models)
- [Claude model directory](https://platform.claude.com/docs/en/models/overview), [Messages API](https://platform.claude.com/docs/en/api/messages/create), [thinking controls](https://platform.claude.com/docs/en/build-with-claude/thinking)

## Verification

Automated tests cover catalog integrity, regional key isolation, legacy configuration preservation, request formats, unchanged feature budgets and prompts, response decoding, error redaction, and redirect refusal. Local mock requests use dummy credentials only. Live access to every provider requires the user's own API permissions and has not been asserted by these tests.
