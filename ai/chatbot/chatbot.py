"""
Chatbot service for the AI-Based Integrated MOOC Platform.

Standalone entry point that can be called directly for local testing,
or wrapped by a small HTTP server if the AI layer is deployed as its
own microservice instead of being called in-process from Java.

Usage:
    python chatbot.py --prompt "Explain recursion" --context '{"courseId": 1}'
"""
import argparse
import json
import os

try:
    from openai import OpenAI
except ImportError:  # keeps the script runnable without the dependency installed
    OpenAI = None

SYSTEM_PROMPT = (
    "You are a helpful academic assistant for an online learning platform. "
    "Answer clearly and concisely, and relate the answer back to the course "
    "context when one is given. If you don't know something, say so plainly."
)


def load_prompt_template() -> str:
    path = os.path.join(os.path.dirname(__file__), "..", "prompts", "chatbot_system_prompt.txt")
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            return f.read().strip()
    return SYSTEM_PROMPT


def ask(prompt: str, context: dict | None = None) -> str:
    api_key = os.getenv("AI_API_KEY")
    model = os.getenv("AI_MODEL", "gpt-4o-mini")

    if not api_key or OpenAI is None:
        return (
            "[chatbot placeholder] AI_API_KEY not configured. "
            f"You asked: {prompt!r} (context={context})"
        )

    client = OpenAI(api_key=api_key)
    user_message = prompt if not context else f"{prompt}\n\nContext: {json.dumps(context)}"

    response = client.chat.completions.create(
        model=model,
        messages=[
            {"role": "system", "content": load_prompt_template()},
            {"role": "user", "content": user_message},
        ],
    )
    return response.choices[0].message.content


def main():
    parser = argparse.ArgumentParser(description="Chatbot service CLI")
    parser.add_argument("--prompt", required=True)
    parser.add_argument("--context", default=None, help="JSON string of extra context")
    args = parser.parse_args()

    context = json.loads(args.context) if args.context else None
    print(ask(args.prompt, context))


if __name__ == "__main__":
    main()
