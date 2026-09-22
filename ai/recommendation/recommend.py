"""
Recommendation service: suggests courses/lessons for a learner.

A simple content-based scorer ranks the catalog by category overlap
and level progression; the top results can optionally be re-ranked
and explained by an LLM call for a friendlier response.
"""
import argparse
import json


def score_course(course: dict, completed_categories: set, target_level: str) -> float:
    score = 0.0
    if course.get("category") in completed_categories:
        score += 1.0  # learner has shown interest in this area
    if course.get("level") == target_level:
        score += 2.0
    if course.get("published"):
        score += 0.5
    return score


def recommend(catalog: list[dict], completed_categories: set, target_level: str = "BEGINNER", top_n: int = 5):
    ranked = sorted(
        catalog,
        key=lambda c: score_course(c, completed_categories, target_level),
        reverse=True,
    )
    return ranked[:top_n]


def main():
    parser = argparse.ArgumentParser(description="Recommendation service CLI")
    parser.add_argument("--catalog-file", default=None, help="Path to a JSON file with the course catalog")
    parser.add_argument("--goal", default="")
    parser.add_argument("--level", default="BEGINNER")
    args = parser.parse_args()

    if args.catalog_file:
        with open(args.catalog_file, "r", encoding="utf-8") as f:
            catalog = json.load(f)
    else:
        catalog = []  # in production this comes from the backend/database

    results = recommend(catalog, completed_categories=set(), target_level=args.level)
    print(json.dumps(results, indent=2))


if __name__ == "__main__":
    main()
