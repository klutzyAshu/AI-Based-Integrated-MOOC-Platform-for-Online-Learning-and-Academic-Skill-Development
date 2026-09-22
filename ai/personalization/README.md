# Personalization Service

Builds an adaptive study plan for a learner based on their available
time per week, pace, and recent quiz performance.

## Files
- `study_plan.py` — builds a week-by-week plan from remaining lessons.

## Local test
```bash
python study_plan.py --hours-per-week 5 --remaining-lessons 12 --avg-score 72
```
