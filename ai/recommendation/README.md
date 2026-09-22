# Recommendation Service

Suggests the next best course or lesson for a learner based on their
stated goals, current enrollments, and completed content.

## Files
- `recommend.py` — scoring + LLM-assisted ranking entry point.

## Approach
1. Pull the learner's profile (goals, completed courses, quiz scores)
   from the backend via the shared database.
2. Score the catalog with a simple content-based filter (category
   overlap, level progression).
3. Optionally re-rank the top candidates with an LLM call for a more
   natural, explained recommendation list.

## Local test
```bash
python recommend.py --user-id 1 --goal "backend development"
```
