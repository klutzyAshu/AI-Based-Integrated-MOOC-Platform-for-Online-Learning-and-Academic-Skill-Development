"""
Personalization service: builds an adaptive week-by-week study plan
based on a learner's available time, remaining lessons, and recent
quiz performance (used to decide how much review vs. new material to
schedule each week).
"""
import argparse
import json
import math


def build_plan(hours_per_week: float, remaining_lessons: int, avg_score: float, avg_lesson_hours: float = 0.75):
    lessons_per_week = max(1, math.floor(hours_per_week / avg_lesson_hours))
    weeks = max(1, math.ceil(remaining_lessons / lessons_per_week))

    # Lower scores get an extra review slot each week.
    needs_review = avg_score < 70

    plan = []
    lessons_left = remaining_lessons
    for week in range(1, weeks + 1):
        this_week = min(lessons_per_week, lessons_left)
        lessons_left -= this_week
        plan.append({
            "week": week,
            "new_lessons": this_week,
            "review_session": needs_review,
            "focus": "Reinforce fundamentals" if needs_review else "Progress through new material",
        })

    return {
        "weeks": weeks,
        "lessons_per_week": lessons_per_week,
        "plan": plan,
    }


def main():
    parser = argparse.ArgumentParser(description="Personalization / study plan CLI")
    parser.add_argument("--hours-per-week", type=float, required=True)
    parser.add_argument("--remaining-lessons", type=int, required=True)
    parser.add_argument("--avg-score", type=float, default=100.0)
    args = parser.parse_args()

    result = build_plan(args.hours_per_week, args.remaining_lessons, args.avg_score)
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
