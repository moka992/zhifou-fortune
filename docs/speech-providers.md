# Speech transcription providers

The app records locally while the question field is held. A released, confirmed recording is transcribed once; cancelling the gesture discards the recording without uploading it. Cloud transcription is optional and disabled by default. Turning it off selects the existing on-device recognizer.

This catalog was checked against official documentation on **2026-10-18**. It covers short-recording APIs that accept audio directly from the phone, without requiring a public audio URL, a storage bucket, or a separately operated application server. It is not a complete directory of every speech service.

## Free options

| Service | What is free | Conditions |
| --- | --- | --- |
| SiliconFlow, `FunAudioLLM/SenseVoiceSmall` | Currently listed as a free model on the official price page | Requires the user's own API key; account access and rate limits apply. This does not make other SiliconFlow models free. |
| Groq, Whisper Large V3 / Turbo | A limited Free tier | The published baseline is 20 requests/minute, 2,000 requests/day, 7,200 audio seconds/hour, and 28,800 audio seconds/day. The first reached limit applies; actual account limits and regional availability may differ. Paid-tier requests can incur charges. |
| On-device recognition | No transcription API usage or cloud fee | Works offline with the app's bundled model. Quality depends on the recording and model. |

These are current policies, not promises of unlimited or permanent free service. Trial credits, introductory allowances, and paid-plan included minutes are not described as permanently free. The other presets below are labelled as account-dependent or metered. In particular, OpenAI API access is separate from a ChatGPT subscription.

Sources: [SiliconFlow prices](https://www.siliconflow.cn/pricing), [Groq rate limits](https://console.groq.com/docs/rate-limits), [Groq billing](https://console.groq.com/docs/billing-faqs), [OpenAI transcription models](https://developers.openai.com/api/docs/guides/transcription).

## Preset endpoints

These are complete request URLs, not SDK base URLs.

| Platform | Endpoint | Models |
| --- | --- | --- |
| SiliconFlow | `https://api.siliconflow.cn/v1/audio/transcriptions` | `FunAudioLLM/SenseVoiceSmall` |
| Groq | `https://api.groq.com/openai/v1/audio/transcriptions` | `whisper-large-v3-turbo`, `whisper-large-v3` |
| Qwen, mainland China | `https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions` | `qwen3-asr-flash` |
| Qwen, Singapore | `https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions` | `qwen3-asr-flash` |
| OpenAI | `https://api.openai.com/v1/audio/transcriptions` | `gpt-transcribe`, `gpt-4o-mini-transcribe`, `gpt-4o-transcribe`, `whisper-1` |
| Mistral | `https://api.mistral.ai/v1/audio/transcriptions` | `voxtral-mini-latest` (Voxtral Mini Transcribe 2) |
| ElevenLabs | `https://api.elevenlabs.io/v1/speech-to-text` | `scribe_v2` |

Each preset offers a manual model-ID entry as well. The Custom option retains the existing OpenAI-compatible multipart transcription configuration. Existing endpoint, model, key, and cloud-enabled preference are preserved during upgrade; no preset silently replaces them.

Qwen's shared regional domains remain supported according to its documentation. Workspace-specific domains are also recognized when entered through Custom. No workspace ID is required for the shared-domain presets. Keys for different regions are not interchangeable.

Services that require cloud-project credentials, multiple signing secrets, WebSocket sessions, or upload-and-poll workflows are not represented as a one-key drop-in preset. A general chat model is not automatically a transcription model. Language auto-detection is left enabled rather than forcing English or Chinese, so mixed-language recordings are not restricted to a single language hint. Accuracy still needs evaluation on representative recordings; a connection test cannot establish recognition quality.

## Protocol handling

- OpenAI, Groq, and custom compatible endpoints use multipart `model`, `file`, and `response_format=json`.
- SiliconFlow and Mistral use the documented multipart `model` and `file` fields without unsupported formatting parameters.
- Qwen receives Base64 WAV data through its audio Chat Completions schema, with no divination prompt or user profile. The client checks both duration and Base64 expansion against the documented input limits before sending.
- ElevenLabs uses `xi-api-key`, multipart `model_id`, and `file`. Diarization and audio-event tagging are disabled. Optional paid enrichment features are not requested.
- Recordings are encoded in memory as mono PCM WAV. No temporary recording file or public media upload is created by this transport. SenseVoice metadata tags are removed from the resulting text.
- All endpoints must use HTTPS. Authenticated requests do not follow redirects or automatically retry a failed connection. Timeout and provider errors do not silently switch to another paid service.

## Saving and connection tests

Platform, model, endpoint, and key are saved together. Unsaved changes can be cancelled. A switch to a different credential origin, including a regional endpoint change, clears the draft key. The speech key is independent of the conversational AI key; neither is copied into the other configuration.

Saving and app startup do **not** send a speech request. Connection tests are manual. A successful configuration fingerprint is remembered locally and restored without a network call; changing the actual credentials, endpoint, or model invalidates that result.

The connection test sends a **one-second synthetic silent WAV**, never microphone input, existing recordings, history, or profile data. The response must have the correct transcription schema; an HTTP 200 with an unrelated body is not accepted. An empty transcript is valid for this silent test, but is not accepted as a successful normal transcription. A provider may reject silence, in which case the test reports the error rather than falsely declaring the key invalid. A successful probe verifies API connectivity and schema compatibility, not accuracy.

Tests may consume provider allowance or minimum billable duration; Groq, for example, documents a minimum billable audio length. The UI discloses this before the user runs a test. No automatic daily or startup probe is scheduled. Lack of validated internet connectivity is reported separately from authentication and model errors.

Credentials remain in the application's private local preferences, and the application disables Android Auto Backup. Draft keys are excluded from Activity saved-state bundles. The adapter logs neither keys nor audio, redacts keys from provider error messages, and never bundles a service account key in the APK or repository. Cloud-provider retention and data-processing rules remain governed by the selected provider's policies.

## Official references

- SiliconFlow: [transcription API](https://docs.siliconflow.cn/docs/api/audio-transcriptions-post), [pricing](https://www.siliconflow.cn/pricing)
- Groq: [speech to text](https://console.groq.com/docs/speech-to-text), [Free tier limits](https://console.groq.com/docs/rate-limits)
- Qwen: [audio API and regional endpoints](https://help.aliyun.com/en/model-studio/qwen-asr-api-reference), [new-user quota rules](https://help.aliyun.com/en/model-studio/new-free-quota)
- OpenAI: [file transcription](https://developers.openai.com/api/docs/guides/speech-to-text), [model guidance](https://developers.openai.com/api/docs/guides/transcription)
- Mistral: [file transcription](https://docs.mistral.ai/studio/audio/speech_to_text/offline_transcription), [Voxtral Mini Transcribe 2](https://docs.mistral.ai/models/voxtral-mini-transcribe-26-02)
- ElevenLabs: [Scribe API](https://elevenlabs.io/docs/api-reference/speech-to-text/convert), [pricing](https://elevenlabs.io/pricing/api)

## Verification

Automated tests cover all preset request formats and authentication headers, PCM WAV encoding, mixed-language response parsing, regional key isolation, legacy settings, cached connection states, silent-versus-real transcription semantics, error redaction, redirect refusal, and input limits. Network mocks use dummy keys and generated audio only. Physical-device checks exercise the actual settings UI; availability and recognition quality for each provider still require a valid account key and representative speech.
