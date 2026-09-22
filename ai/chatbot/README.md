# Chatbot Service

Provides the 24/7 conversational academic assistant used from the
course view page. Answers student questions in real time, optionally
grounded in the current course/lesson context passed from the backend.

## Files
- `chatbot.py` — request handler / CLI entry point for local testing.

## How it's called
The Spring Boot backend (`AIService`) calls into this layer either by:
1. Invoking `chatbot.py` as a subprocess / microservice, or
2. Calling an LLM provider directly (OpenAI-compatible `/chat/completions`)
   using the prompt template in `../prompts/chatbot_system_prompt.txt`.

## Environment variables
- `AI_API_KEY` — provider API key
- `AI_MODEL` — model name (default: `gpt-4o-mini`)

## Local test
```bash
python chatbot.py --prompt "Explain binary search" --context '{"courseId": 1}'
```
