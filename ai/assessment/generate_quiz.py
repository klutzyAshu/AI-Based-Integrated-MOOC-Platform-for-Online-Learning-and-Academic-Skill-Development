"""
Assessment service: generates multiple-choice quiz questions from
lesson content using an LLM, returning JSON that matches the backend's
QuizRequest.QuestionItem contract.
"""
import argparse
import json
import os

try:
    from openai import OpenAI
except ImportError:
    OpenAI = None

SYSTEM_PROMPT = (
    "You generate multiple-choice quiz questions from lesson content. "
    "Return ONLY valid JSON: a list of objects with keys "
    "'text', 'options' (exactly 4 strings), 'correctIndex' (0-based int), "
    "and 'explanation'. Do not include any other text."
)


def generate_quiz(lesson_text: str, num_questions: int = 5) -> list:
    api_key = os.getenv("AI_API_KEY")
    model = os.getenv("AI_MODEL", "gpt-4o-mini")

    if not api_key or OpenAI is None:
        # Deterministic placeholder so the pipeline still works without a key.
        return [
            {
                "text": f"Placeholder question {i + 1} about the lesson content",
                "options": ["Option A", "Option B", "Option C", "Option D"],
                "correctIndex": 0,
                "explanation": "AI_API_KEY not configured; this is a placeholder question.",
            }
            for i in range(num_questions)
        ]

    client = OpenAI(api_key=api_key)
    response = client.chat.completions.create(
        model=model,
        messages=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {
                "role": "user",
                "content": f"Generate {num_questions} questions from this lesson:\n\n{lesson_text}",
            },
        ],
    )
    content = response.choices[0].message.content
    return json.loads(content)


def main():
    parser = argparse.ArgumentParser(description="Assessment / quiz generation CLI")
    parser.add_argument("--lesson-file", required=True)
    parser.add_argument("--num-questions", type=int, default=5)
    args = parser.parse_args()

    with open(args.lesson_file, "r", encoding="utf-8") as f:
        lesson_text = f.read()

    questions = generate_quiz(lesson_text, args.num_questions)
    print(json.dumps(questions, indent=2))


if __name__ == "__main__":
    main()
